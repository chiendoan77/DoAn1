"use client";


import styles from "./providers-page.module.css";
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
                <img className={styles.style11} src={provider.imageUrl} alt={`Hình ảnh ${provider.fullName}`} />
                <div className={styles.style12}>
                  <p className={styles.style13}>{provider.categories.map((category) => category.name).join(" · ")}</p>
                  <h2 className={styles.style14}>{provider.fullName}</h2>
                  <p className={styles.style5}>{provider.address}</p>
                  <p className={styles.style15}>{provider.description}</p>
                  <div className={styles.style16}>
                    <button className={styles.style17} onClick={() => void decide(provider, true)}>Duyệt hồ sơ</button>
                    <button className={styles.style18} onClick={() => void decide(provider, false)}>Từ chối</button>
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
