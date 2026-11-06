# Data ownership

| Dữ liệu | Owner | Trạng thái / giới hạn |
|---|---|---|
| Account, credential, role, status, audit, hashed refresh tokens | Identity & Access | RS256 và refresh rotation/revoke đã triển khai; không cấp role từ client. |
| Customer/provider profile, provider approval/suspension | Profile | Hồ sơ customer/provider và provider eligibility đã triển khai. |
| Rating projection | Profile | Nhận `ReviewSubmitted`; projection idempotent theo review ID. |
| Category, service package, giá, moderation history | Catalog | Package workflow và Flyway đã triển khai. |
| Provider/review records cũ | Marketplace | Legacy data/export phục vụ migration; không nhận review mới qua Gateway. |
| Booking, participant/package snapshots, current status, outbox | Booking | Tạo booking gắn package, snapshot giá và `BookingCompleted` đã triển khai; slot/quota/history chưa có. |
| Review records và outbox | Review | Service/schema riêng; Booking xác minh eligibility qua API nội bộ. |
| Payment transaction/provider attempts | Payment | Chưa có schema nghiệp vụ hoặc lifecycle; được hoãn. |
| Notification inbox | Notification | In-app notifications từ package/booking events; không sở hữu booking/package. |
| Complaint | Chưa chốt | Workflow chưa xác nhận; không tạo bảng hoặc API dựa trên giả định. |

Mỗi service sở hữu schema riêng. Cross-service data được tra cứu bằng API/DTO hoặc lưu snapshot/projection qua event; không join DB và không tạo foreign key giữa service schemas.
