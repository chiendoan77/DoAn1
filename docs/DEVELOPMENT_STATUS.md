# Trạng thái phát triển

Ngày đối chiếu source/configuration: 2026-11-06.

## Kiến trúc và inventory

- Backend là Maven reactor gồm API Gateway và bốn Spring Boot apps: Identity, Marketplace, Booking, Payment.
- Frontend là Next.js App Router/React/TypeScript trong `font-end/`; không phải Angular.
- MySQL local nằm trong Docker Compose, tạo ba schema `doan1_identity`, `doan1_marketplace`, `doan1_booking`; không publish cổng MySQL ra host.
- Admin là role/portal, không có `admin-service`. Review thuộc Marketplace; Booking xác minh review qua API nội bộ.
- Cây source được ghi tại [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md).

## Đã triển khai trong source hiện tại

- **Identity:** đăng ký CUSTOMER/PROVIDER, đăng nhập email/phone, BCrypt, JWT, `/api/me`, bootstrap Admin tùy chọn.
- **Marketplace:** danh mục cố định `cleaning`/`repair`, provider application/profile/update, Admin duyệt provider, public provider list/detail, review hai chiều và tổng hợp rating. Provider cập nhật hồ sơ của mình sẽ đưa hồ sơ về `PENDING`.
- **Booking:** tạo booking theo provider/category, lịch sử riêng customer/provider, provider accept/reject/complete với kiểm tra ownership và transition.
- **Tích hợp nội bộ:** Booking xác minh provider qua Marketplace; Marketplace xác minh booking/participant/completion qua Booking. Hai endpoint dùng `SERVICE_API_KEY`, không route qua Gateway.
- **API Gateway:** routing tới các services và cấu hình CORS.
- **Frontend:** Next.js BFF giữ JWT trong cookie HttpOnly; có luồng auth, browse provider, booking, history/review và Admin duyệt provider.
- **Tổ chức source:** Identity, Marketplace và Booking phân lớp controller/request-response DTO/entity/enum/mapper/service; màn hình frontend đặt theo feature auth/admin/booking/marketplace, header ở shared.
- **Payment:** app skeleton và adapter registry, chưa có API thanh toán.
- **Persistence:** Identity, Marketplace và Booking có cấu hình MySQL; Payment chưa có schema nghiệp vụ. Tests dùng H2. Schema local được Hibernate cập nhật bằng `ddl-auto: update`.

## Yêu cầu đã nêu nhưng chưa thấy implementation

- **Provider service offerings/gói và giá:** chưa có `ServiceOffering`, bảng `service_offerings`, controller endpoints tạo/sửa/archive, public listing, Admin offering approval hoặc UI tương ứng trong source hiện tại. Các API `/api/provider/services`, `/api/admin/offerings/**` và `/api/services/{categoryId}/offerings` trong một số tài liệu cũ không được xác nhận bởi code hiện có.
- **Offering-to-booking:** API booking hiện nhận `providerId` và `categoryId`, không nhận `offeringId` hoặc price snapshot. Việc nối booking với offering chưa được xác nhận.
- **Booking:** slot overlap, quota booking lặp/Admin review, admin booking APIs, cancellation/reschedule, status history.
- **Identity:** Admin user management, xác minh email/phone, khôi phục mật khẩu, refresh/revoke token, rate limiting.
- **Marketplace:** catalog CRUD, tìm kiếm nâng cao, upload ảnh, audit/moderation history.
- **Payment:** transaction schema/API, cổng thanh toán, webhook/callback.
- **Infrastructure:** migration scripts, production deployment/CI, browser E2E.
- **Các domain chưa được xác nhận:** complaint workflow và notification service.

## Kiểm thử

- Sau khi sắp xếp source: Maven reactor test pass (16 tests); frontend lint, typecheck và production build pass.
- Không có browser E2E tự động được ghi nhận.

## Còn cần xác nhận

- Định nghĩa trạng thái booking “đang mở”, timezone/biên slot và hành vi Admin review cho booking lặp.
- Quy tắc giá cuối cùng/fees, cancellation/refund và provider thanh toán.
- Review edit/delete/moderation.
- Chính sách migration và môi trường CI/deployment.

## Môi trường và giới hạn

- Backend target Java 21; cấu hình local Compose dùng MySQL với schema riêng, volume persistent. Cổng ứng dụng được bind `127.0.0.1`; cổng MySQL không publish.
- JWT/API key và mật khẩu MySQL mặc định trong Compose chỉ dành cho local development; không dùng defaults trong production.
- Tên thư mục hiện có là `back-end/` và `font-end/`; không đổi tên trong lần cập nhật tài liệu.

## Việc tiếp theo

1. Nếu tiếp tục yêu cầu quản lý gói provider, triển khai Marketplace API/schema/UI và Admin approval; đồng bộ API/database docs cùng tests khi implementation được làm.
2. Chốt có cần booking tham chiếu offering và lưu price snapshot hay không trước khi nối hai domain.
3. Chốt các policy booking/payment còn mở trước khi mở rộng flows.
