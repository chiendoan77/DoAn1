"use client";

import { ReactNode, useEffect, useRef } from "react";

type Props = {
  open: boolean;
  title: string;
  description?: string;
  onClose: () => void;
  children: ReactNode;
};

export function Dialog({ open, title, description, onClose, children }: Props) {
  const dialogRef = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    const dialog = dialogRef.current;
    if (!dialog) return;
    if (open && !dialog.open) {
      dialog.showModal();
    } else if (!open && dialog.open) {
      dialog.close();
    }
  }, [open]);

  return (
    <dialog
      ref={dialogRef}
      aria-labelledby="dialog-title"
      aria-describedby={description ? "dialog-description" : undefined}
      onCancel={(event) => {
        event.preventDefault();
        onClose();
      }}
      onClose={onClose}
      className="m-auto max-h-[calc(100vh-32px)] w-[calc(100%-32px)] max-w-xl rounded-lg border border-border bg-surface p-0 text-text shadow-md"
    >
      <div className="border-b border-border px-5 py-4">
        <h2 id="dialog-title" className="text-xl font-bold">{title}</h2>
        {description && <p id="dialog-description" className="mt-1 text-sm text-muted">{description}</p>}
      </div>
      <div className="p-5">{children}</div>
    </dialog>
  );
}
