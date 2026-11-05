import { NextRequest, NextResponse } from "next/server";

const sessionCookie = "nhatot_session";

async function proxy(
  request: NextRequest,
  context: { params: Promise<{ path: string[] }> },
) {
  const { path } = await context.params;
  const gatewayUrl = process.env.API_GATEWAY_URL ?? "http://localhost:8080";
  const target = new URL(`/api/${path.map(encodeURIComponent).join("/")}`, gatewayUrl);
  target.search = new URL(request.url).search;

  const headers = new Headers();
  const contentType = request.headers.get("content-type");
  if (contentType) {
    headers.set("content-type", contentType);
  }
  const token = request.cookies.get(sessionCookie)?.value;
  if (token) {
    headers.set("authorization", `Bearer ${token}`);
  }

  try {
    const upstream = await fetch(target, {
      method: request.method,
      headers,
      body: request.method === "GET" || request.method === "HEAD" ? undefined : await request.arrayBuffer(),
      cache: "no-store",
    });

    if (path.join("/") === "auth/login" && upstream.ok) {
      const login = await upstream.json() as {
        accessToken?: string;
        expiresIn?: number;
        account?: unknown;
      };
      if (!login.accessToken || !login.expiresIn) {
        return NextResponse.json({ message: "Phản hồi đăng nhập từ máy chủ không hợp lệ." }, { status: 502 });
      }

      const response = NextResponse.json({ account: login.account, expiresIn: login.expiresIn });
      response.cookies.set(sessionCookie, login.accessToken, {
        httpOnly: true,
        secure: process.env.NODE_ENV === "production",
        sameSite: "lax",
        path: "/",
        maxAge: login.expiresIn,
      });
      return response;
    }

    const responseHeaders = new Headers();
    const upstreamContentType = upstream.headers.get("content-type");
    if (upstreamContentType) {
      responseHeaders.set("content-type", upstreamContentType);
    }
    return new Response(await upstream.arrayBuffer(), {
      status: upstream.status,
      headers: responseHeaders,
    });
  } catch {
    return NextResponse.json({ message: "Không thể kết nối API Gateway. Hãy kiểm tra các service backend." }, { status: 503 });
  }
}

export const GET = proxy;
export const POST = proxy;
export const PATCH = proxy;
