"use client";

import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Account, PackageApproval, PageResult, ServicePackage, apiRequest } from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";

const statusLabels: Record<ServicePackage["status"], string> = {
  DRAFT: "Bản nháp",
  PENDING_APPROVAL: "Đang chờ duyệt",
  APPROVED: "Đã duyệt",
  REJECTED: "Bị từ chối",
  INACTIVE: "Đã ngừng hoạt động",
};

export function AdminPackagesPage() {
  const router = useRouter();
  const [packages, setPackages] = useState<ServicePackage[]>([]);
  const [allPackages, setAllPackages] = useState(false);
  const [reasons, setReasons] = useState<Record<string, string>>({});
  const [history, setHistory] = useState<Record<string, PackageApproval[]>>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    setError("");
    try {
      const account = await apiRequest<Account>("me");
      if (account.role !== "ADMIN") {
        router.replace("/login?next=/admin/packages");
        return;
      }
      const path = allPackages
        ? "admin/packages?page=0&size=100&sort=createdAt&direction=DESC"
        : "admin/packages/pending?page=0&size=100";
      const result = await apiRequest<PageResult<ServicePackage>>(path);
      setPackages(result.content);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không tải được gói dịch vụ.");
    } finally {
      setLoading(false);
    }
  }, [allPackages, router]);

  useEffect(() => { void Promise.resolve().then(load); }, [load]);

  async function approve(servicePackage: ServicePackage) {
    setError("");
    try {
      await apiRequest<ServicePackage>(`admin/packages/${servicePackage.id}/approve`, {
        method: "POST",
        body: JSON.stringify({ expectedVersion: servicePackage.version }),
      });
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không duyệt được gói dịch vụ.");
    }
  }

  async function reject(servicePackage: ServicePackage) {
    const reason = reasons[servicePackage.id]?.trim();
    if (!reason) {
      setError("Vui lòng nhập lý do từ chối.");
      return;
    }
    setError("");
    try {
      await apiRequest<ServicePackage>(`admin/packages/${servicePackage.id}/reject`, {
        method: "POST",
        headers: { "If-Match": String(servicePackage.version) },
        body: JSON.stringify({ reason }),
      });
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không từ chối được gói dịch vụ.");
    }
  }

  async function deactivate(servicePackage: ServicePackage) {
    setError("");
    try {
      await apiRequest<ServicePackage>(`admin/packages/${servicePackage.id}/deactivate`, {
        method: "POST",
        body: JSON.stringify({ expectedVersion: servicePackage.version }),
      });
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không ngừng hoạt động được gói dịch vụ.");
    }
  }

  async function showHistory(servicePackage: ServicePackage) {
    setError("");
    try {
      const items = await apiRequest<PackageApproval[]>(`admin/packages/${servicePackage.id}/history`);
      setHistory((current) => ({ ...current, [servicePackage.id]: items }));
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không tải được lịch sử kiểm duyệt.");
    }
  }

  return (
    <main className="min-h-screen bg-[#f7f8f4]">
      <SiteHeader />
      <section className="mx-auto max-w-6xl px-5 py-10 sm:px-8 sm:py-14">
        <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#719071]">Catalog · Admin</p>
        <h1 className="mt-2 text-3xl font-semibold text-[#173c33]">Kiểm duyệt gói dịch vụ</h1>
        <p className="mt-2 text-sm text-[#6c7971]">Chỉ gói được duyệt mới hiển thị công khai cho khách hàng.</p>
        <div className="mt-5 flex gap-2">
          <button onClick={() => setAllPackages(false)} aria-pressed={!allPackages} className={`rounded-full px-4 py-2 text-sm font-semibold ${!allPackages ? "bg-[#1f594a] text-white" : "border border-[#dce5dc] bg-white text-[#315246]"}`}>Chờ duyệt</button>
          <button onClick={() => setAllPackages(true)} aria-pressed={allPackages} className={`rounded-full px-4 py-2 text-sm font-semibold ${allPackages ? "bg-[#1f594a] text-white" : "border border-[#dce5dc] bg-white text-[#315246]"}`}>Tất cả</button>
        </div>
        {error && <p role="alert" className="mt-5 rounded-2xl bg-[#fff1ec] p-4 text-sm text-[#9b3d28]">{error}</p>}
        {loading ? <p className="mt-8 text-sm text-[#6c7971]">Đang tải…</p> : packages.length === 0 ? (
          <p className="mt-8 rounded-3xl border border-dashed border-[#cfdacf] bg-white p-8 text-center text-sm text-[#6c7971]">Không có gói dịch vụ cần hiển thị.</p>
        ) : (
          <div className="mt-8 space-y-4">
            {packages.map((servicePackage) => (
              <article key={servicePackage.id} className="rounded-3xl border border-[#e5eae3] bg-white p-5 sm:p-6">
                <div className="flex flex-col justify-between gap-3 sm:flex-row">
                  <div>
                    <p className="text-xs font-semibold uppercase tracking-wide text-[#719071]">{servicePackage.categoryId} · Provider {servicePackage.providerId.slice(0, 8)}</p>
                    <h2 className="mt-1 text-xl font-semibold text-[#23483a]">{servicePackage.name}</h2>
                  </div>
                  <span className="h-fit rounded-full bg-[#f1f7ef] px-3 py-1.5 text-xs font-semibold text-[#315246]">{statusLabels[servicePackage.status]}</span>
                </div>
                <p className="mt-3 text-sm leading-6 text-[#6c7971]">{servicePackage.description}</p>
                <p className="mt-2 text-sm font-semibold text-[#1f594a]">{new Intl.NumberFormat("vi-VN").format(servicePackage.price)} ₫ · {servicePackage.durationMinutes} phút · {servicePackage.providerPhone}</p>
                <div className="mt-4 flex flex-wrap gap-2">
                  {servicePackage.status === "PENDING_APPROVAL" && (
                    <>
                      <button onClick={() => void approve(servicePackage)} className="rounded-full bg-[#1f594a] px-4 py-2 text-sm font-semibold text-white">Duyệt</button>
                      <input aria-label={`Lý do từ chối ${servicePackage.name}`} maxLength={500} placeholder="Lý do từ chối" value={reasons[servicePackage.id] ?? ""} onChange={(event) => setReasons((current) => ({ ...current, [servicePackage.id]: event.target.value }))} className="min-w-52 rounded-full border border-[#dce5dc] px-4 py-2 text-sm" />
                      <button onClick={() => void reject(servicePackage)} className="rounded-full border border-[#ead4cc] px-4 py-2 text-sm font-semibold text-[#9b3d28]">Từ chối</button>
                    </>
                  )}
                  {servicePackage.status === "APPROVED" && <button onClick={() => void deactivate(servicePackage)} className="rounded-full border border-[#ead4cc] px-4 py-2 text-sm font-semibold text-[#9b3d28]">Ngừng hoạt động</button>}
                  <button onClick={() => void showHistory(servicePackage)} className="rounded-full border border-[#dce5dc] px-4 py-2 text-sm font-semibold text-[#315246]">Lịch sử duyệt</button>
                </div>
                {history[servicePackage.id] && (
                  <ol className="mt-4 space-y-2 border-l-2 border-[#dce5dc] pl-4">
                    {history[servicePackage.id].map((item, index) => (
                      <li key={item.id ?? `${item.createdAt}-${index}`} className="text-xs leading-5 text-[#6c7971]">
                        <span className="font-semibold text-[#315246]">{item.action}</span> · {item.createdAt} · {item.adminId ?? item.actorId}{item.reason ? ` · ${item.reason}` : ""}
                      </li>
                    ))}
                  </ol>
                )}
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}
