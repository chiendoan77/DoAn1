# Catalog API

Public API qua Gateway; provider/Admin routes yêu cầu JWT đúng role. Package response có `id`, `providerId`, `categoryId`, `name`, `description`, `price`, `durationMinutes`, `providerPhone`, `status`, `rejectionReason`, `version`, `createdAt`, `updatedAt`.

| Method | Path | Quyền | Mục đích |
|---|---|---|---|
| GET | `/api/catalog/categories`, `/api/services/categories` | Public | Danh mục `cleaning` và `repair`; alias thứ hai giữ tương thích Gateway route cũ. |
| GET | `/api/catalog/categories/{id}` | Public | Chi tiết category. |
| GET | `/api/catalog/packages` | Public | Browse/filter theo category, text, price range, paging/sort; chỉ packages APPROVED. |
| GET | `/api/catalog/packages/{id}` | Public | Package APPROVED. |
| POST | `/api/provider/packages` | PROVIDER | Tạo package DRAFT; yêu cầu provider approved/active. |
| GET | `/api/provider/packages` | PROVIDER | Danh sách package của account hiện tại. |
| GET | `/api/provider/packages/{id}` | PROVIDER owner | Chi tiết package của mình. |
| PUT | `/api/provider/packages/{id}` | PROVIDER owner | Cập nhật package, body gồm `expectedVersion`; sửa APPROVED cần duyệt lại. |
| DELETE | `/api/provider/packages/{id}?expectedVersion=...` | PROVIDER owner | Archive package, trả 204. |
| POST | `/api/provider/packages/{id}/submit` | PROVIDER owner | Body `{ "expectedVersion": n }`, gửi duyệt. |
| GET | `/api/admin/packages` | ADMIN | Tìm/lọc packages. |
| GET | `/api/admin/packages/pending` | ADMIN | Queue chờ duyệt. |
| GET | `/api/admin/packages/{id}` | ADMIN | Chi tiết package. |
| POST | `/api/admin/packages/{id}/approve` | ADMIN | Body `{ "expectedVersion": n }`. |
| POST | `/api/admin/packages/{id}/reject` | ADMIN | Header `If-Match: n`; body `{ "reason": "..." }`. |
| GET | `/api/admin/packages/{id}/history` | ADMIN | Moderation/history actions. |
| POST | `/api/admin/packages/{id}/deactivate` | ADMIN | Body `{ "expectedVersion": n }`. |
| GET | `/internal/catalog/packages/{id}` | Booking API key | Trả package chỉ khi APPROVED; không qua Gateway. |

`price` là VND nguyên số, `providerPhone` bắt buộc. Stale `expectedVersion` trả conflict. Category IDs là string codes, không phải UUID.

Internal package lookup uses the dedicated `BOOKING_CATALOG_API_KEY`; Booking uses this contract when creating a booking and stores package/price snapshots.
