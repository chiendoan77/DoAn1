# Frontend requirements

Các portal/page dưới đây phản ánh luồng MVP được chọn; page chi tiết và UI behavior vẫn cần thiết kế.

- Public: trang chủ, danh sách/tìm kiếm, chi tiết dịch vụ, đăng ký/đăng nhập.
- Customer: hồ sơ, lịch sử/chi tiết booking, cảnh báo booking lặp, payment và review provider sau completed.
- Provider: hồ sơ, offering, booking được phân công, hoàn thành công việc và review khách sau completed. UI offering là yêu cầu đã xác nhận nhưng chưa triển khai; Marketplace API/schema offering cũng chưa có.
- Admin: quản lý user/provider/booking, duyệt booking lặp và tra cứu payment theo quyền qua APIs domain.
- Admin Portal dùng JWT + role ADMIN; không đặt server-to-server API key trong trình duyệt.
- Trạng thái UI: loading, empty, error; layout responsive.
- Không dùng mock data như dữ liệu production; mọi mock trong giai đoạn design phải được đánh dấu.

Routes, design system, accessibility target và nội dung tiếng Việt/đa ngôn ngữ cần chốt.

Hiện đã triển khai public landing/provider/package browse, auth/refresh, customer profile, booking từ package/history/review, provider application/history/status actions và Admin provider/package moderation qua BFF. Admin user/booking, payment, search/filter nâng cao và booking repeat/slot workflows chưa triển khai.
