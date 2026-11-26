import Link from "next/link";
import { Headphones, Mail, MapPin } from "lucide-react";

export function Footer() {
  return (
    <footer className="border-t border-border bg-surface">
      <div className="mx-auto grid max-w-[1280px] grid-cols-4 gap-10 px-6 py-12">
        <div>
          <Link href="/" className="flex items-center gap-2 text-lg font-bold text-brand-700">
            <span className="grid h-9 w-9 place-items-center rounded-md bg-brand-600 text-lg text-white">n</span>
            nhà tốt
          </Link>
          <p className="mt-4 max-w-xs text-sm leading-6 text-muted">
            Kết nối khách hàng với nhà cung cấp dịch vụ dọn dẹp và sửa chữa đáng tin cậy.
          </p>
        </div>
        <div>
          <h2 className="text-sm font-bold text-text">Về Nhà Tốt</h2>
          <div className="mt-4 grid gap-3 text-sm text-muted">
            <Link href="/#services" className="hover:text-brand-700">Giới thiệu dịch vụ</Link>
            <Link href="/register?role=PROVIDER" className="hover:text-brand-700">Trở thành đối tác</Link>
            <Link href="/notifications" className="hover:text-brand-700">Cập nhật mới</Link>
          </div>
        </div>
        <div>
          <h2 className="text-sm font-bold text-text">Dịch vụ</h2>
          <div className="mt-4 grid gap-3 text-sm text-muted">
            <Link href="/services/cleaning" className="hover:text-brand-700">Dọn dẹp nhà</Link>
            <Link href="/services/repair" className="hover:text-brand-700">Sửa chữa gia đình</Link>
            <Link href="/history" className="hover:text-brand-700">Lịch hẹn của tôi</Link>
          </div>
        </div>
        <div>
          <h2 className="text-sm font-bold text-text">Hỗ trợ</h2>
          <div className="mt-4 grid gap-3 text-sm text-muted">
            <span className="flex items-center gap-2"><Headphones className="h-4 w-4" /> Hỗ trợ 24/7</span>
            <span className="flex items-center gap-2"><Mail className="h-4 w-4" /> support@nhatot.vn</span>
            <span className="flex items-center gap-2"><MapPin className="h-4 w-4" /> Hoạt động tại Việt Nam</span>
          </div>
        </div>
      </div>
      <div className="border-t border-border">
        <div className="mx-auto flex max-w-[1280px] items-center justify-between px-6 py-5 text-xs text-muted">
          <p>© 2026 Nhà Tốt.</p>
          <div className="flex gap-5">
            <Link href="/notifications" className="hover:text-brand-700">Điều khoản</Link>
            <Link href="/notifications" className="hover:text-brand-700">Bảo mật</Link>
          </div>
        </div>
      </div>
    </footer>
  );
}
