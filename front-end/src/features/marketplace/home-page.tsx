"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { ArrowRight, CheckCircle2, CalendarCheck, Search, Sparkles, Wrench } from "lucide-react";
import { ServiceCategory, apiRequest } from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";
import { Skeleton } from "@/shared/ui/skeleton";
import { Button } from "@/shared/ui/button";

export function HomePage() {
  const [categories, setCategories] = useState<ServiceCategory[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiRequest<ServiceCategory[]>("services/categories")
      .then(setCategories)
      .finally(() => setLoading(false));
  }, []);

  return (
    <main id="main-content">
      <SiteHeader />

      <section className="border-b border-border bg-[linear-gradient(135deg,#f1f7f4_0%,#ffffff_65%)]">
        <div className="mx-auto grid max-w-7xl gap-10 px-4 py-16 md:grid-cols-[1.15fr_.85fr] md:px-6 md:py-24">
          <div className="max-w-2xl">
            <p className="mb-4 inline-flex items-center gap-2 rounded-full border border-brand-100 bg-surface px-3 py-1.5 text-xs font-bold text-brand-700">
              <CheckCircle2 className="h-4 w-4" /> Hồ sơ nhà cung cấp được xét duyệt
            </p>
            <h1 className="text-4xl font-bold leading-tight text-text md:text-6xl">
              Việc nhà để <span className="text-brand-600">người giỏi</span> lo.
            </h1>
            <p className="mt-5 max-w-xl text-base leading-7 text-muted md:text-lg">
              Tìm dịch vụ dọn dẹp và sửa chữa đáng tin cậy, chọn nhà cung cấp và đặt lịch chỉ trong vài bước.
            </p>
            <div className="mt-8 flex flex-col gap-3 sm:flex-row">
              <Button size="lg" onClick={() => document.getElementById("services")?.scrollIntoView({ behavior: "smooth" })}>
                Tìm dịch vụ <ArrowRight className="h-4 w-4" />
              </Button>
              <Link href="/register?role=PROVIDER" className="inline-flex min-h-12 items-center justify-center rounded-md border border-border bg-surface px-5 text-base font-semibold text-text">
                Trở thành đối tác
              </Link>
            </div>
          </div>

          <div className="grid gap-4 rounded-lg border border-border bg-surface p-5 shadow-sm md:grid-cols-2">
            <div className="rounded-md bg-brand-50 p-5">
              <Sparkles className="h-6 w-6 text-brand-600" />
              <p className="mt-4 text-sm text-muted">Không gian</p>
              <p className="mt-1 font-bold text-text">Sạch và gọn gàng</p>
            </div>
            <div className="rounded-md bg-info-bg p-5">
              <CalendarCheck className="h-6 w-6 text-info" />
              <p className="mt-4 text-sm text-muted">Lịch hẹn</p>
              <p className="mt-1 font-bold text-text">Theo thời gian bạn</p>
            </div>
            <div className="md:col-span-2 rounded-md border border-border p-5">
              <div className="flex items-start gap-3">
                <CheckCircle2 className="mt-0.5 h-5 w-5 text-success" />
                <p className="text-sm leading-6 text-muted">Nhà cung cấp chỉ xuất hiện công khai sau khi hồ sơ được Admin duyệt.</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section id="services" className="mx-auto max-w-7xl px-4 py-16 md:px-6 md:py-20">
        <div>
          <p className="text-sm font-bold text-brand-600">Dịch vụ cho ngôi nhà</p>
          <h2 className="mt-2 text-3xl font-bold text-text">Bạn đang cần giúp việc gì?</h2>
        </div>
        {loading ? (
          <div className="mt-8 grid gap-4 md:grid-cols-2">
            <Skeleton className="h-64" /><Skeleton className="h-64" />
          </div>
        ) : (
          <div className="mt-8 grid gap-4 md:grid-cols-2">
            {categories.map((category) => {
              const repair = category.id === "repair";
              return (
                <Link key={category.id} href={`/services/${category.id}`} className="group rounded-lg border border-border bg-surface p-6 transition hover:border-brand-500 hover:shadow-sm md:p-8">
                  <span className="grid h-12 w-12 place-items-center rounded-md bg-brand-50 text-brand-700">
                    {repair ? <Wrench className="h-6 w-6" /> : <Sparkles className="h-6 w-6" />}
                  </span>
                  <h3 className="mt-6 text-xl font-bold text-text">{category.name}</h3>
                  <p className="mt-2 min-h-12 text-sm leading-6 text-muted">{category.description || "Tìm nhà cung cấp phù hợp và đặt lịch theo thời gian của bạn."}</p>
                  <span className="mt-6 inline-flex items-center gap-2 text-sm font-bold text-brand-700">
                    Khám phá dịch vụ <ArrowRight className="h-4 w-4 transition group-hover:translate-x-1" />
                  </span>
                </Link>
              );
            })}
          </div>
        )}
      </section>

      <section className="border-y border-border bg-surface">
        <div className="mx-auto max-w-7xl px-4 py-16 md:px-6">
          <h2 className="text-center text-2xl font-bold text-text">Đặt dịch vụ trong 3 bước</h2>
          <div className="mt-8 grid gap-4 md:grid-cols-3">
            {[
              [Search, "Chọn gói", "Tìm dịch vụ và nhà cung cấp phù hợp."],
              [CalendarCheck, "Đặt lịch", "Chọn giờ, địa chỉ và mô tả công việc."],
              [CheckCircle2, "Nhận xác nhận", "Nhà cung cấp phản hồi và thực hiện theo lịch."],
            ].map(([Icon, title, description], index) => {
              const IconComponent = Icon as typeof Search;
              return (
                <div key={String(title)} className="rounded-lg border border-border p-6">
                  <div className="flex items-center gap-3">
                    <span className="grid h-9 w-9 place-items-center rounded-full bg-brand-100 text-sm font-bold text-brand-700">{index + 1}</span>
                    <IconComponent className="h-5 w-5 text-brand-600" />
                  </div>
                  <h3 className="mt-5 font-bold text-text">{String(title)}</h3>
                  <p className="mt-2 text-sm leading-6 text-muted">{String(description)}</p>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      <footer className="bg-text text-white">
        <div className="mx-auto flex max-w-7xl flex-col gap-4 px-4 py-8 text-sm md:flex-row md:items-center md:justify-between md:px-6">
          <p>© 2026 Nhà Tốt. Kết nối dịch vụ, chăm sóc tổ ấm.</p>
          <Link href="/admin/providers" className="text-brand-100">Dành cho quản trị viên</Link>
        </div>
      </footer>
    </main>
  );
}
