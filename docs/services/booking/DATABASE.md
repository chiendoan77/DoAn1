# Booking database

Booking sở hữu schema `doan1_booking`; provider/customer IDs là tham chiếu/snapshot, không có foreign key xuyên service.

| Bảng | Dữ liệu chính |
|---|---|
| `bookings` | Customer account ID, provider profile/account ID, tên snapshot hai bên, category ID/name, package ID/name/price/duration snapshot, thời điểm có offset, địa chỉ, mô tả, status, thời điểm tạo. |
| `booking_outbox_events` | Event ID, booking ID, event type, payload, trạng thái publish và timestamp; chứa `BookingCompleted`. |

Flyway quản lý schema bằng baseline V1 và migration V2; Hibernate dùng `ddl-auto: validate`. Booking giữ các ID tham chiếu và snapshot, không có foreign key xuyên service. Review service truy vấn eligibility qua API nội bộ. Chưa lưu status history/schedule allocation và chưa có locking/index logic cho slot overlap hay quota.
