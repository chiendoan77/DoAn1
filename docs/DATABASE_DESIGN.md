# Thiết kế dữ liệu (khái niệm)

Đây là mô tả schema hiện được Hibernate tạo/cập nhật khi chạy, không phải migration có quản lý phiên bản. Xem thêm [database overview](./database/database-overview.md), [data ownership](./database/data-ownership.md), [entity relationship](./database/entity-relationship.md).

## Entity ứng viên

- Identity: bảng `accounts` gồm email, phone, username, full name, BCrypt hash và role.
- Marketplace: `provider_profiles`, `provider_profile_categories`, `provider_reviews`; category hiện là danh sách code cố định.
- Booking: `bookings` gồm participant/provider references, snapshot tên/category, lịch, địa chỉ, mô tả và status.
- Payment: `PaymentTransaction`/`PaymentAttempt` và provider references thuộc Payment service; schema cụ thể phụ thuộc gateway.
- Feedback: `provider_reviews` thuộc Marketplace; Booking cung cấp kiểm tra eligibility nội bộ.
- Support: complaint workflow độc lập chưa được xác nhận; repeat booking Admin review có thể tham chiếu complaint context theo quyền.

## Nguyên tắc

- Mỗi service sở hữu schema/database riêng; service khác chỉ dùng API/DTO.
- Không dùng foreign key xuyên database. Dùng ID tham chiếu và kiểm tra qua API hoặc snapshot theo quyết định nghiệp vụ.
- Lưu giá/thông tin dịch vụ tại thời điểm đặt nếu lịch sử booking phải giữ nguyên; chi tiết cần xác nhận.
- Cấu hình hiện dùng schema MySQL riêng cho từng service và `ddl-auto: update`; chưa có migrations. Test dùng H2. Không có foreign key xuyên service.
- Slot overlap/quota, giá/offering và payment schema chưa triển khai; không suy ra chúng từ các bảng hiện có.
