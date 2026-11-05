# Booking API

Public API đi qua Gateway. Booking API yêu cầu Bearer JWT; provider chỉ có thể cập nhật booking được gán cho tài khoản đó.

| Method | Path | Quyền | Mục đích |
|---|---|---|---|
| POST | `/api/bookings` | CUSTOMER | Tạo booking: `{providerId, categoryId, scheduledAt, address, description}`; provider được xác minh qua Marketplace. |
| GET | `/api/bookings/mine` | CUSTOMER/PROVIDER | Lịch sử booking theo tài khoản hiện tại. |
| PATCH | `/api/provider/bookings/{bookingId}/status` | PROVIDER được gán | Body `{status}`; chuyển trạng thái hợp lệ theo state machine. |
| GET | `/internal/bookings/{bookingId}/review-eligibility?reviewerAccountId=...&reviewerRole=...` | Service API key | Marketplace xác minh booking COMPLETED, người tham gia và target role/ID; không qua Gateway. |

Trạng thái hiện có: `REQUESTED`, `ACCEPTED`, `REJECTED`, `COMPLETED`. Không có endpoint admin, get-by-id, cancel hoặc reschedule. Slot overlap và repeat-category policy chưa triển khai.
