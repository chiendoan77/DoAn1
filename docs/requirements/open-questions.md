# Câu hỏi cần xác nhận

Các quyết định đã chốt được ghi rõ; câu hỏi còn mở không được tự suy diễn trong code.

| ID | Câu hỏi | Trạng thái / lý do |
|---|---|---|
| OQ-001 | MVP cần hoàn thành những luồng nào? | Phần đã chốt: auth/role, marketplace/provider approval, booking, Admin user/booking/provider, review hai chiều và Payment service. |
| OQ-002 | Modular monolith hay microservices? | **Đã trả lời:** microservices với Identity, Marketplace, Booking, Payment + Gateway. |
| OQ-003 | Provider là account riêng hay account có thể kiêm customer? | Cần chốt identity/role model. |
| OQ-004 | Khách chọn provider hay hệ thống phân công? | Chỉ slot overlap gây conflict đã chốt; assignment method còn mở. |
| OQ-005 | Slot duration, timezone, inclusive/exclusive interval boundary là gì? | Cần chốt conflict detection và availability. |
| OQ-006 | Giá cố định, quote, phí/thuế, currency và snapshot rule ra sao? | Cần chốt catalog/booking/payment amount. |
| OQ-007 | Payment providers cụ thể và phương thức (card, debit, Visa, QR, ...) nào cần tích hợp? | Payment service riêng và multi-adapter cùng chạy đã chốt; provider/method/callbacks còn mở. |
| OQ-008 | Chính sách chấp nhận/từ chối, đổi/hủy booking và refund ra sao? | Chặn state machine đầy đủ. |
| OQ-009 | Review có cho sửa/xóa hoặc Admin ẩn/kiểm duyệt không? | Hai chiều sau completed, mỗi bên tối đa một review/booking đã chốt; moderation/edit chưa. |
| OQ-010 | Khiếu nại có thuộc MVP, ai xử lý và trạng thái nào cần có? | Repeat booking Admin review có thể dùng complaint context; complaint workflow độc lập chưa chốt. |
| OQ-011 | Upload ảnh có cần trong MVP; hạn mức, định dạng, lưu trữ và quyền truy cập? | Cần trước khi chọn object storage. |
| OQ-012 | Booking notification cần kênh nào? | Chưa cần tách notification service; channel chưa chốt. |
| OQ-013 | Admin được phép quản trị user/booking/payment đến mức nào; cần audit gì? | JWT + ADMIN role cho Portal và server-to-server API key đã chốt; scope/audit/rotation chi tiết còn mở. |
| OQ-014 | Khu vực, tiền tệ và ngôn ngữ có giới hạn không? | Ảnh hưởng địa chỉ/amount/UI. |
| OQ-015 | Target deployment và backup/monitoring/security requirements? | Cần trước khi triển khai thật. |
| OQ-016 | CI nào và runtime Java nào dùng để triển khai? | Framework versions đã chọn; CI/deployment runtime còn mở. |
| OQ-017 | “Booking đang mở” gồm status nào; booking lặp lần hai có bị giữ chờ Admin duyệt hay vẫn tiếp tục? Complaint context nào được Admin xem? | Cần state machine, privacy/access control và Admin queue contract. |
| OQ-018 | Quota hai booking đang mở tính theo category ID nào; rejected/cancelled/completed booking có giải phóng quota không? | Cần định nghĩa countable/open state. |
| OQ-019 | Admin có thể tạo provider profile thay provider hay chỉ phê duyệt hồ sơ do provider đăng ký? | Người dùng xác nhận Admin “thêm/duyệt”; thao tác create-on-behalf cần phân biệt. |
| OQ-020 | Review ownership nằm trong Booking hay service riêng; provider có review khách hàng dựa trên tiêu chí nào? | **Đã chốt:** Marketplace lưu review; Booking xác minh completed/participant. Edit/delete/moderation còn mở. |

## Có thể làm trước các câu hỏi còn mở

Có thể tạo skeleton service, health checks và contract placeholders. Không triển khai booking quota logic, payment gateway thật hoặc review moderation cho đến khi các câu hỏi liên quan được chốt.
