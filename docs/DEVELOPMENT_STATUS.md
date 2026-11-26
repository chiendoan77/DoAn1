# Trạng thái phát triển

Ngày đối chiếu source/configuration, full backend tests và frontend checks: 2026-11-26.

## Kiến trúc và inventory

- Backend Maven reactor gồm API Gateway, 1 module `security-common` và 6 Spring Boot services: Identity, Profile, Catalog, Booking, Review và Notification. Marketplace, Payment và security-test-support đã được dọn dẹp/gom gọn.
- Frontend là Next.js App Router/React/TypeScript trong `front-end/`; browser đi qua BFF và API Gateway.
- Các service có dữ liệu sở hữu schema riêng.
- Admin là role/portal, không có `admin-service`; Review có service và database riêng.
- `security-common` chứa `RsaJwtKeyLoader` và `JwtPrincipalService` dùng chung cho tất cả services; không còn bản copy cục bộ tại từng service.
- Inventory source nằm trong [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md).

## Đã triển khai

- **Identity:** đăng nhập/đăng ký, RS256 JWT, issuer/audience, refresh token opaque đã hash, rotation và revoke theo token family; có account status và Admin audit history. Logout bằng refresh token hợp lệ của bất kỳ tài khoản `ADMIN` nào xóa tất cả refresh sessions và account trong transaction; access JWT đã phát vẫn có hiệu lực đến TTL.
- **API Gateway:** route domain APIs, xác minh JWT RS256 cho endpoint được bảo vệ; service tiếp tục authorization theo role. Customer profile đã được route và bảo vệ.
- **Profile:** sở hữu customer/provider profiles, approval/suspension, provider lookup/eligibility và rating projection. Danh sách nhà cung cấp công khai hỗ trợ phân trang, tối đa 12 hồ sơ/trang. `ReviewSubmitted` được áp dụng idempotent theo review ID.
- **Catalog:** category/package CRUD, moderation, giá, Flyway và package outbox; Booking đồng bộ package qua API/DTO.
- **Booking:** tạo từ package APPROVED, xác minh provider qua Profile, lưu package name/price/duration snapshots, lịch sử và provider status transitions. `BookingCompleted` được ghi qua transactional outbox.
- **Review:** service riêng xác minh eligibility qua Booking; lưu review và phát `ReviewSubmitted` qua outbox. Marketplace chỉ còn dữ liệu/API legacy migration.
- **Notification:** nhận Catalog package events và `BookingCompleted`, lưu inbox idempotent. Email/push/SMS chưa triển khai.
- **Frontend/BFF:** booking gắn package, customer profile và notification views; token nằm trong HttpOnly cookies. BFF proxy hỗ trợ GET/POST/PUT/PATCH/DELETE.
- **Customer experience:** trang chủ bỏ khu vực danh mục và nhà cung cấp nổi bật, không đặt ô tìm kiếm dưới hero; tìm kiếm và điều hướng danh mục trong header vẫn giữ. Hỗ trợ mở email `support@nhatot.vn`. Chọn nhà cung cấp mở popup chứa các gói dạng card và form ngày/giờ, địa chỉ, mô tả; khách chưa đăng nhập được nhắc đăng nhập hoặc đăng ký. Payload và luồng booking hiện có được giữ nguyên. Khách hàng có thể cập nhật tên hiển thị, số liên hệ, địa chỉ mặc định và avatar HTTPS từ menu tài khoản. Đặt lịch lấy địa chỉ từ hồ sơ; provider menu chỉ xem để tránh tự chuyển hồ sơ về trạng thái chờ duyệt.
- **Payment:** adapter registry skeleton; payment lifecycle, `PaymentSucceeded`, webhook/refund/idempotency và saga được hoãn theo yêu cầu.
- **security-common refactor (DEC-017):** `JwtPrincipalService` được gom vào `security-common`; 6 bản duplicate đã xóa; 8 controller đã cập nhật import; `mvn compile` 9 module — BUILD SUCCESS.
- **notification-service refactor:** Thêm `NotificationService` layer; `NotificationController` và 2 consumers không còn inject repository trực tiếp.
- **review-service:** Thêm `ReviewExceptionHandler` (`@RestControllerAdvice`) trả RFC 7807 cho `ResponseStatusException`, `MethodArgumentNotValidException` và `DataIntegrityViolationException`.
- **payment-service (DEC-019):** Đã loại bỏ hoàn toàn khỏi backend reactor, docker-compose và gateway routing.
- **marketplace-service (DEC-018):** Đã loại bỏ hoàn toàn (chấp nhận bắt đầu dữ liệu từ đầu); không còn service, schema hay route nào liên quan.

## Persistence và vận hành

