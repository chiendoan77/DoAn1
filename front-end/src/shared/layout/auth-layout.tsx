import Link from "next/link";
import { ReactNode } from "react";
import { CheckCircle2, MapPin, ShieldCheck } from "lucide-react";

type Props = {
  title: string;
  eyebrow: string;
  description: string;
  children: ReactNode;
  compact?: boolean;
};

export function AuthLayout({ title, eyebrow, description, children, compact = false }: Props) {
  return (
    <main className="grid min-h-screen grid-cols-1 bg-surface lg:grid-cols-2">
      <section className="relative flex min-h-screen flex-col bg-surface px-6 py-5 sm:px-10 lg:px-12 xl:px-16">
        <header className="flex h-12 items-center justify-between">
          <Link href="/" aria-label="Nhà Tốt - Trang chủ" className="flex items-center gap-2 text-lg font-bold text-brand-700">
            <span className="grid h-9 w-9 place-items-center rounded-md bg-brand-600 text-lg text-white">n</span>
            nhà tốt
          </Link>
          <Link href="/" className="min-h-11 items-center text-sm font-semibold text-muted hover:text-brand-700 lg:inline-flex">
            Về trang chủ
          </Link>
        </header>

        <div className={`mx-auto flex w-full max-w-[480px] flex-1 flex-col justify-center ${compact ? "py-2" : "py-6"}`}>
          <p className="text-sm font-bold uppercase tracking-[0.16em] text-brand-600">{eyebrow}</p>
          <h1 className="mt-2 text-[32px] font-bold leading-tight text-text">{title}</h1>
          <p className="mt-2 text-base leading-7 text-muted">{description}</p>
          <div className={compact ? "mt-5" : "mt-6"}>{children}</div>
        </div>
      </section>

      <aside className="relative hidden min-h-screen overflow-hidden bg-brand-700 text-white lg:flex">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_80%_15%,rgba(255,255,255,0.10),transparent_28%),linear-gradient(145deg,#1f594a_0%,#184a3d_100%)]" />
        <div className="relative z-10 flex min-h-screen w-full flex-col justify-between px-12 py-10 xl:px-16 xl:py-12">
          <div className="mt-auto mb-auto">
            <p className="text-sm font-bold uppercase tracking-[0.16em] text-brand-100">Nhà Tốt</p>
            <h2 className="mt-4 max-w-xl text-4xl font-bold leading-[1.15] xl:text-5xl">
              Chăm sóc ngôi nhà, bắt đầu từ một kết nối tốt.
            </h2>
            <p className="mt-5 max-w-xl text-base leading-7 text-brand-100">
              Hồ sơ nhà cung cấp được xét duyệt. Lịch sử đặt dịch vụ chỉ hiển thị với tài khoản có quyền.
            </p>

            <div className="mt-8 grid max-w-xl gap-3">
              <div className="flex items-start gap-3 rounded-md border border-white/15 bg-white/10 px-4 py-3">
                <CheckCircle2 className="mt-0.5 h-5 w-5 shrink-0 text-brand-100" />
                <div>
                  <p className="text-sm font-bold">Nhà cung cấp được duyệt</p>
                  <p className="mt-1 text-sm leading-6 text-brand-100">Chỉ hồ sơ hợp lệ xuất hiện công khai.</p>
                </div>
              </div>
              <div className="flex items-start gap-3 rounded-md border border-white/15 bg-white/10 px-4 py-3">
                <ShieldCheck className="mt-0.5 h-5 w-5 shrink-0 text-brand-100" />
                <div>
                  <p className="text-sm font-bold">Thông tin được bảo vệ</p>
                  <p className="mt-1 text-sm leading-6 text-brand-100">Phiên đăng nhập được quản lý bằng cookie bảo mật.</p>
                </div>
              </div>
              <div className="flex items-start gap-3 rounded-md border border-white/15 bg-white/10 px-4 py-3">
                <MapPin className="mt-0.5 h-5 w-5 shrink-0 text-brand-100" />
                <div>
                  <p className="text-sm font-bold">Dịch vụ gần khu vực bạn</p>
                  <p className="mt-1 text-sm leading-6 text-brand-100">Chọn gói, thời gian và địa chỉ phù hợp.</p>
                </div>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-3 gap-3 border-t border-white/15 pt-5 text-sm text-brand-100">
            <span>Hồ sơ xét duyệt</span>
            <span>Đặt lịch trực tuyến</span>
            <span>Hỗ trợ trong nước</span>
          </div>
        </div>
      </aside>
    </main>
  );
}
