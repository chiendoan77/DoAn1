"use client";


import styles from "./history-page.module.css";
import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Account, Booking, CustomerProfile, Provider, Review, ServiceCategory, apiRequest } from "@/lib/api";
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
              <label className={styles.style11}>
                Địa chỉ/khu vực hoạt động
                <input required maxLength={500} value={application.address} onChange={(event) => setApplication({ ...application, address: event.target.value })} className={styles.style12} />
              </label>
              <label className={styles.style11}>
                Liên kết ảnh đại diện (HTTPS)
                <input required type="url" pattern="https://.+" value={application.imageUrl} onChange={(event) => setApplication({ ...application, imageUrl: event.target.value })} className={styles.style12} />
              </label>
              <label className={styles.style11}>
                Giới thiệu dịch vụ
                <textarea required maxLength={1000} rows={3} value={application.description} onChange={(event) => setApplication({ ...application, description: event.target.value })} className={styles.style12} />
              </label>
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
                        <button className={styles.style34} onClick={() => void updateBooking(booking.id, "ACCEPTED")}>Nhận lịch</button>
                        <button className={styles.style35} onClick={() => void updateBooking(booking.id, "REJECTED")}>Từ chối</button>
                      </>
                    )}
                    {booking.status === "ACCEPTED" && <button className={styles.style34} onClick={() => void updateBooking(booking.id, "COMPLETED")}>Đánh dấu hoàn thành</button>}
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
                        <button className={styles.style41} onClick={() => void submitReview(booking.id)}>Gửi đánh giá</button>
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
    </main>
  );
}
