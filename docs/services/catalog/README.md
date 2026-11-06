# Catalog Service

Catalog sở hữu category codes, provider service packages, giá VND và moderation workflow; chạy mặc định ở port 8086 với schema riêng `doan1_catalog`. Provider account/profile được kiểm tra qua Profile API, không đọc dữ liệu Profile trực tiếp.

Package mới là `DRAFT`, provider submit để duyệt. Sửa package đã APPROVED chuyển về `PENDING_APPROVAL`; Admin có thể approve/reject/deactivate. Delete của provider là archive (`INACTIVE`). Expected version ngăn ghi đè từ dữ liệu cũ.

Catalog ghi package lifecycle event vào outbox trong cùng transaction với thay đổi nghiệp vụ. Publisher gửi message persistent qua RabbitMQ và chỉ đánh dấu published sau publisher confirm; Notification tiêu thụ idempotent.

Schema dùng Flyway và Hibernate `ddl-auto: validate`. Xem [API](./API.md), [database](./DATABASE.md), [architecture](./ARCHITECTURE.md).
