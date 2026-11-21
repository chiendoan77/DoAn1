"use client";


import styles from "./provider-packages-page.module.css";
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
    <main className={styles.style1}>
      <SiteHeader />
      <section className={styles.style2}>
        <Link className={styles.style3} href="/history">← Quay lại tài khoản</Link>
        <p className={styles.style4}>Catalog</p>
        <h1 className={styles.style5}>Gói dịch vụ của tôi</h1>
        <p className={styles.style6}>Gói mới và mọi thay đổi nội dung cần được Admin duyệt trước khi công khai.</p>
        {error && <p role="alert" className={styles.style7}>{error}</p>}

        <section className={styles.style8}>
          <h2 className={styles.style9}>{editing ? "Cập nhật gói" : "Tạo gói mới"}</h2>
          <form className={styles.style10} onSubmit={(event) => void save(event)}>
            <label className={styles.style11}>
              Danh mục
              <select required value={form.categoryId} onChange={(event) => setForm({ ...form, categoryId: event.target.value })} className={styles.style12}>
                {categories.map((category) => <option key={category.id} value={category.id}>{category.name}</option>)}
              </select>
            </label>
            <label className={styles.style11}>
              Tên gói
              <input required maxLength={120} value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} className={styles.style12} />
            </label>
            <label className={styles.style11}>
              Giá cố định (VND)
              <input required min="1" step="1" type="number" value={form.price} onChange={(event) => setForm({ ...form, price: event.target.value })} className={styles.style12} />
            </label>
            <label className={styles.style11}>
              Thời lượng (phút)
              <input required min="1" step="1" type="number" value={form.durationMinutes} onChange={(event) => setForm({ ...form, durationMinutes: event.target.value })} className={styles.style12} />
            </label>
            <label className={styles.style13}>
              Số điện thoại hiển thị sau khi duyệt
              <input required maxLength={24} value={form.providerPhone} onChange={(event) => setForm({ ...form, providerPhone: event.target.value })} className={styles.style12} />
            </label>
            <label className={styles.style13}>
              Mô tả
              <textarea required maxLength={1000} rows={4} value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} className={styles.style14} />
            </label>
            <div className={styles.style15}>
              <button disabled={saving || categories.length === 0} className={styles.style16}>
                {saving ? "Đang lưu…" : editing ? "Lưu thay đổi" : "Tạo bản nháp"}
              </button>
              {editing && <button type="button" onClick={resetForm} className={styles.style17}>Hủy sửa</button>}
            </div>
          </form>
        </section>

        <section className={styles.style18}>
          <h2 className={styles.style19}>Danh sách gói</h2>
          {loading ? <p className={styles.style20}>Đang tải…</p> : packages.length === 0 ? (
            <p className={styles.style21}>Bạn chưa tạo gói dịch vụ nào.</p>
          ) : (
            <div className={styles.style22}>
              {packages.map((servicePackage) => (
                <article key={servicePackage.id} className={styles.style23}>
                  <div className={styles.style24}>
                    <div>
                      <p className={styles.style25}>{categories.find((category) => category.id === servicePackage.categoryId)?.name ?? servicePackage.categoryId}</p>
                      <h3 className={styles.style26}>{servicePackage.name}</h3>
                    </div>
                    <span className={styles.style27}>{statusLabels[servicePackage.status]}</span>
                  </div>
                  <p className={styles.style6}>{servicePackage.description}</p>
                  <p className={styles.style28}>{new Intl.NumberFormat("vi-VN").format(servicePackage.price)} ₫ · {servicePackage.durationMinutes} phút</p>
                  {servicePackage.status === "REJECTED" && servicePackage.rejectionReason && <p className={styles.style29}>Lý do từ chối: {servicePackage.rejectionReason}</p>}
                  <div className={styles.style30}>
                    {servicePackage.status !== "PENDING_APPROVAL" && servicePackage.status !== "INACTIVE" && (
                      <button onClick={() => startEdit(servicePackage)} className={styles.style31}>Chỉnh sửa</button>
                    )}
                    {(servicePackage.status === "DRAFT" || servicePackage.status === "REJECTED") && (
                      <button onClick={() => void submitForApproval(servicePackage)} className={styles.style32}>Gửi duyệt</button>
                    )}
                    {servicePackage.status !== "INACTIVE" && (
                      <button onClick={() => void archive(servicePackage)} className={styles.style33}>Ngừng hoạt động</button>
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
