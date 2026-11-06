"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { Account, apiRequest } from "@/lib/api";

export function SiteHeader() {
  const router = useRouter();
  const [account, setAccount] = useState<Account | null>(null);

  useEffect(() => {
    apiRequest<Account>("me")
      .then(setAccount)
      .catch(() => setAccount(null));
  }, []);

  async function signOut() {
    await fetch("/api/session", { method: "DELETE" });
    setAccount(null);
    router.push("/");
  }

  return (
    <header className="border-b border-[#e6e9e2] bg-[#fbfcfa]">
      <nav aria-label="Điều hướng chính" className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-5 py-4 lg:px-10">
        <Link className="flex shrink-0 items-center gap-3" href="/" aria-label="Nhà Tốt - Trang chủ">
          <span className="grid size-10 place-items-center rounded-2xl bg-[#1f594a] text-xl text-white">n</span>
          <span className="text-lg font-bold tracking-tight text-[#173c33]">nhà tốt</span>
        </Link>
        <div className="flex items-center gap-3 text-sm font-medium text-[#52645e] sm:gap-6">
          <Link className="hidden transition hover:text-[#1f594a] sm:inline" href="/#services">Dịch vụ</Link>
          {account ? (
            <>
              <Link className="transition hover:text-[#1f594a]" href="/history">Lịch sử</Link>
              <Link className="hidden transition hover:text-[#1f594a] sm:inline" href="/notifications">Thông báo</Link>
              {account.role === "PROVIDER" && <Link className="hidden transition hover:text-[#1f594a] sm:inline" href="/provider/packages">Gói dịch vụ</Link>}
              {account.role === "ADMIN" && <Link className="hidden transition hover:text-[#1f594a] sm:inline" href="/admin/providers">Duyệt hồ sơ</Link>}
              {account.role === "ADMIN" && <Link className="hidden transition hover:text-[#1f594a] sm:inline" href="/admin/packages">Duyệt gói</Link>}
              <button className="rounded-full border border-[#d6ded7] px-4 py-2 text-sm font-semibold text-[#214d40] hover:bg-white" onClick={signOut}>
                Đăng xuất
              </button>
            </>
          ) : (
            <>
              <Link className="hidden transition hover:text-[#1f594a] sm:inline" href="/register">Đăng ký đối tác</Link>
              <Link className="rounded-full border border-[#d6ded7] px-4 py-2 text-sm font-semibold text-[#214d40] hover:bg-white" href="/login">Đăng nhập</Link>
            </>
          )}
        </div>
      </nav>
    </header>
  );
}
