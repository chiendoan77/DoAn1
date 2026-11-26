"use client";

import Link from "next/link";
import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import {
  Building2,
  Eye,
  EyeOff,
  Image as ImageIcon,
  LockKeyhole,
  Mail,
  MapPin,
  Phone,
  Tag,
  UserRound,
} from "lucide-react";
import { Account, CustomerProfile, Provider, ServiceCategory, apiRequest } from "@/lib/api";
import { AuthLayout } from "@/shared/layout/auth-layout";
import { Button } from "@/shared/ui/button";
import { FormField } from "@/shared/ui/form-field";
import { Input } from "@/shared/ui/input";
import { RoleCard } from "@/shared/ui/role-card";

type Props = {
  mode: "login" | "register";
  defaultRole?: "CUSTOMER" | "PROVIDER";
};

const categories: ServiceCategory[] = [
  { id: "cleaning", name: "Dọn dẹp nhà", description: "" },
  { id: "repair", name: "Sửa chữa gia đình", description: "" },
];

export function AuthForm({ mode, defaultRole = "CUSTOMER" }: Props) {
  const router = useRouter();
  const [role, setRole] = useState<"CUSTOMER" | "PROVIDER">(defaultRole);
  const [showPassword, setShowPassword] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
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

  function fieldError(
    event: FormEvent<HTMLInputElement | HTMLTextAreaElement>,
    fieldName: string,
  ) {
    const input = event.currentTarget;
    const message = input.validity.valueMissing
      ? `${fieldName} không được để trống.`
      : input.validity.typeMismatch
        ? `${fieldName} không đúng định dạng.`
        : input.validity.patternMismatch
          ? `${fieldName} không đúng định dạng.`
          : input.validity.tooShort
            ? `${fieldName} chưa đủ độ dài tối thiểu.`
            : input.validity.tooLong
              ? `${fieldName} vượt quá độ dài cho phép.`
              : `${fieldName} không hợp lệ.`;
    setFieldErrors((current) => ({ ...current, [input.name]: message }));
  }

  function clearFieldError(fieldName: string) {
    setFieldErrors((current) => {
      const next = { ...current };
      delete next[fieldName];
      return next;
    });
  }

  function toggleCategory(categoryId: string) {
    setForm((current) => {
      if (current.categoryIds.includes(categoryId) && current.categoryIds.length === 1) return current;
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
    <AuthLayout
      compact={isRegister}
      eyebrow={isRegister ? "Bắt đầu cùng Nhà Tốt" : "Chào mừng trở lại"}
      title={isRegister ? "Tạo tài khoản" : "Đăng nhập"}
      description={isRegister
        ? "Thông tin cá nhân được dùng để quản lý lịch hẹn và hồ sơ của bạn."
        : "Dùng email hoặc số điện thoại đã đăng ký."}
    >
      <form className={isRegister ? "space-y-2.5" : "space-y-4"} onSubmit={(event) => void submit(event)}>
        {isRegister && (
          <fieldset>
            <legend className="mb-2 text-sm font-semibold text-text">Tôi muốn tham gia với vai trò</legend>
            <div className="grid grid-cols-2 gap-3">
              <RoleCard
                selected={role === "CUSTOMER"}
                title="Khách hàng"
                description="Đặt lịch dịch vụ"
                icon={<UserRound />}
                onClick={() => setRole("CUSTOMER")}
              />
              <RoleCard
                selected={role === "PROVIDER"}
                title="Nhà cung cấp"
                description="Cung cấp dịch vụ"
                icon={<Building2 />}
                onClick={() => setRole("PROVIDER")}
              />
            </div>
          </fieldset>
        )}

        {isRegister && (
          <div className="grid grid-cols-2 gap-3">
            <FormField htmlFor="fullName" label="Họ và tên" error={fieldErrors.fullName}>
              <Input
                id="fullName"
                name="fullName"
                required
                autoComplete="name"
                maxLength={120}
                icon={<UserRound />}
                invalid={Boolean(fieldErrors.fullName)}
                value={form.fullName}
                onInvalid={(event) => fieldError(event, "Họ và tên")}
                onInput={() => clearFieldError("fullName")}
                onChange={(event) => update("fullName", event.target.value)}
              />
            </FormField>
            <FormField htmlFor="username" label="Tên đăng nhập" error={fieldErrors.username}>
              <Input
                id="username"
                name="username"
                required
                autoComplete="username"
                minLength={3}
                maxLength={40}
                pattern="(?:[a-zA-Z0-9._]|-)+"
                icon={<Tag />}
                invalid={Boolean(fieldErrors.username)}
                value={form.username}
                onInvalid={(event) => fieldError(event, "Tên đăng nhập")}
                onInput={() => clearFieldError("username")}
                onChange={(event) => update("username", event.target.value)}
              />
            </FormField>
          </div>
        )}

        {isRegister ? (
          <div className="grid grid-cols-2 gap-3">
            <FormField htmlFor="email" label="Email" error={fieldErrors.email}>
              <Input
                id="email"
                name="email"
                required
                type="email"
                autoComplete="email"
                maxLength={320}
                placeholder="ban@example.com"
                icon={<Mail />}
                invalid={Boolean(fieldErrors.email)}
                value={form.email}
                onInvalid={(event) => fieldError(event, "Email")}
                onInput={() => clearFieldError("email")}
                onChange={(event) => update("email", event.target.value)}
              />
            </FormField>
            <FormField htmlFor="phoneNumber" label="Số điện thoại" error={fieldErrors.phoneNumber}>
              <Input
                id="phoneNumber"
                name="phoneNumber"
                required
                type="tel"
                autoComplete="tel"
                pattern="[+]?[0-9](?:[0-9]| |\\(|\\)|-){6,19}"
                placeholder="+84…"
                icon={<Phone />}
                invalid={Boolean(fieldErrors.phoneNumber)}
                value={form.phoneNumber}
                onInvalid={(event) => fieldError(event, "Số điện thoại")}
                onInput={() => clearFieldError("phoneNumber")}
                onChange={(event) => update("phoneNumber", event.target.value)}
              />
            </FormField>
          </div>
        ) : (
          <FormField htmlFor="email" label="Email hoặc số điện thoại" error={fieldErrors.email}>
            <Input
              id="email"
              name="email"
              required
              autoComplete="username"
              placeholder="ban@example.com hoặc 090…"
              icon={<Mail />}
              invalid={Boolean(fieldErrors.email)}
              value={form.email}
              onInvalid={(event) => fieldError(event, "Email hoặc số điện thoại")}
              onInput={() => clearFieldError("email")}
              onChange={(event) => update("email", event.target.value)}
            />
          </FormField>
        )}

        {isRegister && role === "CUSTOMER" && (
          <FormField htmlFor="address" label="Địa chỉ mặc định để đặt lịch" error={fieldErrors.address}>
            <Input
              id="address"
              name="address"
              required
              maxLength={500}
              placeholder="Số nhà, đường, quận/huyện"
              icon={<MapPin />}
              invalid={Boolean(fieldErrors.address)}
              value={form.address}
              onInvalid={(event) => fieldError(event, "Địa chỉ mặc định")}
              onInput={() => clearFieldError("address")}
              onChange={(event) => update("address", event.target.value)}
            />
          </FormField>
        )}

        {isRegister && role === "PROVIDER" && (
          <div className="grid grid-cols-2 gap-3 rounded-md border border-border bg-bg p-3">
            <div className="col-span-2">
              <p className="text-sm font-semibold text-text">Thông tin hồ sơ dịch vụ</p>
              <div className="mt-2 grid grid-cols-2 gap-2">
                {categories.map((category) => (
                  <label key={category.id} className="flex min-h-11 items-center gap-2 rounded-md border border-border bg-surface px-3 text-sm">
                    <input
                      type="checkbox"
                      checked={form.categoryIds.includes(category.id)}
                      onChange={() => toggleCategory(category.id)}
                    />
                    {category.name}
                  </label>
                ))}
              </div>
            </div>
            <FormField htmlFor="providerAddress" label="Địa chỉ/khu vực" error={fieldErrors.providerAddress}>
              <Input id="providerAddress" name="providerAddress" required maxLength={500} icon={<MapPin />} value={form.address} onChange={(event) => update("address", event.target.value)} />
            </FormField>
            <FormField htmlFor="imageUrl" label="Liên kết ảnh (HTTPS)" error={fieldErrors.imageUrl}>
              <Input id="imageUrl" name="imageUrl" required type="url" pattern="https://.+"
                icon={<ImageIcon />} value={form.imageUrl} onChange={(event) => update("imageUrl", event.target.value)} placeholder="https://…" />
            </FormField>
            <div className="col-span-2">
              <FormField htmlFor="description" label="Giới thiệu dịch vụ" error={fieldErrors.description}>
                <textarea id="description" name="description" required maxLength={1000} rows={2}
                  value={form.description} onChange={(event) => update("description", event.target.value)}
                  className="w-full resize-y rounded-md border border-border bg-surface px-3.5 py-2.5 text-base outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-500/20" />
              </FormField>
            </div>
          </div>
        )}

        <FormField htmlFor="password" label="Mật khẩu" helper={isRegister ? "Tối thiểu 8 ký tự" : undefined} error={fieldErrors.password}>
          <Input
            id="password"
            name="password"
            required
            autoComplete={isRegister ? "new-password" : "current-password"}
            minLength={isRegister ? 8 : undefined}
            maxLength={72}
            type={showPassword ? "text" : "password"}
            icon={<LockKeyhole />}
            invalid={Boolean(fieldErrors.password)}
            value={form.password}
            onInvalid={(event) => fieldError(event, "Mật khẩu")}
            onInput={() => clearFieldError("password")}
            onChange={(event) => update("password", event.target.value)}
            trailing={
              <button
                type="button"
                onClick={() => setShowPassword((current) => !current)}
                className="grid h-9 w-9 place-items-center rounded-md text-muted hover:bg-brand-50 hover:text-brand-700"
                aria-label={showPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu"}
              >
                {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
              </button>
            }
          />
        </FormField>

        {error && (
          <p role="alert" className="rounded-md border border-danger/20 bg-danger-bg px-3 py-2 text-sm leading-6 text-danger">
            {error}
          </p>
        )}
        {notice && (
          <p role="status" className="rounded-md border border-success/20 bg-success-bg px-3 py-2 text-sm leading-6 text-success">
            {notice} <Link className="font-semibold underline" href="/history">Xem hồ sơ</Link>
          </p>
        )}

        <Button type="submit" size="lg" loading={busy} className="w-full">
          {busy ? "Đang xử lý…" : isRegister ? "Tạo tài khoản" : "Đăng nhập"}
        </Button>
      </form>

      <p className="mt-4 text-center text-sm text-muted">
        {isRegister ? "Đã có tài khoản?" : "Chưa có tài khoản?"}{" "}
        <Link className="font-bold text-brand-700 underline" href={isRegister ? "/login" : "/register"}>
          {isRegister ? "Đăng nhập" : "Đăng ký"}
        </Link>
      </p>
    </AuthLayout>
  );
}
