"use client";

/* eslint-disable @next/next/no-img-element */
import Link from "next/link";
import { FormEvent, useEffect, useState } from "react";
import { Booking, PageResult, Provider, ServiceCategory, ServicePackage, apiRequest } from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";

function localDateTimeMinimum() {
  const date = new Date(Date.now() + 60 * 60 * 1000);
  date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
  return date.toISOString().slice(0, 16);
}

export function HomePage() {
  const [categories, setCategories] = useState<ServiceCategory[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<ServiceCategory | null>(null);
  const [providers, setProviders] = useState<Provider[]>([]);
  const [packages, setPackages] = useState<ServicePackage[]>([]);
  const [loadingCategories, setLoadingCategories] = useState(true);
  const [loadingProviders, setLoadingProviders] = useState(false);
  const [loadingPackages, setLoadingPackages] = useState(false);
  const [packageError, setPackageError] = useState("");
  const [error, setError] = useState("");
  const [selectedProvider, setSelectedProvider] = useState<Provider | null>(null);
  const [selectedPackage, setSelectedPackage] = useState<ServicePackage | null>(null);
  const [bookingError, setBookingError] = useState("");
  const [bookingMessage, setBookingMessage] = useState("");
  const [bookingSubmitting, setBookingSubmitting] = useState(false);
  const [bookingForm, setBookingForm] = useState({ scheduledAt: "", address: "", description: "" });

  useEffect(() => {
    apiRequest<ServiceCategory[]>("services/categories")
      .then(setCategories)
      .catch((cause: Error) => setError(cause.message))
      .finally(() => setLoadingCategories(false));
  }, []);

  async function chooseCategory(category: ServiceCategory) {
    setSelectedCategory(category);
    setProviders([]);
    setPackages([]);
    setError("");
    setPackageError("");
    setLoadingProviders(true);
    setLoadingPackages(true);
    const [providerResult, packageResult] = await Promise.allSettled([
      apiRequest<Provider[]>(`services/${category.id}/providers?limit=10`),
      apiRequest<PageResult<ServicePackage>>(`catalog/packages?categoryId=${encodeURIComponent(category.id)}&page=0&size=20`),
    ]);
    if (providerResult.status === "fulfilled") setProviders(providerResult.value);
    else setError(providerResult.reason instanceof Error ? providerResult.reason.message : "Không tải được nhà cung cấp.");
    if (packageResult.status === "fulfilled") setPackages(packageResult.value.content);
    else setPackageError(packageResult.reason instanceof Error ? packageResult.reason.message : "Không tải được gói dịch vụ.");
    setLoadingProviders(false);
    setLoadingPackages(false);
  }

  async function submitBooking(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedProvider || !selectedCategory || !selectedPackage) return;
    setBookingError("");
    setBookingSubmitting(true);
    try {
      const booking = await apiRequest<Booking>("bookings", {
        method: "POST",
        body: JSON.stringify({
          packageId: selectedPackage.id,
          scheduledAt: new Date(bookingForm.scheduledAt).toISOString(),
          address: bookingForm.address,
          description: bookingForm.description,
        }),
      });
      setBookingMessage(`Đã gửi yêu cầu ${booking.id.slice(0, 8)} cho ${booking.providerName}.`);
      setSelectedProvider(null);
      setSelectedPackage(null);
      setBookingForm({ scheduledAt: "", address: "", description: "" });
    } catch (cause) {
      setBookingError(cause instanceof Error ? cause.message : "Không gửi được yêu cầu đặt lịch.");
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
    <main className="min-h-screen">
      <SiteHeader />
      <section className="relative overflow-hidden">
        <div className="pointer-events-none absolute -right-36 -top-32 size-[34rem] rounded-full bg-[#e6efe7] blur-3xl" />
        <div className="relative mx-auto grid max-w-7xl gap-10 px-5 py-16 sm:px-8 lg:grid-cols-[1.05fr_0.95fr] lg:px-10 lg:py-24">
          <div className="flex flex-col justify-center">
            <p className="mb-5 inline-flex w-fit items-center gap-2 rounded-full border border-[#d9e6da] bg-white/80 px-4 py-2 text-xs font-semibold uppercase tracking-[0.15em] text-[#356a55]">
              <span className="size-2 rounded-full bg-[#71a87b]" /> Chăm nhà nhẹ nhàng hơn
            </p>
            <h1 className="max-w-2xl text-5xl font-semibold leading-[1.08] tracking-[-0.045em] text-[#173c33] sm:text-6xl lg:text-7xl">
              Việc nhà để <span className="text-[#65916b]">người giỏi</span> lo.
            </h1>
            <p className="mt-6 max-w-xl text-lg leading-8 text-[#64736b]">
              Chọn dịch vụ, xem nhà cung cấp được khách hàng đánh giá tốt và gửi yêu cầu ngay tại nhà.
            </p>
            <a className="mt-8 inline-flex w-fit rounded-full bg-[#1f594a] px-7 py-3.5 text-sm font-semibold text-white transition hover:bg-[#18483b]" href="#services">
              Khám phá dịch vụ <span className="ml-2" aria-hidden="true">→</span>
            </a>
          </div>
          <div className="grid min-h-72 grid-cols-2 gap-4 rounded-[2rem] bg-[#dce8dc] p-5 sm:p-8">
            <div className="flex flex-col justify-between rounded-3xl bg-[#a9c5a5] p-6 text-[#173c33]">
              <span aria-hidden="true" className="text-4xl">⌂</span>
              <div><p className="text-sm">Không gian</p><p className="mt-1 text-xl font-semibold">Sạch &amp; gọn</p></div>
            </div>
            <div className="mt-8 flex flex-col justify-between rounded-3xl bg-[#f0dfbd] p-6 text-[#4e4938]">
              <span aria-hidden="true" className="text-4xl">✳</span>
              <div><p className="text-sm">Đặt lịch</p><p className="mt-1 text-xl font-semibold">Theo thời gian bạn</p></div>
            </div>
            <div className="col-span-2 flex items-center gap-4 rounded-2xl bg-[#234c3e] px-5 py-4 text-white">
              <span className="grid size-10 place-items-center rounded-xl bg-white/10" aria-hidden="true">✓</span>
              <p className="text-sm leading-6">Hồ sơ nhà cung cấp được xét duyệt trước khi hiển thị công khai.</p>
            </div>
          </div>
        </div>
      </section>

      <section id="services" className="bg-white py-16 sm:py-20">
        <div className="mx-auto max-w-7xl px-5 sm:px-8 lg:px-10">
          <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
            <div>
              <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#719071]">Dịch vụ cho ngôi nhà</p>
              <h2 className="mt-3 text-3xl font-semibold tracking-tight text-[#173c33] sm:text-4xl">Bạn đang cần giúp việc gì?</h2>
            </div>
            <p className="max-w-md text-sm leading-6 text-[#6c7971]">Chọn nhóm dịch vụ để xem tối đa 10 nhà cung cấp, xếp theo đánh giá.</p>
          </div>

          {loadingCategories ? (
            <p className="mt-8 text-sm text-[#64736b]">Đang tải danh mục dịch vụ…</p>
          ) : error && categories.length === 0 ? (
            <p role="alert" className="mt-8 rounded-2xl bg-[#fff1ec] p-4 text-sm text-[#9b3d28]">{error}</p>
          ) : (
            <div className="mt-8 grid gap-4 sm:grid-cols-2">
              {categories.map((category) => (
                <button
                  key={category.id}
                  type="button"
                  onClick={() => void chooseCategory(category)}
                  aria-pressed={selectedCategory?.id === category.id}
                  className={`rounded-3xl border p-6 text-left transition hover:-translate-y-0.5 hover:border-[#a9c5a5] ${selectedCategory?.id === category.id ? "border-[#4b7c58] bg-[#f1f7ef]" : "border-[#e8ece6] bg-[#fbfcfa]"}`}
                >
                  <span className="grid size-12 place-items-center rounded-2xl bg-[#e9f1e7] text-2xl text-[#477352]" aria-hidden="true">
                    {category.id === "cleaning" ? "⌂" : "＋"}
                  </span>
                  <span className="mt-5 block text-lg font-semibold text-[#23483a]">{category.name}</span>
                  <span className="mt-2 block text-sm leading-6 text-[#6c7971]">{category.description}</span>
                </button>
              ))}
            </div>
          )}

          {selectedCategory && (
            <div className="mt-14" id="providers">
              <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-end">
                <div>
                  <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#719071]">Nhà cung cấp</p>
                  <h3 className="mt-2 text-2xl font-semibold text-[#173c33]">{selectedCategory.name} gần bạn</h3>
                </div>
                <p className="text-sm text-[#718078]">Đang hiển thị tối đa 10 hồ sơ, ưu tiên đánh giá cao.</p>
              </div>
              {loadingProviders ? (
                <p className="mt-6 text-sm text-[#64736b]">Đang tìm nhà cung cấp…</p>
              ) : error ? (
                <p role="alert" className="mt-6 rounded-2xl bg-[#fff1ec] p-4 text-sm text-[#9b3d28]">{error}</p>
              ) : providers.length === 0 ? (
                <p className="mt-6 rounded-2xl bg-[#f5f8f3] p-5 text-sm text-[#64736b]">Chưa có nhà cung cấp được duyệt trong danh mục này.</p>
              ) : (
                <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">
                  {providers.slice(0, 10).map((provider) => (
                    <article key={provider.id} className="overflow-hidden rounded-3xl border border-[#e5eae3] bg-white shadow-sm">
                      <img className="h-40 w-full object-cover" src={provider.imageUrl} alt={`Hình ảnh ${provider.fullName}`} loading="lazy" />
                      <div className="p-4">
                        <div className="flex items-start justify-between gap-2">
                          <h4 className="font-semibold text-[#23483a]">{provider.fullName}</h4>
                          <span className="shrink-0 rounded-full bg-[#fff6dd] px-2 py-1 text-xs font-bold text-[#86651f]" aria-label={`Đánh giá ${provider.rating} trên 5 sao`}>
                            ★ {provider.rating.toFixed(1)}
                          </span>
                        </div>
                        <p className="mt-1 text-xs text-[#87938c]">{provider.reviewCount} đánh giá{provider.demoData ? " · hồ sơ mẫu" : ""}</p>
                        <p className="mt-3 min-h-10 text-xs leading-5 text-[#6c7971]">⌖ {provider.address}</p>
                        <p className="mt-2 line-clamp-2 min-h-10 text-xs leading-5 text-[#6c7971]">{provider.description}</p>
                        <button
                          type="button"
                          className="mt-4 w-full rounded-full bg-[#1f594a] px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-[#18483b]"
                          onClick={() => {
                            setSelectedProvider(provider);
                            setSelectedPackage(null);
                            setBookingError("");
                            setBookingMessage("");
                          }}
                        >
                          Chọn nhà cung cấp
                        </button>
                      </div>
                    </article>
                  ))}
                </div>
              )}
              {selectedCategory && (
                <section className="mt-14" aria-labelledby="packages-heading">
                  <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-end">
                    <div>
                      <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#719071]">Gói dịch vụ đã duyệt</p>
                      <h3 id="packages-heading" className="mt-2 text-2xl font-semibold text-[#173c33]">{selectedCategory.name} theo gói</h3>
                    </div>
                    <p className="text-sm text-[#718078]">Chỉ gói đang hoạt động và đã được Admin duyệt mới xuất hiện.</p>
                  </div>
                  {loadingPackages ? (
                    <p className="mt-6 text-sm text-[#64736b]">Đang tải gói dịch vụ…</p>
                  ) : packageError ? (
                    <p role="alert" className="mt-6 rounded-2xl bg-[#fff1ec] p-4 text-sm text-[#9b3d28]">{packageError}</p>
                  ) : packages.length === 0 ? (
                    <p className="mt-6 rounded-2xl bg-[#f5f8f3] p-5 text-sm text-[#64736b]">Chưa có gói dịch vụ được duyệt trong danh mục này.</p>
                  ) : (
                    <div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                      {packages.map((servicePackage) => (
                        <article key={servicePackage.id} className="rounded-3xl border border-[#e5eae3] bg-white p-5 shadow-sm">
                          <p className="text-xs font-semibold uppercase tracking-wide text-[#719071]">{selectedCategory.name}</p>
                          <h4 className="mt-2 text-lg font-semibold text-[#23483a]">{servicePackage.name}</h4>
                          <p className="mt-2 min-h-12 text-sm leading-6 text-[#6c7971]">{servicePackage.description}</p>
                          <p className="mt-4 text-xl font-bold text-[#1f594a]">{new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(servicePackage.price)}</p>
                          <p className="mt-1 text-xs text-[#87938c]">Thời lượng dự kiến: {servicePackage.durationMinutes} phút</p>
                          <p className="mt-3 text-sm text-[#52645e]">Nhà cung cấp: {servicePackage.providerPhone}</p>
                          <button
                            type="button"
                            onClick={() => void choosePackage(servicePackage)}
                            className="mt-4 w-full rounded-full bg-[#1f594a] px-4 py-2.5 text-sm font-semibold text-white hover:bg-[#18483b]"
                          >
                            Đặt gói này
                          </button>
                        </article>
                      ))}
                    </div>
                  )}
                </section>
              )}
            </div>
          )}
          {bookingMessage && <p role="status" className="mt-6 rounded-2xl bg-[#eaf4e9] p-4 text-sm text-[#28583d]">{bookingMessage} <Link className="font-semibold underline" href="/history">Xem lịch sử</Link></p>}
        </div>
      </section>

      <section className="px-5 py-16 sm:px-8 lg:px-10">
        <div className="mx-auto flex max-w-7xl flex-col justify-between gap-6 rounded-[2rem] bg-[#244d3f] px-7 py-9 text-white sm:px-10 lg:flex-row lg:items-center lg:px-14">
          <div>
            <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#b8d2b5]">Dành cho nhà cung cấp</p>
            <h2 className="mt-3 max-w-xl text-2xl font-semibold tracking-tight sm:text-3xl">Mang dịch vụ của bạn đến gần khách hàng hơn.</h2>
          </div>
          <Link className="inline-flex w-fit rounded-full bg-[#e9f0e2] px-6 py-3.5 text-sm font-semibold text-[#234b3c] transition hover:bg-white" href="/register?role=PROVIDER">
            Đăng ký làm đối tác <span className="ml-2" aria-hidden="true">→</span>
          </Link>
        </div>
      </section>

      <footer className="border-t border-[#e3e8e1] bg-white">
        <div className="mx-auto flex max-w-7xl flex-col gap-3 px-5 py-7 text-xs text-[#7b8980] sm:flex-row sm:justify-between lg:px-10">
          <p>© 2026 Nhà Tốt. Kết nối dịch vụ, chăm sóc tổ ấm.</p>
          <Link href="/admin/providers">Dành cho quản trị viên</Link>
        </div>
      </footer>

      {selectedProvider && (
        <div className="fixed inset-0 z-50 grid place-items-center overflow-y-auto bg-[#102820]/55 p-4" role="presentation" onMouseDown={(event) => {
          if (event.target === event.currentTarget) setSelectedProvider(null);
        }}>
          <section aria-labelledby="booking-title" aria-modal="true" role="dialog" className="my-8 w-full max-w-lg rounded-3xl bg-white p-6 shadow-2xl sm:p-8">
            <div className="flex items-start justify-between gap-4">
              <div>
                <p className="text-xs font-bold uppercase tracking-[0.16em] text-[#719071]">Gửi yêu cầu</p>
                <h2 id="booking-title" className="mt-2 text-2xl font-semibold text-[#173c33]">Đặt lịch với {selectedProvider.fullName}</h2>
              </div>
              <button className="grid size-9 shrink-0 place-items-center rounded-full bg-[#f1f5ef] text-[#355d49]" onClick={() => setSelectedProvider(null)} aria-label="Đóng">×</button>
            </div>
            <p className="mt-2 text-sm text-[#6c7971]">{selectedCategory?.name} · {selectedProvider.address}</p>
            <form className="mt-6 space-y-4" onSubmit={(event) => void submitBooking(event)}>
              <label className="block text-sm font-medium text-[#315246]">
                Gói dịch vụ
                <select
                  required
                  value={selectedPackage?.id ?? ""}
                  onChange={(event) => setSelectedPackage(
                    packages.find((item) => item.id === event.target.value) ?? null,
                  )}
                  className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3 text-[#173c33]"
                >
                  <option value="">Chọn gói đã được duyệt</option>
                  {packages.filter((item) => item.providerId === selectedProvider.id).map((item) => (
                    <option key={item.id} value={item.id}>
                      {item.name} · {new Intl.NumberFormat("vi-VN").format(item.price)} ₫
                    </option>
                  ))}
                </select>
                {selectedPackage && (
                  <span className="mt-1 block text-xs text-[#718078]">
                    Giá chốt tại thời điểm đặt: {new Intl.NumberFormat("vi-VN").format(selectedPackage.price)} ₫ · {selectedPackage.durationMinutes} phút
                  </span>
                )}
              </label>
              <label className="block text-sm font-medium text-[#315246]">
                Thời gian mong muốn
                <input required min={localDateTimeMinimum()} type="datetime-local" value={bookingForm.scheduledAt} onChange={(event) => setBookingForm({ ...bookingForm, scheduledAt: event.target.value })} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3 text-[#173c33] outline-none focus:border-[#4b7c58]" />
              </label>
              <label className="block text-sm font-medium text-[#315246]">
                Địa chỉ thực hiện
                <input required maxLength={500} value={bookingForm.address} onChange={(event) => setBookingForm({ ...bookingForm, address: event.target.value })} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3 text-[#173c33] outline-none focus:border-[#4b7c58]" placeholder="Số nhà, đường, quận/huyện" />
              </label>
              <label className="block text-sm font-medium text-[#315246]">
                Mô tả công việc
                <textarea required maxLength={1000} rows={3} value={bookingForm.description} onChange={(event) => setBookingForm({ ...bookingForm, description: event.target.value })} className="mt-2 w-full resize-y rounded-xl border border-[#dce5dc] px-4 py-3 text-[#173c33] outline-none focus:border-[#4b7c58]" placeholder="Chia sẻ các thông tin cần thiết…" />
              </label>
              {bookingError && <p role="alert" className="rounded-xl bg-[#fff1ec] p-3 text-sm text-[#9b3d28]">{bookingError} {bookingError.includes("đăng nhập") && <Link className="font-semibold underline" href="/login">Đăng nhập</Link>}</p>}
              <button disabled={bookingSubmitting || !selectedPackage} className="w-full rounded-full bg-[#1f594a] px-6 py-3.5 text-sm font-semibold text-white hover:bg-[#18483b] disabled:opacity-60">
                {bookingSubmitting ? "Đang gửi…" : "Gửi yêu cầu đặt lịch"}
              </button>
              <p className="text-xs leading-5 text-[#87938c]">Yêu cầu được ghi vào lịch sử; nhà cung cấp sẽ xác nhận lịch hẹn.</p>
            </form>
          </section>
        </div>
      )}
    </main>
  );
}
