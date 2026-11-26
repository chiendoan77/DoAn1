"use client";

import styles from "./service-page.module.css";

/* eslint-disable @next/next/no-img-element */
import Link from "next/link";
import { FormEvent, useEffect, useState } from "react";
import { Booking, CustomerProfile, PageResult, Provider, ServiceCategory, ServicePackage, apiRequest } from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";
import { ConfirmDialog } from "@/shared/ui/confirm-dialog";
import { useToast } from "@/shared/ui/toast";
import { ArrowLeft, MapPin, Search, Sparkles, Wrench, X } from "lucide-react";

const serviceDetails = {
  cleaning: { id: "cleaning", name: "Dọn dẹp nhà", description: "Dọn dẹp định kỳ và tổng vệ sinh.", icon: "cleaning" },
  repair: { id: "repair", name: "Sửa chữa gia đình", description: "Kết nối thợ sửa chữa cho ngôi nhà.", icon: "repair" },
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
  const toast = useToast();
  const category = serviceDetails[service];
  const [providerPage, setProviderPage] = useState(0);
  const [providerPageState, setProviderPageState] = useState<ProviderPageState | null>(null);
  const [packages, setPackages] = useState<ServicePackage[]>([]);
  const [loadingPackages, setLoadingPackages] = useState(true);
  const [packageError, setPackageError] = useState("");
  const [selectedProvider, setSelectedProvider] = useState<Provider | null>(null);
  const [selectedPackage, setSelectedPackage] = useState<ServicePackage | null>(null);
  const [scheduledAt, setScheduledAt] = useState("");
  const [address, setAddress] = useState("");
  const [description, setDescription] = useState("");
  const [confirmOpen, setConfirmOpen] = useState(false);
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

  useEffect(() => {
    if (!selectedProvider) return;
    function handleEscape(event: KeyboardEvent) {
      if (event.key === "Escape") closeBooking();
    }
    window.addEventListener("keydown", handleEscape);
    return () => window.removeEventListener("keydown", handleEscape);
  }, [selectedProvider]);

  function closeBooking() {
    setSelectedProvider(null);
    setSelectedPackage(null);
    setScheduledAt("");
    setAddress("");
    setDescription("");
    setConfirmOpen(false);
    setBookingError("");
  }

  async function prepareBooking(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedProvider || !selectedPackage || !scheduledAt || !address.trim() || !description.trim()) {
      setBookingError("Vui lòng chọn gói, thời gian, địa chỉ và mô tả công việc.");
      return;
    }
    setBookingError("");
    setConfirmOpen(true);
  }

  async function confirmBooking() {
    if (!selectedProvider || !selectedPackage || !scheduledAt) return;
    setBookingError("");
    setBookingSubmitting(true);
    try {
      const booking = await apiRequest<Booking>("bookings", {
        method: "POST",
        body: JSON.stringify({
          packageId: selectedPackage.id,
          scheduledAt: new Date(scheduledAt).toISOString(),
          address: address.trim(),
          description: description.trim().slice(0, 1000),
        }),
      });
      setBookingMessage(`Đã gửi yêu cầu cho ${booking.providerName}.`);
      toast.success("Đã gửi yêu cầu đặt lịch.");
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
      const profile = await apiRequest<CustomerProfile>("customer/profile").catch(() => null);
      setSelectedProvider(provider);
      setSelectedPackage(servicePackage);
      setAddress(profile?.defaultAddress ?? "");
    } catch (cause) {
      setBookingError(cause instanceof Error ? cause.message : "Không tải được nhà cung cấp của gói.");
    }
  }

  return (
    <main className={styles.style1}>
      <SiteHeader />
      <section className={styles.style2}>
        <div className={styles.style3}>
          <Link href="/" className={styles.style4}><ArrowLeft className="mr-1 inline h-4 w-4" /> Trang chủ</Link>
          <div className={styles.style5}>
            <div>
              <p className={styles.style6}>Dịch vụ Nhà Tốt</p>
              <h1 className={styles.style7}>{category.name}</h1>
              <p className={styles.style8}>{category.description} Chọn nhà cung cấp hoặc gói dịch vụ, rồi chọn thời gian hẹn.</p>
            </div>
            <span className={styles.style9} aria-hidden="true">
              {category.icon === "repair" ? <Wrench /> : <Sparkles />}
            </span>
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

        <div className={styles.toolbar}>
          <div className={styles.toolbarSearch}>
            <span className={styles.toolbarIcon} aria-hidden="true"><Search /></span>
            <input type="text" placeholder="Tìm nhà cung cấp…" className={styles.toolbarInput} />
          </div>
          <div className={styles.toolbarFilter}>
            <label className={styles.toolbarLabel}>Quận/huyện</label>
            <select className={styles.toolbarSelect} defaultValue="Tất cả">
              <option>Tất cả</option>
              <option>Quận 1</option>
              <option>Quận 2</option>
              <option>Quận 3</option>
            </select>
          </div>
          <div className={styles.toolbarSort}>
            <label className={styles.toolbarLabel}>Sắp xếp</label>
            <select className={styles.toolbarSelect} defaultValue="Đánh giá cao nhất">
              <option>Đánh giá cao nhất</option>
              <option>Nhiều đánh giá nhất</option>
              <option>Gần nhất</option>
            </select>
          </div>
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
                <div className={styles.thumbnail}>
                  <img className={styles.style19} src={provider.imageUrl} alt={`Hình ảnh ${provider.fullName}`} loading="lazy" />
                </div>
                <div className={styles.style20}>
                  <div className={styles.cardHeader}>
                    <h3 className={styles.style22}>{provider.fullName}</h3>
                    <span className={styles.ratingPill} aria-label={`Đánh giá ${provider.rating} trên 5 sao`}>★ {provider.rating.toFixed(1)}</span>
                  </div>
                  <p className={styles.style24}>{provider.reviewCount} đánh giá{provider.demoData ? " · hồ sơ mẫu" : ""}</p>
                  <p className={styles.style25}><span className={styles.pinIcon} aria-hidden="true"><MapPin /></span> {provider.address}</p>
                  <p className={styles.style26}>{provider.description}</p>
                  <button type="button" className={styles.style27} onClick={async () => {
                    setSelectedProvider(provider);
                    setSelectedPackage(null);
                    setBookingError("");
                    setBookingMessage("");
                    try {
                      const profile = await apiRequest<CustomerProfile>("customer/profile");
                      setAddress(profile.defaultAddress);
                    } catch {
                      setAddress("");
                    }
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
              <button autoFocus type="button" className={styles.style42} onClick={closeBooking} aria-label="Đóng"><X /></button>
            </div>
            <form className={styles.style44} onSubmit={(event) => void prepareBooking(event)}>
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
                  {selectedProvider && selectedPackage && (
                    <ConfirmDialog
                      open={confirmOpen}
                      title="Gửi yêu cầu đặt lịch?"
                      description="Kiểm tra lại thông tin dưới đây trước khi gửi."
                      confirmLabel="Gửi yêu cầu"
                      loading={bookingSubmitting}
                      onCancel={() => setConfirmOpen(false)}
                      onConfirm={() => void confirmBooking()}
                    >
                      <dl className="grid gap-3 rounded-md border border-border bg-bg p-4 text-sm">
                        <div><dt className="text-muted">Gói dịch vụ</dt><dd className="mt-1 font-bold">{selectedPackage.name}</dd></div>
                        <div><dt className="text-muted">Giá · thời lượng</dt><dd className="mt-1 font-semibold">{new Intl.NumberFormat("vi-VN").format(selectedPackage.price)} ₫ · {selectedPackage.durationMinutes} phút</dd></div>
                        <div><dt className="text-muted">Thời gian</dt><dd className="mt-1 font-semibold">{scheduledAt ? new Date(scheduledAt).toLocaleString("vi-VN") : ""}</dd></div>
                        <div><dt className="text-muted">Địa chỉ</dt><dd className="mt-1 font-semibold">{address}</dd></div>
                        <div><dt className="text-muted">Mô tả</dt><dd className="mt-1">{description}</dd></div>
                      </dl>
                    </ConfirmDialog>
                  )}
                  <div className={styles.style56}>
                    <label className={styles.style45}>
                      Thời gian hẹn
                      <input required min={localDateTimeMinimum()} type="datetime-local" value={scheduledAt} onChange={(event) => setScheduledAt(event.target.value)} className={styles.style49} />
                    </label>
                  </div>
                  <label className={styles.style45}>
                    Địa chỉ thực hiện
                    <input required maxLength={500} value={address} onChange={(event) => setAddress(event.target.value)} className={styles.style49} />
                  </label>
                  <label className={styles.style45}>
                    Mô tả công việc
                    <textarea required maxLength={1000} rows={3} value={description} onChange={(event) => setDescription(event.target.value)} className={styles.style49} placeholder="Ví dụ: Dọn phòng khách, lau cửa sổ..." />
                  </label>
                  <button disabled={bookingSubmitting || !selectedPackage} className={styles.style61}>
                    {bookingSubmitting ? "Đang gửi…" : "Tiếp tục"}
                  </button>
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
