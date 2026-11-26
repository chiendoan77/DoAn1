"use client";


import styles from "./providers-page.module.css";
import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Account, Provider, apiRequest } from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";
import { ConfirmDialog } from "@/shared/ui/confirm-dialog";
import { SafeImage } from "@/shared/ui/safe-image";
import { useToast } from "@/shared/ui/toast";

export function AdminProvidersPage() {
  const router = useRouter();
  const toast = useToast();
  const [providers, setProviders] = useState<Provider[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [approveProvider, setApproveProvider] = useState<Provider | null>(null);
  const [rejectProvider, setRejectProvider] = useState<Provider | null>(null);
  const [actionBusy, setActionBusy] = useState(false);

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

  async function decide(provider: Provider, approved: boolean, reason?: string) {
    setActionBusy(true);
    setError("");
    try {
      await apiRequest<Provider>(`admin/providers/${provider.id}/approval`, {
        method: "PATCH",
        body: JSON.stringify({ approved, reason: reason?.trim() || undefined }),
      });
      toast.success(approved ? "Đã duyệt hồ sơ nhà cung cấp." : "Đã từ chối hồ sơ nhà cung cấp.");
      setApproveProvider(null);
      setRejectProvider(null);
      setProviders((current) => current.filter((item) => item.id !== provider.id));
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không cập nhật được hồ sơ.");
    } finally {
      setActionBusy(false);
    }
  }

  return (
    <main className={styles.style1}>
      <SiteHeader />
      <section className={styles.style2}>
        <p className={styles.style3}>Marketplace</p>
        <h1 className={styles.style4}>Duyệt hồ sơ nhà cung cấp</h1>
        <p className={styles.style5}>Chỉ hồ sơ được duyệt mới xuất hiện trong danh sách dịch vụ công khai.</p>
        {error && <p role="alert" className={styles.style6}>{error}</p>}
        {loading ? (
          <p className={styles.style7}>Đang tải hồ sơ…</p>
        ) : providers.length === 0 ? (
          <p className={styles.style8}>Không có hồ sơ nào đang chờ duyệt.</p>
        ) : (
          <div className={styles.style9}>
            {providers.map((provider) => (
              <article key={provider.id} className={styles.style10}>
                <SafeImage className={styles.style11} src={provider.imageUrl} alt={provider.fullName} />
                <div className={styles.style12}>
                  <p className={styles.style13}>{provider.categories.map((category) => category.name).join(" · ")}</p>
                  <h2 className={styles.style14}>{provider.fullName}</h2>
                  <p className={styles.style5}>{provider.address}</p>
                  <p className={styles.style15}>{provider.description}</p>
                  <div className={styles.style16}>
                    <button className={styles.style17} onClick={() => setApproveProvider(provider)}>Duyệt hồ sơ</button>
                    <button className={styles.style18} onClick={() => setRejectProvider(provider)}>Từ chối</button>
                  </div>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
      <ConfirmDialog
        open={approveProvider !== null}
        title={approveProvider ? `Duyệt hồ sơ "${approveProvider.fullName}"?` : "Duyệt hồ sơ?"}
        description="Hồ sơ sẽ xuất hiện công khai trong danh mục nhà cung cấp."
        confirmLabel="Duyệt hồ sơ"
        loading={actionBusy}
        onCancel={() => setApproveProvider(null)}
        onConfirm={() => approveProvider ? void decide(approveProvider, true) : undefined}
      />
      <ConfirmDialog
        open={rejectProvider !== null}
        title={rejectProvider ? `Từ chối hồ sơ "${rejectProvider.fullName}"?` : "Từ chối hồ sơ?"}
        description="Nhà cung cấp sẽ nhận thông báo và không xuất hiện trong danh sách công khai."
        confirmLabel="Từ chối hồ sơ"
        requireReason
        loading={actionBusy}
        onCancel={() => setRejectProvider(null)}
        onConfirm={(reason) => rejectProvider ? void decide(rejectProvider, false, reason) : undefined}
      />
    </main>
  );
}
