import { ButtonHTMLAttributes } from "react";
import { LoaderCircle } from "lucide-react";

type Props = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: "primary" | "secondary" | "ghost" | "destructive";
  size?: "sm" | "md" | "lg";
  loading?: boolean;
};

const variants = {
  primary: "border-brand-600 bg-brand-600 text-white hover:bg-brand-700",
  secondary: "border-border bg-surface text-text hover:border-brand-500",
  ghost: "border-transparent bg-transparent text-text hover:bg-brand-50",
  destructive: "border-danger text-danger hover:bg-danger-bg",
};

const sizes = {
  sm: "min-h-10 px-3 text-xs",
  md: "min-h-11 px-4 text-sm",
  lg: "min-h-12 px-5 text-base",
};

export function Button({
  variant = "primary",
  size = "md",
  loading = false,
  disabled,
  className = "",
  children,
  ...props
}: Props) {
  return (
    <button
      {...props}
      disabled={disabled || loading}
      className={`inline-flex items-center justify-center gap-2 rounded-md border font-semibold transition disabled:cursor-not-allowed disabled:opacity-60 ${variants[variant]} ${sizes[size]} ${className}`}
    >
      {loading && <LoaderCircle className="h-4 w-4 animate-spin" aria-hidden="true" />}
      {children}
    </button>
  );
}
