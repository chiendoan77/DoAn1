# Hướng dẫn tích hợp API từ bên ngoài

Tài liệu này dành cho web/mobile clients, Postman và hệ thống server bên ngoài. Chỉ gọi API qua API Gateway; không gọi service port, database hoặc `/internal/**` từ client.

## 1. Địa chỉ kết nối

| Môi trường | Base URL | Ghi chú |
|---|---|---|
| Docker Compose, gọi từ máy host | `http://localhost:8080` | Gateway được bind vào loopback trong Compose. |
| Container cùng Docker network | `http://api-gateway:8080` | Dùng tên service Compose; không dùng `localhost` bên trong container. |
| Môi trường triển khai | `https://<api-domain>` | Domain/TLS/reverse proxy phải được cấu hình để forward tới Gateway; repository chưa định nghĩa domain production. |

Ghép base URL với path API, ví dụ `http://localhost:8080/api/catalog/packages`. Mọi service port khác là chi tiết triển khai, không phải public API. Gateway nội bộ không được expose trực tiếp ra Internet; môi trường public cần TLS, firewall/reverse proxy và secret configuration riêng.

## 2. Headers và định dạng

- JSON request: `Content-Type: application/json`.
- Protected endpoint: `Authorization: Bearer <accessToken>`.
- Query/path ID dạng UUID trừ `categoryId` là mã string (ví dụ `cleaning`, `repair`).
- Thời gian booking là ISO-8601 có UTC offset, ví dụ `2030-01-15T09:00:00+07:00`; thời điểm gửi phải ở tương lai.
- Giá dịch vụ là số nguyên VND, không gửi phần thập phân.
- Admin endpoint cần access token có role `ADMIN`; chỉ backend Identity được cấu hình bootstrap Admin. Không cho phép client tự cấp role.

## 3. Đăng ký, đăng nhập và phiên

Đăng ký user public chỉ chấp nhận role `CUSTOMER` hoặc `PROVIDER`. Admin được khởi tạo riêng bằng cấu hình server-side; không đăng ký Admin qua API.

PowerShell example:

```powershell
$baseUrl = "http://localhost:8080"
$login = Invoke-RestMethod `
  -Method Post `
  -Uri "$baseUrl/api/auth/login" `
  -ContentType "application/json" `
  -Body (@{ identifier = "customer@example.test"; password = "replace-with-local-password" } | ConvertTo-Json)

$accessToken = $login.accessToken
$headers = @{ Authorization = "Bearer $accessToken" }
Invoke-RestMethod -Method Get -Uri "$baseUrl/api/me" -Headers $headers
```

`identifier` có thể là email hoặc số điện thoại. Response login trả `accessToken`, `refreshToken`, thời hạn và account DTO. Access token dùng với protected API; refresh token chỉ dùng với Identity refresh/logout, lưu kín và không ghi log.

Refresh rotation:

```powershell
$refreshed = Invoke-RestMethod `
  -Method Post `
  -Uri "$baseUrl/api/auth/refresh" `
  -ContentType "application/json" `
  -Body (@{ refreshToken = $login.refreshToken } | ConvertTo-Json)
$accessToken = $refreshed.accessToken
```

Logout:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri "$baseUrl/api/auth/logout" `
  -ContentType "application/json" `
  -Body (@{ refreshToken = $refreshed.refreshToken } | ConvertTo-Json)
```

Refresh token cũ không được dùng lại sau rotation; replay có thể revoke cả token family. Logout revoke refresh-token family, nhưng access JWT đã phát vẫn có hiệu lực tới hết TTL. Browser app dùng Next.js BFF với HttpOnly cookies; client bên ngoài gọi Gateway trực tiếp bằng Bearer token, không sao chép cookies BFF.

## 4. Ví dụ gọi API nghiệp vụ

### Duyệt gói đã được duyệt

```powershell
Invoke-RestMethod -Method Get -Uri "$baseUrl/api/catalog/packages?categoryId=cleaning&page=0&size=20"
```

### Lưu hồ sơ khách hàng

```powershell
$customerProfile = @{
  displayName = "Khách hàng mẫu"
  phoneNumber = "+84901234567"
  defaultAddress = "Quận 1, TP. Hồ Chí Minh"
} | ConvertTo-Json

Invoke-RestMethod -Method Put `
  -Uri "$baseUrl/api/customer/profile" `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $customerProfile
```

### Tạo booking từ package

Lấy `id` từ một package APPROVED và thay UUID ví dụ bên dưới bằng ID đó.

```powershell
$booking = @{
  packageId = "00000000-0000-0000-0000-000000000000"
  scheduledAt = "2030-01-15T09:00:00+07:00"
  address = "Quận 1, TP. Hồ Chí Minh"
  description = "Mô tả công việc cần thực hiện"
} | ConvertTo-Json

Invoke-RestMethod -Method Post `
  -Uri "$baseUrl/api/bookings" `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $booking
