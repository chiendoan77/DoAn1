"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Bell, CalendarCheck, Package } from "lucide-react";
import { Account, Notification, apiRequest } from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";
import { EmptyState, Skeleton } from "@/shared/ui/skeleton";

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
    <main id="main-content" className="min-h-screen bg-bg">
      <SiteHeader />
      <section className="mx-auto max-w-4xl px-4 py-10 md:px-6 md:py-14">
        <p className="text-sm font-bold text-brand-600">Tài khoản</p>
        <h1 className="mt-2 text-3xl font-bold text-text">Thông báo</h1>
        {error && <p role="alert" className="mt-6 rounded-md border border-danger/20 bg-danger-bg p-4 text-sm text-danger">{error}</p>}
        {loading ? (
          <div className="mt-8 space-y-3" aria-label="Đang tải thông báo">
            <Skeleton className="h-24" /><Skeleton className="h-24" /><Skeleton className="h-24" />
          </div>
        ) : notifications.length === 0 ? (
          <div className="mt-8">
            <EmptyState
              icon={<Bell className="h-8 w-8" />}
              title="Bạn chưa có thông báo"
              description="Thông báo về gói dịch vụ và lịch hẹn sẽ xuất hiện tại đây."
              action={<Link href="/#services" className="inline-flex min-h-11 items-center rounded-md bg-brand-600 px-4 text-sm font-semibold text-white">Khám phá dịch vụ</Link>}
            />
          </div>
        ) : (
          <ol className="mt-8 space-y-3">
            {notifications.map((notification) => {
              const isBooking = notification.relatedType === "BOOKING";
              const href = isBooking ? "/history" : notification.packageId ? "/provider/packages" : "/notifications";
              return (
                <li key={notification.id}>
                  <Link href={href} className="flex gap-4 rounded-lg border border-border bg-surface p-4 hover:border-brand-500">
                    <span className="grid h-10 w-10 shrink-0 place-items-center rounded-md bg-brand-50 text-brand-700">
                      {isBooking ? <CalendarCheck className="h-5 w-5" /> : notification.packageId ? <Package className="h-5 w-5" /> : <Bell className="h-5 w-5" />}
                    </span>
                    <div>
                      <p className="text-sm font-semibold leading-6 text-text">{notification.message}</p>
                      <p className="mt-1 text-xs text-muted">{new Date(notification.createdAt).toLocaleString("vi-VN")}</p>
                    </div>
                  </Link>
                </li>
              );
            })}
          </ol>
        )}
      </section>
    </main>
  );
}
