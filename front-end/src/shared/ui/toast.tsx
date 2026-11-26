"use client";

import { createContext, useCallback, useContext, useState } from "react";
import { CheckCircle2, CircleAlert, X } from "lucide-react";

type Toast = { id: number; tone: "success" | "error"; message: string };
type ToastContextValue = { success: (message: string) => void; error: (message: string) => void };
const ToastContext = createContext<ToastContextValue | null>(null);

export function ToastProvider({ children }: { children: React.ReactNode }) {
  const [toasts, setToasts] = useState<Toast[]>([]);
  const show = useCallback((tone: Toast["tone"], message: string) => {
    const id = Date.now();
    setToasts((current) => [...current, { id, tone, message }]);
    window.setTimeout(() => setToasts((current) => current.filter((item) => item.id !== id)), 5000);
  }, []);
  const value = {
    success: (message: string) => show("success", message),
    error: (message: string) => show("error", message),
  };
  return (
    <ToastContext.Provider value={value}>
      {children}
      <div className="fixed bottom-4 right-4 z-[200] flex w-[calc(100%-32px)] max-w-sm flex-col gap-2" aria-live="polite">
        {toasts.map((toast) => (
          <div key={toast.id} role={toast.tone === "error" ? "alert" : "status"} className={`flex items-start gap-3 rounded-md border p-4 shadow-md ${toast.tone === "success" ? "border-success/20 bg-success-bg text-success" : "border-danger/20 bg-danger-bg text-danger"}`}>
            {toast.tone === "success" ? <CheckCircle2 className="mt-0.5 h-5 w-5" /> : <CircleAlert className="mt-0.5 h-5 w-5" />}
            <p className="flex-1 text-sm font-semibold">{toast.message}</p>
            <button type="button" aria-label="Đóng thông báo" onClick={() => setToasts((current) => current.filter((item) => item.id !== toast.id))}>
              <X className="h-4 w-4" />
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const value = useContext(ToastContext);
  if (!value) throw new Error("useToast must be used inside ToastProvider.");
  return value;
}
