import { AuthForm } from "@/features/auth/auth-form";
import { Suspense } from "react";

type Props = {
  searchParams: Promise<{ role?: string }>;
};

export default function RegisterPage({ searchParams }: Props) {
  return (
    <Suspense fallback={<p className="p-8 text-center text-sm text-[#6c7971]">Đang tải biểu mẫu đăng ký…</p>}>
      <RegisterForm searchParams={searchParams} />
    </Suspense>
  );
}

async function RegisterForm({ searchParams }: Props) {
  const params = await searchParams;
  const defaultRole = params.role === "PROVIDER" ? "PROVIDER" : "CUSTOMER";
  return <AuthForm mode="register" defaultRole={defaultRole} />;
}
