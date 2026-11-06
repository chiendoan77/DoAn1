# API reference

## Quy ước

- Public API entry point là API Gateway. Local base URL: `http://localhost:8080`; mọi path dưới đây được nối sau base URL.
- Browser app gọi Next.js BFF; client/service bên ngoài gọi Gateway, không gọi trực tiếp microservice.
- JSON request dùng `Content-Type: application/json`. API cần đăng nhập dùng `Authorization: Bearer <accessToken>`; service xác minh role và quyền sở hữu.
- Path `/internal/**` là server-to-server only, không được expose qua Gateway. Payment route có thể được cấu hình nhưng chưa có transaction API.
- API hiện chưa có version prefix `/v1`. Thay đổi contract phải được cập nhật ở API docs của service sở hữu.

## Identity & Access

| Method | Path | Quyền | Hợp đồng |
|---|---|---|---|
| POST | `/api/auth/register` | Public | `{email, phoneNumber, username, fullName, password, role}`; role `CUSTOMER` hoặc `PROVIDER`. |
| POST | `/api/auth/login` | Public | `{identifier, password}`; trả access/refresh tokens, TTL và account DTO. |
| POST | `/api/auth/refresh` | Public | `{refreshToken}`; rotate refresh token. |
| POST | `/api/auth/logout` | Public | `{refreshToken}`; revoke refresh-token family. |
| GET | `/api/me` | Authenticated | Account hiện tại. |
| PATCH | `/api/admin/users/{accountId}/status` | ADMIN | `{status: ACTIVE\|SUSPENDED, reason}`; audit khi đổi status. |

## Profile

| Method | Path | Quyền | Hợp đồng |
|---|---|---|---|
| GET | `/api/services/{categoryId}/providers?limit=10` | Public | Tối đa 10 provider đã duyệt ở trang đầu (tương thích cũ). |
| GET | `/api/services/{categoryId}/providers/page?page=0&limit=10` | Public | Trang provider đã duyệt; response có `content`, `totalElements`, `totalPages`, `size`, `number`. |
| GET | `/api/services/providers/{providerId}` | Public | Provider detail đã duyệt/không suspended. |
| GET | `/api/services/{categoryId}/providers/{providerId}` | Public | Provider theo category. |
| POST | `/api/provider/applications` | PROVIDER | Tạo provider application. |
| GET/PUT | `/api/provider/profile` | PROVIDER | Xem/cập nhật profile; update về trạng thái cần duyệt. |
| GET/PUT | `/api/customer/profile` | CUSTOMER | Profile khách hàng; PUT nhận `{displayName, phoneNumber, defaultAddress}`. |
| GET | `/api/admin/providers/pending` | ADMIN | Danh sách hồ sơ chờ duyệt. |
| PATCH | `/api/admin/providers/{providerId}/approval` | ADMIN | Approve/reject provider application. |
| PATCH | `/api/admin/providers/{providerId}/suspension` | ADMIN | `{suspended: boolean}`. |

## Catalog

| Method | Path | Quyền | Hợp đồng |
|---|---|---|---|
| GET | `/api/catalog/categories`, `/api/services/categories` | Public | Danh mục. |
| GET | `/api/catalog/categories/{id}` | Public | Chi tiết category. |
| GET | `/api/catalog/packages` | Public | Lọc bằng `categoryId`, `q`, `minPrice`, `maxPrice`, `page`, `size`, `sort`, `direction`; chỉ package APPROVED. |
| GET | `/api/catalog/packages/{id}` | Public | Package APPROVED. |
| POST | `/api/provider/packages` | PROVIDER | Tạo DRAFT package; giá VND nguyên số. |
| GET | `/api/provider/packages`, `/api/provider/packages/my` | PROVIDER | Danh sách package của mình. |
| GET/PUT/DELETE | `/api/provider/packages/{id}` | PROVIDER owner | Xem/sửa/archive; PUT cần `expectedVersion`; DELETE cần query `expectedVersion`. |
| POST | `/api/provider/packages/{id}/submit` | PROVIDER owner | `{expectedVersion}` để gửi duyệt. |
| GET | `/api/admin/packages` | ADMIN | Lọc package theo status/category/provider/query và paging. |
| GET | `/api/admin/packages/pending` | ADMIN | Queue chờ duyệt. |
| GET | `/api/admin/packages/{id}`, `/api/admin/packages/{id}/history` | ADMIN | Xem package và moderation history. |
| POST | `/api/admin/packages/{id}/approve`, `/api/admin/packages/{id}/deactivate` | ADMIN | `{expectedVersion}`. |
| POST | `/api/admin/packages/{id}/reject` | ADMIN | `If-Match: <version>` và `{reason}`. |

