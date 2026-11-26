"use client";

import { useState } from "react";
import { ReactNode } from "react";
import { Dialog } from "./dialog";
import { Button } from "./button";

type Props = {
  open: boolean;
  title: string;
  description: string;
  confirmLabel: string;
  cancelLabel?: string;
  requireReason?: boolean;
  requireText?: string;
  loading?: boolean;
  onCancel: () => void;
  onConfirm: (reason?: string) => void | Promise<void>;
  children?: ReactNode;
};

export function ConfirmDialog({
  open,
  title,
  description,
  confirmLabel,
  cancelLabel = "Hủy",
  requireReason = false,
  requireText,
  loading = false,
  onCancel,
  onConfirm,
  children,
}: Props) {
  const [reason, setReason] = useState("");
  const [text, setText] = useState("");
  const disabled = loading
    || (requireReason && reason.trim().length < 10)
    || (requireText ? text !== requireText : false);

  return (
    <Dialog open={open} title={title} description={description} onClose={onCancel}>
      {children}
      {requireReason && (
        <label className="block text-sm font-semibold">
          Lý do
          <textarea
            autoFocus
            value={reason}
            onChange={(event) => setReason(event.target.value)}
            minLength={10}
            maxLength={500}
            rows={4}
            className="mt-2 w-full rounded-md border border-border px-3 py-2"
          />
          <span className="mt-1 block font-normal text-muted">Tối thiểu 10 ký tự, tối đa 500 ký tự.</span>
        </label>
      )}
      {requireText && (
        <label className="block text-sm font-semibold">
          Gõ lại <span className="font-bold">{requireText}</span> để xác nhận
          <input
            autoFocus
            value={text}
            onChange={(event) => setText(event.target.value)}
            className="mt-2 w-full rounded-md border border-border px-3 py-2"
          />
        </label>
      )}
      <div className="mt-5 flex justify-end gap-3">
        <Button variant="secondary" type="button" onClick={onCancel}>{cancelLabel}</Button>
        <Button variant="destructive" type="button" loading={loading} disabled={disabled} onClick={() => void onConfirm(requireReason ? reason.trim() : undefined)}>
          {confirmLabel}
        </Button>
      </div>
    </Dialog>
  );
}
