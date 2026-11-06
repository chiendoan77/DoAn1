"use client";

import styles from "./auth-form.module.css";

import Link from "next/link";
import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { Account, CustomerProfile, Provider, ServiceCategory, apiRequest } from "@/lib/api";

type Props = {
  mode: "login" | "register";
  defaultRole?: "CUSTOMER" | "PROVIDER";
};

function showInputValidationError(
  event: FormEvent<HTMLInputElement | HTMLTextAreaElement>,
  fieldName: string,
) {
  const input = event.currentTarget;
  const validity = input.validity;
  if (validity.valueMissing) {
    input.setCustomValidity(`${fieldName} không được để trống.`);
  } else if (validity.typeMismatch) {
    input.setCustomValidity(`${fieldName} không đúng định dạng.`);
  } else if (validity.patternMismatch) {
    input.setCustomValidity(`${fieldName} không đúng định dạng.`);
  } else if (validity.tooShort) {
    input.setCustomValidity(`${fieldName} chưa đủ độ dài tối thiểu.`);
  } else if (validity.tooLong) {
    input.setCustomValidity(`${fieldName} vượt quá độ dài cho phép.`);
  } else {
    input.setCustomValidity(`${fieldName} không hợp lệ.`);
  }
}

function clearInputValidationError(event: FormEvent<HTMLInputElement | HTMLTextAreaElement>) {
  event.currentTarget.setCustomValidity("");
}

const categories: ServiceCategory[] = [
  { id: "cleaning", name: "Dọn dẹp nhà", description: "" },
  { id: "repair", name: "Sửa chữa gia đình", description: "" },
];

