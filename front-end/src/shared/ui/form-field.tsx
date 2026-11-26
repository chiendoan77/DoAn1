import { ReactNode } from "react";

type Props = {
  label: string;
  htmlFor: string;
  error?: string;
  helper?: string;
  children: ReactNode;
};

export function FormField({ label, htmlFor, error, helper, children }: Props) {
  return (
    <div>
      <label htmlFor={htmlFor} className="mb-1.5 block text-sm font-semibold text-text">
        {label}
      </label>
      {children}
      {error ? (
        <p id={`${htmlFor}-error`} role="alert" className="mt-1.5 text-[13px] leading-5 text-danger">
          {error}
        </p>
      ) : helper ? (
        <p className="mt-1.5 text-[13px] leading-5 text-muted">{helper}</p>
      ) : null}
    </div>
  );
}
