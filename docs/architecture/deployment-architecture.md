# Deployment architecture (đề xuất)

## Local

Root `docker-compose.yml` khởi chạy Next.js frontend, API Gateway, bốn business services và MySQL. MySQL tạo schema riêng cho Identity, Marketplace và Booking. Service-to-service và frontend-to-Gateway traffic dùng Docker network.

Frontend, Gateway và các service ports được publish vào loopback (`127.0.0.1`) để phát triển/kiểm thử trên máy host. MySQL không publish port; không có port nào được bind trên mọi interface. Frontend là entry point cho UI; Gateway là entry point API.

## Production

Compose hiện tại và các development defaults không dành cho production. Trước khi deploy cần cấu hình secret/database credentials an toàn, TLS/domain, backup/restore, logging/monitoring và quy trình release.

## Quy tắc

- Không expose internal service/database ports công khai; Compose local chỉ bind service ports vào loopback.
- JWT/API key mặc định chỉ dùng local; credentials production không lưu trong repository.
- Chỉ thêm container/service hạ tầng cần cho use case.
- Compose có health checks, tạo schema và seed demo provider; database migrations chưa có.

Target host/cloud, TLS termination, domain, backup, monitoring và CI/CD chưa được xác định.
