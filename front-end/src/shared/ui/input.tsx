import { InputHTMLAttributes, ReactNode, forwardRef } from "react";

type Props = InputHTMLAttributes<HTMLInputElement> & {
  icon?: ReactNode;
  trailing?: ReactNode;
  invalid?: boolean;
};

export const Input = forwardRef<HTMLInputElement, Props>(function Input(
  { icon, trailing, invalid = false, className = "", ...props },
  ref,
) {
  return (
    <div className="group relative">
      {icon && (
        <span aria-hidden="true" className="pointer-events-none absolute left-3.5 top-1/2 h-5 w-5 -translate-y-1/2 text-muted transition-colors group-focus-within:text-brand-600 [&>svg]:h-5 [&>svg]:w-5">
          {icon}
        </span>
      )}
      <input
        {...props}
        ref={ref}
        aria-invalid={invalid || undefined}
        className={`h-12 w-full rounded-md border bg-surface text-base text-text placeholder:text-muted/80 transition outline-none ${
          icon ? "pl-11" : "pl-3.5"
        } ${trailing ? "pr-12" : "pr-3.5"} ${
          invalid ? "border-danger" : "border-border"
        } focus:border-brand-500 focus:ring-2 focus:ring-brand-500/20 ${className}`}
      />
      {trailing && (
        <div className="absolute right-2 top-1/2 -translate-y-1/2">
          {trailing}
        </div>
      )}
    </div>
  );
});