export function AuthForm({ mode, defaultRole = "CUSTOMER" }: Props) {
  const router = useRouter();
  const [role, setRole] = useState<"CUSTOMER" | "PROVIDER">(defaultRole);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [form, setForm] = useState({
    email: "",
    phoneNumber: "",
    username: "",
    fullName: "",
    password: "",
    categoryIds: ["cleaning"],
    address: "",
    imageUrl: "",
    description: "",
  });

  function update(field: keyof typeof form, value: string) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  function toggleCategory(categoryId: string) {
    setForm((current) => {
      if (current.categoryIds.includes(categoryId) && current.categoryIds.length === 1) {
        return current;
      }
      return {
        ...current,
        categoryIds: current.categoryIds.includes(categoryId)
          ? current.categoryIds.filter((item) => item !== categoryId)
          : [...current.categoryIds, categoryId],
      };
    });
  }

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setBusy(true);
    setError("");
    setNotice("");
    let registrationCreated = false;
    try {
      if (mode === "register") {
        await apiRequest<Account>("auth/register", {
          method: "POST",
          body: JSON.stringify({
            email: form.email,
            phoneNumber: form.phoneNumber,
            username: form.username,
            fullName: form.fullName,
            password: form.password,
            role,
          }),
        });
        registrationCreated = true;
      }

      const login = await apiRequest<{ account: Account }>("auth/login", {
        method: "POST",
        body: JSON.stringify({
          identifier: form.email,
          password: form.password,
        }),
      });

      if (mode === "register" && role === "PROVIDER") {
        const profile = await apiRequest<Provider>("provider/applications", {
          method: "POST",
          body: JSON.stringify({
            categoryIds: form.categoryIds,
            address: form.address,
            imageUrl: form.imageUrl,
            description: form.description,
          }),
        });
        setNotice(`Đã tạo tài khoản. Hồ sơ nhà cung cấp đang ${profile.status === "PENDING" ? "chờ Admin duyệt" : profile.status.toLowerCase()}.`);
        return;
      }

      if (mode === "register" && role === "CUSTOMER") {
        await apiRequest<CustomerProfile>("customer/profile", {
          method: "PUT",
          body: JSON.stringify({
            displayName: form.fullName,
            phoneNumber: form.phoneNumber,
            defaultAddress: form.address,
            avatarUrl: null,
          }),
        });
      }

      if (mode === "login" && login.account.role === "ADMIN") {
        router.push("/admin/providers");
        return;
      }
      const next = new URLSearchParams(window.location.search).get("next");
      router.push(next?.startsWith("/") && !next.startsWith("//") ? next : "/history");
    } catch (cause) {
      const message = cause instanceof Error ? cause.message : "Không thể hoàn tất yêu cầu.";
      setError(registrationCreated
        ? `Tài khoản đã được tạo nhưng chưa hoàn tất hồ sơ: ${message} Hãy đăng nhập, bấm avatar và lưu hồ sơ trước khi đặt lịch.`
        : message);
    } finally {
      setBusy(false);
    }
  }

  const isRegister = mode === "register";
  return (
    <main className={styles.style1}>
      <section className={styles.style2}>
        <div className={styles.style3}>
          <Link className={styles.style4} href="/">
            <span className={styles.style5}>n</span> nhà tốt
          </Link>
          <p className={styles.style6}>{isRegister ? "Bắt đầu cùng Nhà Tốt" : "Chào mừng trở lại"}</p>
          <h1 className={styles.style7}>{isRegister ? "Tạo tài khoản" : "Đăng nhập"}</h1>
          <p className={styles.style8}>
            {isRegister ? "Thông tin cá nhân được dùng để quản lý lịch hẹn và hồ sơ của bạn." : "Dùng email hoặc số điện thoại đã đăng ký."}
          </p>

          <form className={styles.style9} onSubmit={(event) => void submit(event)}>
            {isRegister && (
              <>
                <fieldset>
                  <legend className={styles.style10}>Tôi muốn tham gia với vai trò</legend>
                  <div className={styles.style11}>
                    {(["CUSTOMER", "PROVIDER"] as const).map((choice) => (
                      <button key={choice} type="button" aria-pressed={role === choice} onClick={() => setRole(choice)} className={`${styles.roleChoice} ${role === choice ? styles.roleChoiceSelected : styles.roleChoiceUnselected}`}>
                        {choice === "CUSTOMER" ? "Khách hàng" : "Nhà cung cấp"}
                      </button>
                    ))}
                  </div>
                </fieldset>
                <label className={styles.style12}>
                  Họ và tên
                  <input required autoComplete="name" maxLength={120} value={form.fullName} onInvalid={(event) => showInputValidationError(event, "Họ và tên")} onInput={clearInputValidationError} onChange={(event) => update("fullName", event.target.value)} className={styles.style13} />
                </label>
                <label className={styles.style12}>
                  Tên đăng nhập
                  <input required autoComplete="username" minLength={3} maxLength={40} pattern="(?:[a-zA-Z0-9._]|-)+" value={form.username} onInvalid={(event) => showInputValidationError(event, "Tên đăng nhập")} onInput={clearInputValidationError} onChange={(event) => update("username", event.target.value)} className={styles.style13} />
                </label>
              </>
            )}

            <label className={styles.style12}>
              {isRegister ? "Email" : "Email hoặc số điện thoại"}
              <input required autoComplete={isRegister ? "email" : "username"} type={isRegister ? "email" : "text"} maxLength={isRegister ? 320 : undefined} value={form.email} onInvalid={(event) => showInputValidationError(event, isRegister ? "Email" : "Email hoặc số điện thoại")} onInput={clearInputValidationError} onChange={(event) => update("email", event.target.value)} className={styles.style13} placeholder={isRegister ? "ban@example.com" : "ban@example.com hoặc 090…"} />
            </label>

            {isRegister && (
              <label className={styles.style12}>
                Số điện thoại
                <input required autoComplete="tel" type="tel" pattern="[+]?[0-9](?:[0-9]| |\\(|\\)|-){6,19}" value={form.phoneNumber} onInvalid={(event) => showInputValidationError(event, "Số điện thoại")} onInput={clearInputValidationError} onChange={(event) => update("phoneNumber", event.target.value)} className={styles.style13} placeholder="+84…" />
              </label>
            )}

            {isRegister && role === "CUSTOMER" && (
              <label className={styles.style12}>
                Địa chỉ mặc định để đặt lịch
                <input required maxLength={500} value={form.address} onInvalid={(event) => showInputValidationError(event, "Địa chỉ mặc định")} onInput={clearInputValidationError} onChange={(event) => update("address", event.target.value)} className={styles.style13} placeholder="Số nhà, đường, quận/huyện" />
              </label>
            )}

            <label className={styles.style12}>
              Mật khẩu
              <input required autoComplete={isRegister ? "new-password" : "current-password"} minLength={isRegister ? 8 : undefined} maxLength={72} type="password" value={form.password} onInvalid={(event) => showInputValidationError(event, "Mật khẩu")} onInput={clearInputValidationError} onChange={(event) => update("password", event.target.value)} className={styles.style13} />
              {isRegister && <span className={styles.style14}>Tối thiểu 8 ký tự</span>}
            </label>

            {isRegister && role === "PROVIDER" && (
              <div className={styles.style15}>
                <p className={styles.style16}>Thông tin hồ sơ dịch vụ</p>
                <label className={styles.style12}>
                  Dịch vụ bạn cung cấp
                  <div className={styles.style17}>
                    {categories.map((category) => (
                      <label key={category.id} className={styles.style18}>
                        <input type="checkbox" checked={form.categoryIds.includes(category.id)} onChange={() => toggleCategory(category.id)} />
                        <span>{category.name}</span>
                      </label>
                    ))}
                  </div>
                </label>
                <label className={styles.style12}>
                  Địa chỉ/khu vực hoạt động
                  <input required maxLength={500} value={form.address} onInvalid={(event) => showInputValidationError(event, "Địa chỉ/khu vực hoạt động")} onInput={clearInputValidationError} onChange={(event) => update("address", event.target.value)} className={styles.style19} />
                </label>
                <label className={styles.style12}>
                  Liên kết ảnh đại diện (HTTPS)
                  <input required type="url" pattern="https://.+" value={form.imageUrl} onInvalid={(event) => showInputValidationError(event, "Liên kết ảnh đại diện")} onInput={clearInputValidationError} onChange={(event) => update("imageUrl", event.target.value)} className={styles.style19} placeholder="https://…" />
                </label>
                <label className={styles.style12}>
                  Giới thiệu dịch vụ
                  <textarea required maxLength={1000} rows={3} value={form.description} onInvalid={(event) => showInputValidationError(event, "Giới thiệu dịch vụ")} onInput={clearInputValidationError} onChange={(event) => update("description", event.target.value)} className={styles.style20} />
                </label>
                <p className={styles.style21}>Hồ sơ sẽ ở trạng thái chờ duyệt và chưa xuất hiện trong danh sách công khai.</p>
              </div>
            )}

            {error && <p role="alert" className={styles.style22}>{error}</p>}
            {notice && <p role="status" className={styles.style23}>{notice} <Link className={styles.style24} href="/history">Xem hồ sơ</Link></p>}
            <button disabled={busy} className={styles.style25}>
              {busy ? "Đang xử lý…" : isRegister ? "Tạo tài khoản" : "Đăng nhập"}
            </button>
          </form>

          <p className={styles.style26}>
            {isRegister ? "Đã có tài khoản?" : "Chưa có tài khoản?"}{" "}
            <Link className={styles.style27} href={isRegister ? "/login" : "/register"}>{isRegister ? "Đăng nhập" : "Đăng ký"}</Link>
          </p>
        </div>
      </section>
      <aside className={styles.style28}>
        <p className={styles.style29}>Nhà Tốt</p>
        <h2 className={styles.style30}>Chăm sóc ngôi nhà, bắt đầu từ một kết nối tốt.</h2>
        <p className={styles.style31}>Hồ sơ nhà cung cấp được xét duyệt. Lịch sử đặt dịch vụ chỉ hiển thị với tài khoản có quyền.</p>
      </aside>
    </main>
  );
}
