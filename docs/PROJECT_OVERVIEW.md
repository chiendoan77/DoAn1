# Tổng quan dự án

## Mục tiêu

Xây dựng website kết nối khách hàng với nhà cung cấp dịch vụ dọn dẹp và sửa chữa dân dụng, hỗ trợ khám phá dịch vụ, đặt lịch và theo dõi quá trình thực hiện.

## Phạm vi thông tin hiện có

Mô tả sản phẩm và stack ban đầu nằm trong [Readme.md](../Readme.md). Đây là mô tả khởi đầu; các chức năng tại đó chưa mặc nhiên là yêu cầu đã được xác nhận. Quy mô triển khai được định hướng là đồ án cá nhân, vì vậy kiến trúc cần cân bằng mục tiêu học tập với chi phí vận hành và độ phức tạp.

## Kiểm kê và triển khai hiện tại

Kiểm tra ngày 2026-11-04:

| Thành phần | Hiện trạng |
|---|---|
| `back-end/` | Maven reactor với Gateway và 4 Spring Boot app; Identity, Marketplace, Booking có business APIs, MySQL persistence config và H2 tests. Payment vẫn là adapter registry skeleton. |
| `font-end/` | Next.js/TypeScript/Tailwind; có landing page, auth, provider listing/approval, booking history và review. |
| `docs/` | Domain requirements, architecture và service docs; được cập nhật theo phần đã triển khai và phần còn mở. |
| `skill/` | Thư mục trống; chưa sử dụng. |
| Database / deployment | Local Docker Compose chạy MySQL với schema riêng cho Identity/Marketplace/Booking và toàn bộ app; schema do Hibernate `ddl-auto: update` cập nhật, chưa có migration. |

Lần kiểm kê ban đầu không có source code/API/database. Các luồng triển khai hiện tại được liệt kê trong [DEVELOPMENT_STATUS.md](./DEVELOPMENT_STATUS.md); đây chưa phải hệ thống production-ready.

## Khởi tạo và nghiệp vụ MVP

Đã triển khai đăng ký/đăng nhập JWT, hồ sơ/duyệt provider, danh mục và danh sách provider, booking cơ bản, history hai phía và review hai chiều sau completed. Frontend nối các luồng này qua Next.js BFF với cookie HttpOnly. Admin là role; thao tác duyệt provider nằm trong Marketplace, không có `admin-service`.

Backend full reactor tests, frontend lint/typecheck/build pass. Docker Compose đã được xác minh với MySQL thật, toàn bộ app healthy và frontend/API Gateway truy cập được trên local ports. Browser E2E và production deployment chưa được xác minh. Máy dùng JDK 25 với Java target 21; Maven chạy từ cache do không có trên PATH.

## Định hướng kỹ thuật được nêu

README nêu Java 21, Spring Boot 3, Next.js/React/TypeScript, MySQL và Docker. Backend dùng Spring Boot 3.5.0/Spring Cloud 2025.0.0; service DB cấu hình MySQL riêng với Hibernate `ddl-auto: update` (chưa có migrations). Next.js 16.4.5 bật Cache Components. Broker/Redis/payment gateway thật chưa được thêm.

## Trạng thái phê duyệt

Người dùng đã chọn bốn business services (Identity & Access, Marketplace, Booking, Payment) cùng API Gateway; đã xác nhận MVP gồm xác thực/phân quyền, marketplace/provider approval, booking, các thao tác Admin liên quan, review hai chiều và Payment service có khả năng mở rộng adapter. Quy tắc chi tiết về giờ, giá, nhà cung cấp thanh toán, và xử lý booking lặp còn mở. Xem [open-questions.md](./requirements/open-questions.md) và [DEVELOPMENT_STATUS.md](./DEVELOPMENT_STATUS.md).
