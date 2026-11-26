"use client";

import Link from "next/link";
import { FormEvent, useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useSearchParams } from "next/navigation";
import { ArrowLeft, CalendarDays, Check, Clock3, MapPin, Search, SlidersHorizontal, Sparkles, Star, Wrench, X } from "lucide-react";
import {
  Booking,
  CustomerProfile,
  PageResult,
  Provider,
  ServicePackage,
  apiRequest,
} from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";
import { Footer } from "@/shared/layout/footer";
import { ConfirmDialog } from "@/shared/ui/confirm-dialog";
import { Dialog } from "@/shared/ui/dialog";
import { FormField } from "@/shared/ui/form-field";
import { Input } from "@/shared/ui/input";
import { EmptyState, Skeleton } from "@/shared/ui/skeleton";
import { SafeImage } from "@/shared/ui/safe-image";
import { useToast } from "@/shared/ui/toast";
import { useSession } from "@/shared/state/session";

type Props = { service: "cleaning" | "repair" };

const serviceDetails = {
  cleaning: { name: "Dọn dẹp nhà", description: "Định kỳ, tổng vệ sinh và chăm sóc không gian sống." },
  repair: { name: "Sửa chữa gia đình", description: "Sửa chữa thiết bị và khu vực trong gia đình." },
};

function minimumDateTime() {
  const date = new Date(Date.now() + 60 * 60 * 1000);
  date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
  return date.toISOString().slice(0, 16);
}

