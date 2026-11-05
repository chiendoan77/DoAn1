# API hiện tại và phần còn dự kiến

Public API đi qua Gateway; endpoint `/internal/**` chỉ dùng server-to-server và không route công khai. Chi tiết request/response từng domain ở [Identity](./services/identity-access/API.md), [Marketplace](./services/marketplace/API.md), [Booking](./services/booking/API.md).

| Domain | Đã triển khai |
|---|---|
| Identity | `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/me`. |
| Marketplace | `GET /api/services/categories`, provider listing/detail, provider application/profile, `PUT /api/provider/profile` (cập nhật của chính provider; cần Admin duyệt lại), Admin pending/approval, `POST /api/reviews`, `GET /api/reviews/mine`. |
| Booking | `POST /api/bookings`, `GET /api/bookings/mine`, provider status patch. |
| Nội bộ | Booking xác minh provider qua `GET /internal/providers/{id}`; Marketplace xác minh review qua `GET /internal/bookings/{id}/review-eligibility`; cả hai yêu cầu `X-Service-Api-Key`. |

Admin user/booking, cancel/reschedule, slot/quota policies, payment transactions/webhooks và gateway-specific contracts chưa triển khai. Các endpoint này trong tài liệu requirements là mục tiêu, không phải API đang hoạt động.

Admin Portal dùng JWT + role ADMIN. API key chỉ dành cho server-to-server. JWT/BFF và error behavior có thể xem trong service/frontend docs.
