# Booking flow

## Luồng đã triển khai

1. CUSTOMER gửi `packageId`, `scheduledAt`, `address`, `description` tới `POST /api/bookings`.
2. Booking đồng bộ lấy package APPROVED và snapshot giá/tên/thời lượng từ Catalog; xác minh provider/category qua Profile.
3. Booking lưu yêu cầu với trạng thái `REQUESTED` cùng snapshots, không có foreign key xuyên service.
4. Provider được gán xem lịch sử và chuyển trạng thái qua `ACCEPTED`, `REJECTED` hoặc `COMPLETED` theo transition/ownership checks.
5. Khi hoàn tất, Booking ghi `BookingCompleted` trong outbox; Notification phát inbox cho customer/provider.
6. Review xác minh eligibility qua Booking; khi submit review, `ReviewSubmitted` cập nhật rating projection ở Profile.

Slot overlap, repeat booking policy, cancellation/reschedule và Payment saga chưa triển khai.

## Luồng mục tiêu chưa triển khai

Yêu cầu đã xác nhận gồm slot conflict và giới hạn booking lặp theo category; trạng thái “đang mở”, timezone/slot boundaries, Admin review behavior và cancellation/refund còn cần chốt. Payment lifecycle/saga đang được hoãn. Các mục tiêu chưa hoàn tất không mô tả API đang chạy.
