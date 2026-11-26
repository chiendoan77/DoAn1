import { ReactNode } from "react";
import { CheckCircle2, CircleAlert, Clock3, XCircle } from "lucide-react";

type Props = {
  tone?: "neutral" | "warning" | "success" | "danger" | "info";
  children: ReactNode;
  withIcon?: boolean;
};

const tones = {
  neutral: "border-border bg-bg text-muted",
  warning: "border-warning/20 bg-warning-bg text-warning",
  success: "border-success/20 bg-success-bg text-success",
  danger: "border-danger/20 bg-danger-bg text-danger",
  info: "border-info/20 bg-info-bg text-info",
};

export function Badge({ tone = "neutral", children, withIcon = true }: Props) {
  const Icon = tone === "success" ? CheckCircle2 : tone === "danger" ? XCircle : tone === "warning" ? Clock3 : tone === "info" ? CircleAlert : null;
  return (
    <span className={`inline-flex min-h-7 items-center gap-1.5 rounded-full border px-2.5 text-xs font-semibold ${tones[tone]}`}>
      {withIcon && Icon && <Icon className="h-3.5 w-3.5" aria-hidden="true" />}
      {children}
    </span>
  );
}
