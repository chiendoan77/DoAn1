"use client";

/* eslint-disable @next/next/no-img-element */
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { Bell, ChevronDown, LogOut, Menu, UserRound, X } from "lucide-react";
import { useSession } from "@/shared/state/session";
import { ConfirmDialog } from "@/shared/ui/confirm-dialog";

export function SiteHeader() {
  const router = useRouter();
  const { account, customerProfile, providerProfile, signOut } = useSession();
  const [menuOpen, setMenuOpen] = useState(false);
  const [accountOpen, setAccountOpen] = useState(false);
  const [logoutOpen, setLogoutOpen] = useState(false);
  const [signingOut, setSigningOut] = useState(false);

  async function handleSignOut() {
    setSigningOut(true);
    await signOut();
    setSigningOut(false);
    setLogoutOpen(false);
    setAccountOpen(false);
    router.push("/");
  }

  const navLinks = account?.role === "PROVIDER"
    ? [
        { href: "/history", label: "Lịch hẹn" },
        { href: "/provider/packages", label: "Gói dịch vụ" },
        { href: "/provider/profile", label: "Hồ sơ" },
      ]
    : account?.role === "ADMIN"
      ? [
          { href: "/admin/providers", label: "Duyệt hồ sơ" },
          { href: "/admin/packages", label: "Duyệt gói" },
          { href: "/admin/accounts", label: "Tài khoản" },
        ]
      : [
          { href: "/#services", label: "Dịch vụ" },
          { href: "/history", label: "Lịch hẹn" },
          { href: "/notifications", label: "Thông báo" },
        ];

  const avatarUrl = account?.role === "CUSTOMER"
    ? customerProfile?.avatarUrl
    : account?.role === "PROVIDER"
      ? providerProfile?.imageUrl
      : null;

  return (
    <header className="sticky top-0 z-40 border-b border-border bg-surface/95 backdrop-blur">
      <nav aria-label="Điều hướng chính" className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 md:px-6">
        <Link href="/" className="flex min-h-11 items-center gap-2 font-bold text-brand-700" aria-label="Nhà Tốt - Trang chủ">
          <span className="grid h-9 w-9 place-items-center rounded-md bg-brand-600 text-lg text-white">n</span>
          <span>Nhà Tốt</span>
        </Link>

        <div className="hidden items-center gap-1 md:flex">
          {account && navLinks.map((link) => (
            <Link key={link.href} href={link.href} className="flex min-h-11 items-center rounded-md px-3 text-sm font-semibold text-muted hover:bg-brand-50 hover:text-brand-700">
              {link.label}
            </Link>
          ))}
        </div>

        <div className="flex items-center gap-2">
          {account ? (
            <>
              <Link href="/notifications" aria-label="Thông báo" className="grid h-11 w-11 place-items-center rounded-md text-muted hover:bg-brand-50">
                <Bell className="h-5 w-5" />
              </Link>
              <button
                type="button"
                aria-expanded={accountOpen}
                onClick={() => setAccountOpen((current) => !current)}
                className="flex min-h-11 items-center gap-2 rounded-md border border-border bg-surface px-2 text-sm font-semibold"
              >
                {avatarUrl ? (
                  <img src={avatarUrl} alt="" className="h-8 w-8 rounded-full object-cover" />
                ) : (
                  <span className="grid h-8 w-8 place-items-center rounded-full bg-brand-100 text-brand-700">
                    <UserRound className="h-4 w-4" />
                  </span>
                )}
                <span className="hidden max-w-32 truncate lg:block">{account.fullName}</span>
                <ChevronDown className="h-4 w-4" />
              </button>
            </>
          ) : (
            <div className="hidden items-center gap-2 sm:flex">
              <Link href="/register?role=PROVIDER" className="text-sm font-semibold text-muted">Đăng ký đối tác</Link>
              <Link href="/login" className="inline-flex min-h-11 items-center rounded-md bg-brand-600 px-4 text-sm font-semibold text-white">Đăng nhập</Link>
            </div>
          )}
          <button type="button" aria-label={menuOpen ? "Đóng menu" : "Mở menu"} aria-expanded={menuOpen} onClick={() => setMenuOpen((current) => !current)} className="grid h-11 w-11 place-items-center rounded-md md:hidden">
            {menuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </button>
        </div>
      </nav>

      {menuOpen && (
        <div className="border-t border-border bg-surface px-4 py-3 md:hidden">
          <div className="mx-auto flex max-w-7xl flex-col">
            {account
              ? navLinks.map((link) => <Link key={link.href} href={link.href} onClick={() => setMenuOpen(false)} className="min-h-11 rounded-md px-3 py-3 text-sm font-semibold">{link.label}</Link>)
              : (
                  <>
                    <Link href="/#services" onClick={() => setMenuOpen(false)} className="min-h-11 px-3 py-3 text-sm font-semibold">Dịch vụ</Link>
                    <Link href="/login" onClick={() => setMenuOpen(false)} className="mt-2 rounded-md bg-brand-600 px-4 py-3 text-center text-sm font-semibold text-white">Đăng nhập</Link>
                  </>
                )}
          </div>
        </div>
      )}

      {account && accountOpen && (
        <div className="absolute right-4 top-14 w-72 rounded-lg border border-border bg-surface p-3 shadow-md md:right-6">
          <p className="px-2 py-1 text-sm font-bold">{account.fullName}</p>
          <p className="px-2 pb-3 text-xs text-muted">{account.email}</p>
          <Link href="/history" onClick={() => setAccountOpen(false)} className="flex min-h-11 items-center rounded-md px-3 text-sm hover:bg-brand-50">Lịch hẹn</Link>
          {account.role === "CUSTOMER" && <Link href="/history" onClick={() => setAccountOpen(false)} className="flex min-h-11 items-center rounded-md px-3 text-sm hover:bg-brand-50">Hồ sơ cá nhân</Link>}
          {account.role === "PROVIDER" && <Link href="/history" onClick={() => setAccountOpen(false)} className="flex min-h-11 items-center rounded-md px-3 text-sm hover:bg-brand-50">Hồ sơ nhà cung cấp</Link>}
          <button type="button" onClick={() => setLogoutOpen(true)} className="flex min-h-11 w-full items-center gap-2 rounded-md px-3 text-sm font-semibold text-danger hover:bg-danger-bg">
            <LogOut className="h-4 w-4" /> Đăng xuất
          </button>
        </div>
      )}

      <ConfirmDialog
        open={logoutOpen}
        title="Đăng xuất khỏi Nhà Tốt?"
        description="Phiên đăng nhập hiện tại sẽ kết thúc trên thiết bị này."
        confirmLabel="Đăng xuất"
        loading={signingOut}
        onCancel={() => setLogoutOpen(false)}
        onConfirm={() => void handleSignOut()}
      />
    </header>
  );
}
