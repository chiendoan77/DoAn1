# Booking database

Booking sở hữu schema `doan1_booking`; provider/customer IDs là tham chiếu/snapshot, không có foreign key xuyên service.

| Bảng | Dữ liệu chính |
|---|---|
| `bookings` | Customer account ID, provider profile/account ID, tên snapshot hai bên, category ID/name, thời điểm có offset, địa chỉ, mô tả, status, thời điểm tạo. |

Hibernate dùng `ddl-auto: update`; chưa có migration. Review được Marketplace sở hữu; Booking chỉ xác nhận eligibility qua API nội bộ. Chưa lưu status history/schedule allocation và chưa có locking/index logic cho slot overlap hay quota.
