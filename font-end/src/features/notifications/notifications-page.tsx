"use client";


import styles from "./notifications-page.module.css";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Account, Notification, apiRequest } from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";

export function NotificationsPage() {
  const router = useRouter();
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    async function load() {
      try {
        await apiRequest<Account>("me");
        const items = await apiRequest<Notification[]>("notifications/mine");
        if (active) setNotifications(items);
      } catch (cause) {
        if (cause instanceof Error && cause.message.includes("đăng nhập")) {
          router.replace("/login?next=/notifications");
          return;
        }
        if (active) setError(cause instanceof Error ? cause.message : "Không tải được thông báo.");
      } finally {
        if (active) setLoading(false);
      }
    }
    void load();
    return () => { active = false; };
  }, [router]);

  return (
    <main className={styles.style1}>
      <SiteHeader />
      <section className={styles.style2}>
        <p className={styles.style3}>Tài khoản</p>
        <h1 className={styles.style4}>Thông báo</h1>
        {error && <p role="alert" className={styles.style5}>{error}</p>}
        {loading ? <p className={styles.style6}>Đang tải…</p> : notifications.length === 0 ? (
          <p className={styles.style7}>Bạn chưa có thông báo nào.</p>
        ) : (
          <ol className={styles.style8}>
            {notifications.map((notification) => (
              <li key={notification.id} className={styles.style9}>
                <p className={styles.style10}>{notification.message}</p>
                <p className={styles.style11}>
                  {new Date(notification.createdAt).toLocaleString("vi-VN")}
                  {notification.relatedType === "BOOKING" && notification.relatedId
                    ? ` · Lịch hẹn ${notification.relatedId.slice(0, 8)}`
                    : notification.packageId
                      ? ` · Gói ${notification.packageId.slice(0, 8)}`
                      : ""}
                </p>
              </li>
            ))}
          </ol>
        )}
      </section>
    </main>
  );
}
