# Trạng thái phát triển

Ngày đối chiếu source/configuration, full backend tests và frontend checks: 2026-11-06.

## Kiến trúc và inventory

- Backend Maven reactor gồm API Gateway, security modules và 8 Spring Boot services: Identity, Profile, Catalog, Marketplace, Booking, Review, Payment và Notification.
- Frontend là Next.js App Router/React/TypeScript trong `font-end/`; browser đi qua BFF và API Gateway.
- Các service có dữ liệu sở hữu schema riêng. Payment lifecycle/database chưa triển khai.
- Admin là role/portal, không có `admin-service`; Review có service và database riêng. Marketplace giữ dữ liệu/API legacy phục vụ migration.
- Inventory source nằm trong [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md).

## Đã triển khai

- **Identity:** đăng nhập/đăng ký, RS256 JWT, issuer/audience, refresh token opaque đã hash, rotation và revoke theo token family; có account status và Admin audit history. Access JWT còn hiệu lực đến TTL sau logout.
- **API Gateway:** route domain APIs, xác minh JWT RS256 cho endpoint được bảo vệ; service tiếp tục authorization theo role. Customer profile đã được route và bảo vệ.
- **Profile:** sở hữu customer/provider profiles, approval/suspension, provider lookup/eligibility và rating projection. `ReviewSubmitted` được áp dụng idempotent theo review ID.
- **Catalog:** category/package CRUD, moderation, giá, Flyway và package outbox; Booking đồng bộ package qua API/DTO.
- **Booking:** tạo từ package APPROVED, xác minh provider qua Profile, lưu package name/price/duration snapshots, lịch sử và provider status transitions. `BookingCompleted` được ghi qua transactional outbox.
- **Review:** service riêng xác minh eligibility qua Booking; lưu review và phát `ReviewSubmitted` qua outbox. Marketplace chỉ còn dữ liệu/API legacy migration.
- **Notification:** nhận Catalog package events và `BookingCompleted`, lưu inbox idempotent. Email/push/SMS chưa triển khai.
- **Frontend/BFF:** booking gắn package, customer profile và notification views; token nằm trong HttpOnly cookies. BFF proxy hỗ trợ GET/POST/PUT/PATCH/DELETE.
- **Payment:** adapter registry skeleton; payment lifecycle, `PaymentSucceeded`, webhook/refund/idempotency và saga được hoãn theo yêu cầu.

## Persistence và vận hành

- Identity, Profile, Catalog, Marketplace, Booking, Review và Notification dùng Flyway với Hibernate `ddl-auto: validate`; Payment chưa có schema nghiệp vụ.
- Marketplace/Booking có baseline/versioned migrations; chưa dry-run nâng cấp trên bản sao MySQL hiện hữu.
- Booking/Review/Catalog outbox events được dùng cho luồng đã triển khai; không có transaction xuyên service. Payment event/saga chưa làm.
- Compose định nghĩa RabbitMQ, Prometheus, Grafana và Jaeger. Metrics/tracing đã cấu hình nhưng chưa xác minh runtime; centralized log collection chưa tích hợp.
- API keys riêng bảo vệ một số giao tiếp nội bộ; production mTLS/secret management chưa xác minh.

## Kiểm thử và xác minh

- `mvn -DskipTests package`: toàn bộ 12 reactor modules build thành công.
- Full `mvn test`: 39 tests trên 12 suite, 0 failure, 0 error, 0 skipped. [TEST_CASES.csv](./TEST_CASES.csv) có 52 dòng: 42 `PASS`, 5 `NOT_RUN`, 4 `NOT_IMPLEMENTED`, 1 `DEFERRED`; từng backend test trỏ tới Surefire report.
- Frontend lint/typecheck/production build và `docker compose config -q` pass; frontend checks được chạy lại sau khi bổ sung PUT/DELETE cho BFF.
- `mvn install` trước đó không hoàn tất do Maven không thể move artifact tạm trong mounted `.m2`; reactor `package` và full tests thành công.

## Chưa triển khai hoặc chưa xác minh

- Slot overlap, repeat-booking quota/Admin review, booking cancel/reschedule, status history và Booking admin endpoints.
- Payment lifecycle/saga (được hoãn), email/push/SMS và centralized logs.
- Pact suite; `CatalogClientContractTests` là focused client contract test, không phải Pact. Browser E2E chưa có.
- Full runtime Compose, MySQL migration rehearsal, production deployment, và runtime Prometheus/Grafana/Jaeger chưa được xác minh.

Không xem cấu hình, route hoặc skeleton là bằng chứng feature đã hoàn tất; các policy booking còn mở vẫn cần chốt/triển khai riêng.
