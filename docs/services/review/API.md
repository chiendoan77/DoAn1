# Review API

Public API đi qua Gateway; JWT/role được kiểm tra trong Review. Internal APIs không được route qua Gateway.

| Method | Path | Quyền | Hợp đồng |
|---|---|---|---|
| POST | `/api/reviews` | CUSTOMER/PROVIDER | Body `{bookingId, rating, comment?}`; Booking phải xác nhận eligibility. |
| GET | `/api/reviews/mine` | Authenticated | Danh sách review của account hiện tại. |
| POST | `/internal/migration/reviews/import` | Review migration API key | Import danh sách review legacy; bỏ qua ID đã tồn tại và không phát event. |

Review gọi `GET /internal/bookings/{bookingId}/review-eligibility` ở Booking bằng service-specific API key. `ReviewSubmitted` chứa event/review IDs, target ID/role, rating và thời điểm; Profile là consumer cập nhật rating.
