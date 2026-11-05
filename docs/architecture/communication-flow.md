# Giao tiếp giữa các thành phần

- Browser gọi API qua frontend và public API Gateway.
- Bốn microservices giao tiếp bằng REST/DTO qua internal network; không gọi repository hoặc DB của nhau.
- Tác vụ cần dữ liệu domain khác phải gọi API sở hữu dữ liệu đó hoặc dùng snapshot đã thống nhất; không có transaction xuyên service.
- Giao tiếp async/event, saga/outbox/message broker chỉ thêm khi yêu cầu nhất quán và failure recovery biện minh chi phí.
- Booking kiểm tra slot overlap và quota trong DB sở hữu; các policy còn mở phải được xác nhận trước khi triển khai logic.
- Payment định tuyến qua adapter interface; lỗi một adapter không làm ngừng các adapter khác.
- Payment callbacks cần xác minh chữ ký và idempotency; provider cụ thể chưa được chọn.
