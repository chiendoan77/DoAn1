"use client";

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
    <main className="min-h-screen bg-[#f7f8f4]">
      <SiteHeader />
      <section className="mx-auto max-w-4xl px-5 py-10 sm:px-8 sm:py-14">
        <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#719071]">Tài khoản</p>
        <h1 className="mt-2 text-3xl font-semibold text-[#173c33]">Thông báo</h1>
        {error && <p role="alert" className="mt-6 rounded-2xl bg-[#fff1ec] p-4 text-sm text-[#9b3d28]">{error}</p>}
        {loading ? <p className="mt-8 text-sm text-[#6c7971]">Đang tải…</p> : notifications.length === 0 ? (
          <p className="mt-8 rounded-3xl border border-dashed border-[#cfdacf] bg-white p-8 text-center text-sm text-[#6c7971]">Bạn chưa có thông báo nào.</p>
        ) : (
          <ol className="mt-8 space-y-3">
            {notifications.map((notification) => (
              <li key={notification.id} className="rounded-2xl border border-[#e5eae3] bg-white p-5">
                <p className="text-sm leading-6 text-[#315246]">{notification.message}</p>
                <p className="mt-2 text-xs text-[#87938c]">
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
