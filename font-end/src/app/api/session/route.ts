import { NextResponse } from "next/server";

export function DELETE() {
  const response = NextResponse.json({ signedOut: true });
  response.cookies.delete("nhatot_session");
  return response;
}
