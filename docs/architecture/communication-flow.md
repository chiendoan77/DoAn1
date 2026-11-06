# Giao tiếp giữa các thành phần

- Browser gọi API qua frontend và public API Gateway.
- Services giao tiếp bằng REST/DTO qua internal network; không gọi repository hoặc DB của nhau.
- Tác vụ cần dữ liệu domain khác phải gọi API sở hữu dữ liệu đó hoặc dùng snapshot đã thống nhất; không có transaction xuyên service.
- Booking đồng bộ hỏi Catalog package details và Profile provider eligibility trước khi lưu booking snapshot.
- Catalog/Booking/Review ghi domain event trong outbox cùng transaction; publisher gửi durable message qua RabbitMQ và đánh dấu published sau publisher confirm.
- Notification consume package events và `BookingCompleted`, ghi inbox idempotent theo event ID; Profile consume `ReviewSubmitted` để cập nhật rating idempotently.
- `PaymentSucceeded`, Payment outbox/saga chưa triển khai theo phạm vi đã chốt.
- Booking kiểm tra slot overlap và quota trong DB sở hữu; các policy còn mở phải được xác nhận trước khi triển khai logic.
- Payment định tuyến qua adapter interface; lỗi một adapter không làm ngừng các adapter khác.
- Payment callbacks cần xác minh chữ ký và idempotency; provider cụ thể chưa được chọn.
