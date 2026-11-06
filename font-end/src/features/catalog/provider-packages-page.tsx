"use client";

import Link from "next/link";
import { FormEvent, useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Account, PageResult, ServiceCategory, ServicePackage, apiRequest } from "@/lib/api";
import { SiteHeader } from "@/shared/components/site-header";

type PackageForm = {
  categoryId: string;
  name: string;
  description: string;
  price: string;
  durationMinutes: string;
  providerPhone: string;
};

const emptyForm: PackageForm = {
  categoryId: "",
  name: "",
  description: "",
  price: "",
  durationMinutes: "",
  providerPhone: "",
};

const statusLabels: Record<ServicePackage["status"], string> = {
  DRAFT: "Bản nháp",
  PENDING_APPROVAL: "Đang chờ duyệt",
  APPROVED: "Đã duyệt",
  REJECTED: "Bị từ chối",
  INACTIVE: "Đã ngừng hoạt động",
};

export function ProviderPackagesPage() {
  const router = useRouter();
  const [categories, setCategories] = useState<ServiceCategory[]>([]);
  const [packages, setPackages] = useState<ServicePackage[]>([]);
  const [editing, setEditing] = useState<ServicePackage | null>(null);
  const [form, setForm] = useState<PackageForm>(emptyForm);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    setError("");
    try {
      const account = await apiRequest<Account>("me");
      if (account.role !== "PROVIDER") {
        router.replace("/login?next=/provider/packages");
        return;
      }
      const [categoryList, result] = await Promise.all([
        apiRequest<ServiceCategory[]>("services/categories"),
        apiRequest<PageResult<ServicePackage>>("provider/packages/my?page=0&size=100"),
      ]);
      setCategories(categoryList);
      setPackages(result.content);
      setForm((current) => current.categoryId || !categoryList.length
        ? current
        : { ...current, categoryId: categoryList[0].id });
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không tải được gói dịch vụ.");
    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => { void Promise.resolve().then(load); }, [load]);

  function startEdit(servicePackage: ServicePackage) {
    setEditing(servicePackage);
    setForm({
      categoryId: servicePackage.categoryId,
      name: servicePackage.name,
      description: servicePackage.description,
      price: String(servicePackage.price),
      durationMinutes: String(servicePackage.durationMinutes),
      providerPhone: servicePackage.providerPhone,
    });
  }

  function resetForm() {
    setEditing(null);
    setForm({ ...emptyForm, categoryId: categories[0]?.id ?? "" });
  }

  async function save(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setSaving(true);
    const body = JSON.stringify({
      ...form,
      price: Number(form.price),
      durationMinutes: Number(form.durationMinutes),
      ...(editing ? { expectedVersion: editing.version } : {}),
    });
    try {
      await apiRequest<ServicePackage>(editing ? `provider/packages/${editing.id}` : "provider/packages", {
        method: editing ? "PUT" : "POST",
        body,
      });
      resetForm();
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không lưu được gói dịch vụ.");
    } finally {
      setSaving(false);
    }
  }

  async function submitForApproval(servicePackage: ServicePackage) {
    setError("");
    try {
      await apiRequest<ServicePackage>(`provider/packages/${servicePackage.id}/submit`, {
        method: "POST",
        body: JSON.stringify({ expectedVersion: servicePackage.version }),
      });
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không gửi duyệt được gói dịch vụ.");
    }
  }

  async function archive(servicePackage: ServicePackage) {
    setError("");
    try {
      await apiRequest<void>(`provider/packages/${servicePackage.id}?expectedVersion=${servicePackage.version}`, { method: "DELETE" });
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không ngừng hoạt động được gói dịch vụ.");
    }
  }

  return (
    <main className="min-h-screen bg-[#f7f8f4]">
      <SiteHeader />
      <section className="mx-auto max-w-6xl px-5 py-10 sm:px-8 sm:py-14">
        <Link className="text-sm font-semibold text-[#477352] hover:underline" href="/history">← Quay lại tài khoản</Link>
        <p className="mt-6 text-xs font-bold uppercase tracking-[0.18em] text-[#719071]">Catalog</p>
        <h1 className="mt-2 text-3xl font-semibold text-[#173c33]">Gói dịch vụ của tôi</h1>
        <p className="mt-2 text-sm text-[#6c7971]">Gói mới và mọi thay đổi nội dung cần được Admin duyệt trước khi công khai.</p>
        {error && <p role="alert" className="mt-6 rounded-2xl bg-[#fff1ec] p-4 text-sm text-[#9b3d28]">{error}</p>}

        <section className="mt-8 rounded-3xl border border-[#dce5dc] bg-white p-5 sm:p-7">
          <h2 className="text-lg font-semibold text-[#23483a]">{editing ? "Cập nhật gói" : "Tạo gói mới"}</h2>
          <form className="mt-5 grid gap-4 sm:grid-cols-2" onSubmit={(event) => void save(event)}>
            <label className="text-sm font-medium text-[#315246]">
              Danh mục
              <select required value={form.categoryId} onChange={(event) => setForm({ ...form, categoryId: event.target.value })} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3">
                {categories.map((category) => <option key={category.id} value={category.id}>{category.name}</option>)}
              </select>
            </label>
            <label className="text-sm font-medium text-[#315246]">
              Tên gói
              <input required maxLength={120} value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3" />
            </label>
            <label className="text-sm font-medium text-[#315246]">
              Giá cố định (VND)
              <input required min="1" step="1" type="number" value={form.price} onChange={(event) => setForm({ ...form, price: event.target.value })} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3" />
            </label>
            <label className="text-sm font-medium text-[#315246]">
              Thời lượng (phút)
              <input required min="1" step="1" type="number" value={form.durationMinutes} onChange={(event) => setForm({ ...form, durationMinutes: event.target.value })} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3" />
            </label>
            <label className="text-sm font-medium text-[#315246] sm:col-span-2">
              Số điện thoại hiển thị sau khi duyệt
              <input required maxLength={24} value={form.providerPhone} onChange={(event) => setForm({ ...form, providerPhone: event.target.value })} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3" />
            </label>
            <label className="text-sm font-medium text-[#315246] sm:col-span-2">
              Mô tả
              <textarea required maxLength={1000} rows={4} value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} className="mt-2 w-full resize-y rounded-xl border border-[#dce5dc] px-4 py-3" />
            </label>
            <div className="flex flex-wrap gap-3 sm:col-span-2">
              <button disabled={saving || categories.length === 0} className="rounded-full bg-[#1f594a] px-6 py-3 text-sm font-semibold text-white disabled:opacity-60">
                {saving ? "Đang lưu…" : editing ? "Lưu thay đổi" : "Tạo bản nháp"}
              </button>
              {editing && <button type="button" onClick={resetForm} className="rounded-full border border-[#dce5dc] px-6 py-3 text-sm font-semibold text-[#52645e]">Hủy sửa</button>}
            </div>
          </form>
        </section>

        <section className="mt-10">
          <h2 className="text-xl font-semibold text-[#23483a]">Danh sách gói</h2>
          {loading ? <p className="mt-5 text-sm text-[#6c7971]">Đang tải…</p> : packages.length === 0 ? (
            <p className="mt-5 rounded-2xl border border-dashed border-[#cfdacf] bg-white p-7 text-center text-sm text-[#6c7971]">Bạn chưa tạo gói dịch vụ nào.</p>
          ) : (
            <div className="mt-5 space-y-4">
              {packages.map((servicePackage) => (
                <article key={servicePackage.id} className="rounded-3xl border border-[#e5eae3] bg-white p-5 sm:p-6">
                  <div className="flex flex-col justify-between gap-3 sm:flex-row">
                    <div>
                      <p className="text-xs font-semibold uppercase tracking-wide text-[#719071]">{categories.find((category) => category.id === servicePackage.categoryId)?.name ?? servicePackage.categoryId}</p>
                      <h3 className="mt-1 text-lg font-semibold text-[#23483a]">{servicePackage.name}</h3>
                    </div>
                    <span className="h-fit rounded-full bg-[#f1f7ef] px-3 py-1.5 text-xs font-semibold text-[#315246]">{statusLabels[servicePackage.status]}</span>
                  </div>
                  <p className="mt-2 text-sm text-[#6c7971]">{servicePackage.description}</p>
                  <p className="mt-2 text-sm font-semibold text-[#1f594a]">{new Intl.NumberFormat("vi-VN").format(servicePackage.price)} ₫ · {servicePackage.durationMinutes} phút</p>
                  {servicePackage.status === "REJECTED" && servicePackage.rejectionReason && <p className="mt-3 rounded-xl bg-[#fff1ec] p-3 text-sm text-[#9b3d28]">Lý do từ chối: {servicePackage.rejectionReason}</p>}
                  <div className="mt-4 flex flex-wrap gap-2">
                    {servicePackage.status !== "PENDING_APPROVAL" && servicePackage.status !== "INACTIVE" && (
                      <button onClick={() => startEdit(servicePackage)} className="rounded-full border border-[#dce5dc] px-4 py-2 text-sm font-semibold text-[#315246]">Chỉnh sửa</button>
                    )}
                    {(servicePackage.status === "DRAFT" || servicePackage.status === "REJECTED") && (
                      <button onClick={() => void submitForApproval(servicePackage)} className="rounded-full bg-[#1f594a] px-4 py-2 text-sm font-semibold text-white">Gửi duyệt</button>
                    )}
                    {servicePackage.status !== "INACTIVE" && (
                      <button onClick={() => void archive(servicePackage)} className="rounded-full border border-[#ead4cc] px-4 py-2 text-sm font-semibold text-[#9b3d28]">Ngừng hoạt động</button>
                    )}
                  </div>
                </article>
              ))}
            </div>
          )}
        </section>
      </section>
    </main>
  );
}
