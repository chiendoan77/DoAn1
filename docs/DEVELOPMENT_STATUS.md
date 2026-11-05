# Trạng thái phát triển

Ngày cập nhật: 2026-11-04.

## Đã triển khai

- Kiến trúc domain: Identity & Access, Marketplace, Booking, Payment và API Gateway; Admin là role/portal, không có `admin-service`.
- Identity: đăng ký CUSTOMER/PROVIDER với email, phone, username, họ tên; BCrypt, login email/phone, JWT, `/api/me`, bootstrap Admin cấu hình qua environment.
- Validation đăng ký: form dùng pattern tương thích trình duyệt và hiển thị lỗi tiếng Việt; Identity trả `errors` theo từng field cùng `detail` cho các lỗi validation/nghiệp vụ.
- Marketplace: danh mục `cleaning`/`repair`, provider application/profile/update, Admin duyệt, public list/detail provider đã duyệt (tối đa 10 theo rating), ảnh HTTPS URL, địa chỉ, review hai chiều và aggregate rating provider. Khi provider cập nhật hồ sơ thuộc mình, status trở lại `PENDING` và nội dung mới không public cho tới khi Admin duyệt lại.
- Booking: tạo booking, lịch sử riêng customer/provider, provider accept/reject/complete với ownership/state transition checks.
- Giao tiếp nội bộ: Booking xác minh provider qua Marketplace; Marketplace xác minh booking/participant/completion qua Booking. Cả hai endpoint dùng `SERVICE_API_KEY`, không route qua Gateway.
- API Gateway cấu hình CORS cho Angular local tại `http://localhost:4200`, cho phép preflight các method API và headers `Content-Type`/`Authorization`; origin ngoài allowlist bị từ chối.
- Frontend: trang chủ/service browse, auth, provider cards, booking, history/review, Admin provider approval; BFF giữ JWT trong cookie HttpOnly.
- Payment: adapter registry skeleton, chưa có payment API hoặc gateway thật.
- Backend Java sources đã được sắp theo package trách nhiệm (`controller`, `dto`, `model`, `repository`, `security`, cùng `client`, `service`, `config`, `exception` khi cần); tests ở `src/test/java` riêng cho từng Maven module.
- Docker Compose local khởi chạy MySQL, frontend, Gateway và bốn business services; tạo schema tự động và seed provider demo.
- Docs service, API, database và decisions được đồng bộ lại theo implementation hiện có.

## Kiểm thử đã xác minh

- Maven full reactor: 13 tests pass (Gateway 1, Identity 4, Marketplace 4, Booking 1, Payment 3).
- API Gateway CORS tests xác minh preflight từ Angular local được chấp nhận và origin ngoài allowlist bị từ chối.
- Marketplace integration tests xác minh provider chỉ sửa hồ sơ thuộc account của mình; thay đổi chuyển thành `PENDING`, ẩn bản cũ/mới khỏi public cho tới lần Admin duyệt lại.
- Identity validation tests: 4 pass, gồm lỗi định dạng từng field và mật khẩu vượt giới hạn 72 byte; frontend lint/typecheck pass.
- Maven full reactor `-DskipTests package`: pass.
- Docker thật: MySQL 8.0 chạy trong container tạm với ba schema riêng; Identity, Marketplace, Booking và Gateway đều health `UP`. Đã chạy luồng đăng ký/login, provider application/Admin approval, tạo booking, chuyển `ACCEPTED`/`COMPLETED`, gửi review qua Gateway; xác nhận dữ liệu được ghi trong cả ba schema. Container kiểm thử đã được dừng và xóa.
- `docker compose up --build --detach`: build thành công và toàn bộ 7 container (MySQL, frontend, Gateway, 4 business services) healthy; frontend và frontend BFF trả HTTP 200, Gateway trả hai danh mục và 10 provider demo.
- Frontend `npm run lint`, `npm run typecheck`, `npm run build`: pass; production build xác nhận `/register` dùng partial prerendering/Suspense.
- Test suite hiện dùng H2; Docker Compose dùng MySQL thật. Chưa có browser E2E tự động.

## Chưa triển khai

- Identity: Admin user management, email/phone verification, password recovery, token refresh/revocation, rate limiting.
- Marketplace: offering/giá, catalog CRUD, tìm kiếm/pagination, upload ảnh, audit/moderation review.
- Booking: slot conflict, repeat-category quota/Admin review, admin booking APIs, cancellation/reschedule, status history.
- Payment APIs, transaction schema, provider adapter thật/webhooks.
- MySQL migration scripts, production deployment/CI và browser E2E tự động.

## Còn cần xác nhận

- Booking open states, Admin review gate và complaint context visibility.
- Slot duration/timezone/boundaries, provider assignment, giá, cancellation/refund.
- Payment gateway/provider và phương thức cụ thể.
- Review edit/delete/moderation; Admin tạo provider thay provider hay chỉ duyệt.
- CI và deployment target.

## Môi trường và giới hạn

- Repository ban đầu không có source; frontend folder hiện tên `font-end/`, giữ nguyên để tránh đổi cấu trúc.
- Backend target Java 21; môi trường test đang dùng JDK 25 và Maven 3.9.16 trong cache (Maven không có trên PATH).
- Cấu hình local Compose dùng MySQL trong network nội bộ, ba schema riêng, volume giữ dữ liệu; các cổng frontend/API/service chỉ bind `127.0.0.1`.
- JWT/API key mặc định và MySQL không mật khẩu chỉ dành cho local development; không dùng Compose defaults trong production.

## Tiếp theo

1. Tạo migration scripts trước khi cần quản lý schema production.
2. Chốt các câu hỏi booking còn mở trước khi triển khai overlap/quota/Admin review.
3. Hoàn thiện Admin user/booking và payment chỉ khi đã chốt nghiệp vụ tương ứng.
