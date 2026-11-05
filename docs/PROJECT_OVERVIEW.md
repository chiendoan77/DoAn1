# Tổng quan dự án

## Mục tiêu

Xây dựng website kết nối khách hàng với nhà cung cấp dịch vụ dọn dẹp và sửa chữa dân dụng, hỗ trợ khám phá dịch vụ, đặt lịch và theo dõi quá trình thực hiện.

## Phạm vi thông tin hiện có

Mô tả sản phẩm và stack ban đầu nằm trong [Readme.md](../Readme.md). Đây là mô tả khởi đầu; các chức năng tại đó chưa mặc nhiên là yêu cầu đã được xác nhận. Quy mô triển khai được định hướng là đồ án cá nhân, vì vậy kiến trúc cần cân bằng mục tiêu học tập với chi phí vận hành và độ phức tạp.

## Kiểm kê và triển khai hiện tại

Kiểm tra source và cấu hình repository ngày 2026-11-06:

| Thành phần | Hiện trạng |
|---|---|
| `back-end/` | Maven reactor có API Gateway và 4 Spring Boot apps. Identity, Marketplace, Booking có API và persistence; Payment là adapter-registry skeleton. |
| `font-end/` | Next.js App Router, React, TypeScript, Tailwind và BFF. Không phải Angular. |
| `docker/`, `docker-compose.yml` | Compose chạy MySQL, Gateway, frontend và bốn business apps; ba schema MySQL cho Identity, Marketplace, Booking; MySQL không publish port host. |
| `docs/` | Tài liệu yêu cầu, kiến trúc, database, API và từng service. [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md) là inventory source hiện tại. |

Repository hiện có source code. Không dùng trạng thái kiểm kê ban đầu ở các tài liệu cũ để mô tả hiện trạng. Các luồng đã có bằng chứng được liệt kê trong [DEVELOPMENT_STATUS.md](./DEVELOPMENT_STATUS.md); đây chưa phải hệ thống production-ready.

## Khởi tạo và nghiệp vụ MVP

Đã triển khai đăng ký/đăng nhập JWT, hồ sơ/duyệt provider, danh mục và danh sách provider, booking cơ bản, history hai phía và review hai chiều sau completed. Frontend nối các luồng này qua Next.js BFF với cookie HttpOnly. Admin là role; thao tác duyệt provider nằm trong Marketplace, không có `admin-service`. Quản lý gói dịch vụ/provider offerings là yêu cầu đã nêu nhưng chưa tìm thấy implementation tương ứng trong source hiện tại.

Hồ sơ kiểm thử trước đây ghi nhận backend tests, frontend lint/typecheck/build và Docker Compose với MySQL local đã pass; các lệnh này không được chạy lại trong lần cập nhật tài liệu này. Browser E2E và production deployment chưa được xác minh. Máy dùng JDK 25 với Java target 21; Maven chạy từ cache do không có trên PATH.

## Định hướng kỹ thuật được nêu

Stack có trong manifests: Java 21, Spring Boot 3.5.0, Spring Cloud 2025.0.0, Next.js 16.4.5, React, TypeScript, MySQL và Docker Compose. Schema hiện được Hibernate cập nhật bằng `ddl-auto: update`; chưa có migrations. Broker/Redis/payment gateway thật chưa được thêm. Cấu trúc đề xuất trong README không thay thế inventory thực tế.

## Trạng thái phê duyệt

Người dùng đã chọn bốn business services (Identity & Access, Marketplace, Booking, Payment) cùng API Gateway; đã xác nhận MVP gồm xác thực/phân quyền, marketplace/provider approval, booking, các thao tác Admin liên quan, review hai chiều và Payment service có khả năng mở rộng adapter. Cấu trúc repository triển khai là microservices, không phải Angular + modular monolith như một số sơ đồ tham khảo. Quy tắc chi tiết về giờ, giá, nhà cung cấp thanh toán, và xử lý booking lặp còn mở. Xem [open-questions.md](./requirements/open-questions.md) và [DEVELOPMENT_STATUS.md](./DEVELOPMENT_STATUS.md).
