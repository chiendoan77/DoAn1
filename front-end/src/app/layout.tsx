import styles from "./layout.module.css";
import type { Metadata } from "next";
import "./globals.css";
import { SessionProvider } from "@/shared/state/session";
import { ToastProvider } from "@/shared/ui/toast";

export const metadata: Metadata = {
  title: "Nhà Tốt | Đặt lịch dịch vụ cho ngôi nhà",
  description: "Tìm dịch vụ dọn dẹp và sửa chữa phù hợp cho ngôi nhà của bạn.",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html lang="vi" className={styles.style1}>
      <body className={styles.style2}>
        <a className="skip-link" href="#main-content">Bỏ qua tới nội dung</a>
        <SessionProvider>
          <ToastProvider>{children}</ToastProvider>
        </SessionProvider>
      </body>
    </html>
  );
}
