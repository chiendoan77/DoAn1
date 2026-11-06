# Chạy local và triển khai

## Mục đích và prerequisites

Docker Compose hiện phục vụ local development, không phải production deployment. Cần Docker Desktop đang chạy; tạo RSA keypair local theo [SECURITY.md](./SECURITY.md) trước khi bật stack. Dùng secret/key riêng cho mỗi môi trường, không commit `.env`.

## Khởi động và theo dõi

Từ repository root, chạy `docker compose up --build` để build frontend, Gateway và các service, khởi tạo MySQL/RabbitMQ, và chờ health checks. Dùng `docker compose up --build -d` để chạy detached.

Kiểm tra container với `docker compose ps`; xem log bằng `docker compose logs -f <service-name>` hoặc `docker compose logs -f`. Lệnh cụ thể và workflow phát triển được liên kết tại [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md).

## Local ports

Các host ports bind vào `127.0.0.1`; đây không phải cấu hình public.

| Thành phần | URL/port mặc định |
|---|---|
| Frontend | `http://localhost:3000` |
| API Gateway (API entry point) | `http://localhost:8080` |
| MySQL (local development) | `localhost:${MYSQL_PORT:-3306}` |
| Identity | `http://localhost:8081` |
| Marketplace legacy | `http://localhost:8082` |
| Booking | `http://localhost:8083` |
| Payment skeleton | `http://localhost:8084` |
| Profile | `http://localhost:8085` |
| Catalog | `http://localhost:8086` |
| Notification | `http://localhost:8087` |
| Review | `http://localhost:8088` |
| Prometheus | `http://localhost:9090` |
| Grafana | `http://localhost:3001` |
| Jaeger UI | `http://localhost:16686` |

MySQL được publish trên loopback host tại `127.0.0.1:${MYSQL_PORT:-3306}` để truy cập local; đặt `MYSQL_PORT` trong `.env` để đổi host port. Cổng nội bộ MySQL giữa các container vẫn là `3306`. Ví dụ cấu hình nằm trong [.env.example](../.env.example).

Integration client chỉ dùng Gateway. Các service port được publish loopback cho debug local, không được expose trong môi trường public. RabbitMQ không publish port host.

## Dừng và dữ liệu local

Dừng stack foreground bằng `Ctrl+C`; chạy detached thì dùng `docker compose down`. Volume MySQL được giữ lại sau khi dừng. `docker compose down --volumes` xóa volume dữ liệu local; chỉ dùng khi chủ ý reset data, không chạy trên dữ liệu cần giữ.

Các port có thể override qua biến trong `.env`; xem `docker-compose.yml` để biết tên chính xác. Không sửa `API_GATEWAY_URL` của frontend thành một service URL.

## Database và migrations

Identity, Profile, Catalog, Marketplace, Booking, Review và Notification dùng Flyway; Payment chưa có schema nghiệp vụ. Marketplace/Booking baseline migrations chưa được rehearsal trên backup MySQL hiện hữu. Trước khi nâng cấp database có dữ liệu: backup, phục hồi vào môi trường riêng, chạy migration, đối chiếu số lượng/constraint và xác nhận rollback/restore.

## Production — chưa được chứng nhận

Repository chưa cung cấp production domain, TLS termination, secrets manager, network policy, rate limiting, backup/restore runbook, log aggregation hoặc CI/CD. Trước khi public:

1. Chỉ expose reverse proxy/API Gateway bằng HTTPS; không expose MySQL, RabbitMQ hoặc từng service port.
2. Thay mọi credential local và dùng RSA private key/API key/RabbitMQ/DB secrets được quản lý an toàn.
3. Cấu hình allowlisted CORS origins, firewall/network policy và rate limiting.
4. Thiết lập backup/restore và migration rehearsal.
5. Xác minh health checks, outbox/broker recovery, metrics/traces, central logs và alerting.
6. Chạy contract/E2E/security tests trong staging trước rollout.

Do các mục này chưa được xác minh, Compose chỉ được dùng cho môi trường phát triển cá nhân.
