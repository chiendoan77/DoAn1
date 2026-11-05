# Marketplace API

Public API đi qua Gateway. Provider/Admin endpoints yêu cầu Bearer JWT với role tương ứng.

| Method | Path | Quyền | Mục đích |
|---|---|---|---|
| GET | `/api/services/categories` | Public | Danh mục `cleaning` và `repair`. |
| GET | `/api/services/{categoryId}/providers?limit=10` | Public | Provider đã duyệt; limit được chặn tối đa 10 và kết quả xếp theo rating. |
| GET | `/api/services/{categoryId}/providers/{providerId}` | Public | Chi tiết provider đã duyệt, chỉ khi thuộc danh mục. |
| GET | `/api/services/{categoryId}/offerings?limit=50` | Public | Gói đã duyệt thuộc provider đã duyệt; trả tên provider, số điện thoại công khai, tên/mô tả gói, giá VND và danh mục. Giới hạn tối đa 50. |
| POST | `/api/provider/applications` | PROVIDER | Tạo hồ sơ đang chờ duyệt: `{categoryIds, address, imageUrl, description}`. |
| GET | `/api/provider/profile` | PROVIDER | Lấy hồ sơ của tài khoản hiện tại. |
| PUT | `/api/provider/profile` | PROVIDER | Cập nhật hồ sơ của tài khoản hiện tại với `{categoryIds, address, imageUrl, description}`; hồ sơ chuyển về `PENDING` và bị ẩn khỏi API public tới khi Admin duyệt lại. |
| GET | `/api/provider/services` | PROVIDER | Liệt kê các gói của provider hiện tại, gồm trạng thái kiểm duyệt. |
| POST | `/api/provider/services` | PROVIDER | Tạo gói: `{categoryId, name, description, price, providerPhone}`; `providerPhone` được hiển thị công khai khi gói được duyệt. Provider được suy ra từ JWT; gói tạo mới ở `PENDING`. |
| PUT | `/api/provider/services/{offeringId}` | PROVIDER | Sửa gói của chính provider; cập nhật nội dung/giá và chuyển về `PENDING` để Admin duyệt lại. |
| DELETE | `/api/provider/services/{offeringId}` | PROVIDER | Archive gói của chính provider (status `ARCHIVED`), ẩn khỏi khách mới nhưng giữ dữ liệu. Trả `204 No Content`. |
| GET | `/api/admin/providers/pending` | ADMIN | Danh sách hồ sơ đang chờ duyệt. |
| PATCH | `/api/admin/providers/{providerId}/approval` | ADMIN | `{approved, reason?}`; chỉ xử lý hồ sơ PENDING. |
| GET | `/api/admin/offerings/pending` | ADMIN | Danh sách gói đang chờ duyệt. |
| PATCH | `/api/admin/offerings/{offeringId}/approval` | ADMIN | `{approved, reason?}`; chỉ xử lý gói PENDING. Chỉ có thể approve khi hồ sơ provider đã được duyệt. |
| POST | `/api/reviews` | CUSTOMER/PROVIDER | Gửi đánh giá sau khi Booking xác nhận đủ điều kiện; `{bookingId, rating, comment?}`. |
| GET | `/api/reviews/mine` | CUSTOMER/PROVIDER | Danh sách review do tài khoản hiện tại gửi. |
| GET | `/internal/providers/{providerId}?categoryId=...` | Service API key | Booking xác minh provider và nhận provider account ID; không qua Gateway. |

Public DTO provider không chứa `identityAccountId`; trường này chỉ có trong DTO nội bộ phục vụ Booking. `ProviderResponse` có `id`, `fullName`, `categoryIds`, `categories`, `address`, `imageUrl`, `description`, `status`, `rating`, `reviewCount`, `demoData`. `ServiceOfferingResponse` có `providerProfileId`, tên provider từ hồ sơ Marketplace, `providerPhone` do provider cung cấp cho từng gói, và `price` theo VND.

Upload ảnh, admin-created provider, category CRUD, tìm kiếm nâng cao, moderation/audit history và tích hợp gói vào booking chưa triển khai.
