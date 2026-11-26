"use client";

/* eslint-disable @next/next/no-img-element */
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { Bell, ChevronDown, LogOut, Menu, Search, UserRound, Wrench, Sparkles, CalendarCheck, Headphones, X } from "lucide-react";
import { useSession } from "@/shared/state/session";
import { ConfirmDialog } from "@/shared/ui/confirm-dialog";

export function SiteHeader() {
  const router = useRouter();
  const { account, customerProfile, providerProfile, signOut } = useSession();
  const [menuOpen, setMenuOpen] = useState(false);
  const [accountOpen, setAccountOpen] = useState(false);
  const [logoutOpen, setLogoutOpen] = useState(false);
  const [signingOut, setSigningOut] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");

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

  function submitSearch(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const query = searchQuery.trim();
    router.push(query ? `/services/cleaning?q=${encodeURIComponent(query)}` : "/#services");
  }

  return (
    <header className="sticky top-0 z-40 border-b border-border bg-surface shadow-sm">
      <nav aria-label="Điều hướng chính" className="mx-auto grid h-[72px] max-w-[1280px] grid-cols-[170px_minmax(280px,1fr)_auto] items-center gap-6 px-6">
        <Link href="/" className="flex min-h-11 items-center gap-2 text-xl font-bold text-brand-700" aria-label="Nhà Tốt - Trang chủ">
          <span className="grid h-10 w-10 place-items-center rounded-md bg-brand-600 text-xl text-white">n</span>
          nhà tốt
        </Link>

        <form onSubmit={submitSearch} className="flex h-11 items-center rounded-md border border-brand-100 bg-bg focus-within:border-brand-500 focus-within:ring-2 focus-within:ring-brand-500/20">
          <Search className="ml-3.5 h-5 w-5 text-muted" aria-hidden="true" />
          <input
            aria-label="Tìm dịch vụ hoặc nhà cung cấp"
            value={searchQuery}
            onChange={(event) => setSearchQuery(event.target.value)}
            placeholder="Tìm dịch vụ, nhà cung cấp..."
            className="h-full min-w-0 flex-1 bg-transparent px-3 text-sm text-text outline-none placeholder:text-muted"
          />
          <button type="submit" className="mr-1.5 h-8 rounded-md bg-brand-600 px-4 text-sm font-bold text-white hover:bg-brand-700">
            Tìm kiếm
          </button>
        </form>

        <div className="flex items-center justify-end gap-2">
          {account && (
            <Link href="/history" className="hidden min-h-11 items-center gap-2 rounded-md px-2 text-sm font-semibold text-muted hover:bg-brand-50 xl:flex">
              <CalendarCheck className="h-5 w-5" /> Đơn đặt lịch
            </Link>
          )}
          <a href="mailto:support@nhatot.vn" className="hidden min-h-11 items-center gap-2 rounded-md px-2 text-sm font-semibold text-muted hover:bg-brand-50 xl:flex">
            <Headphones className="h-5 w-5" /> Hỗ trợ
          </a>
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
      <div className="hidden border-t border-border/70 bg-surface md:block">
        <nav aria-label="Danh mục dịch vụ" className="mx-auto flex h-11 max-w-[1280px] items-center gap-2 px-6">
          <Link href="/services/cleaning" className="flex min-h-11 items-center gap-2 px-3 text-sm font-semibold text-text hover:bg-brand-50">
            <Sparkles className="h-4 w-4 text-brand-600" /> Dọn dẹp nhà
          </Link>
          <Link href="/services/repair" className="flex min-h-11 items-center gap-2 px-3 text-sm font-semibold text-text hover:bg-brand-50">
            <Wrench className="h-4 w-4 text-brand-600" /> Sửa chữa gia đình
          </Link>
          {account?.role === "PROVIDER" && (
            <Link href="/provider/packages" className="ml-auto flex min-h-11 items-center px-3 text-sm font-semibold text-brand-700">Quản lý gói dịch vụ</Link>
          )}
          {account?.role === "ADMIN" && (
            <>
              <Link href="/admin/providers" className="ml-auto flex min-h-11 items-center px-3 text-sm font-semibold text-brand-700">Duyệt hồ sơ</Link>
              <Link href="/admin/packages" className="flex min-h-11 items-center px-3 text-sm font-semibold text-brand-700">Duyệt gói</Link>
            </>
          )}
        </nav>
      </div>

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