export function ServiceCatalogPage({ service }: Props) {
  const searchParams = useSearchParams();
  const toast = useToast();
  const { account, loading: sessionLoading, refresh } = useSession();
  const category = serviceDetails[service];
  const servicePath = `/services/${service}`;
  const [providerPage, setProviderPage] = useState(0);
  const [providerResult, setProviderResult] = useState<PageResult<Provider> | null>(null);
  const [packages, setPackages] = useState<ServicePackage[]>([]);
  const [packagesLoading, setPackagesLoading] = useState(true);
  const [packagesError, setPackagesError] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState(searchParams.get("q") ?? "");
  const [sort, setSort] = useState("rating");
  const [selectedProvider, setSelectedProvider] = useState<Provider | null>(null);
  const [selectedPackage, setSelectedPackage] = useState<ServicePackage | null>(null);
  const [scheduledAt, setScheduledAt] = useState("");
  const [address, setAddress] = useState("");
  const [description, setDescription] = useState("");
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [authPromptOpen, setAuthPromptOpen] = useState(false);
  const [bookingError, setBookingError] = useState("");
  const [bookingSubmitting, setBookingSubmitting] = useState(false);
  const bookingDialogRef = useRef<HTMLElement>(null);
  const closeButtonRef = useRef<HTMLButtonElement>(null);
  const bookingOpen = Boolean(selectedProvider);

  const closeBooking = useCallback(() => {
    setSelectedProvider(null);
    setSelectedPackage(null);
    setScheduledAt("");
    setAddress("");
    setDescription("");
    setConfirmOpen(false);
  }, []);

  useEffect(() => {
    let active = true;
    apiRequest<PageResult<Provider>>(`services/${service}/providers/page?page=${providerPage}&limit=12`)
      .then((result) => { if (active) setProviderResult(result); })
      .catch((cause: Error) => { if (active) setError(cause.message); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [service, providerPage]);

  useEffect(() => {
    let active = true;
    apiRequest<PageResult<ServicePackage>>(
      `catalog/packages?categoryId=${encodeURIComponent(service)}&page=0&size=8&sort=price&direction=ASC`,
    )
      .then((result) => { if (active) setPackages(result.content); })
      .catch((cause: Error) => { if (active) setPackagesError(cause.message); })
      .finally(() => { if (active) setPackagesLoading(false); });
    return () => { active = false; };
  }, [service]);

  useEffect(() => {
    if (!bookingOpen) return;
    const previousOverflow = document.body.style.overflow;
    const previouslyFocused = document.activeElement instanceof HTMLElement
      ? document.activeElement
      : null;
    document.body.style.overflow = "hidden";
    closeButtonRef.current?.focus();

    function trapFocus(event: KeyboardEvent) {
      if (event.key === "Escape" && !confirmOpen) {
        event.preventDefault();
        closeBooking();
        return;
      }
      if (event.key !== "Tab" || confirmOpen) return;

      const focusable = bookingDialogRef.current?.querySelectorAll<HTMLElement>(
        'a[href], button:not([disabled]), input:not([disabled]), textarea:not([disabled]), select:not([disabled]), [tabindex]:not([tabindex="-1"])',
      );
      if (!focusable?.length) {
        event.preventDefault();
        bookingDialogRef.current?.focus();
        return;
      }

      const first = focusable[0];
      const last = focusable[focusable.length - 1];
      if (event.shiftKey && document.activeElement === first) {
        event.preventDefault();
        last.focus();
      } else if (!event.shiftKey && document.activeElement === last) {
        event.preventDefault();
        first.focus();
      }
    }

    window.addEventListener("keydown", trapFocus);
    return () => {
      window.removeEventListener("keydown", trapFocus);
      document.body.style.overflow = previousOverflow;
      previouslyFocused?.focus();
    };
  }, [bookingOpen, closeBooking, confirmOpen]);

  const providers = useMemo(() => {
    const value = search.trim().toLowerCase();
    const filtered = (providerResult?.content ?? []).filter((provider) =>
      !value
      || provider.fullName.toLowerCase().includes(value)
      || provider.address.toLowerCase().includes(value)
      || provider.description.toLowerCase().includes(value),
    );
    if (sort === "rating") return [...filtered].sort((a, b) => b.rating - a.rating);
    if (sort === "reviews") return [...filtered].sort((a, b) => b.reviewCount - a.reviewCount);
    return [...filtered].sort((a, b) => a.fullName.localeCompare(b.fullName, "vi"));
  }, [providerResult, search, sort]);

  const visiblePackages = useMemo(() => {
    const value = search.trim().toLowerCase();
    return packages.filter((item) =>
      item.providerId === selectedProvider?.id
      && (!value
        || item.name.toLowerCase().includes(value)
        || item.description.toLowerCase().includes(value)),
    );
  }, [packages, search, selectedProvider]);

  function submitSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
  }

  function deselectProvider() {
    closeBooking();
  }

  function prepareBooking(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedProvider || !selectedPackage || !scheduledAt || !address.trim() || !description.trim()) {
      setBookingError("Vui lòng chọn gói và nhập đầy đủ thời gian, địa chỉ và mô tả công việc.");
      return;
    }
    setBookingError("");
    setConfirmOpen(true);
  }

  async function confirmBooking() {
    if (!selectedProvider || !selectedPackage || !scheduledAt) return;
    setBookingSubmitting(true);
    setBookingError("");
    try {
      await apiRequest<Booking>("bookings", {
        method: "POST",
        body: JSON.stringify({
          packageId: selectedPackage.id,
          scheduledAt: new Date(scheduledAt).toISOString(),
          address: address.trim(),
          description: description.trim().slice(0, 1000),
        }),
      });
      toast.success("Đã gửi yêu cầu đặt lịch.");
      closeBooking();
    } catch (cause) {
      setBookingError(cause instanceof Error ? cause.message : "Không thể tạo lịch hẹn.");
    } finally {
      setBookingSubmitting(false);
    }
  }

  async function chooseProvider(provider: Provider) {
    const currentAccount = sessionLoading ? await refresh() : account;
    if (!currentAccount) {
      setAuthPromptOpen(true);
      return;
    }

    closeBooking();
    setSelectedProvider(provider);
    setBookingError("");
    try {
      const profile = await apiRequest<CustomerProfile>("customer/profile").catch(() => null);
      setAddress(profile?.defaultAddress ?? "");
    } catch (cause) {
      setBookingError(cause instanceof Error ? cause.message : "Không tải được hồ sơ khách hàng.");
    }
  }

  function choosePackage(item: ServicePackage) {
    if (!selectedProvider || item.providerId !== selectedProvider.id) return;
    setSelectedPackage(item);
    setBookingError("");
  }

  return (
    <main id="main-content" className="min-h-screen bg-bg">
      <SiteHeader />

      <section className="border-b border-border bg-surface">
        <div className="mx-auto max-w-[1280px] px-6 py-8">
          <Link href="/" className="inline-flex min-h-11 items-center gap-2 text-sm font-semibold text-muted hover:text-brand-700">
            <ArrowLeft className="h-4 w-4" /> Trang chủ
          </Link>
          <div className="mt-4 flex items-end justify-between">
            <div>
              <p className="text-sm font-bold text-brand-600">Danh mục dịch vụ</p>
              <h1 className="mt-2 text-4xl font-bold text-text">{category.name}</h1>
              <p className="mt-3 max-w-2xl text-sm leading-7 text-muted">{category.description}</p>
            </div>
            <span className="grid h-16 w-16 place-items-center rounded-lg bg-brand-50 text-brand-700">
              {service === "repair" ? <Wrench className="h-8 w-8" /> : <Sparkles className="h-8 w-8" />}
            </span>
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-[1280px] px-6 py-8">
        <div className="grid grid-cols-[220px_minmax(0,1fr)] items-start gap-6">
          <aside className="sticky top-[120px] rounded-lg border border-border bg-surface p-5">
            <div className="flex items-center gap-2 text-sm font-bold text-text">
              <SlidersHorizontal className="h-4 w-4" /> Bộ lọc
            </div>
            <form onSubmit={submitSearch} className="mt-5">
              <label htmlFor="service-search" className="text-xs font-bold text-muted">Từ khóa</label>
              <div className="mt-2 flex h-11 items-center rounded-md border border-border bg-surface">
                <Search className="ml-3 h-4 w-4 text-muted" />
                <input
                  id="service-search"
                  value={search}
                  onChange={(event) => setSearch(event.target.value)}
                  placeholder="Tên hoặc khu vực"
                  className="h-full min-w-0 flex-1 px-2 text-sm outline-none"
                />
              </div>
            </form>
            <label htmlFor="service-sort" className="mt-5 block text-xs font-bold text-muted">Sắp xếp</label>
            <select
              id="service-sort"
              value={sort}
              onChange={(event) => setSort(event.target.value)}
              className="mt-2 h-11 w-full rounded-md border border-border bg-surface px-3 text-sm outline-none"
            >
              <option value="rating">Đánh giá cao nhất</option>
              <option value="reviews">Nhiều đánh giá nhất</option>
              <option value="name">Tên A-Z</option>
            </select>
            <button type="button" onClick={() => { setSearch(""); setSort("rating"); }} className="mt-4 h-11 w-full rounded-md border border-border text-sm font-semibold text-text hover:bg-brand-50">
              Đặt lại
            </button>
          </aside>

          <div className="min-w-0">
            <div className="flex items-end justify-between">
              <div>
                <p className="text-sm font-bold text-brand-600">Nhà cung cấp</p>
                <h2 className="mt-1 text-2xl font-bold text-text">{category.name} gần bạn</h2>
              </div>
              <p className="text-sm text-muted">{providers.length} kết quả trên trang</p>
            </div>

            {error && <p role="alert" className="mt-5 rounded-md border border-danger/20 bg-danger-bg p-4 text-sm text-danger">{error}</p>}
            {loading ? (
              <div className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
                {[1, 2, 3].map((item) => <Skeleton key={item} className="h-72" />)}
              </div>
            ) : providers.length === 0 ? (
              <div className="mt-5"><EmptyState title="Không tìm thấy nhà cung cấp" description="Thử từ khóa khác hoặc đặt lại bộ lọc." /></div>
            ) : (
              <div className="mt-5 grid grid-cols-1 items-start gap-4 md:grid-cols-2 lg:grid-cols-3">
                {providers.map((provider) => {
                  const isSelected = selectedProvider?.id === provider.id;
                  return (
                    <article key={provider.id} className={`overflow-hidden rounded-lg border bg-surface ${isSelected ? "border-brand-600 ring-2 ring-brand-600" : "border-border"}`}>
                      <Link href={`/providers/${provider.id}`} className="block aspect-[16/10] bg-brand-50">
                        <SafeImage src={provider.imageUrl} alt={provider.fullName} className="h-full w-full" />
                      </Link>
                      <div className="p-5">
                        <div className="flex items-start justify-between gap-3">
                          <Link href={`/providers/${provider.id}`} className="font-bold text-text hover:text-brand-700">{provider.fullName}</Link>
                          <span className="flex items-center gap-1 rounded-full bg-warning-bg px-2 py-1 text-xs font-bold text-warning">
                            <Star className="h-3.5 w-3.5 fill-current" /> {provider.rating.toFixed(1)}
                          </span>
                        </div>
                        <p className="mt-2 flex items-start gap-2 text-sm leading-6 text-muted">
                          <MapPin className="mt-1 h-4 w-4 shrink-0" /> {provider.address}
                        </p>
                        <p className="mt-3 line-clamp-2 min-h-12 text-sm leading-6 text-muted">{provider.description}</p>
                        <button
                          type="button"
                          onClick={() => {
                            if (isSelected) {
                              deselectProvider();
                              return;
                            }
                            void chooseProvider(provider);
                          }}
                          aria-pressed={isSelected}
                          className={`mt-4 flex h-11 w-full items-center justify-center gap-2 rounded-md border text-sm font-bold ${isSelected ? "border-brand-600 bg-brand-50 text-brand-700" : "border-brand-600 text-brand-700 hover:bg-brand-50"}`}
                        >
                          {isSelected ? <><Check className="h-4 w-4" /> Đang chọn</> : "Chọn nhà cung cấp"}
                        </button>
                      </div>
                    </article>
                  );
                })}
              </div>
            )}

            {providerResult && providerResult.totalPages > 1 && (
              <nav aria-label="Phân trang nhà cung cấp" className="mt-6 flex items-center justify-center gap-3">
                <button type="button" disabled={providerPage === 0} onClick={() => setProviderPage((value) => value - 1)} className="h-11 rounded-md border border-border px-4 text-sm font-semibold disabled:opacity-50">Trước</button>
                <span className="text-sm text-muted">Trang {providerPage + 1}/{providerResult.totalPages}</span>
                <button type="button" disabled={providerPage + 1 >= providerResult.totalPages} onClick={() => setProviderPage((value) => value + 1)} className="h-11 rounded-md border border-border px-4 text-sm font-semibold disabled:opacity-50">Sau</button>
              </nav>
            )}
          </div>
        </div>

      </section>

      {selectedProvider && (
        <div
          className="fixed inset-0 z-50 overflow-y-auto bg-text/50 p-4 sm:p-6"
          onMouseDown={(event) => {
            if (event.target === event.currentTarget) closeBooking();
          }}
        >
          <section
            ref={bookingDialogRef}
            role="dialog"
            aria-modal="true"
            aria-labelledby="booking-dialog-title"
            tabIndex={-1}
            lang="vi-VN"
            className="mx-auto my-2 grid max-h-[calc(100vh-32px)] w-full max-w-5xl overflow-hidden rounded-lg bg-surface shadow-md lg:my-0 lg:grid-cols-[minmax(0,0.85fr)_minmax(0,1.15fr)]"
          >
            <aside className="flex min-h-0 flex-col bg-brand-50">
              <SafeImage src={selectedProvider.imageUrl} alt={selectedProvider.fullName} className="aspect-[16/7] w-full lg:aspect-[4/5] lg:flex-1" />
              <div className="grid gap-2 p-5">
                <p className="text-sm font-semibold text-brand-700">Nhà cung cấp</p>
                <h3 className="text-lg font-bold text-text">{selectedProvider.fullName}</h3>
                <p className="text-sm text-muted">{selectedPackage?.name ?? "Chưa chọn gói dịch vụ"}</p>
                {selectedPackage && (
                  <p className="whitespace-nowrap text-xl font-bold text-brand-700">
                    {new Intl.NumberFormat("vi-VN").format(selectedPackage.price)} ₫
                  </p>
                )}
              </div>
            </aside>
            <div className="min-h-0 overflow-y-auto">
              <header className="flex items-start justify-between border-b border-border px-6 py-5">
                <div>
                  <p className="text-sm font-bold text-brand-600">Gửi yêu cầu</p>
                  <h2 id="booking-dialog-title" className="mt-1 text-xl font-bold text-text">Đặt lịch dịch vụ</h2>
                </div>
                <button
                  ref={closeButtonRef}
                  type="button"
                  onClick={closeBooking}
                  aria-label="Đóng cửa sổ đặt lịch"
                  className="grid h-10 w-10 place-items-center rounded-md text-muted hover:bg-brand-50 hover:text-brand-700"
                >
                  <X className="h-5 w-5" />
                </button>
              </header>
              <form onSubmit={prepareBooking} className="grid gap-5 p-6">
                <section aria-labelledby="booking-packages-title" className="grid gap-3">
                  <h3 id="booking-packages-title" className="font-bold text-text">Chọn gói dịch vụ</h3>
                  {packagesError ? (
                    <p role="alert" className="rounded-md border border-danger/20 bg-danger-bg p-4 text-sm text-danger">{packagesError}</p>
                  ) : packagesLoading ? (
                    <div className="grid grid-cols-1 items-stretch gap-4 sm:grid-cols-2" role="status" aria-label="Đang tải gói dịch vụ">
                      {[1, 2].map((item) => <Skeleton key={item} className="h-64" />)}
                    </div>
                  ) : visiblePackages.length === 0 ? (
                    <EmptyState title="Nhà cung cấp chưa có gói được duyệt" description="Hãy chọn nhà cung cấp khác hoặc quay lại sau." />
                  ) : (
                    <div className="grid grid-cols-1 items-stretch gap-4 sm:grid-cols-2">
                      {visiblePackages.map((item) => {
                        const isPackageSelected = selectedPackage?.id === item.id;
                        return (
                          <article key={item.id} className={`flex h-full flex-col rounded-lg border p-4 ${isPackageSelected ? "border-brand-600 bg-brand-50 ring-1 ring-brand-600" : "border-border bg-surface"}`}>
                            <p className="text-sm font-bold text-brand-600">{category.name}</p>
                            <h4 className="mt-2 text-base font-bold text-text">{item.name}</h4>
                            <p className="mt-2 line-clamp-2 min-h-12 text-sm leading-6 text-muted">{item.description}</p>
                            <p className="mt-3 whitespace-nowrap text-xl font-bold text-brand-700">
                              {new Intl.NumberFormat("vi-VN").format(item.price)} ₫
                            </p>
                            <p className="mt-2 flex items-center gap-2 text-sm text-muted">
                              <Clock3 className="h-4 w-4" /> {item.durationMinutes} phút
                            </p>
                            <button
                              type="button"
                              onClick={() => choosePackage(item)}
                              aria-pressed={isPackageSelected}
                              className={`mt-auto min-h-11 w-full rounded-md text-sm font-bold ${isPackageSelected ? "border border-brand-600 bg-surface text-brand-700" : "bg-brand-600 text-white hover:bg-brand-700"}`}
                            >
                              {isPackageSelected ? "Đã chọn gói này" : "Chọn gói này"}
                            </button>
                          </article>
                        );
                      })}
                    </div>
                  )}
                </section>
                <FormField htmlFor="scheduledAt" label="Thời gian hẹn">
                  <Input
                    id="scheduledAt"
                    required
                    lang="vi-VN"
                    type="datetime-local"
                    min={minimumDateTime()}
                    value={scheduledAt}
                    icon={<CalendarDays />}
                    onChange={(event) => setScheduledAt(event.target.value)}
                  />
                </FormField>
                <FormField htmlFor="bookingAddress" label="Địa chỉ thực hiện">
                  <Input
                    id="bookingAddress"
                    required
                    maxLength={500}
                    value={address}
                    icon={<MapPin />}
                    onChange={(event) => setAddress(event.target.value)}
                  />
                </FormField>
                <FormField htmlFor="bookingDescription" label="Mô tả công việc">
                  <div className="group relative">
                    <Sparkles aria-hidden="true" className="pointer-events-none absolute left-3.5 top-3.5 h-5 w-5 text-muted transition-colors group-focus-within:text-brand-600" />
                    <textarea
                      id="bookingDescription"
                      required
                      maxLength={1000}
                      rows={3}
                      value={description}
                      onChange={(event) => setDescription(event.target.value)}
                      className="w-full resize-y rounded-md border border-border bg-surface py-3 pl-11 pr-3.5 text-base text-text outline-none transition focus:border-brand-500 focus:ring-2 focus:ring-brand-500/20"
                    />
                  </div>
                </FormField>
                {bookingError && <p role="alert" className="rounded-md bg-danger-bg p-3 text-sm text-danger">{bookingError}</p>}
                <div>
                  <button
                    type="submit"
                    disabled={bookingSubmitting || !selectedPackage || !scheduledAt || !address.trim() || !description.trim()}
                    className="h-12 w-full rounded-md bg-brand-600 text-base font-bold text-white hover:bg-brand-700 disabled:cursor-not-allowed disabled:opacity-60"
                  >
                    {bookingSubmitting ? "Đang gửi..." : "Tiếp tục"}
                  </button>
                  {(!selectedPackage || !scheduledAt || !address.trim() || !description.trim()) && (
                    <p className="mt-2 text-sm text-muted">Chọn gói, nhập thời gian, địa chỉ và mô tả công việc để tiếp tục.</p>
                  )}
                  <button type="button" onClick={closeBooking} className="mt-2 min-h-11 w-full rounded-md border border-border text-sm font-semibold text-text hover:bg-bg">
                    Hủy
                  </button>
                </div>
              </form>
            </div>
          </section>
        </div>
      )}

      {selectedProvider && selectedPackage && (
        <ConfirmDialog
          open={confirmOpen}
          title="Gửi yêu cầu đặt lịch?"
          description="Kiểm tra lại gói, thời gian, địa chỉ và mô tả công việc."
          confirmLabel="Gửi yêu cầu"
          loading={bookingSubmitting}
          onCancel={() => setConfirmOpen(false)}
          onConfirm={() => void confirmBooking()}
        >
          <dl className="grid gap-3 rounded-md border border-border bg-bg p-4 text-sm">
            <div><dt className="text-muted">Gói</dt><dd className="mt-1 font-bold">{selectedPackage.name}</dd></div>
            <div><dt className="text-muted">Thời gian</dt><dd className="mt-1">{scheduledAt ? new Date(scheduledAt).toLocaleString("vi-VN") : ""}</dd></div>
            <div><dt className="text-muted">Địa chỉ</dt><dd className="mt-1">{address}</dd></div>
          </dl>
        </ConfirmDialog>
      )}

      <Dialog
        open={authPromptOpen}
        id="booking-auth-dialog"
        title="Đăng nhập để đặt lịch"
        description="Bạn cần đăng nhập hoặc tạo tài khoản khách hàng trước khi đặt lịch."
        onClose={() => setAuthPromptOpen(false)}
      >
        <div className="grid gap-4">
          <div className="grid gap-3 sm:grid-cols-2">
            <Link
              href={`/login?next=${encodeURIComponent(servicePath)}`}
              className="flex min-h-11 items-center justify-center rounded-md bg-brand-600 px-4 text-sm font-bold text-white hover:bg-brand-700"
            >
              Đăng nhập
            </Link>
            <Link
              href={`/register?role=CUSTOMER&next=${encodeURIComponent(servicePath)}`}
              className="flex min-h-11 items-center justify-center rounded-md border border-brand-600 px-4 text-sm font-bold text-brand-700 hover:bg-brand-50"
            >
              Đăng ký khách hàng
            </Link>
          </div>
          <button
            type="button"
            onClick={() => setAuthPromptOpen(false)}
            className="min-h-11 rounded-md border border-border px-4 text-sm font-semibold text-muted hover:bg-bg"
          >
            Để sau
          </button>
        </div>
      </Dialog>

      <Footer />
    </main>
  );
}
