# Thiết kế dữ liệu (khái niệm)

Mỗi service sở hữu schema riêng. Identity, Profile, Catalog, Marketplace, Booking, Review và Notification dùng Flyway; Marketplace/Booking migrations có baseline cần dry-run trên bản DB hiện hữu trước rollout. Xem thêm [database overview](./database/database-overview.md), [data ownership](./database/data-ownership.md), [entity relationship](./database/entity-relationship.md).

## Entity ứng viên

- Identity: `accounts`, `account_status_history`, `refresh_tokens` (SHA-256 hash/family/revocation; raw token không lưu).
- Profile: `provider_profiles`, `provider_profile_categories`, `provider_rating_reviews`, `customer_profiles`.
- Catalog: `categories`, `service_packages`, `package_history`, `outbox_events`.
- Marketplace: `provider_reviews` và provider profile legacy chỉ là nguồn export migration.
- Booking: `bookings` gồm participant/provider/package references, snapshot tên/category/giá/thời lượng, lịch, địa chỉ, mô tả và status; `booking_outbox_events`.
- Review: `reviews` và `review_outbox_events`.
- Notification: `notifications` lưu inbox in-app với unique `event_id` để chống xử lý lặp.
- Payment: `PaymentTransaction`/`PaymentAttempt` và provider references thuộc Payment service; schema cụ thể phụ thuộc gateway.
- Feedback: review record thuộc Review; rating projection thuộc Profile; Booking cung cấp eligibility nội bộ.
- Support: complaint workflow độc lập chưa được xác nhận; repeat booking Admin review có thể tham chiếu complaint context theo quyền.

## Nguyên tắc

- Mỗi service sở hữu schema/database riêng; service khác chỉ dùng API/DTO.
- Không dùng foreign key xuyên database. Dùng ID tham chiếu và kiểm tra qua API hoặc snapshot theo quyết định nghiệp vụ.
- Booking xác thực package đã duyệt ở Catalog và giữ snapshot để lịch sử không đổi khi giá/gói cập nhật.
- Giá package là VND nguyên số trong Catalog; không có FK sang database Catalog.
- Không có foreign key xuyên service. Profile migration gọi API Marketplace, không đọc database Marketplace trực tiếp.
- Tests dùng H2. Marketplace/Booking baseline chưa được dry-run với bản sao MySQL existing; Payment chưa có schema.
- Slot overlap/quota, saga và payment transaction schema chưa triển khai; không suy ra chúng từ các bảng hiện có.
