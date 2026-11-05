# Gateway routes

Gateway định tuyến public API; các URL đích cấu hình qua environment variables. Quyền và ownership được xác thực tại service domain.

| Prefix | Destination |
|---|---|
| `/api/auth/**`, `/api/me/**`, `/api/admin/users/**` | Identity & Access |
| `/api/services/**`, `/api/reviews/**`, `/api/provider/**`, `/api/admin/providers/**`, `/api/admin/categories/**` | Marketplace |
| `/api/bookings/**`, `/api/provider/bookings/**`, `/api/admin/bookings/**` | Booking |
| `/api/payments/**`, `/api/admin/payments/**` | Payment |

Các endpoint `/internal/**` không có route Gateway; service-to-service traffic dùng API key. Gateway route không đồng nghĩa endpoint downstream đã tồn tại: admin users/bookings, payments và một số catalog route hiện chưa được triển khai.

## CORS cho frontend local

Gateway cho phép browser origin `http://localhost:4200` gọi API qua Gateway. Preflight hỗ trợ `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS` và headers `Content-Type`, `Authorization`. Origin khác bị từ chối; cấu hình này không thay thế xác thực hoặc phân quyền. Khi triển khai frontend ở origin khác, cập nhật allowlist theo origin cụ thể đó; không dùng wildcard cho credentials.
