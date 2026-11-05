import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Nhà Tốt | Đặt lịch dịch vụ cho ngôi nhà",
  description: "Tìm dịch vụ dọn dẹp và sửa chữa phù hợp cho ngôi nhà của bạn.",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html lang="vi" className="h-full antialiased">
      <body className="flex min-h-full flex-col">{children}</body>
    </html>
  );
}
