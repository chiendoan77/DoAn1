# Notification database

Notification sở hữu schema `doan1_notification`, quản lý bằng Flyway và Hibernate `ddl-auto: validate`.

| Bảng | Dữ liệu |
|---|---|
| `notifications` | Notification ID, unique event ID, recipient account/role, event type, package ID, in-app message, timestamp. |

Unique `event_id` hỗ trợ deduplication khi RabbitMQ redeliver. Schema hiện không có `read_at`, delivery channel, template hoặc provider delivery status.