```

Booking đồng bộ xác thực package/provider qua Catalog/Profile và lưu snapshot tên, giá, thời lượng. Không có API hủy/đổi lịch hoặc thanh toán trong phạm vi hiện tại.

## 5. Endpoint public qua Gateway

Path dưới đây được nối sau base URL. Quyền chi tiết, fields và trạng thái triển khai xem API reference theo service.

| Service owner | Path / method | Quyền |
|---|---|---|
| Identity | `POST /api/auth/register`, `/api/auth/login`, `/api/auth/refresh`, `/api/auth/logout`; `GET /api/me` | Public trừ `/api/me` cần đăng nhập |
| Identity | `PATCH /api/admin/users/{accountId}/status` | ADMIN |
| Profile | `GET /api/services/{categoryId}/providers`, `/api/services/providers/{providerId}`, `/api/services/{categoryId}/providers/{providerId}` | Public |
| Profile | `POST /api/provider/applications`; `GET/PUT /api/provider/profile` | PROVIDER |
| Profile | `GET/PUT /api/customer/profile` | CUSTOMER |
| Profile | `GET /api/admin/providers/pending`; `PATCH /api/admin/providers/{providerId}/approval`, `/suspension` | ADMIN |
| Catalog | `GET /api/catalog/categories`, `/api/catalog/categories/{id}`, `/api/catalog/packages`, `/api/catalog/packages/{id}`; `GET /api/services/categories` | Public |
| Catalog | `POST/GET/PUT/DELETE /api/provider/packages/**`; `POST /api/provider/packages/{id}/submit` | PROVIDER; owner đối với package cụ thể |
| Catalog | `GET /api/admin/packages/**`; `POST /api/admin/packages/{id}/approve`, `/reject`, `/deactivate` | ADMIN |
| Booking | `POST /api/bookings`; `GET /api/bookings/mine`; `PATCH /api/provider/bookings/{bookingId}/status` | CUSTOMER, authenticated, PROVIDER được gán tương ứng |
| Review | `POST /api/reviews`; `GET /api/reviews/mine` | Authenticated; participant/eligibility được Booking xác minh |
| Notification | `GET /api/notifications/mine` | Authenticated |
| Payment | Chưa có transaction API | Không hỗ trợ thanh toán qua API hiện tại |

Prefix `/api/admin/bookings/**`, `/api/payments/**` có route cấu hình ở Gateway nhưng service chưa cung cấp workflow/payment API tương ứng; route tồn tại không có nghĩa endpoint đã triển khai.

## 6. Versioning và lỗi

- API hiện tại chưa có path version như `/v1`; thay đổi contract cần được ghi trong service API docs và thông báo cho consumer.
- Package update/moderation dùng `expectedVersion` hoặc `If-Match` theo endpoint; version cũ bị từ chối để tránh ghi đè thay đổi mới.
- HTTP `401` thường là thiếu/sai/hết hạn access token; `403` là không đủ quyền/origin; `404` là tài nguyên không tồn tại hoặc không được public; `400` là request validation/business input; `409` là xung đột trạng thái/version. Xem response thực tế của từng endpoint khi tích hợp.
- Không retry các POST tạo nghiệp vụ một cách mù quáng: Payment idempotency chưa được cung cấp và các API tạo hiện tại không cam kết idempotency key.

## 7. Browser, BFF và CORS

Frontend trong repo dùng BFF tại `/api/backend/{path}` để thêm Bearer token từ HttpOnly cookie và forward request tới Gateway. BFF session endpoints xử lý login/refresh/logout; browser không đọc token trực tiếp. Các API bên ngoài có thể dùng Gateway trực tiếp.

Gateway CORS hiện cho phép origin dev `http://localhost:4200`; frontend Next.js mặc định chạy ở `http://localhost:3000` và gọi Gateway server-side qua BFF nên không cần browser gọi thẳng Gateway. Browser client đặt ở origin khác phải cấu hình allowlist chính xác tại Gateway trước khi gọi trực tiếp. CORS không thay thế authentication/authorization.

## 8. Giao tiếp nội bộ (không phải public integration)

| Caller → owner | Internal operation | Mục đích |
|---|---|---|
| Booking → Catalog | Lấy package qua `/internal/catalog/packages/{id}` | Xác minh trạng thái/package snapshot khi tạo booking |
| Booking → Profile | Kiểm tra provider/package eligibility | Xác minh provider trước khi tạo booking |
| Review → Booking | `/internal/bookings/{id}/review-eligibility` | Xác minh completion, reviewer và review target |
| Profile → Identity | `/internal/accounts/{id}/status` | Từ chối account không ACTIVE |
| Profile/Notification | RabbitMQ `ReviewSubmitted` / `BookingCompleted` và Catalog events | Cập nhật rating projection / in-app inbox |

Internal HTTP endpoints yêu cầu API key theo service qua header `X-Service-Api-Key`; event consumers phải idempotent. Không đưa API key, broker credentials, private JWT key hoặc endpoint `/internal/**` vào ứng dụng client. Các integration này chỉ hoạt động trên network backend.

## 9. Lưu ý triển khai

- RS256 keys, database/broker credentials và service API keys phải được cấu hình riêng theo môi trường; không dùng giá trị local mặc định cho production.
- Production API cần HTTPS và kiểm soát nguồn truy cập ở reverse proxy/firewall.
- Không public port MySQL/RabbitMQ hoặc port microservice. Chỉ công bố một API domain qua reverse proxy tới Gateway.
- Metrics/tracing được cấu hình trong Compose; runtime production, rate limiting, central log collection và mTLS chưa được xác minh/triển khai hoàn chỉnh.
