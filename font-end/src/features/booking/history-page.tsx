"use client";

import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Account, Booking, Provider, Review, ServiceCategory, apiRequest } from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";

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
  const [account, setAccount] = useState<Account | null>(null);
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [profile, setProfile] = useState<Provider | null>(null);
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
    setError("");
    try {
      await apiRequest<Booking>(`provider/bookings/${bookingId}/status`, {
        method: "PATCH",
        body: JSON.stringify({ status }),
      });
      await loadHistory();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không cập nhật được lịch hẹn.");
    }
  }

  async function submitReview(bookingId: string) {
    setError("");
    try {
      await apiRequest<Review>("reviews", {
        method: "POST",
        body: JSON.stringify({ bookingId, rating: Number(reviewRating), comment: reviewComment }),
      });
      setReviewingBookingId(null);
      setReviewComment("");
      await loadHistory();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không gửi được đánh giá.");
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

  return (
    <main className="min-h-screen bg-[#f7f8f4]">
      <SiteHeader />
      <section className="mx-auto max-w-5xl px-5 py-10 sm:px-8 sm:py-14">
        <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#719071]">Tài khoản</p>
        <h1 className="mt-2 text-3xl font-semibold text-[#173c33]">Lịch sử hoạt động</h1>
        {account && <p className="mt-2 text-sm text-[#6c7971]">Xin chào {account.fullName} · {account.role === "CUSTOMER" ? "Khách hàng" : "Nhà cung cấp"}</p>}

        {profile && (
          <div className="mt-6 rounded-2xl border border-[#dce5dc] bg-white p-5">
            <p className="text-sm font-semibold text-[#315246]">Hồ sơ nhà cung cấp</p>
            <p className="mt-1 text-sm text-[#6c7971]">{profile.categories.map((category) => category.name).join(" · ")} · {profile.address}</p>
            <p className="mt-2 text-sm font-medium text-[#477352]">
              {profile.status === "PENDING" ? "Đang chờ Admin duyệt" : profile.status === "APPROVED" ? "Đã được duyệt" : "Hồ sơ chưa được duyệt"}
            </p>
          </div>
        )}
        {account?.role === "PROVIDER" && needsProviderProfile && (
          <section className="mt-6 rounded-3xl border border-[#dce5dc] bg-white p-5 sm:p-6">
            <h2 className="text-lg font-semibold text-[#23483a]">Hoàn tất hồ sơ nhà cung cấp</h2>
            <p className="mt-1 text-sm text-[#6c7971]">Hồ sơ cần được Admin duyệt trước khi khách hàng có thể tìm thấy bạn.</p>
            <form className="mt-4 grid gap-4" onSubmit={(event) => {
              event.preventDefault();
              void submitProviderProfile();
            }}>
              <fieldset>
                <legend className="mb-2 text-sm font-medium text-[#315246]">Dịch vụ bạn cung cấp</legend>
                <div className="flex flex-wrap gap-3">
                  {serviceCategories.map((category) => (
                    <label key={category.id} className="flex items-center gap-2 rounded-xl border border-[#dce5dc] px-3 py-2 text-sm">
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
              <label className="text-sm font-medium text-[#315246]">
                Địa chỉ/khu vực hoạt động
                <input required maxLength={500} value={application.address} onChange={(event) => setApplication({ ...application, address: event.target.value })} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3" />
              </label>
              <label className="text-sm font-medium text-[#315246]">
                Liên kết ảnh đại diện (HTTPS)
                <input required type="url" pattern="https://.+" value={application.imageUrl} onChange={(event) => setApplication({ ...application, imageUrl: event.target.value })} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3" />
              </label>
              <label className="text-sm font-medium text-[#315246]">
                Giới thiệu dịch vụ
                <textarea required maxLength={1000} rows={3} value={application.description} onChange={(event) => setApplication({ ...application, description: event.target.value })} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3" />
              </label>
              <button type="submit" className="w-fit rounded-full bg-[#1f594a] px-5 py-3 text-sm font-semibold text-white">Gửi hồ sơ chờ duyệt</button>
            </form>
          </section>
        )}

        {error && <p role="alert" className="mt-6 rounded-2xl bg-[#fff1ec] p-4 text-sm text-[#9b3d28]">{error}</p>}
        {loading ? (
          <p className="mt-8 text-sm text-[#6c7971]">Đang tải lịch sử…</p>
        ) : bookings.length === 0 ? (
          <div className="mt-8 rounded-3xl border border-dashed border-[#cfdacf] bg-white p-8 text-center">
            <p className="font-semibold text-[#315246]">Chưa có lịch sử đặt dịch vụ</p>
            <p className="mt-2 text-sm text-[#6c7971]">{account?.role === "PROVIDER" ? "Yêu cầu của khách hàng sẽ xuất hiện tại đây." : "Chọn một dịch vụ để gửi yêu cầu đầu tiên."}</p>
            {account?.role === "CUSTOMER" && <Link className="mt-5 inline-flex rounded-full bg-[#1f594a] px-5 py-3 text-sm font-semibold text-white" href="/#services">Khám phá dịch vụ</Link>}
          </div>
        ) : (
          <div className="mt-8 space-y-4">
            {bookings.map((booking) => (
              <article key={booking.id} className="rounded-3xl border border-[#e5eae3] bg-white p-5 sm:p-6">
                <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-start">
                  <div>
                    <p className="text-xs font-semibold uppercase tracking-wide text-[#719071]">{booking.categoryName}</p>
                    <h2 className="mt-1 text-lg font-semibold text-[#23483a]">
                      {account?.role === "CUSTOMER" ? booking.providerName : `Khách hàng: ${booking.customerName}`}
                    </h2>
                    <p className="mt-2 text-sm text-[#6c7971]">{formatDate(booking.scheduledAt)} · {booking.address}</p>
                    <p className="mt-2 text-sm leading-6 text-[#6c7971]">{booking.description}</p>
                  </div>
                  <span className="w-fit rounded-full bg-[#f1f7ef] px-3 py-1.5 text-xs font-semibold text-[#35684c]">{statusLabels[booking.status]}</span>
                </div>
                {account?.role === "PROVIDER" && (
                  <div className="mt-4 flex flex-wrap gap-2 border-t border-[#edf0eb] pt-4">
                    {booking.status === "REQUESTED" && (
                      <>
                        <button className="rounded-full bg-[#1f594a] px-4 py-2 text-xs font-semibold text-white" onClick={() => void updateBooking(booking.id, "ACCEPTED")}>Nhận lịch</button>
                        <button className="rounded-full border border-[#dce5dc] px-4 py-2 text-xs font-semibold text-[#52645e]" onClick={() => void updateBooking(booking.id, "REJECTED")}>Từ chối</button>
                      </>
                    )}
                    {booking.status === "ACCEPTED" && <button className="rounded-full bg-[#1f594a] px-4 py-2 text-xs font-semibold text-white" onClick={() => void updateBooking(booking.id, "COMPLETED")}>Đánh dấu hoàn thành</button>}
                  </div>
                )}
                {booking.status === "COMPLETED" && !reviews.some((review) => review.bookingId === booking.id) && (
                  <div className="mt-4 border-t border-[#edf0eb] pt-4">
                    {reviewingBookingId === booking.id ? (
                      <div className="grid gap-3 sm:grid-cols-[120px_1fr_auto]">
                        <label className="text-xs font-medium text-[#52645e]">
                          Số sao
                          <select value={reviewRating} onChange={(event) => setReviewRating(event.target.value)} className="mt-1 w-full rounded-xl border border-[#dce5dc] bg-white px-3 py-2 text-sm">
                            {[5, 4, 3, 2, 1].map((rating) => <option key={rating} value={rating}>{rating} sao</option>)}
                          </select>
                        </label>
                        <label className="text-xs font-medium text-[#52645e]">
                          Nhận xét
                          <input maxLength={1000} value={reviewComment} onChange={(event) => setReviewComment(event.target.value)} className="mt-1 w-full rounded-xl border border-[#dce5dc] px-3 py-2 text-sm" placeholder="Chia sẻ trải nghiệm (không bắt buộc)" />
                        </label>
                        <button className="self-end rounded-full bg-[#1f594a] px-4 py-2 text-xs font-semibold text-white" onClick={() => void submitReview(booking.id)}>Gửi đánh giá</button>
                      </div>
                    ) : (
                      <button className="rounded-full border border-[#dce5dc] px-4 py-2 text-xs font-semibold text-[#35684c]" onClick={() => setReviewingBookingId(booking.id)}>
                        Đánh giá {account?.role === "CUSTOMER" ? "nhà cung cấp" : "khách hàng"}
                      </button>
                    )}
                  </div>
                )}
                {booking.status === "COMPLETED" && reviews.some((review) => review.bookingId === booking.id) && (
                  <p className="mt-4 border-t border-[#edf0eb] pt-4 text-xs font-medium text-[#477352]">Bạn đã gửi đánh giá cho lịch hẹn này.</p>
                )}
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}
