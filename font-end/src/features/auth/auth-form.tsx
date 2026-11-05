"use client";

import Link from "next/link";
import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { Account, Provider, ServiceCategory, apiRequest } from "@/lib/api";

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

      if (mode === "login" && login.account.role === "ADMIN") {
        router.push("/admin/providers");
        return;
      }
      const next = new URLSearchParams(window.location.search).get("next");
      router.push(next?.startsWith("/") && !next.startsWith("//") ? next : "/history");
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Không thể hoàn tất yêu cầu.");
    } finally {
      setBusy(false);
    }
  }

  const isRegister = mode === "register";
  return (
    <main className="grid min-h-screen bg-[#f7f8f4] lg:grid-cols-[1fr_0.85fr]">
      <section className="flex items-center justify-center px-5 py-12 sm:px-8">
        <div className="w-full max-w-xl rounded-[2rem] bg-white p-6 shadow-sm sm:p-10">
          <Link className="inline-flex items-center gap-3 text-lg font-bold text-[#173c33]" href="/">
            <span className="grid size-10 place-items-center rounded-2xl bg-[#1f594a] text-xl text-white">n</span> nhà tốt
          </Link>
          <p className="mt-8 text-xs font-bold uppercase tracking-[0.18em] text-[#719071]">{isRegister ? "Bắt đầu cùng Nhà Tốt" : "Chào mừng trở lại"}</p>
          <h1 className="mt-2 text-3xl font-semibold tracking-tight text-[#173c33]">{isRegister ? "Tạo tài khoản" : "Đăng nhập"}</h1>
          <p className="mt-2 text-sm leading-6 text-[#6c7971]">
            {isRegister ? "Thông tin cá nhân được dùng để quản lý lịch hẹn và hồ sơ của bạn." : "Dùng email hoặc số điện thoại đã đăng ký."}
          </p>

          <form className="mt-7 space-y-4" onSubmit={(event) => void submit(event)}>
            {isRegister && (
              <>
                <fieldset>
                  <legend className="mb-2 text-sm font-medium text-[#315246]">Tôi muốn tham gia với vai trò</legend>
                  <div className="grid grid-cols-2 gap-2">
                    {(["CUSTOMER", "PROVIDER"] as const).map((choice) => (
                      <button key={choice} type="button" aria-pressed={role === choice} onClick={() => setRole(choice)} className={`rounded-xl border px-3 py-3 text-sm font-semibold ${role === choice ? "border-[#4b7c58] bg-[#f1f7ef] text-[#234b3c]" : "border-[#dce5dc] text-[#6c7971]"}`}>
                        {choice === "CUSTOMER" ? "Khách hàng" : "Nhà cung cấp"}
                      </button>
                    ))}
                  </div>
                </fieldset>
                <label className="block text-sm font-medium text-[#315246]">
                  Họ và tên
                  <input required autoComplete="name" maxLength={120} value={form.fullName} onInvalid={(event) => showInputValidationError(event, "Họ và tên")} onInput={clearInputValidationError} onChange={(event) => update("fullName", event.target.value)} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3 outline-none focus:border-[#4b7c58]" />
                </label>
                <label className="block text-sm font-medium text-[#315246]">
                  Tên đăng nhập
                  <input required autoComplete="username" minLength={3} maxLength={40} pattern="(?:[a-zA-Z0-9._]|-)+" value={form.username} onInvalid={(event) => showInputValidationError(event, "Tên đăng nhập")} onInput={clearInputValidationError} onChange={(event) => update("username", event.target.value)} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3 outline-none focus:border-[#4b7c58]" />
                </label>
              </>
            )}

            <label className="block text-sm font-medium text-[#315246]">
              {isRegister ? "Email" : "Email hoặc số điện thoại"}
              <input required autoComplete={isRegister ? "email" : "username"} type={isRegister ? "email" : "text"} maxLength={isRegister ? 320 : undefined} value={form.email} onInvalid={(event) => showInputValidationError(event, isRegister ? "Email" : "Email hoặc số điện thoại")} onInput={clearInputValidationError} onChange={(event) => update("email", event.target.value)} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3 outline-none focus:border-[#4b7c58]" placeholder={isRegister ? "ban@example.com" : "ban@example.com hoặc 090…"} />
            </label>

            {isRegister && (
              <label className="block text-sm font-medium text-[#315246]">
                Số điện thoại
                <input required autoComplete="tel" type="tel" pattern="[+]?[0-9](?:[0-9]| |\\(|\\)|-){6,19}" value={form.phoneNumber} onInvalid={(event) => showInputValidationError(event, "Số điện thoại")} onInput={clearInputValidationError} onChange={(event) => update("phoneNumber", event.target.value)} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3 outline-none focus:border-[#4b7c58]" placeholder="+84…" />
              </label>
            )}

            <label className="block text-sm font-medium text-[#315246]">
              Mật khẩu
              <input required autoComplete={isRegister ? "new-password" : "current-password"} minLength={isRegister ? 8 : undefined} maxLength={72} type="password" value={form.password} onInvalid={(event) => showInputValidationError(event, "Mật khẩu")} onInput={clearInputValidationError} onChange={(event) => update("password", event.target.value)} className="mt-2 w-full rounded-xl border border-[#dce5dc] px-4 py-3 outline-none focus:border-[#4b7c58]" />
              {isRegister && <span className="mt-1 block text-xs font-normal text-[#87938c]">Tối thiểu 8 ký tự</span>}
            </label>

            {isRegister && role === "PROVIDER" && (
              <div className="space-y-4 rounded-2xl bg-[#f6f8f4] p-4">
                <p className="text-sm font-semibold text-[#315246]">Thông tin hồ sơ dịch vụ</p>
                <label className="block text-sm font-medium text-[#315246]">
                  Dịch vụ bạn cung cấp
                  <div className="mt-2 grid gap-2 sm:grid-cols-2">
                    {categories.map((category) => (
                      <label key={category.id} className="flex items-center gap-2 rounded-xl border border-[#dce5dc] bg-white px-3 py-3">
                        <input type="checkbox" checked={form.categoryIds.includes(category.id)} onChange={() => toggleCategory(category.id)} />
                        <span>{category.name}</span>
                      </label>
                    ))}
                  </div>
                </label>
                <label className="block text-sm font-medium text-[#315246]">
                  Địa chỉ/khu vực hoạt động
                  <input required maxLength={500} value={form.address} onInvalid={(event) => showInputValidationError(event, "Địa chỉ/khu vực hoạt động")} onInput={clearInputValidationError} onChange={(event) => update("address", event.target.value)} className="mt-2 w-full rounded-xl border border-[#dce5dc] bg-white px-4 py-3" />
                </label>
                <label className="block text-sm font-medium text-[#315246]">
                  Liên kết ảnh đại diện (HTTPS)
                  <input required type="url" pattern="https://.+" value={form.imageUrl} onInvalid={(event) => showInputValidationError(event, "Liên kết ảnh đại diện")} onInput={clearInputValidationError} onChange={(event) => update("imageUrl", event.target.value)} className="mt-2 w-full rounded-xl border border-[#dce5dc] bg-white px-4 py-3" placeholder="https://…" />
                </label>
                <label className="block text-sm font-medium text-[#315246]">
                  Giới thiệu dịch vụ
                  <textarea required maxLength={1000} rows={3} value={form.description} onInvalid={(event) => showInputValidationError(event, "Giới thiệu dịch vụ")} onInput={clearInputValidationError} onChange={(event) => update("description", event.target.value)} className="mt-2 w-full resize-y rounded-xl border border-[#dce5dc] bg-white px-4 py-3" />
                </label>
                <p className="text-xs leading-5 text-[#6c7971]">Hồ sơ sẽ ở trạng thái chờ duyệt và chưa xuất hiện trong danh sách công khai.</p>
              </div>
            )}

            {error && <p role="alert" className="whitespace-pre-line rounded-xl bg-[#fff1ec] p-3 text-sm text-[#9b3d28]">{error}</p>}
            {notice && <p role="status" className="rounded-xl bg-[#eaf4e9] p-3 text-sm leading-6 text-[#28583d]">{notice} <Link className="font-semibold underline" href="/history">Xem hồ sơ</Link></p>}
            <button disabled={busy} className="w-full rounded-full bg-[#1f594a] px-6 py-3.5 text-sm font-semibold text-white hover:bg-[#18483b] disabled:opacity-60">
              {busy ? "Đang xử lý…" : isRegister ? "Tạo tài khoản" : "Đăng nhập"}
            </button>
          </form>

          <p className="mt-6 text-center text-sm text-[#6c7971]">
            {isRegister ? "Đã có tài khoản?" : "Chưa có tài khoản?"}{" "}
            <Link className="font-semibold text-[#35684c] underline" href={isRegister ? "/login" : "/register"}>{isRegister ? "Đăng nhập" : "Đăng ký"}</Link>
          </p>
        </div>
      </section>
      <aside className="hidden flex-col justify-end bg-[#244d3f] p-12 text-white lg:flex">
        <p className="text-xs font-bold uppercase tracking-[0.18em] text-[#b8d2b5]">Nhà Tốt</p>
        <h2 className="mt-4 max-w-lg text-4xl font-semibold leading-tight">Chăm sóc ngôi nhà, bắt đầu từ một kết nối tốt.</h2>
        <p className="mt-4 max-w-lg text-sm leading-7 text-white/75">Hồ sơ nhà cung cấp được xét duyệt. Lịch sử đặt dịch vụ chỉ hiển thị với tài khoản có quyền.</p>
      </aside>
    </main>
  );
}
