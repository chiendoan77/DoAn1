# Booking API

Public API đi qua Gateway. Mọi endpoint trừ internal review eligibility yêu cầu xác thực đúng role; provider chỉ có thể cập nhật booking được gán cho tài khoản đó.

| Method | Path | Quyền | Mục đích |
|---|---|---|---|
| POST | `/api/bookings` | CUSTOMER | Tạo booking: `{packageId, scheduledAt, address, description}`; package phải APPROVED ở Catalog, provider được xác minh qua Profile. |
| GET | `/api/bookings/mine` | CUSTOMER/PROVIDER | Lịch sử booking theo tài khoản hiện tại. |
| PATCH | `/api/provider/bookings/{bookingId}/status` | PROVIDER được gán | Body `{status}`; chuyển trạng thái hợp lệ theo state machine. |
| GET | `/internal/bookings/{bookingId}/review-eligibility?reviewerAccountId=...&reviewerRole=...` | Service API key | Review xác minh booking COMPLETED, người tham gia và target role/ID; không qua Gateway. |

Response booking có `packageId`, `packageName`, `packagePrice`, `packageDurationMinutes` cùng category/provider/customer snapshots. Trạng thái: `REQUESTED`, `ACCEPTED`, `REJECTED`, `COMPLETED`; chuyển sang COMPLETED tạo `BookingCompleted` qua outbox. Không có endpoint admin, get-by-id, cancel hoặc reschedule. Slot overlap/repeat-category policy chưa triển khai.

Booking dùng `BOOKING_PROFILE_API_KEY` khi gọi Profile, `BOOKING_CATALOG_API_KEY` khi gọi Catalog; giá trị này phải trùng với `CATALOG_BOOKING_API_KEY` ở Catalog. `REVIEW_BOOKING_API_KEY` bảo vệ internal review-eligibility endpoint.
