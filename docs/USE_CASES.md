# Use cases

Các use case đang ở trạng thái đề xuất, phụ thuộc xác nhận MVP.

| ID | Use case | Actor chính | Kết quả |
|---|---|---|---|
| UC-01 | Đăng ký/đăng nhập | Khách hàng, nhà cung cấp | Tài khoản/phiên được xác thực. |
| UC-02 | Duyệt và xem dịch vụ | Khách hàng | Thông tin dịch vụ/nhà cung cấp được hiển thị. |
| UC-03 | Đăng ký cung cấp dịch vụ | Nhà cung cấp | Hồ sơ chờ hoặc đạt trạng thái được duyệt (quy trình cần chốt). |
| UC-04 | Duyệt hồ sơ nhà cung cấp | Admin | Hồ sơ được cập nhật trong Marketplace domain. |
| UC-05 | Tạo booking | Khách hàng | Booking được ghi nhận; category quota/slot overlap được kiểm tra. |
| UC-06 | Xử lý booking | Nhà cung cấp | Booking được chấp nhận/từ chối/cập nhật theo state machine. |
| UC-07 | Xử lý booking lặp | Khách hàng, Admin | Lần hai cùng category cảnh báo/Admin review; lần ba đang mở bị từ chối. |
| UC-08 | Hủy/đổi lịch | Khách hàng, nhà cung cấp, Admin | Booking chuyển trạng thái theo chính sách đã duyệt. |
| UC-09 | Thanh toán | Khách hàng | Payment service định tuyến qua adapter/provider đang bật. |
| UC-10 | Đánh giá hai chiều | Khách hàng, nhà cung cấp | Mỗi bên review một lần sau khi hoàn tất booking. |
| UC-11 | Quản lý nền tảng | Admin | Các thao tác được thực hiện bởi domain sở hữu, không qua service CRUD tổng hợp. |

Các luồng lỗi, ngoại lệ và acceptance criteria cần được cụ thể hóa sau khi trả lời các câu hỏi mở.
