"use client";

import { SiteHeader } from "@/shared/components/site-header";
import { Footer } from "@/shared/layout/footer";

export function HomePage() {
  return (
    <main id="main-content" className="min-h-screen bg-bg">
      <SiteHeader />

      <section id="services" className="border-b border-border bg-surface">
        <div className="mx-auto flex min-h-[360px] max-w-[1280px] items-center px-6 py-12">
          <div className="max-w-3xl">
            <h1 className="max-w-2xl text-5xl font-bold leading-[1.12] text-text">
              Tìm dịch vụ tốt cho <span className="text-brand-600">ngôi nhà của bạn</span>
            </h1>
            <p className="mt-5 max-w-xl text-base leading-7 text-muted">
              Chọn dịch vụ, xem nhà cung cấp phù hợp và gửi yêu cầu đặt lịch trong vài bước.
            </p>
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-[1280px] px-6 py-12">
        <h2 className="mb-6 text-2xl font-bold text-text">Đặt lịch với Nhà Tốt trong 3 bước</h2>
        <div className="grid grid-cols-3 gap-4">
          {[
            ["01", "Chọn dịch vụ", "Tìm theo danh mục, khu vực và nhu cầu."],
            ["02", "Đặt lịch", "Chọn gói, thời gian và địa chỉ thực hiện."],
            ["03", "Nhận xác nhận", "Theo dõi trạng thái và đánh giá sau hoàn thành."],
          ].map(([number, title, description]) => (
            <div key={number} className="rounded-lg border border-border bg-surface p-6">
              <span className="grid h-9 w-9 place-items-center rounded-full bg-brand-100 text-sm font-bold text-brand-700">{number}</span>
              <h3 className="mt-5 font-bold text-text">{title}</h3>
              <p className="mt-2 text-sm leading-6 text-muted">{description}</p>
            </div>
          ))}
        </div>
      </section>

      <Footer />
    </main>
  );
}
