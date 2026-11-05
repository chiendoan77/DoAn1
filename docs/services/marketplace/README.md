# Marketplace

Marketplace hiện sở hữu hồ sơ nhà cung cấp, trạng thái duyệt và đánh giá; Admin duyệt hồ sơ bằng API của domain này, không cần service riêng. Quản lý gói dịch vụ/provider offerings là yêu cầu đã được nêu nhưng chưa có model/API trong source hiện tại.

Đã triển khai danh mục cố định `cleaning`/`repair`, provider application/profile/update, Admin approve/reject hồ sơ, public provider list/detail và review sau khi Booking xác nhận booking hoàn tất. Provider mới bắt đầu 0 sao; rating/count được tổng hợp từ review khách hàng. Gói dịch vụ, giá, archive và Admin offering approval chưa được triển khai.

Booking tra cứu provider qua `/internal/providers/{providerId}` với `X-Service-Api-Key`; endpoint nội bộ không được route qua Gateway. API key phải cấu hình giống nhau giữa Marketplace và Booking.

Provider profile và review có API. Chưa có API hoặc bảng `service_offerings`. Ảnh dùng URL HTTPS, chưa có upload ảnh, tìm kiếm nâng cao hoặc audit history. Schema dùng Hibernate `ddl-auto: update`, chưa có migration.

## Java package layout

`src/main/java/com/doan1/marketplace` separates controllers, request/response DTOs, `entity`, `enums`, `mapper`, `service`, repositories, internal clients and security/configuration. Tests are isolated in `src/test/java/com/doan1/marketplace`.