- Identity, Profile, Catalog, Booking, Review và Notification dùng Flyway với Hibernate `ddl-auto: validate`.
- Booking/Catalog/Profile/Review đặt timeout 2 giây cho kết nối và 3 giây cho phản hồi HTTP liên service.
- Gateway có kiểm tra role theo prefix, rate limit cho `/api/auth/**` và security headers.
- Booking có optimistic locking, exception handler, index trạng thái/thời gian và kiểm tra trùng gói/thời gian.
- Booking/Review/Notification hỗ trợ phân trang `page`/`size` tối đa 100.
- Booking/Review/Catalog outbox events được dùng cho luồng đã triển khai; không có transaction xuyên service. Payment event/saga chưa làm.
- Compose định nghĩa RabbitMQ, Prometheus, Grafana và Jaeger. Metrics/tracing đã cấu hình nhưng chưa xác minh runtime; centralized log collection chưa tích hợp.
- API keys riêng bảo vệ một số giao tiếp nội bộ; production mTLS/secret management chưa xác minh.

## Kiểm thử và xác minh

- **Docker Compose runtime & RabbitMQ:** Toàn bộ 13 containers khởi động thành công và healthy (MAN-013). Đã kiểm thử gửi/nhận message qua RabbitMQ topic exchange `doan1.events` thành công vào database (MAN-003). RabbitMQ management và Grafana đã được cấu hình credential `admin / admin123`.
- `mvn compile` toàn bộ reactor sau khi xóa marketplace, payment và gom security-test-support: BUILD SUCCESS.
- `mvn test` trên các service cốt lõi: Profile (6/6 tests PASS), Notification (2/2 tests PASS), Identity (9/9 tests PASS), Catalog (7/7 tests PASS), Booking (3/3 tests PASS), Review (2/2 tests PASS), API Gateway (6/6 tests PASS). Tổng cộng: 35 backend tests chạy trực tiếp và PASS 100%.
- [TEST_CASES.csv](./TEST_CASES.csv) có 92 test cases: 48 `PASS`, 12 `NOT_RUN`, 4 `NOT_IMPLEMENTED`, 28 `PLANNED`. Hai test CORS đã gỡ cùng cấu hình cố định khỏi API Gateway. Các testcase bổ sung ghi nhận các hạng mục chưa triển khai hoặc chưa có bằng chứng chạy, không được xem là PASS.
- Desktop auth `/register` và `/login` đã chuyển sang layout hai cột 50/50, cùng component auth, input icon bên trong và role card hai trạng thái. `/register` đã kiểm tra render trên desktop Chrome 1920×1020 và `/login` đã xác minh route 200.
- Trang `/services/cleaning` và `/services/repair` dùng catalog mới; chọn provider mở popup đặt lịch với danh sách gói, trạng thái selected, SafeImage, empty/error/loading, ngày/giờ, scroll lock/focus/Esc/backdrop.
- Full backend reactor chạy lại 2026-11-26: Gateway 6/6, Identity 9/9, Profile 6/6, Catalog 7/7, Review 2/2, Booking 3/3, Notification 2/2 — tổng 35 test PASS.
- Frontend lint/typecheck pass; production Webpack build pass với `CIRCLE_NODE_TOTAL=2` và Node heap 4096 MB. Build Turbopack mặc định không hoàn tất trên máy này do lỗi Node worker khi thu thập/kiểm tra trang trong điều kiện thiếu bộ nhớ. Browser xác nhận homepage không còn các khu vực danh mục/nhà cung cấp nổi bật hoặc thanh tìm kiếm dưới hero; ô tìm kiếm header vẫn còn. Trạng thái kiểm thử booking prompt được ghi riêng trong [TEST_CASES.csv](./TEST_CASES.csv).
- Xác minh phân trang provider: 7 Profile tests pass, bao gồm page size 12 và cap limit lớn hơn 12.
- Xác minh Admin logout/delete: 9 Identity tests pass, gồm xóa Admin cùng mọi refresh session, giữ nguyên account CUSTOMER và không xóa account bằng token không hợp lệ.
- `mvn install` trước đó không hoàn tất do Maven không thể move artifact tạm trong mounted `.m2`; reactor `package` và full tests thành công.

## Chưa triển khai hoặc chưa xác minh

- Slot overlap, repeat-booking quota/Admin review, booking cancel/reschedule, status history và Booking admin endpoints.
- Payment lifecycle/saga (được hoãn), email/push/SMS và centralized logs.
- Pact suite; `CatalogClientContractTests` là focused client contract test, không phải Pact. Browser E2E chưa có.
- Full runtime Compose, MySQL migration rehearsal, production deployment, và runtime Prometheus/Grafana/Jaeger chưa được xác minh.

Không xem cấu hình, route hoặc skeleton là bằng chứng feature đã hoàn tất; các policy booking còn mở vẫn cần chốt/triển khai riêng.

## Đã triển khai

