# Thiết kế hệ thống

## Thành phần logic

```text
Browser (Next.js)
        |
        v
API Gateway (public API entry)
   |          |          |          |
Identity  Marketplace  Booking    Payment
   |          |          |          |
 DB/schema  DB/schema  DB/schema  DB/schema
```

Kiến trúc bốn business service đã được chọn. Identity, Marketplace và Booking có các luồng auth, provider, booking và review cơ bản cùng persistence và frontend BFF; Payment vẫn là adapter registry skeleton. MySQL thật và deployment chưa được xác minh.

## Quy tắc liên service

- Dùng REST/DTO và timeout rõ ràng cho request đồng bộ.
- Không chia sẻ repository/entity/database, không có transaction xuyên service.
- Domain service là nguồn chân lý cho dữ liệu của nó; Gateway không sở hữu nghiệp vụ.
- Payment adapter tách theo provider/capability; lỗi một adapter không được làm tắt các adapter khác.
- Dùng API/DTO để kiểm tra provider và booking review eligibility; không query DB domain khác.
- Broker/Saga/Outbox chỉ thêm khi có use case, yêu cầu phục hồi và test phù hợp.

## Luồng liên quan

Đăng ký/đăng nhập xem [authentication flow](./architecture/authentication-flow.md). Booking/slot/repeat policy xem [booking flow](./architecture/booking-flow.md). API dự kiến tại [API_DOCUMENTATION.md](./API_DOCUMENTATION.md).
