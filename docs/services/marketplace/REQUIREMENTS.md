# Requirements — Marketplace

> Trạng thái source hiện tại: provider profile/onboarding/approval, public provider browse và review đã triển khai một phần. Provider offerings/gói dịch vụ và giá là yêu cầu đã được xác nhận, nhưng chưa có model, API, database hay UI trong source hiện tại.

## 1. Giới thiệu

Marketplace quản lý nội dung dịch vụ và thông tin provider để khách hàng khám phá, đồng thời xử lý quy trình duyệt provider.

## 2. Mục tiêu và phạm vi

Marketplace service sở hữu hồ sơ provider, danh mục và public queries. Offerings/giá là phạm vi nghiệp vụ đã chọn; chưa có schema/API implementation.

## 3. Trách nhiệm nghiệp vụ

- Hiện sở hữu `ProviderProfile`, category code cố định và `ProviderReview`.
- Theo yêu cầu đã chọn, Marketplace sẽ sở hữu offering, giá và trạng thái moderation khi feature được triển khai.
- Quản lý trạng thái xét duyệt provider.
- Cung cấp dữ liệu public cho tìm kiếm/chi tiết dịch vụ.
- Admin thực hiện moderation trên dữ liệu Marketplace qua API domain này.

## 4. Chức năng thuộc phạm vi

Đã triển khai duyệt provider, xem/gửi/cập nhật profile và public provider browse. Mỗi cập nhật profile chỉ áp dụng hồ sơ gắn với account trong JWT và đưa về `PENDING`. Chưa triển khai provider-managed offerings, giá, archive offering, public offering browse hoặc Admin approval cho offering.

## 5. Chức năng ngoài phạm vi

Credential/role source of truth, booking lifecycle, payment transaction, complaint workflow độc lập, hoặc DB của service khác. Marketplace lưu review và rating; Booking vẫn là owner của booking state/participants.

## 6. Actors

Khách hàng (đọc public), nhà cung cấp (quản lý dữ liệu của mình), Admin (moderation/catalog).

## 7. Functional Requirements

- `MARKET-FR-001` (Đã xác nhận): khách hàng duyệt/tìm dịch vụ, xem giá và provider. Hiện mới có categories và provider list/detail, chưa có offering/giá hoặc tìm kiếm nâng cao.
- `MARKET-FR-002` (Đã xác nhận): provider khai báo hồ sơ và dịch vụ/gói thuộc quyền sở hữu. Profile có; offering chưa có.
- `MARKET-FR-003` (Đã xác nhận): Admin quản lý/duyệt provider và catalog theo quyền. Provider approval có; catalog CRUD chưa có.

## 8. Non-functional Requirements

API public chỉ trả dữ liệu được phép hiển thị; dữ liệu đầu vào được validate; thao tác provider/Admin phải kiểm tra role và ownership; kết quả tìm kiếm cần pagination nếu scope yêu cầu. Chưa có ngưỡng latency/tải.

## 9. Business Rules

- Các rule offering dưới đây là quyết định nghiệp vụ đã chọn, nhưng chưa có API/model thực thi.
- Provider chỉ được thay đổi profile/offerings thuộc quyền sở hữu.
- Create/update offering và thay đổi giá phải chờ Admin duyệt lại.
- Giá gói là số tiền cố định theo VND; provider cung cấp số điện thoại theo gói, hiển thị công khai sau khi duyệt.
- Xóa offering là archive để giữ dữ liệu, không hard-delete.
- Chưa có quyết định xác nhận rằng booking phải bắt đầu từ một offering hay lưu price snapshot; không tích hợp hai domain trước khi chốt scope.

## 10. Use Cases

- `MARKET-UC-01`: khách hàng tìm kiếm/xem dịch vụ.
- `MARKET-UC-02`: provider đăng ký/cập nhật profile.
- `MARKET-UC-03`: provider khai báo offering.
- `MARKET-UC-04`: Admin duyệt provider/quản lý danh mục.

## 11. Input / Output

Payload dự kiến đã trao đổi cho offering: `{categoryId, name, description, price, providerPhone}`; provider ownership phải lấy từ JWT, không nhận provider ID từ client. Đây là yêu cầu thiết kế, chưa phải API contract đang chạy.

## 12. API endpoints

Các endpoint hiện triển khai: `GET /api/services/categories`, provider listing/detail, provider application/profile, Admin provider pending/approval, `POST /api/reviews`, `GET /api/reviews/mine` và API nội bộ phục vụ Booking. Chưa có endpoint offering; xem [API.md](./API.md) để biết contract hiện hành.

## 13. Payload offering dự kiến (chưa phải API contract)

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

Request/response DTO và endpoint chưa được triển khai. Khi thiết kế API, cần bảo đảm provider được suy ra từ JWT và mọi create/update đều chờ Admin duyệt theo quyết định nghiệp vụ.

## 14. Xác thực và phân quyền

Public browse/detail chỉ trả dữ liệu public. Provider endpoints yêu cầu xác thực và ownership; Admin endpoints yêu cầu quyền moderation/catalog. Kiểm tra tại backend, không dựa vào UI gating.

## 15. Dữ liệu service sở hữu

Hiện tại: provider profile, category code, review và rating. Theo phạm vi đã chọn: offering, giá, trạng thái approval và timestamps thuộc Marketplace khi được triển khai. Credential không thuộc service này.

## 16. Database schema dự kiến

Entity hiện có: `ProviderProfile`, `ProviderReview`. Chưa có `ServiceOffering` entity/table. Các trạng thái offering và fields cụ thể cần được triển khai theo quyết định nghiệp vụ, không được xem là schema đang chạy. Chưa có quyết định về việc Booking tham chiếu offering hoặc lưu price snapshot.

## 17. Quan hệ với service khác

Provider gắn với Identity account ID; Booking hiện tham chiếu provider/category. Có cần cho Booking tham chiếu offering hay không vẫn cần xác nhận. Không dùng foreign key xuyên database và không để Booking ghi Marketplace data.

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

Đã triển khai: danh mục cố định, provider application/profile/update với re-approval, Admin provider approval, public provider list/detail, Marketplace reviews/aggregate rating, API nội bộ phục vụ Booking, persistence và tests. Chưa triển khai: offering create/update/archive, offering public list/Admin approval, catalog CRUD, tìm kiếm nâng cao, upload ảnh, audit history và migration scripts. Việc Booking tham chiếu offering/price snapshot chưa được xác nhận.
