"use client";

/* eslint-disable @next/next/no-img-element */
import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Account, Provider, apiRequest } from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";

export function AdminProvidersPage() {
  const router = useRouter();
  const [providers, setProviders] = useState<Provider[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadPending = useCallback(async () => {
    setError("");
    try {
      const account = await apiRequest<Account>("me");
      if (account.role !== "ADMIN") {
        router.replace("/login?next=/admin/providers");
        return;
      }
      setProviders(await apiRequest<Provider[]>("admin/providers/pending"));
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không tải được hồ sơ chờ duyệt.");
    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => {
    void Promise.resolve().then(loadPending);
  }, [loadPending]);

  async function decide(provider: Provider, approved: boolean) {
    setError("");
    try {
      await apiRequest<Provider>(`admin/providers/${provider.id}/approval`, {
        method: "PATCH",
        body: JSON.stringify({ approved }),
      });
      setProviders((current) => current.filter((item) => item.id !== provider.id));
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không cập nhật được hồ sơ.");
    }
  }

  return (
    <main className="min-h-screen bg-[#f7f8f4]">
      <SiteHeader />
      <section className="mx-auto max-w-5xl px-5 py-10 sm:px-8 sm:py-14">
        <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#719071]">Marketplace</p>
        <h1 className="mt-2 text-3xl font-semibold text-[#173c33]">Duyệt hồ sơ nhà cung cấp</h1>
        <p className="mt-2 text-sm text-[#6c7971]">Chỉ hồ sơ được duyệt mới xuất hiện trong danh sách dịch vụ công khai.</p>
        {error && <p role="alert" className="mt-6 rounded-2xl bg-[#fff1ec] p-4 text-sm text-[#9b3d28]">{error}</p>}
        {loading ? (
          <p className="mt-8 text-sm text-[#6c7971]">Đang tải hồ sơ…</p>
        ) : providers.length === 0 ? (
          <p className="mt-8 rounded-3xl border border-dashed border-[#cfdacf] bg-white p-8 text-center text-sm text-[#6c7971]">Không có hồ sơ nào đang chờ duyệt.</p>
        ) : (
          <div className="mt-8 space-y-4">
            {providers.map((provider) => (
              <article key={provider.id} className="flex flex-col gap-5 rounded-3xl border border-[#e5eae3] bg-white p-5 sm:flex-row">
                <img className="h-48 w-full rounded-2xl object-cover sm:w-56" src={provider.imageUrl} alt={`Hình ảnh ${provider.fullName}`} />
                <div className="flex-1">
                  <p className="text-xs font-semibold uppercase tracking-wide text-[#719071]">{provider.categories.map((category) => category.name).join(" · ")}</p>
                  <h2 className="mt-1 text-xl font-semibold text-[#23483a]">{provider.fullName}</h2>
                  <p className="mt-2 text-sm text-[#6c7971]">{provider.address}</p>
                  <p className="mt-3 text-sm leading-6 text-[#6c7971]">{provider.description}</p>
                  <div className="mt-5 flex gap-2">
                    <button className="rounded-full bg-[#1f594a] px-5 py-2.5 text-sm font-semibold text-white" onClick={() => void decide(provider, true)}>Duyệt hồ sơ</button>
                    <button className="rounded-full border border-[#dce5dc] px-5 py-2.5 text-sm font-semibold text-[#52645e]" onClick={() => void decide(provider, false)}>Từ chối</button>
                  </div>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}
