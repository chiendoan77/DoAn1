# Kiến trúc hệ thống

## Trạng thái

Kiến trúc hiện tại có Identity & Access, Profile, Catalog, Booking, Review và Notification; API Gateway là edge component. Review có deployable riêng. Marketplace legacy service và Payment skeleton đã được loại bỏ hoàn toàn (DEC-018, DEC-019).

## Nguyên tắc

- Phân ranh giới theo năng lực nghiệp vụ, quy trình và quyền sở hữu dữ liệu; không theo role/UI.
- Không tạo `admin-service`. Admin Portal dùng JWT + role ADMIN; từng domain service xử lý thao tác trên dữ liệu mình sở hữu.
- API key chỉ dành cho tích hợp server-to-server, không nhúng vào browser.
- Mỗi service sở hữu database/schema riêng; service khác chỉ dùng API/DTO.
- Payment có provider adapter độc lập, nhiều phương thức/provider có thể chạy đồng thời; thêm adapter không tắt toàn bộ Payment module.
- Dùng REST cho quyết định cần kết quả ngay; domain events được ghi bằng transactional outbox và phát qua RabbitMQ. Không dùng transaction xuyên service.
- Saga Payment và Payment lifecycle chưa triển khai theo phạm vi đã chốt; không tạo `PaymentSucceeded` giả.

## Thành phần và mức triển khai

1. **Identity & Access** — tài khoản, credential, role, account status/audit; RS256, refresh-token rotation/revocation và Admin status API đã triển khai.
2. **Profile** — customer/provider profiles, approval/suspension, public lookup, eligibility và idempotent rating projection.
3. **Catalog** — categories, provider packages/prices, Admin moderation/history, Flyway và package outbox.
4. **Booking** — package validation, price/name/duration snapshots, state transitions và `BookingCompleted` outbox. Slot conflict/repeat policy chưa triển khai.
5. **Review** — API riêng, Booking eligibility check, review storage và `ReviewSubmitted` outbox.
6. **Notification** — tiêu thụ package và `BookingCompleted` events, lưu inbox in-app; chưa có email/push/SMS.
7. **API Gateway** — public entry, routing và JWT RS256 validation cho route cần auth; backend services tiếp tục kiểm tra role.
8. **Next.js frontend** — public/customer/provider/Admin flows một phần; mọi API qua Gateway qua BFF.

Booking là source of truth cho booking status/participants; Review hỏi endpoint nội bộ để xác minh eligibility. Booking/Review/Catalog events được ghi bằng outbox; Notification tiêu thụ `BookingCompleted`, Profile tiêu thụ `ReviewSubmitted`. Complaint workflow chưa triển khai.

Các service có API/DTO và schema riêng. Identity/Profile/Catalog/Booking/Review/Notification dùng Flyway. Tích hợp nội bộ dùng API keys riêng; Gateway không route `/internal/**`. Compose định nghĩa MySQL, RabbitMQ, Prometheus, Grafana và Jaeger. Metrics/traces có cấu hình; central log collection chưa được nối. Chi tiết ở [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md).

## Luồng và tài liệu chi tiết

- [Service architecture](./architecture/service-architecture.md)
- [System context](./architecture/system-context.md)
- [Communication flow](./architecture/communication-flow.md)
- [Authentication flow](./architecture/authentication-flow.md)
- [Booking flow](./architecture/booking-flow.md)
- [Deployment architecture](./architecture/deployment-architecture.md)
- [Decisions](./DECISIONS.md)
