"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
import { ArrowLeft, BadgeCheck, MapPin, Star } from "lucide-react";
import { PageResult, Provider, ServicePackage, apiRequest } from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";
import { Footer } from "@/shared/layout/footer";
import { EmptyState, Skeleton } from "@/shared/ui/skeleton";
import { SafeImage } from "@/shared/ui/safe-image";

export function ProviderDetailPage({ providerId }: { providerId: string }) {
  const [provider, setProvider] = useState<Provider | null>(null);
  const [packages, setPackages] = useState<ServicePackage[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    async function load() {
      try {
        const result = await apiRequest<Provider>(`services/providers/${providerId}`);
        if (!active) return;
        setProvider(result);
        const packagePages = await Promise.all(
          result.categoryIds.map((categoryId) =>
            apiRequest<PageResult<ServicePackage>>(
              `catalog/packages?categoryId=${encodeURIComponent(categoryId)}&page=0&size=100&sort=createdAt&direction=DESC`,
            ).catch(() => null),
          ),
        );
        if (active) {
          const unique = new Map<string, ServicePackage>();
          packagePages.forEach((page) => page?.content.forEach((item) => {
            if (item.providerId === providerId) unique.set(item.id, item);
          }));
          setPackages([...unique.values()]);
        }
      } catch (cause) {
        if (active) setError(cause instanceof Error ? cause.message : "Không tải được nhà cung cấp.");
      } finally {
        if (active) setLoading(false);
      }
    }
    void load();
    return () => { active = false; };
  }, [providerId]);

  return (
    <main id="main-content" className="min-h-screen bg-bg">
      <SiteHeader />
      <section className="border-b border-border bg-surface">
        <div className="mx-auto max-w-[1280px] px-6 py-8">
          <Link href="/services/cleaning" className="inline-flex min-h-11 items-center gap-2 text-sm font-semibold text-muted hover:text-brand-700">
            <ArrowLeft className="h-4 w-4" /> Quay lại danh sách
          </Link>
          {loading ? (
            <div className="mt-6 grid grid-cols-[280px_1fr] gap-8">
              <Skeleton className="aspect-[4/3]" />
              <div className="space-y-3"><Skeleton className="h-10 w-72" /><Skeleton className="h-24 w-full" /></div>
            </div>
          ) : error ? (
            <p role="alert" className="mt-6 rounded-md border border-danger/20 bg-danger-bg p-4 text-sm text-danger">{error}</p>
          ) : provider ? (
            <div className="mt-6 grid grid-cols-[280px_1fr] gap-8">
              <SafeImage
                src={provider.imageUrl}
                alt={provider.fullName}
                className="aspect-[4/3] rounded-lg border border-border"
                placeholderIcon={<BadgeCheck className="h-12 w-12" />}
              />
              <div>
                <div className="flex items-start justify-between gap-6">
                  <div>
                    <p className="flex items-center gap-2 text-sm font-bold text-brand-600">
                      <BadgeCheck className="h-4 w-4" /> Hồ sơ đã duyệt
                    </p>
                    <h1 className="mt-2 text-4xl font-bold text-text">{provider.fullName}</h1>
                  </div>
                  <span className="flex items-center gap-1 rounded-full bg-warning-bg px-3 py-1.5 text-sm font-bold text-warning">
                    <Star className="h-4 w-4 fill-current" /> {provider.rating.toFixed(1)} · {provider.reviewCount} đánh giá
                  </span>
                </div>
                <p className="mt-4 flex items-start gap-2 text-sm leading-6 text-muted">
                  <MapPin className="mt-1 h-4 w-4 shrink-0" /> {provider.address}
                </p>
                <p className="mt-4 max-w-3xl text-sm leading-7 text-muted">{provider.description}</p>
                <div className="mt-5 flex flex-wrap gap-2">
                  {provider.categories.map((category) => (
                    <span key={category.id} className="rounded-full border border-border bg-bg px-3 py-1.5 text-xs font-semibold text-muted">
                      {category.name}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          ) : null}
        </div>
      </section>

      <section className="mx-auto max-w-[1280px] px-6 py-10">
        <div className="grid grid-cols-[1fr_280px] gap-6">
          <div>
            <p className="text-sm font-bold text-brand-600">Gói dịch vụ</p>
            <h2 className="mt-1 text-2xl font-bold text-text">Chọn gói phù hợp</h2>
            {loading ? (
              <div className="mt-5 grid grid-cols-2 gap-4"><Skeleton className="h-56" /><Skeleton className="h-56" /></div>
            ) : packages.length === 0 ? (
              <div className="mt-5"><EmptyState title="Chưa có gói dịch vụ" description="Nhà cung cấp chưa công khai gói dịch vụ." /></div>
            ) : (
              <div className="mt-5 grid grid-cols-2 gap-4">
                {packages.map((item) => (
                  <article key={item.id} className="rounded-lg border border-border bg-surface p-5">
                    <p className="text-xs font-bold text-brand-600">{item.categoryId === "repair" ? "Sửa chữa gia đình" : "Dọn dẹp nhà"}</p>
                    <h3 className="mt-2 text-lg font-bold text-text">{item.name}</h3>
                    <p className="mt-2 min-h-12 text-sm leading-6 text-muted">{item.description}</p>
                    <p className="mt-4 text-xl font-bold text-brand-700">{new Intl.NumberFormat("vi-VN").format(item.price)} ₫</p>
                    <p className="mt-1 text-sm text-muted">{item.durationMinutes} phút</p>
                    {provider && (
                      <Link href={provider.categoryIds.includes("repair") ? "/services/repair" : "/services/cleaning"} className="mt-5 flex h-11 items-center justify-center rounded-md bg-brand-600 text-sm font-bold text-white hover:bg-brand-700">
                        Chọn gói này
                      </Link>
                    )}
                  </article>
                ))}
              </div>
            )}
          </div>
          <aside className="h-fit rounded-lg border border-border bg-surface p-5">
            <p className="text-sm font-bold text-text">Cam kết dịch vụ</p>
            <ul className="mt-4 grid gap-3 text-sm leading-6 text-muted">
              <li>Hồ sơ nhà cung cấp đã được duyệt.</li>
              <li>Thông tin lịch hẹn được lưu trong tài khoản.</li>
              <li>Chỉ thanh toán ngoài trang khi bên cung cấp hướng dẫn.</li>
            </ul>
          </aside>
        </div>
      </section>
      <Footer />
    </main>
  );
}
