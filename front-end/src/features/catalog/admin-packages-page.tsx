"use client";


import styles from "./admin-packages-page.module.css";
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
    <main className={styles.style1}>
      <SiteHeader />
      <section className={styles.style2}>
        <p className={styles.style3}>Catalog · Admin</p>
        <h1 className={styles.style4}>Kiểm duyệt gói dịch vụ</h1>
        <p className={styles.style5}>Chỉ gói được duyệt mới hiển thị công khai cho khách hàng.</p>
        <div className={styles.style6}>
          <button onClick={() => setAllPackages(false)} aria-pressed={!allPackages} className={`${styles.filterButton} ${!allPackages ? styles.filterButtonActive : styles.filterButtonInactive}`}>Chờ duyệt</button>
          <button onClick={() => setAllPackages(true)} aria-pressed={allPackages} className={`${styles.filterButton} ${allPackages ? styles.filterButtonActive : styles.filterButtonInactive}`}>Tất cả</button>
        </div>
        {error && <p role="alert" className={styles.style7}>{error}</p>}
        {loading ? <p className={styles.style8}>Đang tải…</p> : packages.length === 0 ? (
          <p className={styles.style9}>Không có gói dịch vụ cần hiển thị.</p>
        ) : (
          <div className={styles.style10}>
            {packages.map((servicePackage) => (
              <article key={servicePackage.id} className={styles.style11}>
                <div className={styles.style12}>
                  <div>
                    <p className={styles.style13}>{servicePackage.categoryId} · Provider {servicePackage.providerId.slice(0, 8)}</p>
                    <h2 className={styles.style14}>{servicePackage.name}</h2>
                  </div>
                  <span className={styles.style15}>{statusLabels[servicePackage.status]}</span>
                </div>
                <p className={styles.style16}>{servicePackage.description}</p>
                <p className={styles.style17}>{new Intl.NumberFormat("vi-VN").format(servicePackage.price)} ₫ · {servicePackage.durationMinutes} phút · {servicePackage.providerPhone}</p>
                <div className={styles.style18}>
                  {servicePackage.status === "PENDING_APPROVAL" && (
                    <>
                      <button onClick={() => void approve(servicePackage)} className={styles.style19}>Duyệt</button>
                      <input aria-label={`Lý do từ chối ${servicePackage.name}`} maxLength={500} placeholder="Lý do từ chối" value={reasons[servicePackage.id] ?? ""} onChange={(event) => setReasons((current) => ({ ...current, [servicePackage.id]: event.target.value }))} className={styles.style20} />
                      <button onClick={() => void reject(servicePackage)} className={styles.style21}>Từ chối</button>
                    </>
                  )}
                  {servicePackage.status === "APPROVED" && <button onClick={() => void deactivate(servicePackage)} className={styles.style21}>Ngừng hoạt động</button>}
                  <button onClick={() => void showHistory(servicePackage)} className={styles.style22}>Lịch sử duyệt</button>
                </div>
                {history[servicePackage.id] && (
                  <ol className={styles.style23}>
                    {history[servicePackage.id].map((item, index) => (
                      <li key={item.id ?? `${item.createdAt}-${index}`} className={styles.style24}>
                        <span className={styles.style25}>{item.action}</span> · {item.createdAt} · {item.adminId ?? item.actorId}{item.reason ? ` · ${item.reason}` : ""}
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
