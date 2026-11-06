import { NextRequest, NextResponse } from "next/server";

const accessCookie = "nhatot_session";
const refreshCookie = "nhatot_refresh";
const authPaths = new Set(["auth/login", "auth/refresh", "auth/logout"]);

type TokenResponse = {
  accessToken?: string;
  expiresIn?: number;
  refreshToken?: string;
  refreshExpiresIn?: number;
  account?: unknown;
};

function setSessionCookies(response: NextResponse, tokens: TokenResponse) {
  if (!tokens.accessToken || !tokens.expiresIn || !tokens.refreshToken || !tokens.refreshExpiresIn) {
    throw new Error("Phản hồi phiên đăng nhập từ máy chủ không hợp lệ.");
  }
  const options = {
    httpOnly: true,
    secure: process.env.NODE_ENV === "production",
    sameSite: "lax" as const,
    path: "/",
  };
  response.cookies.set(accessCookie, tokens.accessToken, { ...options, maxAge: tokens.expiresIn });
  response.cookies.set(refreshCookie, tokens.refreshToken, { ...options, maxAge: tokens.refreshExpiresIn });
}

async function proxy(
  request: NextRequest,
  context: { params: Promise<{ path: string[] }> },
) {
  const { path } = await context.params;
  const gatewayUrl = process.env.API_GATEWAY_URL ?? "http://localhost:8080";
  const target = new URL(`/api/${path.map(encodeURIComponent).join("/")}`, gatewayUrl);
  target.search = new URL(request.url).search;
  const pathKey = path.join("/");

  const headers = new Headers();
  const contentType = request.headers.get("content-type");
  if (contentType) {
    headers.set("content-type", contentType);
  }
  const accessToken = request.cookies.get(accessCookie)?.value;
  const refreshToken = request.cookies.get(refreshCookie)?.value;
  if (accessToken) {
    headers.set("authorization", `Bearer ${accessToken}`);
  }

  try {
    let body: BodyInit | undefined = request.method === "GET" || request.method === "HEAD"
      ? undefined
      : await request.arrayBuffer();
    if (pathKey === "auth/refresh" || pathKey === "auth/logout") {
      if (!refreshToken) {
        return clearSession(NextResponse.json({ message: "Phiên đăng nhập đã hết hạn." }, { status: 401 }));
      }
      headers.set("content-type", "application/json");
      body = JSON.stringify({ refreshToken });
    }

    let upstream = await fetch(target, {
      method: request.method,
      headers,
      body,
      cache: "no-store",
    });

    let rotatedTokens: TokenResponse | undefined;
    if (upstream.status === 401 && refreshToken && !authPaths.has(pathKey)) {
      const refreshed = await fetch(new URL("/api/auth/refresh", gatewayUrl), {
        method: "POST",
        headers: { "content-type": "application/json" },
        body: JSON.stringify({ refreshToken }),
        cache: "no-store",
      });
      if (refreshed.ok) {
        rotatedTokens = await refreshed.json() as TokenResponse;
        if (!rotatedTokens.accessToken || !rotatedTokens.refreshToken) {
          return NextResponse.json({ message: "Phản hồi làm mới phiên từ máy chủ không hợp lệ." }, { status: 502 });
        }
        headers.set("authorization", `Bearer ${rotatedTokens.accessToken}`);
        upstream = await fetch(target, {
          method: request.method,
          headers,
          body,
          cache: "no-store",
        });
      } else if (refreshed.status === 401) {
        return clearSession(await responseFrom(upstream));
      }

      if (!refreshed.ok && refreshed.status !== 401) return await responseFrom(refreshed);
    }

    if ((pathKey === "auth/login" || pathKey === "auth/refresh") && upstream.ok) {
      const tokens = await upstream.json() as TokenResponse;
      const response = NextResponse.json({ account: tokens.account, expiresIn: tokens.expiresIn });
      setSessionCookies(response, tokens);
      return response;
    }

    const response = await responseFrom(upstream);
    if (rotatedTokens) {
      setSessionCookies(response, rotatedTokens);
    }
    if (pathKey === "auth/logout" && upstream.ok) return clearSession(response);
    return response;
  } catch {
    return NextResponse.json({ message: "Không thể kết nối API Gateway hoặc phản hồi phiên không hợp lệ." }, { status: 503 });
  }
}

async function responseFrom(upstream: Response) {
  const responseHeaders = new Headers();
  const contentType = upstream.headers.get("content-type");
  if (contentType) responseHeaders.set("content-type", contentType);
  return new NextResponse(await upstream.arrayBuffer(), {
    status: upstream.status,
    headers: responseHeaders,
  });
}

function clearSession(response: NextResponse) {
  response.cookies.delete(accessCookie);
  response.cookies.delete(refreshCookie);
  return response;
}

export const GET = proxy;
export const POST = proxy;
export const PUT = proxy;
export const PATCH = proxy;
export const DELETE = proxy;
