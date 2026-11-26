"use client";


import styles from "./history-page.module.css";
import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Image as ImageIcon, MapPin, Sparkles } from "lucide-react";
import { Account, Booking, CustomerProfile, Provider, Review, ServiceCategory, apiRequest } from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";
import { ConfirmDialog } from "@/shared/ui/confirm-dialog";
import { FormField } from "@/shared/ui/form-field";
import { Input } from "@/shared/ui/input";
import { useToast } from "@/shared/ui/toast";

const statusLabels: Record<Booking["status"], string> = {
  REQUESTED: "Chờ nhà cung cấp xác nhận",
  ACCEPTED: "Đã xác nhận",
  REJECTED: "Không thể nhận lịch",
  COMPLETED: "Đã hoàn thành",
};
const serviceCategories: ServiceCategory[] = [
  { id: "cleaning", name: "Dọn dẹp nhà", description: "" },
  { id: "repair", name: "Sửa chữa gia đình", description: "" },
];

function formatDate(value: string) {
  return new Intl.DateTimeFormat("vi-VN", { dateStyle: "medium", timeStyle: "short" }).format(new Date(value));
}

export function HistoryPage() {
  const router = useRouter();
  const toast = useToast();
  const [account, setAccount] = useState<Account | null>(null);
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [profile, setProfile] = useState<Provider | null>(null);
  const [customerProfile, setCustomerProfile] = useState<CustomerProfile | null>(null);
  const [needsProviderProfile, setNeedsProviderProfile] = useState(false);
  const [application, setApplication] = useState({
    categoryIds: ["cleaning"],
    address: "",
    imageUrl: "",
    description: "",
  });
  const [reviews, setReviews] = useState<Review[]>([]);
  const [reviewingBookingId, setReviewingBookingId] = useState<string | null>(null);
  const [reviewRating, setReviewRating] = useState("5");
  const [reviewComment, setReviewComment] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [bookingAction, setBookingAction] = useState<{ bookingId: string; status: "ACCEPTED" | "REJECTED" | "COMPLETED"; title: string; description: string } | null>(null);
  const [actionBusy, setActionBusy] = useState(false);
  const [reviewBookingId, setReviewBookingId] = useState<string | null>(null);

  const loadHistory = useCallback(async () => {
    setError("");
    try {
      const current = await apiRequest<Account>("me");
      setAccount(current);
      if (current.role === "ADMIN") {
        router.replace("/admin/providers");
        return;
      }
      const results = await apiRequest<Booking[]>("bookings/mine");
      setBookings(results);
      setReviews(await apiRequest<Review[]>("reviews/mine"));
      if (current.role === "CUSTOMER") {
        try {
          setCustomerProfile(await apiRequest<CustomerProfile>("customer/profile"));
        } catch (cause) {
          if (cause instanceof Error && cause.message.includes("Hồ sơ khách hàng")) {
            setCustomerProfile({
              displayName: current.fullName,
              phoneNumber: current.phoneNumber,
              defaultAddress: "",
            });
          } else {
            throw cause;
          }
        }
      }
      if (current.role === "PROVIDER") {
        try {
          setProfile(await apiRequest<Provider>("provider/profile"));
          setNeedsProviderProfile(false);
        } catch (cause) {
          if (cause instanceof Error && cause.message.includes("chưa gửi hồ sơ")) {
            setNeedsProviderProfile(true);
          } else {
            throw cause;
          }
        }
      }
    } catch (cause) {
      if (cause instanceof Error && cause.message.includes("đăng nhập")) {
        router.replace("/login?next=/history");
        return;
      }
      setError(cause instanceof Error ? cause.message : "Không tải được lịch sử.");
    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => {
    void Promise.resolve().then(loadHistory);
  }, [loadHistory]);

  async function updateBooking(bookingId: string, status: "ACCEPTED" | "REJECTED" | "COMPLETED") {
    setActionBusy(true);
    setError("");
    try {
      await apiRequest<Booking>(`provider/bookings/${bookingId}/status`, {
        method: "PATCH",
        body: JSON.stringify({ status }),
      });
      toast.success(status === "COMPLETED" ? "Đã đánh dấu lịch hẹn hoàn thành." : status === "ACCEPTED" ? "Đã nhận lịch hẹn." : "Đã từ chối lịch hẹn.");
      setBookingAction(null);
      await loadHistory();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không cập nhật được lịch hẹn.");
    } finally {
      setActionBusy(false);
    }
  }

  async function submitReview(bookingId: string) {
    setActionBusy(true);
    setError("");
    try {
      await apiRequest<Review>("reviews", {
        method: "POST",
        body: JSON.stringify({ bookingId, rating: Number(reviewRating), comment: reviewComment }),
      });
      setReviewingBookingId(null);
      setReviewBookingId(null);
      setReviewComment("");
      toast.success("Đã gửi đánh giá.");
      await loadHistory();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không gửi được đánh giá.");
    } finally {
      setActionBusy(false);
    }
  }

  async function submitProviderProfile() {
    setError("");
    try {
      const profile = await apiRequest<Provider>("provider/applications", {
        method: "POST",
        body: JSON.stringify(application),
      });
      setProfile(profile);
      setNeedsProviderProfile(false);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không gửi được hồ sơ nhà cung cấp.");
    }
  }

  async function submitCustomerProfile() {
    if (!customerProfile) return;
    setError("");
    try {
      setCustomerProfile(await apiRequest<CustomerProfile>("customer/profile", {
        method: "PUT",
        body: JSON.stringify(customerProfile),
      }));
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không cập nhật được hồ sơ khách hàng.");
    }
  }

  return (
    <main className={styles.style1}>
      <SiteHeader />
      <section className={styles.style2}>
        <p className={styles.style3}>Tài khoản</p>
        <h1 className={styles.style4}>Lịch sử hoạt động</h1>
        {account && <p className={styles.style5}>Xin chào {account.fullName} · {account.role === "CUSTOMER" ? "Khách hàng" : "Nhà cung cấp"}</p>}

        {profile && (
          <div className={styles.style6}>
            <p className={styles.style7}>Hồ sơ nhà cung cấp</p>
            <p className={styles.style8}>{profile.categories.map((category) => category.name).join(" · ")} · {profile.address}</p>
            <p className={styles.style9}>
              {profile.status === "PENDING" ? "Đang chờ Admin duyệt" : profile.status === "APPROVED" ? "Đã được duyệt" : "Hồ sơ chưa được duyệt"}
            </p>
          </div>
        )}
        {account?.role === "CUSTOMER" && customerProfile && (
          <form
            className={styles.style10}
            onSubmit={(event) => {
              event.preventDefault();
              void submitCustomerProfile();
            }}
          >
            <h2 className={styles.style7}>Hồ sơ khách hàng</h2>
            <label className={styles.style11}>
              Tên hiển thị
              <input required maxLength={120} value={customerProfile.displayName}
                onChange={(event) => setCustomerProfile({ ...customerProfile, displayName: event.target.value })}
                className={styles.style12} />
            </label>
            <label className={styles.style11}>
              Số điện thoại
              <input required type="tel" value={customerProfile.phoneNumber}
                onChange={(event) => setCustomerProfile({ ...customerProfile, phoneNumber: event.target.value })}
                className={styles.style12} />
            </label>
            <label className={styles.style11}>
              Địa chỉ mặc định
              <input required maxLength={500} value={customerProfile.defaultAddress}
                onChange={(event) => setCustomerProfile({ ...customerProfile, defaultAddress: event.target.value })}
                className={styles.style12} />
            </label>
            <button className={styles.style13}>
              Lưu hồ sơ
            </button>
          </form>
        )}
        {account?.role === "PROVIDER" && needsProviderProfile && (
          <section className={styles.style14}>
            <h2 className={styles.style15}>Hoàn tất hồ sơ nhà cung cấp</h2>
            <p className={styles.style8}>Hồ sơ cần được Admin duyệt trước khi khách hàng có thể tìm thấy bạn.</p>
            <form className={styles.style16} onSubmit={(event) => {
              event.preventDefault();
              void submitProviderProfile();
            }}>
              <fieldset>
                <legend className={styles.style17}>Dịch vụ bạn cung cấp</legend>
                <div className={styles.style18}>
                  {serviceCategories.map((category) => (
                    <label key={category.id} className={styles.style19}>
                      <input
                        type="checkbox"
                        checked={application.categoryIds.includes(category.id)}
                        onChange={() => setApplication((current) => {
                          if (current.categoryIds.includes(category.id) && current.categoryIds.length === 1) return current;
                          return {
                            ...current,
                            categoryIds: current.categoryIds.includes(category.id)
                              ? current.categoryIds.filter((item) => item !== category.id)
                              : [...current.categoryIds, category.id],
                          };
                        })}
                      />
                      {category.name}
                    </label>
                  ))}
                </div>
              </fieldset>
              <FormField htmlFor="provider-application-address" label="Địa chỉ/khu vực hoạt động">
                <Input
                  id="provider-application-address"
                  required
                  maxLength={500}
                  value={application.address}
                  icon={<MapPin />}
                  onChange={(event) => setApplication({ ...application, address: event.target.value })}
                />
              </FormField>
              <FormField htmlFor="provider-application-image" label="Liên kết ảnh đại diện (HTTPS)">
                <Input
                  id="provider-application-image"
                  required
                  type="url"
                  pattern="https://.+"
                  value={application.imageUrl}
                  icon={<ImageIcon />}
                  onChange={(event) => setApplication({ ...application, imageUrl: event.target.value })}
                />
              </FormField>
              <FormField htmlFor="provider-application-description" label="Giới thiệu dịch vụ">
                <div className="group relative">
                  <Sparkles aria-hidden="true" className="pointer-events-none absolute left-3.5 top-3.5 h-5 w-5 text-muted transition-colors group-focus-within:text-brand-600" />
                  <textarea
                    id="provider-application-description"
                    required
                    maxLength={1000}
                    rows={3}
                    value={application.description}
                    onChange={(event) => setApplication({ ...application, description: event.target.value })}
                    className="w-full resize-y rounded-md border border-border bg-surface py-3 pl-11 pr-3.5 text-base text-text outline-none transition focus:border-brand-500 focus:ring-2 focus:ring-brand-500/20"
                  />
                </div>
              </FormField>
              <button type="submit" className={styles.style20}>Gửi hồ sơ chờ duyệt</button>
            </form>
          </section>
        )}

        {error && <p role="alert" className={styles.style21}>{error}</p>}
        {loading ? (
          <p className={styles.style22}>Đang tải lịch sử…</p>
        ) : bookings.length === 0 ? (
          <div className={styles.style23}>
            <p className={styles.style24}>Chưa có lịch sử đặt dịch vụ</p>
            <p className={styles.style5}>{account?.role === "PROVIDER" ? "Yêu cầu của khách hàng sẽ xuất hiện tại đây." : "Chọn một dịch vụ để gửi yêu cầu đầu tiên."}</p>
            {account?.role === "CUSTOMER" && <Link className={styles.style25} href="/#services">Khám phá dịch vụ</Link>}
          </div>
        ) : (
          <div className={styles.style26}>
            {bookings.map((booking) => (
              <article key={booking.id} className={styles.style27}>
                <div className={styles.style28}>
                  <div>
                    <p className={styles.style29}>{booking.categoryName}</p>
                    <h2 className={styles.style30}>
                      {account?.role === "CUSTOMER" ? booking.providerName : `Khách hàng: ${booking.customerName}`}
                    </h2>
                    <p className={styles.style5}>{formatDate(booking.scheduledAt)} · {booking.address}</p>
                    <p className={styles.style31}>{booking.description}</p>
                  </div>
                  <span className={styles.style32}>{statusLabels[booking.status]}</span>
                </div>
                {account?.role === "PROVIDER" && (
                  <div className={styles.style33}>
                    {booking.status === "REQUESTED" && (
                      <>
                        <button className={styles.style34} onClick={() => setBookingAction({ bookingId: booking.id, status: "ACCEPTED", title: "Xác nhận nhận lịch?", description: `Lịch hẹn của khách ${booking.customerName} sẽ chuyển sang trạng thái đã xác nhận.` })}>Nhận lịch</button>
                        <button className={styles.style35} onClick={() => setBookingAction({ bookingId: booking.id, status: "REJECTED", title: "Từ chối lịch hẹn?", description: `Lịch hẹn của khách ${booking.customerName} sẽ kết thúc với trạng thái đã từ chối.` })}>Từ chối</button>
                      </>
                    )}
                    {booking.status === "ACCEPTED" && <button className={styles.style34} onClick={() => setBookingAction({ bookingId: booking.id, status: "COMPLETED", title: "Xác nhận đã hoàn thành?", description: "Thao tác này không hoàn tác được; hai bên có thể gửi đánh giá sau khi hoàn thành." })}>Đánh dấu hoàn thành</button>}
                  </div>
                )}
                {booking.status === "COMPLETED" && !reviews.some((review) => review.bookingId === booking.id) && (
                  <div className={styles.style36}>
                    {reviewingBookingId === booking.id ? (
                      <div className={styles.style37}>
                        <label className={styles.style38}>
                          Số sao
                          <select value={reviewRating} onChange={(event) => setReviewRating(event.target.value)} className={styles.style39}>
                            {[5, 4, 3, 2, 1].map((rating) => <option key={rating} value={rating}>{rating} sao</option>)}
                          </select>
                        </label>
                        <label className={styles.style38}>
                          Nhận xét
                          <input maxLength={1000} value={reviewComment} onChange={(event) => setReviewComment(event.target.value)} className={styles.style40} placeholder="Chia sẻ trải nghiệm (không bắt buộc)" />
                        </label>
                        <button type="button" className={styles.style41} onClick={() => setReviewBookingId(booking.id)}>Xem lại đánh giá</button>
                      </div>
                    ) : (
                      <button className={styles.style42} onClick={() => setReviewingBookingId(booking.id)}>
                        Đánh giá {account?.role === "CUSTOMER" ? "nhà cung cấp" : "khách hàng"}
                      </button>
                    )}
                  </div>
                )}
                {booking.status === "COMPLETED" && reviews.some((review) => review.bookingId === booking.id) && (
                  <p className={styles.style43}>Bạn đã gửi đánh giá cho lịch hẹn này.</p>
                )}
              </article>
            ))}
          </div>
        )}
      </section>
      <ConfirmDialog
        open={bookingAction !== null}
        title={bookingAction?.title ?? "Cập nhật lịch hẹn?"}
        description={bookingAction?.description ?? ""}
        confirmLabel={bookingAction?.status === "ACCEPTED" ? "Nhận lịch" : bookingAction?.status === "REJECTED" ? "Từ chối" : "Xác nhận hoàn thành"}
        loading={actionBusy}
        onCancel={() => setBookingAction(null)}
        onConfirm={() => bookingAction ? void updateBooking(bookingAction.bookingId, bookingAction.status) : undefined}
      />
      <ConfirmDialog
        open={reviewBookingId !== null}
        title="Gửi đánh giá?"
        description="Đánh giá không thể sửa hoặc xóa sau khi gửi."
        confirmLabel="Gửi đánh giá"
        loading={actionBusy}
        onCancel={() => setReviewBookingId(null)}
        onConfirm={() => reviewBookingId ? void submitReview(reviewBookingId) : undefined}
      >
        <div className="rounded-md border border-border bg-bg p-4 text-sm">
          <p className="font-bold">{reviewRating} sao</p>
          <p className="mt-2 text-muted">{reviewComment.trim() || "Không có nhận xét."}</p>
        </div>
      </ConfirmDialog>
    </main>
  );
}
