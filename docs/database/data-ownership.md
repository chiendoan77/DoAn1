# Data ownership

| Dữ liệu | Owner | Trạng thái / giới hạn |
|---|---|---|
| Tài khoản, credential, role | Identity & Access | Đã triển khai một phần. Domain khác không đọc trực tiếp credential storage. |
| Provider profile, category, review/rating | Marketplace | Đã triển khai một phần. Booking không sửa catalog/provider state. |
| Provider offering và giá | Marketplace | Yêu cầu đã xác nhận nhưng chưa có schema/API. |
| Booking, trạng thái và lịch sử | Booking | Booking cơ bản đã triển khai; Marketplace/Identity không ghi trực tiếp booking. |
| Payment transaction/provider attempts | Payment | Service đã chọn; hiện chỉ có adapter registry skeleton, chưa có transaction schema/API. |
| Complaint | Chưa chốt | Repeat booking review có thể cần complaint context; không lộ complaint/PII ra domain không có quyền. |

ID tham chiếu liên domain không tạo foreign key giữa database. Review thuộc Marketplace theo DEC-008; Booking cung cấp eligibility qua API nội bộ. Quan hệ booking tới offering và price snapshot chưa được xác nhận.
