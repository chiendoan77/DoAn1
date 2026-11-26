"use client";


import styles from "./admin-packages-page.module.css";
import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Account, PackageApproval, PageResult, ServicePackage, apiRequest } from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";
import { ConfirmDialog } from "@/shared/ui/confirm-dialog";
import { useToast } from "@/shared/ui/toast";

const statusLabels: Record<ServicePackage["status"], string> = {
  DRAFT: "Bản nháp",
  PENDING_APPROVAL: "Đang chờ duyệt",
  APPROVED: "Đã duyệt",
  REJECTED: "Bị từ chối",
  INACTIVE: "Đã ngừng hoạt động",
};

const actionLabels: Record<string, string> = {
  SUBMITTED: "Gửi duyệt",
  APPROVED: "Duyệt gói",
  REJECTED: "Từ chối",
  INACTIVE: "Ngừng hoạt động",
};

function formatDateTime(value: string) {
  return new Intl.DateTimeFormat("vi-VN", { dateStyle: "short", timeStyle: "short" }).format(new Date(value));
}

export function AdminPackagesPage() {
  const router = useRouter();
  const toast = useToast();
  const [packages, setPackages] = useState<ServicePackage[]>([]);
  const [allPackages, setAllPackages] = useState(false);
  const [history, setHistory] = useState<Record<string, PackageApproval[]>>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [approvePackage, setApprovePackage] = useState<ServicePackage | null>(null);
  const [rejectPackage, setRejectPackage] = useState<ServicePackage | null>(null);
  const [deactivatePackage, setDeactivatePackage] = useState<ServicePackage | null>(null);
  const [actionBusy, setActionBusy] = useState(false);

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
    setActionBusy(true);
    setError("");
    try {
      await apiRequest<ServicePackage>(`admin/packages/${servicePackage.id}/approve`, {
        method: "POST",
        body: JSON.stringify({ expectedVersion: servicePackage.version }),
      });
      toast.success("Đã duyệt gói dịch vụ.");
      setApprovePackage(null);
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không duyệt được gói dịch vụ.");
    } finally {
      setActionBusy(false);
    }
  }

  async function reject(servicePackage: ServicePackage, suppliedReason?: string) {
    const reason = suppliedReason?.trim();
    if (!reason) {
      setError("Vui lòng nhập lý do từ chối.");
      return;
    }
    setActionBusy(true);
    setError("");
    try {
      await apiRequest<ServicePackage>(`admin/packages/${servicePackage.id}/reject`, {
        method: "POST",
        headers: { "If-Match": String(servicePackage.version) },
        body: JSON.stringify({ reason }),
      });
      toast.success("Đã từ chối gói dịch vụ.");
      setRejectPackage(null);
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không từ chối được gói dịch vụ.");
    } finally {
      setActionBusy(false);
    }
  }

  async function deactivate(servicePackage: ServicePackage) {
    setActionBusy(true);
    setError("");
    try {
      await apiRequest<ServicePackage>(`admin/packages/${servicePackage.id}/deactivate`, {
        method: "POST",
        body: JSON.stringify({ expectedVersion: servicePackage.version }),
      });
      toast.success("Đã ngừng hoạt động gói.");
      setDeactivatePackage(null);
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không ngừng hoạt động được gói dịch vụ.");
    } finally {
      setActionBusy(false);
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
                    <p className={styles.style13}>Gói dịch vụ đã đăng ký</p>
                    <h2 className={styles.style14}>{servicePackage.name}</h2>
                  </div>
                  <span className={styles.style15}>{statusLabels[servicePackage.status]}</span>
                </div>
                <p className={styles.style16}>{servicePackage.description}</p>
                <p className={styles.style17}>{new Intl.NumberFormat("vi-VN").format(servicePackage.price)} ₫ · {servicePackage.durationMinutes} phút · {servicePackage.providerPhone}</p>
                <div className={styles.style18}>
                  {servicePackage.status === "PENDING_APPROVAL" && (
                    <>
                      <button onClick={() => setApprovePackage(servicePackage)} className={styles.style19}>Duyệt</button>
                      <button onClick={() => setRejectPackage(servicePackage)} className={styles.style21}>Từ chối</button>
                    </>
                  )}
                  {servicePackage.status === "APPROVED" && <button onClick={() => setDeactivatePackage(servicePackage)} className={styles.style21}>Ngừng hoạt động</button>}
                  <button onClick={() => void showHistory(servicePackage)} className={styles.style22}>Lịch sử duyệt</button>
                </div>
                {history[servicePackage.id] && (
                  <ol className={styles.style23}>
                    {history[servicePackage.id].map((item, index) => (
                      <li key={item.id ?? `${item.createdAt}-${index}`} className={styles.style24}>
                        <span className={styles.style25}>{actionLabels[item.action] ?? "Cập nhật"}</span> · {formatDateTime(item.createdAt)}{item.reason ? ` · ${item.reason}` : ""}
                      </li>
                    ))}
                  </ol>
                )}
              </article>
            ))}
          </div>
        )}
      </section>
      <ConfirmDialog
        open={approvePackage !== null}
        title={approvePackage ? `Duyệt gói "${approvePackage.name}"?` : "Duyệt gói?"}
        description="Gói sẽ hiển thị công khai cho khách hàng sau khi được duyệt."
        confirmLabel="Duyệt gói"
        loading={actionBusy}
        onCancel={() => setApprovePackage(null)}
        onConfirm={() => approvePackage ? void approve(approvePackage) : undefined}
      />
      <ConfirmDialog
        open={rejectPackage !== null}
        title={rejectPackage ? `Từ chối gói "${rejectPackage.name}"?` : "Từ chối gói?"}
        description="Nhà cung cấp sẽ nhận thông báo cùng lý do từ chối."
        confirmLabel="Từ chối gói"
        requireReason
        loading={actionBusy}
        onCancel={() => setRejectPackage(null)}
        onConfirm={(reason) => rejectPackage ? void reject(rejectPackage, reason) : undefined}
      />
      <ConfirmDialog
        open={deactivatePackage !== null}
        title={deactivatePackage ? `Ngừng hoạt động "${deactivatePackage.name}"?` : "Ngừng hoạt động gói?"}
        description="Gói sẽ ngừng hiển thị công khai; các lịch hẹn đã tạo không bị ảnh hưởng."
        confirmLabel="Ngừng hoạt động"
        requireText={deactivatePackage?.name}
        loading={actionBusy}
        onCancel={() => setDeactivatePackage(null)}
        onConfirm={() => deactivatePackage ? void deactivate(deactivatePackage) : undefined}
      />
    </main>
  );
}
