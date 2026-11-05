# Vai trò người dùng

Các vai trò là đề xuất từ mô tả ban đầu.

| Vai trò | Mục tiêu | Năng lực dự kiến |
|---|---|---|
| Khách hàng | Tìm và đặt dịch vụ. | Duyệt marketplace; quản lý booking của mình; đánh giá provider sau hoàn tất; thanh toán theo phương thức được tích hợp. |
| Nhà cung cấp | Cung cấp dịch vụ và xử lý công việc. | Quản lý hồ sơ/dịch vụ của mình; nhận/từ chối/cập nhật booking; đánh giá khách hàng sau hoàn tất. |
| Quản trị viên | Vận hành và kiểm soát nền tảng. | Quản lý user/provider/catalog/booking/payment theo quyền; xem xét booking lặp dựa trên complaint context. |

## Nguyên tắc phân quyền

- Role không phải service boundary.
- Mỗi API kiểm tra cả role và quyền sở hữu/quan hệ với tài nguyên.
- Ẩn nút trên frontend không thay thế authorization phía backend.
- Tách vai trò `ADMIN` thành service riêng không được suy ra từ việc có Admin Portal.
- Chi tiết role đơn/đa vai trò, phân cấp admin và audit còn cần xác nhận.
- Admin Portal xác thực bằng JWT + role `ADMIN`; API key chỉ dành cho server-to-server integration.
