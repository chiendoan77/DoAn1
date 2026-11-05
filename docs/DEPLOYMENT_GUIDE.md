# Hướng dẫn chạy bằng Docker

## Yêu cầu

- Docker Desktop đang chạy.
- Cổng local `3000`, `8080`–`8084` chưa được ứng dụng khác sử dụng.

## Khởi động toàn bộ dự án

Chạy tại thư mục gốc repository:

```powershell
docker compose up --build
```

Compose tự build frontend và năm ứng dụng Java, khởi tạo MySQL cùng ba schema riêng, chờ các health check và kết nối các service bằng tên DNS nội bộ. Marketplace tự nạp provider demo trên database mới. Muốn để ứng dụng chạy nền, dùng `docker compose up --build -d`.

| Thành phần | URL từ máy host |
|---|---|
| Frontend | `http://localhost:3000` |
| API Gateway | `http://localhost:8080` |
| Identity | `http://localhost:8081` |
| Marketplace | `http://localhost:8082` |
| Booking | `http://localhost:8083` |
| Payment | `http://localhost:8084` |

Database không publish port ra máy host. Các port ứng dụng được bind vào `127.0.0.1`, chỉ truy cập từ máy đang chạy Docker; frontend gọi Gateway qua network nội bộ.

Kiểm tra tình trạng và log:

```powershell
docker compose ps
docker compose logs -f
```

Dừng stack bằng `Ctrl+C` nếu chạy foreground, hoặc:

```powershell
docker compose down
```

Mặc định volume `mysql-data` được giữ lại sau khi dừng. Chỉ khi muốn xóa toàn bộ dữ liệu local và tạo database mới, chạy `docker compose down --volumes`.

Có thể đổi port host bằng biến môi trường trước khi chạy, ví dụ `$env:FRONTEND_PORT = "3100"` hoặc `$env:GATEWAY_PORT = "8090"`. Các biến dùng cho port khác là `IDENTITY_PORT`, `MARKETPLACE_PORT`, `BOOKING_PORT` và `PAYMENT_PORT`.

Các giá trị JWT/API key mặc định chỉ dành cho local development. Không dùng cấu hình mặc định, MySQL không mật khẩu, hoặc port mapping này cho môi trường public/production. Muốn bootstrap Admin, đặt `BOOTSTRAP_ADMIN_ENABLED=true` và cung cấp các biến `BOOTSTRAP_ADMIN_EMAIL`, `BOOTSTRAP_ADMIN_PHONE`, `BOOTSTRAP_ADMIN_USERNAME`, `BOOTSTRAP_ADMIN_PASSWORD` trước khi khởi động.

## Production

Môi trường triển khai, domain, TLS, backup/restore, log retention, quản lý secret và quy trình phát hành chưa được xác định. Compose hiện tại chỉ dành cho local development; production cần cấu hình credentials/secret riêng và không public database hoặc internal service ports.
