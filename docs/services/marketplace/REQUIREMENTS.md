# Requirements — Marketplace

> Trạng thái: provider profile/onboarding/approval, public provider browse, service offering CRUD/re-approval and public offering browse, cùng review đã triển khai. Offering-to-booking integration và một số policy vẫn chưa có.

## 1. Giới thiệu

Marketplace quản lý nội dung dịch vụ và thông tin provider để khách hàng khám phá, đồng thời xử lý quy trình duyệt provider.

## 2. Mục tiêu và phạm vi

Marketplace service sở hữu hồ sơ provider, danh mục/offerings, giá/khu vực nếu được chốt và public queries.

## 3. Trách nhiệm nghiệp vụ

- Sở hữu ProviderProfile, danh mục và offerings.
- Quản lý trạng thái xét duyệt provider.
- Cung cấp dữ liệu public cho tìm kiếm/chi tiết dịch vụ.
- Admin thực hiện moderation trên dữ liệu Marketplace qua API domain này.

## 4. Chức năng thuộc phạm vi

Đã triển khai duyệt provider, xem/gửi/cập nhật profile, provider-managed offerings (tạo, sửa giá/nội dung, archive), public offering browse và Admin approval. Mỗi cập nhật profile chỉ áp dụng hồ sơ gắn với account trong JWT và đưa về `PENDING`; tạo/sửa offering cũng cần Admin duyệt. Offering archived bị ẩn khỏi khách mới nhưng được giữ trong database. Catalog CRUD của Admin vẫn chưa triển khai.

## 5. Chức năng ngoài phạm vi

Credential/role source of truth, booking lifecycle, payment transaction, complaint workflow độc lập, hoặc DB của service khác. Marketplace lưu review và rating; Booking vẫn là owner của booking state/participants.

## 6. Actors

Khách hàng (đọc public), nhà cung cấp (quản lý dữ liệu của mình), Admin (moderation/catalog).

## 7. Functional Requirements

- `MARKET-FR-001` (Đã xác nhận): khách hàng liệt kê/tìm kiếm dịch vụ hiển thị công khai.
- `MARKET-FR-002` (Đã xác nhận): khách hàng xem chi tiết offering/provider và giá được cấu hình.
- `MARKET-FR-003` (Đã xác nhận): provider tạo/cập nhật hồ sơ và offerings thuộc quyền sở hữu.
- `MARKET-FR-004` (Đã xác nhận): Admin duyệt provider và quản lý danh mục theo quyền.

## 8. Non-functional Requirements

API public chỉ trả dữ liệu được phép hiển thị; dữ liệu đầu vào được validate; thao tác provider/Admin phải kiểm tra role và ownership; kết quả tìm kiếm cần pagination nếu scope yêu cầu. Chưa có ngưỡng latency/tải.

## 9. Business Rules

- Chỉ provider đủ điều kiện theo policy được xuất bản offering.
- Provider chỉ sửa hồ sơ/offerings do mình sở hữu.
- Booking chỉ tham chiếu offering hợp lệ; booking domain xác thực qua API/DTO.
- Create/update offering và thay đổi giá phải chờ Admin duyệt lại.
- Provider phone được provider cung cấp cho mỗi gói và hiển thị công khai khi gói được duyệt.
- Xóa offering là archive, giữ dữ liệu; không xóa cứng.
- Giá hiện là số tiền cố định theo VND; tax/fees và booking price snapshot chưa được xác nhận/triển khai.

## 10. Use Cases

- `MARKET-UC-01`: khách hàng tìm kiếm/xem dịch vụ.
- `MARKET-UC-02`: provider đăng ký/cập nhật profile.
- `MARKET-UC-03`: provider khai báo offering.
- `MARKET-UC-04`: Admin duyệt provider/quản lý danh mục.

## 11. Input / Output

Input offering: `{categoryId, name, description, price, providerPhone}`; provider ownership lấy từ JWT, không nhận provider ID từ client. Output gồm provider profile ID/tên, số điện thoại công khai theo gói, nội dung, giá VND, trạng thái và timestamps. Public browse trả gói `APPROVED` của provider `APPROVED`, giới hạn tối đa 50 mỗi request.

