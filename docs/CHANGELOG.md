# Changelog

## 2026-11-06 — Xóa tài khoản Admin khi đăng xuất

- Logout bằng refresh token hợp lệ của Admin thu hồi mọi refresh-token session của account đó và xóa account trong cùng transaction.
- Logout CUSTOMER/PROVIDER và token không hợp lệ không xóa account.
- Admin khác không bị ảnh hưởng; access JWT đã phát vẫn có hiệu lực tới khi hết TTL. Cần bootstrap lại account Admin đã đăng xuất.

## 2026-11-06 — Tách trang dịch vụ và cập nhật hồ sơ khách hàng

- Đưa danh mục dọn dẹp và sửa chữa thành hai trang riêng; trang chủ tập trung giới thiệu dịch vụ.
- Thêm thẻ hồ sơ dạng hóa đơn trong menu avatar cho khách hàng, lưu tên hiển thị, số điện thoại, địa chỉ mặc định và URL avatar HTTPS.
- Tạo hồ sơ/địa chỉ mặc định khi đăng ký khách hàng; luồng đặt lịch tự dùng địa chỉ đã lưu và chỉ yêu cầu chọn gói/thời gian hẹn.
- Giữ hồ sơ nhà cung cấp ở chế độ chỉ xem trong menu tài khoản để không làm thay đổi trạng thái duyệt.

## 2026-11-06 — Security, booking, review and observability follow-up

- Chuyển JWT sang RS256 với refresh-token rotation/revocation; BFF giữ token trong HttpOnly cookies.
- Thêm customer profile; Booking tạo từ Catalog package và lưu giá/tên/thời lượng snapshot.
- Tách Review API/data ownership khỏi Marketplace; thêm BookingCompleted/ReviewSubmitted outboxes và consumers.
- Thêm Flyway baseline/migrations cho Marketplace và Booking, Review schema riêng, và Notification BOOKING linkage.
- Cấu hình Prometheus/Grafana/Jaeger metrics/traces; centralized logs và Payment lifecycle/saga vẫn chưa triển khai.
- Sửa thiếu import validation trong `CreateBookingRequest`.
- Mở rộng Next.js BFF proxy để chuyển tiếp PUT/DELETE, khớp API profile/package và thao tác archive.
- Viết lại README ngoài thành hướng dẫn repository; tạo API external integration guide và CSV testcase kèm trạng thái/bằng chứng.
- Rà soát/cập nhật API, architecture, data ownership, service, testing, deployment và project status docs để khớp source hiện tại.
- Xác minh toàn reactor `mvn -DskipTests package` và `mvn test`: 39 tests/12 suites pass, 0 failure/error/skip. Frontend lint/typecheck/build pass sau thay đổi BFF; Compose config pass.

## 2026-11-06 — Profile, Catalog and Notification services

- Tách provider profile sang Profile; Identity có account status và Admin audit endpoint.
- Thêm Catalog package workflow, VND price validation, moderation history, optimistic versioning và Flyway.
- Thêm transactional outbox publisher/confirm cho Catalog → RabbitMQ → Notification inbox.
- Thêm frontend provider/admin package pages và in-app notification list.
- Tại giai đoạn đó Gateway dùng JWT HS256; cấu hình này sau đó được thay bằng RS256 ở mục cập nhật phía trên. Thêm API keys riêng theo integration.
- Cập nhật service/API/database/architecture docs; ghi rõ Review service, RS256, refresh/revoke, Booking/Payment events, saga và runtime Compose còn thiếu.
- Xác minh Maven reactor: 32 tests/10 suites pass; frontend lint/typecheck/build và Compose config pass.

## 2026-11-06 — Sắp xếp source theo feature/domain

- Tách request/response DTO, entity, enum, mapper và lớp nghiệp vụ trong Identity, Marketplace, Booking.
- Chuyển màn hình Next.js vào `features/` và thành phần dùng chung vào `shared/components/`.
- Giữ nguyên API, schema, hành vi nghiệp vụ và ranh giới microservice; đồng bộ tài liệu cấu trúc/status.
- Xác minh Maven 16 tests, frontend lint, typecheck và production build đều pass.

## 2026-11-06 — Kiểm kê cấu trúc và đồng bộ implementation status

- Thêm [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md) mô tả cây source thực tế, ranh giới deployable và ownership.
- Làm rõ repository hiện dùng Next.js + Spring Boot microservices, không phải Angular + modular monolith trong sơ đồ tham khảo.
- Đối chiếu lại Marketplace: offering CRUD/API/schema/UI được giữ ở trạng thái yêu cầu chưa triển khai vì không có trong source hiện tại; cập nhật API, database và frontend docs để không mô tả endpoint giả là API hoạt động.
- Không di chuyển source code hoặc thay đổi runtime behavior.

