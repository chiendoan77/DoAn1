# Gateway routes

API Gateway local chạy ở `http://localhost:8080`. Đây là public entry point cho client; môi trường public cần domain HTTPS/reverse proxy tới Gateway. Upstream service URLs cấu hình qua environment variables.

| Path | Service đích | Ghi chú |
|---|---|---|
| `/api/auth/**`, `/api/me/**`, `/api/admin/users/**` | Identity | Đăng ký/đăng nhập, session, user status. |
| `/api/services/*/providers/**`, `/api/services/providers/**`, `/api/provider/profile/**`, `/api/provider/applications/**`, `/api/admin/providers/**`, `/api/customer/profile/**` | Profile | Hồ sơ và provider lookup. |
| `/api/services/categories`, `/api/catalog/**`, `/api/provider/packages/**`, `/api/admin/packages/**`, `/api/admin/categories/**` | Catalog | Danh mục/gói và moderation; từng endpoint cần đối chiếu controller tương ứng. |
| `/api/reviews/**` | Review | Review service riêng. |
| `/api/bookings/**`, `/api/provider/bookings/**`, `/api/admin/bookings/**` | Booking | Admin prefix được route nhưng chưa có workflow API được xác nhận. |
| `/api/notifications/**` | Notification | In-app inbox. |
| `/api/payments/**`, `/api/admin/payments/**` | Payment | Route cấu hình nhưng chưa có transaction API. |

Gateway xác minh JWT RS256, issuer `doan1-identity` và audience `doan1-platform` cho protected prefixes; endpoint public tiếp tục cho phép anonymous. Domain service vẫn kiểm tra role và ownership. Route tồn tại không xác nhận endpoint/business workflow tồn tại.

Không có route `/internal/**`; internal HTTP và RabbitMQ chỉ hoạt động trong backend network, xác thực bằng API key/credentials riêng. Không công bố port service ra Internet.

## CORS

Gateway CORS hiện cho phép dev origin `http://localhost:4200`, các method API và headers `Content-Type`/`Authorization`. Next.js frontend mặc định chạy ở `http://localhost:3000` và gọi Gateway server-side qua BFF; browser-origin khác cần được thêm rõ ràng trước khi gọi Gateway trực tiếp. CORS không thay thế JWT/role/ownership checks.