- **Identity:** đăng nhập/đăng ký, RS256 JWT, issuer/audience, refresh token opaque đã hash, rotation và revoke theo token family; có account status và Admin audit history. Logout bằng refresh token hợp lệ của bất kỳ tài khoản `ADMIN` nào xóa tất cả refresh sessions và account trong transaction; access JWT đã phát vẫn có hiệu lực đến TTL.
- **API Gateway:** route domain APIs, xác minh JWT RS256 cho endpoint được bảo vệ; service tiếp tục authorization theo role. Customer profile đã được route và bảo vệ.
- **Profile:** sở hữu customer/provider profiles, approval/suspension, provider lookup/eligibility và rating projection. Danh sách nhà cung cấp công khai hỗ trợ phân trang, tối đa 12 hồ sơ/trang. `ReviewSubmitted` được áp dụng idempotent theo review ID.
- **Catalog:** category/package CRUD, moderation, giá, Flyway và package outbox; Booking đồng bộ package qua API/DTO.
- **Booking:** tạo từ package APPROVED, xác minh provider qua Profile, lưu package name/price/duration snapshots, lịch sử và provider status transitions. `BookingCompleted` được ghi qua transactional outbox.
- **Review:** service riêng xác minh eligibility qua Booking; lưu review và phát `ReviewSubmitted` qua outbox. Marketplace chỉ còn dữ liệu/API legacy migration.
- **Notification:** nhận Catalog package events và `BookingCompleted`, lưu inbox idempotent. Email/push/SMS chưa triển khai.
- **Frontend/BFF:** booking gắn package, customer profile và notification views; token nằm trong HttpOnly cookies. BFF proxy hỗ trợ GET/POST/PUT/PATCH/DELETE.
- **Customer experience:** trang chủ bỏ khu vực danh mục và nhà cung cấp nổi bật, không đặt ô tìm kiếm dưới hero; tìm kiếm và điều hướng danh mục trong header vẫn giữ. Hỗ trợ mở email `support@nhatot.vn`. Chọn nhà cung cấp mở popup chứa các gói dạng card và form ngày/giờ, địa chỉ, mô tả; khách chưa đăng nhập được nhắc đăng nhập hoặc đăng ký. Payload và luồng booking hiện có được giữ nguyên. Khách hàng có thể cập nhật tên hiển thị, số liên hệ, địa chỉ mặc định và avatar HTTPS từ menu tài khoản. Đặt lịch lấy địa chỉ từ hồ sơ; provider menu chỉ xem để tránh tự chuyển hồ sơ về trạng thái chờ duyệt.
- **Payment:** adapter registry skeleton; payment lifecycle, `PaymentSucceeded`, webhook/refund/idempotency và saga được hoãn theo yêu cầu.

## Persistence và vận hành

- Identity, Profile, Catalog, Marketplace, Booking, Review và Notification dùng Flyway với Hibernate `ddl-auto: validate`; Payment chưa có schema nghiệp vụ.
- Marketplace/Booking có baseline/versioned migrations; chưa dry-run nâng cấp trên bản sao MySQL hiện hữu.
- Booking/Review/Catalog outbox events được dùng cho luồng đã triển khai; không có transaction xuyên service. Payment event/saga chưa làm.
- Compose định nghĩa RabbitMQ, Prometheus, Grafana và Jaeger. Metrics/tracing đã cấu hình nhưng chưa xác minh runtime; centralized log collection chưa tích hợp.
- API keys riêng bảo vệ một số giao tiếp nội bộ; production mTLS/secret management chưa xác minh.

## Kiểm thử và xác minh

- `mvn -DskipTests package`: toàn bộ 12 reactor modules build thành công.
- Full `mvn test` gần nhất trước thay đổi phân trang: 39 tests trên 12 suite, 0 failure, 0 error, 0 skipped. Profile suite có 7 tests và Identity suite có 9 tests pass khi chạy riêng. [TEST_CASES.csv](./TEST_CASES.csv) có 92 test cases: 48 `PASS`, 12 `NOT_RUN`, 4 `NOT_IMPLEMENTED`, 28 `PLANNED`; từng backend test trỏ tới Surefire report hoặc ghi rõ lệnh kiểm thử tương ứng.
- Frontend lint/typecheck, production Webpack build và `docker compose config -q` pass; build Turbopack mặc định không hoàn tất trên máy này do lỗi Node worker trong môi trường thiếu bộ nhớ.
- Xác minh phân trang provider: 7 Profile tests pass, bao gồm page size 12 và cap limit lớn hơn 12.
- Xác minh Admin logout/delete: 9 Identity tests pass, gồm xóa Admin cùng mọi refresh session, giữ nguyên account CUSTOMER và không xóa account bằng token không hợp lệ.
- `mvn install` trước đó không hoàn tất do Maven không thể move artifact tạm trong mounted `.m2`; reactor `package` và full tests thành công.

## Chưa triển khai hoặc chưa xác minh

- Slot overlap, repeat-booking quota/Admin review, booking cancel/reschedule, status history và Booking admin endpoints.
- Payment lifecycle/saga (được hoãn), email/push/SMS và centralized logs.
- Pact suite; `CatalogClientContractTests` là focused client contract test, không phải Pact. Browser E2E chưa có.
- Full runtime Compose, MySQL migration rehearsal, production deployment, và runtime Prometheus/Grafana/Jaeger chưa được xác minh.

Không xem cấu hình, route hoặc skeleton là bằng chứng feature đã hoàn tất; các policy booking còn mở vẫn cần chốt/triển khai riêng.