## Booking, Review và Notification

| Method | Path | Quyền | Hợp đồng |
|---|---|---|---|
| POST | `/api/bookings` | CUSTOMER | `{packageId, scheduledAt, address, description}`; package phải APPROVED. Response có package/price snapshots. |
| GET | `/api/bookings/mine` | Authenticated | Lịch sử của customer/provider hiện tại. |
| PATCH | `/api/provider/bookings/{bookingId}/status` | PROVIDER được gán | `{status}`; trạng thái hợp lệ gồm `ACCEPTED`, `REJECTED`, `COMPLETED`. |
| POST | `/api/reviews` | Authenticated participant | `{bookingId, rating, comment?}`; Booking xác minh eligibility; rating 1–5. |
| GET | `/api/reviews/mine` | Authenticated | Review của account hiện tại. |
| GET | `/api/notifications/mine` | Authenticated | Inbox in-app; field liên kết booking/package có thể null. |

Slot conflict, repeat-booking quota, booking cancel/reschedule, Booking admin API, Review edit/moderation và notification read/delete chưa triển khai.

## Payment

Payment service chỉ có adapter-registry skeleton. Chưa có endpoint tạo/tra cứu transaction, webhook, refund hay event `PaymentSucceeded`; không gọi `/api/payments/**` như một contract đã hỗ trợ.

## Internal API và events

| Caller → service owner | Operation | Xác thực / mục đích |
|---|---|---|
| Booking → Catalog | `GET /internal/catalog/packages/{id}` | Service API key; chỉ trả package hợp lệ để tạo booking. |
| Booking → Profile | Provider eligibility lookup | Service API key; xác minh provider/category/account. |
| Review → Booking | `GET /internal/bookings/{bookingId}/review-eligibility?reviewerAccountId=...&reviewerRole=...` | Service API key; completion, participant và target verification. |
| Profile → Identity | `GET /internal/accounts/{accountId}/status` | Service API key; xác minh account active. |
| Marketplace → Profile/Review | Legacy export endpoints `/internal/migration/provider-profiles`, `/internal/migration/reviews` | Migration key; chỉ phục vụ import/đối soát legacy. |

`BookingCompleted` và `ReviewSubmitted` được ghi qua transactional outbox rồi phát RabbitMQ; package lifecycle events cũng dùng outbox. Đây là async integration, không phải HTTP endpoint public. Payment events/saga được hoãn.

## Tài liệu tích hợp

- [Hướng dẫn kết nối API bên ngoài](./API_INTEGRATION_GUIDE.md): base URL, đăng nhập/refresh/logout, PowerShell samples, browser/BFF, routing và internal network.
- [Gateway routes](./services/api-gateway/API.md)
- [Identity](./services/identity-access/API.md) · [Profile](./services/profile/API.md) · [Catalog](./services/catalog/API.md)
- [Booking](./services/booking/API.md) · [Review](./services/review/API.md) · [Notification](./services/notification/API.md)
- [Marketplace migration](./services/marketplace/API.md) · [Payment status](./services/payment/API.md)
