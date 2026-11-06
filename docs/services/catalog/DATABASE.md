# Catalog database

Catalog sở hữu schema `doan1_catalog`; schema quản lý bằng Flyway, Hibernate `ddl-auto: validate`.

| Bảng | Dữ liệu |
|---|---|
| `categories` | Category string code, name, description, active state. Seed codes `cleaning`, `repair`. |
| `service_packages` | Provider/account ID references, category, content, VND integer price, duration, phone, status, rejection reason, version/timestamps. |
| `package_history` | Package moderation/edit lifecycle action, actor, reason, package version, timestamp. |
| `outbox_events` | Event ID, aggregate, type, JSON payload, exchange/routing key, PENDING/PUBLISHED status, timestamps. |

Không có foreign key tới Profile. Profile ID/account eligibility được kiểm tra qua API. Outbox event row được tạo trong transaction của package workflow; publisher confirm thành công mới cập nhật `PUBLISHED`. Nếu confirm không thành công, event vẫn PENDING để retry.

Booking chưa lưu package ID hoặc price snapshot; không có cross-service transaction.
