# Profile API

Public API qua Gateway; JWT role/ownership được kiểm tra trong Profile. Endpoint `/internal/**` không qua Gateway.

| Method | Path | Quyền | Mục đích |
|---|---|---|---|
| GET | `/api/services/{categoryId}/providers?limit=10` | Public | Danh sách tối đa 10 provider đã duyệt ở trang đầu (tương thích cũ). |
| GET | `/api/services/{categoryId}/providers/page?page=0&limit=10` | Public | Trang provider đã duyệt, gồm `content`, `totalElements`, `totalPages`, `size`, `number`; limit tối đa 10. |
| GET | `/api/services/providers/{providerId}` | Public | Chi tiết provider đã duyệt, không bị suspension. |
| GET | `/api/services/{categoryId}/providers/{providerId}` | Public | Provider đã duyệt thuộc category. |
| POST | `/api/provider/applications` | PROVIDER | Tạo provider application. |
| GET | `/api/provider/profile` | PROVIDER | Xem hồ sơ của mình. |
| PUT | `/api/provider/profile` | PROVIDER | Cập nhật hồ sơ; trạng thái về `PENDING`. |
| GET | `/api/customer/profile` | CUSTOMER | Xem hồ sơ khách hàng. |
| PUT | `/api/customer/profile` | CUSTOMER | Tạo/cập nhật `{displayName, phoneNumber, defaultAddress, avatarUrl}`; avatar là URL HTTPS tùy chọn. |
| GET | `/api/admin/providers/pending` | ADMIN | Hồ sơ chờ duyệt. |
| PATCH | `/api/admin/providers/{providerId}/approval` | ADMIN | Approve/reject hồ sơ đang `PENDING`. |
| PATCH | `/api/admin/providers/{providerId}/suspension` | ADMIN | Suspend/unsuspend provider profile. |
| GET | `/internal/providers/{providerId}?categoryId=...` | Booking API key | Provider đủ điều kiện cho booking. |
| GET | `/internal/profiles/providers/{accountId}/eligibility` | Catalog API key | Kiểm tra provider account/profile đã active và approved. |
| POST | `/internal/profiles/providers/{providerId}/rating-reviews` | Review service API key | Nhận rating projection được tiêu thụ từ event `ReviewSubmitted`; duplicate ID là idempotent. |

Profile gọi Identity qua API key riêng để kiểm tra account đang ACTIVE. Migration endpoint chỉ phục vụ import hồ sơ legacy và yêu cầu migration key.

Response public không để lộ `identityAccountId`; ID account chỉ xuất hiện trong DTO nội bộ cần thiết.
