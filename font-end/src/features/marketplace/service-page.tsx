"use client";

import styles from "./service-page.module.css";

/* eslint-disable @next/next/no-img-element */
import Link from "next/link";
import { FormEvent, useEffect, useState } from "react";
import { Booking, CustomerProfile, PageResult, Provider, ServiceCategory, ServicePackage, apiRequest } from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";

const serviceDetails = {
  cleaning: { id: "cleaning", name: "Dọn dẹp nhà", description: "Dọn dẹp định kỳ và tổng vệ sinh.", icon: "⌂" },
  repair: { id: "repair", name: "Sửa chữa gia đình", description: "Kết nối thợ sửa chữa cho ngôi nhà.", icon: "＋" },
} satisfies Record<string, ServiceCategory & { icon: string }>;

type Props = { service: keyof typeof serviceDetails };

type ProviderPageState = {
  categoryId: string;
  page: number;
  result?: PageResult<Provider>;
  error?: string;
};

function localDateTimeMinimum() {
  const date = new Date(Date.now() + 60 * 60 * 1000);
  date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
  return date.toISOString().slice(0, 16);
}

export function ServicePage({ service }: Props) {
  const category = serviceDetails[service];
  const [providerPage, setProviderPage] = useState(0);
  const [providerPageState, setProviderPageState] = useState<ProviderPageState | null>(null);
  const [packages, setPackages] = useState<ServicePackage[]>([]);
  const [loadingPackages, setLoadingPackages] = useState(true);
  const [packageError, setPackageError] = useState("");
  const [selectedProvider, setSelectedProvider] = useState<Provider | null>(null);
  const [selectedPackage, setSelectedPackage] = useState<ServicePackage | null>(null);
  const [scheduledAt, setScheduledAt] = useState("");
  const [bookingError, setBookingError] = useState("");
  const [bookingMessage, setBookingMessage] = useState("");
  const [bookingSubmitting, setBookingSubmitting] = useState(false);
  const isCurrentProviderPage = providerPageState?.categoryId === category.id
    && providerPageState.page === providerPage;
  const providers = isCurrentProviderPage ? providerPageState.result?.content ?? [] : [];
  const providerError = isCurrentProviderPage ? providerPageState.error ?? "" : "";
  const providerTotalPages = isCurrentProviderPage ? providerPageState.result?.totalPages ?? 0 : 0;
  const loadingProviders = !isCurrentProviderPage;

  useEffect(() => {
    apiRequest<PageResult<Provider>>(`services/${category.id}/providers/page?page=${providerPage}&limit=10`)
      .then((result) => setProviderPageState({ categoryId: category.id, page: providerPage, result }))
      .catch((cause: Error) => setProviderPageState({
        categoryId: category.id,
        page: providerPage,
        error: cause.message,
      }));
  }, [category.id, providerPage]);

  useEffect(() => {
    apiRequest<PageResult<ServicePackage>>(`catalog/packages?categoryId=${encodeURIComponent(category.id)}&page=0&size=20`)
      .then((result) => setPackages(result.content))
      .catch((cause: Error) => setPackageError(cause.message))
      .finally(() => setLoadingPackages(false));
  }, [category.id]);

  function closeBooking() {
    setSelectedProvider(null);
    setSelectedPackage(null);
    setScheduledAt("");
    setBookingError("");
  }

  async function submitBooking(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedProvider || !selectedPackage || !scheduledAt) return;
    setBookingError("");
    setBookingSubmitting(true);
    try {
      const profile = await apiRequest<CustomerProfile>("customer/profile");
      if (!profile.defaultAddress.trim()) {
        setBookingError("Lưu địa chỉ mặc định trong hồ sơ cá nhân trước khi đặt lịch.");
        return;
      }
      const booking = await apiRequest<Booking>("bookings", {
        method: "POST",
        body: JSON.stringify({
          packageId: selectedPackage.id,
          scheduledAt: new Date(scheduledAt).toISOString(),
          address: profile.defaultAddress,
          description: `Đặt theo gói dịch vụ: ${selectedPackage.name}`.slice(0, 1000),
        }),
      });
      setBookingMessage(`Đã gửi yêu cầu ${booking.id.slice(0, 8)} cho ${booking.providerName}.`);
      closeBooking();
    } catch (cause) {
      setBookingError(`Chưa thể tạo lịch hẹn. Hãy đăng nhập bằng tài khoản khách hàng và lưu địa chỉ trong hồ sơ cá nhân; chi tiết: ${cause instanceof Error ? cause.message : "yêu cầu không thành công."}`);
    } finally {
      setBookingSubmitting(false);
    }
  }

  async function choosePackage(servicePackage: ServicePackage) {
    setBookingError("");
    setBookingMessage("");
    try {
      const provider = providers.find((item) => item.id === servicePackage.providerId)
        ?? await apiRequest<Provider>(`services/providers/${servicePackage.providerId}`);
      setSelectedProvider(provider);
      setSelectedPackage(servicePackage);
    } catch (cause) {
      setBookingError(cause instanceof Error ? cause.message : "Không tải được nhà cung cấp của gói.");
    }
  }

  return (
    <main className={styles.style1}>
      <SiteHeader />
      <section className={styles.style2}>
        <div className={styles.style3}>
          <Link href="/" className={styles.style4}>← Trang chủ</Link>
          <div className={styles.style5}>
            <div>
              <p className={styles.style6}>Dịch vụ Nhà Tốt</p>
              <h1 className={styles.style7}>{category.name}</h1>
              <p className={styles.style8}>{category.description} Chọn nhà cung cấp hoặc gói dịch vụ, rồi chọn thời gian hẹn.</p>
            </div>
            <span className={styles.style9} aria-hidden="true">{category.icon}</span>
          </div>
        </div>
      </section>

      <section className={styles.style10}>
        <div className={styles.style11}>
          <div>
            <p className={styles.style6}>Nhà cung cấp</p>
            <h2 className={styles.style12}>{category.name} gần bạn</h2>
          </div>
          <p className={styles.style13}>Hồ sơ đã duyệt, ưu tiên đánh giá cao.</p>
        </div>
        {loadingProviders ? (
          <p className={styles.style14}>Đang tìm nhà cung cấp…</p>
        ) : providerError ? (
          <p role="alert" className={styles.style15}>{providerError}</p>
        ) : providers.length === 0 ? (
          <p className={styles.style16}>Chưa có nhà cung cấp được duyệt trong danh mục này.</p>
        ) : (
          <div className={styles.style17}>
            {providers.map((provider) => (
              <article key={provider.id} className={styles.style18}>
                <img className={styles.style19} src={provider.imageUrl} alt={`Hình ảnh ${provider.fullName}`} loading="lazy" />
                <div className={styles.style20}>
                  <div className={styles.style21}>
                    <h3 className={styles.style22}>{provider.fullName}</h3>
                    <span className={styles.style23} aria-label={`Đánh giá ${provider.rating} trên 5 sao`}>★ {provider.rating.toFixed(1)}</span>
                  </div>
                  <p className={styles.style24}>{provider.reviewCount} đánh giá{provider.demoData ? " · hồ sơ mẫu" : ""}</p>
                  <p className={styles.style25}>⌖ {provider.address}</p>
                  <p className={styles.style26}>{provider.description}</p>
                  <button type="button" className={styles.style27} onClick={() => {
                    setSelectedProvider(provider);
                    setSelectedPackage(null);
                    setBookingError("");
                    setBookingMessage("");
                  }}>Chọn nhà cung cấp</button>
                </div>
              </article>
            ))}
          </div>
        )}
        {providerTotalPages > 1 && (
          <nav className={styles.style54} aria-label="Phân trang nhà cung cấp">
            <button
              type="button"
              className={styles.style55}
              disabled={loadingProviders || providerPage === 0}
              onClick={() => setProviderPage((current) => current - 1)}
            >
              Trang trước
            </button>
            <span>Trang {providerPage + 1} / {providerTotalPages}</span>
            <button
              type="button"
              className={styles.style55}
              disabled={loadingProviders || providerPage + 1 >= providerTotalPages}
              onClick={() => setProviderPage((current) => current + 1)}
            >
              Trang sau
            </button>
          </nav>
        )}

        <div className={styles.style28}>
          <div>
            <p className={styles.style6}>Gói dịch vụ đã duyệt</p>
            <h2 className={styles.style12}>{category.name} theo gói</h2>
          </div>
          {loadingPackages ? (
            <p className={styles.style14}>Đang tải gói dịch vụ…</p>
          ) : packageError ? (
            <p role="alert" className={styles.style15}>{packageError}</p>
          ) : packages.length === 0 ? (
            <p className={styles.style16}>Chưa có gói dịch vụ được duyệt trong danh mục này.</p>
          ) : (
            <div className={styles.style29}>
              {packages.map((servicePackage) => (
                <article key={servicePackage.id} className={styles.style30}>
                  <p className={styles.style31}>{category.name}</p>
                  <h3 className={styles.style32}>{servicePackage.name}</h3>
                  <p className={styles.style33}>{servicePackage.description}</p>
                  <p className={styles.style34}>{new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(servicePackage.price)}</p>
                  <p className={styles.style24}>Thời lượng dự kiến: {servicePackage.durationMinutes} phút</p>
                  <p className={styles.style35}>Nhà cung cấp: {servicePackage.providerPhone}</p>
                  <button type="button" onClick={() => void choosePackage(servicePackage)} className={styles.style27}>Đặt gói này</button>
                </article>
              ))}
            </div>
          )}
        </div>
        {bookingMessage && <p role="status" className={styles.style36}>{bookingMessage} <Link className={styles.style37} href="/history">Xem lịch sử</Link></p>}
      </section>

      {selectedProvider && (
        <div className={styles.style38} role="presentation" onMouseDown={(event) => {
          if (event.target === event.currentTarget) closeBooking();
        }}>
          <section aria-labelledby="booking-title" aria-modal="true" role="dialog" className={styles.style39}>
            <div className={styles.style40}>
              <div>
                <p className={styles.style41}>Gửi yêu cầu</p>
                <h2 id="booking-title" className={styles.style12}>Đặt lịch dịch vụ</h2>
              </div>
              <button type="button" className={styles.style42} onClick={closeBooking} aria-label="Đóng">×</button>
            </div>
            <form className={styles.style44} onSubmit={(event) => void submitBooking(event)}>
              <div className={styles.style58}>
                <img
                  className={styles.style59}
                  src={selectedProvider.imageUrl}
                  alt={`Ảnh ${selectedProvider.fullName}`}
                />
                <div className={styles.style60}>
                  <div className={styles.style57}>
                    <p className={styles.style41}>Nhà cung cấp · {category.name}</p>
                    <p className={styles.style22}>{selectedProvider.fullName}</p>
                    <p className={styles.style48}>{selectedProvider.address}</p>
                  </div>
                  {!selectedPackage ? (
                    <label className={styles.style45}>
                      Gói dịch vụ
                      <select required value="" onChange={(event) => setSelectedPackage(packages.find((item) => item.id === event.target.value) ?? null)} className={styles.style46}>
                        <option value="">Chọn gói đã được duyệt</option>
                        {packages.filter((item) => item.providerId === selectedProvider.id).map((item) => <option key={item.id} value={item.id}>{item.name} · {new Intl.NumberFormat("vi-VN").format(item.price)} ₫</option>)}
                      </select>
                    </label>
                  ) : (
                    <div className={styles.style47}>
                      <p className={styles.style31}>Gói dịch vụ</p>
                      <p className={styles.style22}>{selectedPackage.name}</p>
                      <p className={styles.style48}>{selectedPackage.description}</p>
                      <p className={styles.style34}>{new Intl.NumberFormat("vi-VN").format(selectedPackage.price)} ₫</p>
                      <p className={styles.style48}>Thời lượng dự kiến: {selectedPackage.durationMinutes} phút</p>
                    </div>
                  )}
                  <div className={styles.style56}>
                    <label className={styles.style45}>
                      Thời gian hẹn
                      <input required min={localDateTimeMinimum()} type="datetime-local" value={scheduledAt} onChange={(event) => setScheduledAt(event.target.value)} className={styles.style49} />
                    </label>
                    <button disabled={bookingSubmitting || !selectedPackage} className={styles.style61}>
                      {bookingSubmitting ? "Đang gửi…" : "Đặt lịch"}
                    </button>
                  </div>
                  <p className={styles.style50}>Yêu cầu sẽ dùng địa chỉ mặc định trong hồ sơ của bạn.</p>
                  {bookingError && (
                    <div role="alert" className={styles.style51}>
                      <p>{bookingError}</p>
                      <button type="button" onClick={closeBooking} className={styles.style52}>Đóng để cập nhật hồ sơ ở avatar trên thanh menu</button>
                      {" · "}
                      <Link className={styles.style37} href="/login">Đăng nhập</Link>
                    </div>
                  )}
                </div>
              </div>
            </form>
          </section>
        </div>
      )}
    </main>
  );
}
