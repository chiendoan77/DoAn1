# Booking flow

## Luồng đã triển khai

1. CUSTOMER gửi `providerId`, `categoryId`, `scheduledAt`, `address`, `description` tới `POST /api/bookings`.
2. Booking xác thực role và hỏi Marketplace qua API nội bộ để xác minh provider/category; không đọc database Marketplace.
3. Booking lưu yêu cầu với trạng thái `REQUESTED`.
4. Provider được gán xem lịch sử của mình và chuyển trạng thái qua `ACCEPTED`, `REJECTED` hoặc `COMPLETED` theo transition/ownership checks.
5. Customer/provider xem lịch sử booking của mình.

Booking request hiện không nhận offering ID, không lưu price snapshot và không kiểm tra slot overlap.

## Luồng mục tiêu chưa triển khai

Yêu cầu đã xác nhận gồm slot conflict và giới hạn booking lặp theo category. Việc bắt đầu booking từ một gói dịch vụ và lưu giá tại thời điểm đặt chưa được xác nhận. Trạng thái “đang mở”, timezone/slot boundaries, Admin review behavior và cancellation/refund cũng cần chốt trước khi implementation. Các mục tiêu này không mô tả API đang chạy.
