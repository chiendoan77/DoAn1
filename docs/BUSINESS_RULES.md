# Quy tắc nghiệp vụ

## Đã xác nhận

| ID | Quy tắc |
|---|---|
| BR-001 | Backend kiểm tra role và quyền tài nguyên; UI gating không thay thế authorization. |
| BR-002 | Provider chỉ không khả dụng trong slot bị overlap với booking đã nhận. |
| BR-003 | Mỗi khách tối đa hai booking đang mở trong cùng category. |
| BR-004 | Booking cùng category lần hai cần cảnh báo và chuyển Admin review, sử dụng complaint context từ booking trước; lần ba bị chặn. |
| BR-005 | Customer và provider có thể đánh giá nhau sau khi booking hoàn tất; mỗi bên tối đa một review trên mỗi booking. |
| BR-006 | Payment dùng nhiều provider adapters có thể chạy đồng thời; thêm adapter không tắt toàn bộ Payment module. |
| BR-007 | Admin Portal dùng JWT + role ADMIN; API key chỉ dành cho server-to-server integration, không nhúng vào browser. |
| BR-008 | Admin actions thực thi qua service sở hữu nghiệp vụ/dữ liệu; không có `admin-service` mặc định hoặc DB access xuyên service. |

## Cần xác nhận trước khi implement logic tương ứng

- Định nghĩa status nào được tính là booking đang mở; cách quota được giải phóng sau cancel/reject/complete.
- Lịch interval/timezone/biên giờ và cách xử lý hai request đồng thời.
- Booking lần hai chờ Admin duyệt hay vẫn tiếp tục quy trình; complaint fields/PII nào Admin được xem.
- Review sửa/xóa/moderation.
- Giá, hủy/đổi, refund, provider assignment, payment provider và settlement.

Không hardcode các chính sách còn mở.
