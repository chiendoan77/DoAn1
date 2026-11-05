# Marketplace database

Marketplace sở hữu schema `doan1_marketplace`; Identity ID chỉ được lưu làm tham chiếu, không có foreign key xuyên service.

| Bảng | Dữ liệu chính |
|---|---|
| `provider_profiles` | Identity account ID, tên, địa chỉ, image URL, mô tả, trạng thái duyệt, rating, review count, demo flag, thời điểm tạo. |
| `provider_profile_categories` | Tập category ID (`cleaning`, `repair`) của hồ sơ. |
| `provider_reviews` | Booking ID, reviewer/target ID và role, rating, comment, thời điểm tạo; unique constraint trên `(booking_id, reviewer_account_id)`. |

Hibernate dùng `ddl-auto: update`; chưa có migration. Category hiện là danh sách cố định trong ứng dụng, không phải bảng catalog. Provider service offerings/gói và giá là yêu cầu đã nêu, nhưng chưa có entity/table trong source/schema hiện tại; không dựa vào API/database examples cũ như thể đã triển khai.
