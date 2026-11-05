# Marketplace database

Marketplace sở hữu schema `doan1_marketplace`; Identity ID chỉ được lưu làm tham chiếu, không có foreign key xuyên service.

| Bảng | Dữ liệu chính |
|---|---|
| `provider_profiles` | Identity account ID, tên, địa chỉ, image URL, mô tả, trạng thái duyệt, rating, review count, demo flag, thời điểm tạo. |
| `provider_profile_categories` | Tập category ID (`cleaning`, `repair`) của hồ sơ. |
| `service_offerings` | Provider profile ID (cùng schema), category ID, tên/mô tả gói, giá VND, số điện thoại công khai của gói, trạng thái (`PENDING`/`APPROVED`/`REJECTED`/`ARCHIVED`), thời điểm tạo/cập nhật. |
| `provider_reviews` | Booking ID, reviewer/target ID và role, rating, comment, thời điểm tạo; unique constraint trên `(booking_id, reviewer_account_id)`. |

Hibernate dùng `ddl-auto: update`; chưa có migration. Category hiện là danh sách cố định trong ứng dụng, không phải bảng catalog. `service_offerings` được sở hữu bởi Marketplace; xóa provider thực hiện bằng archive trạng thái chứ không hard-delete để giữ lịch sử. Số điện thoại được provider nhập cho từng gói và được public khi gói được duyệt.
