"use client";

import { ReactNode, useEffect, useRef } from "react";

type Props = {
  open: boolean;
  title: string;
  id?: string;
  description?: string;
  onClose: () => void;
  children: ReactNode;
};

export function Dialog({ open, title, id, description, onClose, children }: Props) {
  const dialogRef = useRef<HTMLDialogElement>(null);
  const titleId = id ? `${id}-title` : "dialog-title";
  const descriptionId = id ? `${id}-description` : "dialog-description";

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
      id={id}
      aria-labelledby={titleId}
      aria-describedby={description ? descriptionId : undefined}
      onCancel={(event) => {
        event.preventDefault();
        onClose();
      }}
      onClose={onClose}
      className="m-auto max-h-[calc(100vh-32px)] w-[calc(100%-32px)] max-w-xl rounded-lg border border-border bg-surface p-0 text-text shadow-md"
    >
      <div className="border-b border-border px-5 py-4">
        <h2 id={titleId} className="text-xl font-bold">{title}</h2>
        {description && <p id={descriptionId} className="mt-1 text-sm text-muted">{description}</p>}
      </div>
      <div className="p-5">{children}</div>
    </dialog>
  );
}