## 2026-11-05 — Provider tự cập nhật hồ sơ có duyệt lại

- Thêm `PUT /api/provider/profile`, xác thực role và ownership bằng account ID trong JWT.
- Mỗi lần cập nhật chuyển hồ sơ sang `PENDING`, ẩn khỏi public cho tới khi Admin duyệt lại.
- Bổ sung kiểm thử ownership, ẩn hồ sơ và hiển thị lại sau duyệt.

## 2026-11-05 — CORS cho origin local `localhost:4200` qua API Gateway

- Cho phép origin `http://localhost:4200` gọi Gateway với các method API và headers `Content-Type`/`Authorization`; đây là origin dev client, không phải frontend framework đang có trong repository.
- Thêm kiểm thử preflight cho origin được phép và từ chối origin ngoài allowlist.

## 2026-11-04 — Tài liệu khởi tạo

- Ghi nhận repository rỗng ở các thư mục backend/frontend/docs/skill.
- Tạo baseline requirements, domain proposal, kiến trúc, security, testing, deployment và roadmap.
- Làm rõ Admin là vai trò/portal; thao tác thuộc domain sở hữu dữ liệu, không mặc định có `admin-service`.
- Mọi ranh giới và chức năng chưa được xác nhận đều được giữ ở trạng thái đề xuất/mở.

## 2026-11-04 — Chốt domain và MVP khởi đầu

- Chốt microservices Identity & Access, Marketplace, Booking, Payment và API Gateway; không tạo `admin-service`.
- Chốt auth/role, marketplace/provider approval, booking, Admin operations, review hai chiều và Payment adapter đa-provider.
- Ghi nhận slot overlap, repeat booking quota theo category và Admin review cho lần đặt lặp.
- Chốt Admin Portal JWT + role; API key chỉ cho server-to-server.

## 2026-11-04 — Phase 1 skeleton

- Tạo Maven reactor cho API Gateway, Identity, Marketplace, Booking và Payment.
- Tạo health/config và Spring Boot context tests; thêm Payment provider adapter registry skeleton.
- Scaffold Next.js/TypeScript/Tailwind và landing page tiếng Việt.
- Build/test backend và frontend đều pass; Docker deployment chưa kiểm tra vì Docker daemon không chạy.

## 2026-11-04 — Identity, marketplace và booking flows (MVP một phần)

- Thêm Identity registration/login bằng email hoặc phone, BCrypt, JWT và tùy chọn bootstrap Admin.
- Thêm Marketplace provider profiles, moderation, public provider list/detail, review hai chiều và rating.
- Thêm Booking create/history/state transitions; xác minh provider/review qua các API nội bộ có service key.
- Thêm Next.js auth, provider browsing/approval, booking/history/review, HttpOnly-cookie BFF.
- Loại identity account ID khỏi public provider DTO; chỉ cấp trong DTO nội bộ cần thiết cho Booking.
- Cập nhật API/DB/service docs; xác minh full backend reactor 11 tests, frontend lint/typecheck/build.
- Chưa xác minh MySQL thật hoặc browser E2E; slot/quota policies và payment API vẫn chưa triển khai.

## 2026-11-04 — Sắp xếp package Java và kiểm thử MySQL trong Docker

- Chuyển controller, DTO, model, repository, security, service, client, config và exception vào package trách nhiệm trong các module liên quan; giữ application entry points ở package gốc.
- Tách request/response DTO của Booking khỏi controller/client; giữ test trong `src/test/java` theo Maven.
- Xác minh full Maven reactor: 11 tests pass và package pass.
- Chạy MySQL 8.0 tạm trong Docker, khởi động Identity/Marketplace/Booking/Gateway và xác minh luồng provider-booking-review qua Gateway cùng dữ liệu trong ba schema; container được dọn sau test.

## 2026-11-04 — Docker Compose chạy toàn bộ stack local

- Thêm một lệnh `docker compose up --build` để build/chạy frontend, Gateway, bốn business services và MySQL với schema riêng.
- Thêm health checks, thứ tự khởi động, seed provider demo và persistent MySQL volume.
- Publish các cổng app chỉ trên `127.0.0.1`; giữ MySQL trong network nội bộ.
- Xác minh toàn bộ 7 container healthy, frontend/BFF HTTP 200 và provider demo truy cập qua Gateway.

## 2026-11-04 — Hiển thị lỗi validation đăng ký

- Sửa phone/username pattern cho chuẩn regex `v` của trình duyệt; invalid input nhận lỗi tiếng Việt ngay tại trường.
- Identity trả lỗi validation theo từng field và `detail`; chuẩn hóa lỗi nghiệp vụ để BFF không phải hiện thông báo chung.
- Thêm regression tests cho invalid fields và giới hạn 72 byte BCrypt.