## 12. API endpoints

Các endpoint hiện triển khai: `GET /api/services/categories`, provider listing/detail, `GET /api/services/{categoryId}/offerings`, provider application/profile, `GET`/`POST /api/provider/services`, `PUT`/`DELETE /api/provider/services/{offeringId}`, Admin pending/approval provider/offerings, `POST /api/reviews` và `GET /api/reviews/mine`. Xem [API.md](./API.md) để biết DTO. Public API qua Gateway; Booking-only lookup dùng endpoint nội bộ.

## 13. Request / Response mẫu

Request tạo/sửa offering:

```json
{
  "categoryId": "cleaning",
  "name": "Dọn nhà cơ bản",
  "description": "Dọn căn hộ trong hai giờ.",
  "price": 150000,
  "providerPhone": "+84901234567"
}
```

Response là `ServiceOfferingResponse`, có `id`, `providerProfileId`, `providerName`, `providerPhone`, category, name, description, `price`, `currency` (`VND`), `status`, `createdAt`, `updatedAt`. Provider được suy ra từ tài khoản trong JWT.

## 14. Xác thực và phân quyền

Public browse/detail chỉ trả dữ liệu public. Provider endpoints yêu cầu xác thực và ownership; Admin endpoints yêu cầu quyền moderation/catalog. Kiểm tra tại backend, không dựa vào UI gating.

## 15. Dữ liệu service sở hữu

Provider profile/business profile, service category, offering, price/area (nếu xác nhận), approval state và timestamps. Credential không thuộc service này.

## 16. Database schema dự kiến

Entity đã triển khai: `ProviderProfile`, `ServiceOffering` (provider profile, category, name, description, VND price, public contact phone, status/timestamps), `ProviderReview`. Offering states: `PENDING`, `APPROVED`, `REJECTED`, `ARCHIVED`; archive thay cho hard-delete. Booking chưa lưu offering ID hoặc price snapshot.

## 17. Quan hệ với service khác

Provider gắn với Identity account ID; Booking tham chiếu provider/offering ID. Không dùng foreign key xuyên database và không để Booking ghi Marketplace data.

## 18. Cách giao tiếp

REST/DTO đồng bộ cho provider validation/review eligibility. Public requests đi qua Gateway. Không truy cập Identity/Booking database; không dùng shared entity.

## 19. Events phát ra/nhận vào

Chưa cần broker trong MVP. Nếu workflow approval cần phát event về sau, xác định delivery/retry/idempotency và consumer trước khi triển khai.

## 20. Lỗi và ngoại lệ

Validation error, not found, inactive/unapproved provider, forbidden ownership, duplicate/conflict khi cập nhật, và dependency unavailable. Error format/status code chưa chốt; không trả internal persistence details.

## 21. Acceptance Criteria

- Chỉ dữ liệu public/được duyệt hiển thị cho khách hàng.
- Provider không thể đọc/sửa tài nguyên thuộc provider khác.
- Admin moderation cần quyền; trạng thái approval chỉ chuyển qua hành động được phép.
- Booking/identity không truy cập repository/database Marketplace.

## 22. Test Cases

- Unit: validation, approval transitions, visibility rules.
- API/integration: list/detail; provider create/update own vs another provider; Admin authorize/deny; unapproved visibility.
- Contract: DTO được consumer Booking/Frontend sử dụng nhất quán khi contract được duyệt.

## 23. Vấn đề chưa xác nhận

Provider onboarding/verification; category hierarchy; tax/fees; service area; availability; ảnh upload; moderation audit/history; offering ID và price snapshot trong booking.

## 24. Trạng thái triển khai

Đã triển khai: danh mục cố định, provider application/profile/update với re-approval, offering create/update/archive, public offering list, Admin offering approval, Admin provider approval, public provider list/detail, Marketplace reviews/aggregate rating, API nội bộ phục vụ Booking, persistence và tests. Chưa triển khai: gắn offering/price snapshot vào booking, catalog CRUD, tìm kiếm nâng cao, upload ảnh, audit history và migration scripts.
