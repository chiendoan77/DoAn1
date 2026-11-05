# Marketplace API

Public API đi qua Gateway. Provider/Admin endpoints yêu cầu Bearer JWT với role tương ứng.

| Method | Path | Quyền | Mục đích |
|---|---|---|---|
| GET | `/api/services/categories` | Public | Danh mục `cleaning` và `repair`. |
| GET | `/api/services/{categoryId}/providers?limit=10` | Public | Provider đã duyệt; limit được chặn tối đa 10 và kết quả xếp theo rating. |
| GET | `/api/services/{categoryId}/providers/{providerId}` | Public | Chi tiết provider đã duyệt, chỉ khi thuộc danh mục. |
| POST | `/api/provider/applications` | PROVIDER | Tạo hồ sơ đang chờ duyệt: `{categoryIds, address, imageUrl, description}`. |
| GET | `/api/provider/profile` | PROVIDER | Lấy hồ sơ của tài khoản hiện tại. |
| PUT | `/api/provider/profile` | PROVIDER | Cập nhật hồ sơ của tài khoản hiện tại với `{categoryIds, address, imageUrl, description}`; hồ sơ chuyển về `PENDING` và bị ẩn khỏi API public tới khi Admin duyệt lại. |
| GET | `/api/admin/providers/pending` | ADMIN | Danh sách hồ sơ đang chờ duyệt. |
| PATCH | `/api/admin/providers/{providerId}/approval` | ADMIN | `{approved, reason?}`; chỉ xử lý hồ sơ PENDING. |
| POST | `/api/reviews` | CUSTOMER/PROVIDER | Gửi đánh giá sau khi Booking xác nhận đủ điều kiện; `{bookingId, rating, comment?}`. |
| GET | `/api/reviews/mine` | CUSTOMER/PROVIDER | Danh sách review do tài khoản hiện tại gửi. |
| GET | `/internal/providers/{providerId}?categoryId=...` | Service API key | Booking xác minh provider và nhận provider account ID; không qua Gateway. |

Public DTO provider không chứa `identityAccountId`; trường này chỉ có trong DTO nội bộ phục vụ Booking. `ProviderResponse` có `id`, `fullName`, `categoryIds`, `categories`, `address`, `imageUrl`, `description`, `status`, `rating`, `reviewCount`, `demoData`.

Provider offering CRUD/public browse/Admin offering approval, upload ảnh, admin-created provider, category CRUD, tìm kiếm nâng cao và moderation/audit history chưa có implementation trong source hiện tại. Yêu cầu provider tạo/sửa/archive gói và Admin duyệt cần API/database contract trước khi implementation; không xem các endpoint `/api/provider/services`, `/api/admin/offerings/**` hoặc `/api/services/{categoryId}/offerings` trong tài liệu cũ là API đang hoạt động.
