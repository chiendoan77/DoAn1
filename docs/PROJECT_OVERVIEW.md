# Tổng quan dự án

## Mục tiêu

Xây dựng website kết nối khách hàng với nhà cung cấp dịch vụ dọn dẹp và sửa chữa dân dụng, hỗ trợ khám phá dịch vụ, đặt lịch và theo dõi quá trình thực hiện.

## Phạm vi thông tin hiện có

Mô tả sản phẩm và stack ban đầu nằm trong [Readme.md](../Readme.md). Đây là mô tả khởi đầu; các chức năng tại đó chưa mặc nhiên là yêu cầu đã được xác nhận. Quy mô triển khai được định hướng là đồ án cá nhân, vì vậy kiến trúc cần cân bằng mục tiêu học tập với chi phí vận hành và độ phức tạp.

## Kiểm kê và triển khai hiện tại

Kiểm tra source và cấu hình repository ngày 2026-11-06:

| Thành phần | Hiện trạng |
|---|---|
| `back-end/` | Maven reactor có API Gateway và 8 Spring Boot services: Identity, Profile, Catalog, Marketplace, Booking, Review, Payment, Notification. Payment vẫn là adapter-registry skeleton. |
| `font-end/` | Next.js App Router, React, TypeScript, Tailwind và BFF. Không phải Angular. |
| `docker/`, `docker-compose.yml` | Compose định nghĩa MySQL, RabbitMQ, Prometheus, Grafana, Jaeger, Gateway, frontend và services; schema riêng cho service có dữ liệu; MySQL publish loopback host port qua `MYSQL_PORT` trong `.env`. |
| `docs/` | Tài liệu yêu cầu, kiến trúc, database, API và từng service. [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md) là inventory source hiện tại. |

Repository hiện có source code. Không dùng trạng thái kiểm kê ban đầu ở các tài liệu cũ để mô tả hiện trạng. Các luồng đã có bằng chứng được liệt kê trong [DEVELOPMENT_STATUS.md](./DEVELOPMENT_STATUS.md); đây chưa phải hệ thống production-ready.

## Khởi tạo và nghiệp vụ MVP

Đã triển khai RS256 + refresh/revoke, customer/provider profiles, Catalog packages, Booking gắn package và snapshot giá, Review riêng, outbox events và in-app booking/package notifications. Frontend nối các luồng qua Next.js BFF với HttpOnly cookies. Admin là role; không có `admin-service`.

Full backend Maven reactor: 39 tests trên 12 suites, không failure/error/skip; frontend lint/typecheck/build và Compose configuration validation đã pass. Browser E2E, full Compose runtime và production deployment chưa được xác minh. Payment lifecycle/saga được hoãn.

## Định hướng kỹ thuật được nêu

Stack có trong manifests: Java 21, Spring Boot 3.5.0, Spring Cloud 2025.0.0, Next.js 16.4.5, React, TypeScript, MySQL, RabbitMQ, Prometheus, Grafana, Jaeger và Docker Compose. Các schema dùng Flyway; Marketplace/Booking baseline vẫn cần dry-run trên bản sao DB hiện hữu. Payment gateway thật chưa được thêm.

## Trạng thái phê duyệt

Người dùng đã chốt Profile, Catalog, Review và Notification boundaries; RS256, refresh/revoke, customer profile, Booking/Review events và Booking-package snapshot đã triển khai. Payment lifecycle/saga, Pact/browser E2E, centralized logs và production integrations còn thiếu hoặc được hoãn. Xem [open-questions.md](./requirements/open-questions.md) và [DEVELOPMENT_STATUS.md](./DEVELOPMENT_STATUS.md).
