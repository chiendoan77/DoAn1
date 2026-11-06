# Review Service

Review service sở hữu nội dung đánh giá và eligibility ở mức use case. Trước khi lưu, service hỏi Booking xác minh booking `COMPLETED`, participant và target; không truy cập database Booking. Mỗi reviewer chỉ được đánh giá một lần trên mỗi booking.

Review ghi `ReviewSubmitted` vào transactional outbox trong cùng transaction với review. Publisher gửi qua RabbitMQ sau confirm; Profile tiêu thụ event idempotently để cập nhật rating projection. API Gateway route `/api/reviews/**` tới service này.

Review legacy có thể export từ Marketplace rồi import bằng internal API có key. Import không phát lại event để tránh tính rating projection hai lần. Việc chuyển dữ liệu cần thao tác/đối soát rõ ràng.

Chạy port 8088, schema riêng `doan1_review`, Flyway và Hibernate `ddl-auto: validate`. Xem [API](./API.md), [database](./DATABASE.md), [architecture](./ARCHITECTURE.md).
