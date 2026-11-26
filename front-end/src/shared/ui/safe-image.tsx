"use client";

/* eslint-disable @next/next/no-img-element */
import { ReactNode, useState } from "react";
import { House } from "lucide-react";

type Props = {
  src?: string | null;
  alt: string;
  className: string;
  placeholderIcon?: ReactNode;
};

export function SafeImage({ src, alt, className, placeholderIcon }: Props) {
  const [failedSrc, setFailedSrc] = useState<string | null>(null);
  const hasImage = Boolean(src?.trim()) && failedSrc !== src;

  return (
    <div className={`overflow-hidden bg-brand-50 ${className}`}>
      {hasImage ? (
        <img
          src={src ?? ""}
          alt={alt}
          loading="lazy"
          className="h-full w-full object-cover"
          onError={() => setFailedSrc(src ?? "")}
        />
      ) : (
        <div aria-hidden="true" className="grid h-full w-full place-items-center text-brand-700">
          {placeholderIcon ?? <House className="h-10 w-10" />}
        </div>
      )}
    </div>
  );
}
