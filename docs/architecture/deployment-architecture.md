# Deployment architecture (đề xuất)

## Local

Root `docker-compose.yml` định nghĩa Next.js frontend, API Gateway, Identity, Profile, Catalog, Marketplace, Booking, Review, Payment, Notification, MySQL, RabbitMQ, Prometheus, Grafana và Jaeger. MySQL tạo schema riêng cho các service có persistence; Payment chưa có database nghiệp vụ.

Frontend, Gateway, service ports và host port MySQL được publish vào loopback (`127.0.0.1`) để phát triển/kiểm thử trên máy host. MySQL host port cấu hình qua `MYSQL_PORT` trong `.env` (mặc định `3306`); container vẫn dùng port `3306` trên Docker network. Không có port nào được bind trên mọi interface. Frontend là entry point cho UI; Gateway là entry point API.

## Production

Compose hiện tại và các development defaults không dành cho production. Trước khi deploy cần cấu hình secret/database credentials an toàn, TLS/domain, backup/restore, centralized logs và quy trình release. Metrics/tracing được cấu hình nhưng chưa được kiểm tra runtime.

## Quy tắc

- Không expose internal service/database ports công khai; Compose local chỉ bind service ports vào loopback.
- JWT/API key mặc định chỉ dùng local; credentials production không lưu trong repository.
- Chỉ thêm container/service hạ tầng cần cho use case.
- Compose có health checks, tạo schemas và seed demo provider. Các service có persistence dùng Flyway; Marketplace/Booking baseline phải được dry-run với database backup.
- RabbitMQ exchange/queue phục vụ Catalog/Booking/Review outboxes và Notification/Profile consumers; runtime stack cần được kiểm tra trước production.
- Metrics endpoint được Prometheus scrape; Grafana được cấp datasource; Jaeger nhận OTLP traces. Central log collection chưa được tích hợp.

Target host/cloud, TLS termination, domain, backup, central logs và CI/CD chưa được xác định.
