# Marketplace

Marketplace sở hữu hồ sơ nhà cung cấp, trạng thái duyệt và đánh giá; Admin duyệt hồ sơ bằng API của domain này, không cần service riêng.

Đã triển khai danh mục cố định `cleaning`/`repair`, provider application/profile/update, quản lý các service offerings có giá VND, Admin approve/reject, danh sách provider/gói công khai đã duyệt và review sau khi Booking xác nhận booking hoàn tất. Mỗi lần provider sửa hồ sơ hoặc gói, trạng thái trở về `PENDING`; phiên bản đã sửa không xuất hiện công khai cho tới khi Admin duyệt lại. Xóa gói là archive để giữ dữ liệu; số điện thoại liên hệ do provider khai báo cho gói và công khai sau khi duyệt. Provider mới bắt đầu 0 sao; rating/count được tổng hợp từ review khách hàng.

Booking tra cứu provider qua `/internal/providers/{providerId}` với `X-Service-Api-Key`; endpoint nội bộ không được route qua Gateway. API key phải cấu hình giống nhau giữa Marketplace và Booking.

Offering CRUD và approval đã có API; gói chưa được nối vào booking lifecycle. Ảnh dùng URL HTTPS, chưa có upload ảnh, tìm kiếm nâng cao hoặc audit history. Schema dùng Hibernate `ddl-auto: update`, chưa có migration.

## Java package layout

`src/main/java/com/doan1/marketplace` separates `controller`, `dto`, `model`, `repository`, `service`, `client`, `security`, `config` and `exception`; the Spring Boot entry point stays in the root package. Tests are isolated in `src/test/java/com/doan1/marketplace`.
