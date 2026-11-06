import { NextResponse } from "next/server";

export async function DELETE(request: Request) {
  const refreshToken = request.headers.get("cookie")?.match(/(?:^|;\s*)nhatot_refresh=([^;]+)/)?.[1];
  if (!refreshToken) {
    const response = NextResponse.json({ signedOut: true });
    response.cookies.delete("nhatot_session");
    response.cookies.delete("nhatot_refresh");
    return response;
  }

  const gatewayUrl = process.env.API_GATEWAY_URL ?? "http://localhost:8080";
  let status = 200;
  try {
    const upstream = await fetch(new URL("/api/auth/logout", gatewayUrl), {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ refreshToken: decodeURIComponent(refreshToken) }),
      cache: "no-store",
    });
    status = upstream.status;
  } catch {
    status = 503;
  }
  const response = NextResponse.json({ signedOut: true, serverRevocationStatus: status }, { status: 200 });
  response.cookies.delete("nhatot_session");
  response.cookies.delete("nhatot_refresh");
  return response;
}
