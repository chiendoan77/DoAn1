# Yêu cầu tổng thể

## Quy ước

Trạng thái **Đã xác nhận** phản ánh quyết định người dùng đã chốt trong hội thoại. Trạng thái **Đề xuất/Cần xác nhận** chưa được xem là phạm vi đã duyệt. Implementation status được ghi riêng và không tự thay đổi trạng thái phê duyệt requirement.

## Yêu cầu chức năng

**Trạng thái triển khai:** RS256/refresh token, customer/provider Profile, Catalog packages, Booking package/price snapshots, Review service, Booking/Review outbox events và in-app notification consumers đã có implementation. Contract/Pact suite, browser E2E, centralized logs và production integrations còn thiếu; Payment lifecycle/saga hoãn theo yêu cầu.

| ID | Yêu cầu | Trạng thái |
|---|---|---|
| ID-FR-001 | Người dùng đăng ký và đăng nhập bằng email hoặc số điện thoại. Email, phone, username, họ tên và mật khẩu bắt buộc. | Đã xác nhận; RS256, refresh/revoke có implementation |
| ID-FR-002 | Hệ thống phân biệt quyền khách hàng, nhà cung cấp và Admin. | Đã xác nhận trong MVP |
| MARKET-FR-001 | Khách hàng duyệt/tìm dịch vụ, xem giá và nhà cung cấp. | Đã xác nhận; provider/package browse có, tìm kiếm nâng cao còn hạn chế |
| MARKET-FR-002 | Nhà cung cấp khai báo hồ sơ và dịch vụ/gói có giá. | Đã xác nhận; Profile/Catalog workflow và Booking package snapshots có |
| MARKET-FR-003 | Admin quản lý/duyệt/khởi tạo hồ sơ nhà cung cấp và quản lý catalog. | Đã xác nhận; provider/package approval có, Admin tạo hộ provider còn mở |
| BOOK-FR-001 | Khách hàng tạo booking với thời gian, địa chỉ và mô tả công việc. | Đã xác nhận trong MVP |
| BOOK-FR-002 | Nhà cung cấp xử lý booking; chỉ slot trùng mới làm provider không khả dụng. | Đã xác nhận; biên interval/time zone còn mở |
| BOOK-FR-003 | Mỗi khách tối đa hai booking đang mở cùng danh mục; lần hai cảnh báo/Admin review, lần ba bị chặn. | Đã xác nhận; state “đang mở” và review gate còn mở |
| BOOK-FR-004 | Admin tra cứu và xử lý booking theo quyền. | Đã xác nhận trong MVP |
| PAY-FR-001 | Payment service cung cấp nhiều provider adapters có thể hoạt động đồng thời. | Đã xác nhận kiến trúc; gateway/flow cụ thể còn mở |
| FEEDBACK-FR-001 | Khách hàng và provider đánh giá nhau sau khi booking hoàn tất, tối đa một review mỗi bên/booking. | Đã xác nhận; Review service/outbox có, sửa/xóa/moderation còn mở |
| SUPPORT-FR-001 | Khách hàng gửi khiếu nại và theo dõi xử lý. | Chưa xác nhận MVP |
| NOTIFY-FR-001 | Gửi thông báo khi booking đổi trạng thái quan trọng. | `BookingCompleted` và package in-app notifications có; email/push/SMS còn mở |

Chi tiết: [functional requirements](./requirements/functional-requirements.md), [business requirements](./requirements/business-requirements.md), [open questions](./requirements/open-questions.md).

## Phi chức năng và ràng buộc

- Phù hợp quy mô đồ án cá nhân, dễ chạy local, kiểm thử và bảo trì.
- Frontend chỉ gọi Gateway; không truy cập DB hoặc API service nội bộ.
- Mỗi service sở hữu dữ liệu nghiệp vụ của mình; không truy cập DB service khác.
- Admin Portal dùng JWT + role ADMIN; API key chỉ cho server-to-server, không đưa vào browser.
- Payment hỗ trợ adapter/provider độc lập; nhiều phương thức có thể đồng thời hoạt động.
- Kiểm thử quyền, trạng thái booking, slot overlap và payment callback trước khi đánh dấu hoàn tất.
- RabbitMQ/outbox đang dùng cho Catalog package, `BookingCompleted` và `ReviewSubmitted`; Payment events/saga chưa triển khai theo phạm vi đã hoãn.

## Ranh giới domain đã chọn

1. **Identity & Access** — tài khoản, xác thực RS256/refresh, role và account status.
2. **Profile** — customer/provider profiles, approval/suspension và rating projection.
3. **Catalog** — category, packages, giá và moderation.
4. **Marketplace** — legacy data/migration export; review mới thuộc Review service riêng.
5. **Booking** — đặt lịch, state machine, slot conflict và giới hạn repeat booking theo category.
6. **Payment** — giao dịch và adapters; hiện skeleton.
7. **Notification** — async package/BookingCompleted in-app inbox; email/push/SMS còn mở.
8. **API Gateway** — edge routing/JWT validation, không phải business domain.
9. **Admin** — role/portal; thao tác thuộc service sở hữu dữ liệu, không tạo `admin-service`.

Payment lifecycle/saga, Pact/browser E2E, centralized logs, rate limiting, production mTLS/secret management và các policy booking còn mở. Yêu cầu chi tiết/chưa rõ được liệt kê trong [open questions](./requirements/open-questions.md).
