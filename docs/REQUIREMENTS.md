# Yêu cầu tổng thể

## Quy ước

Trạng thái **Đã xác nhận** phản ánh quyết định người dùng đã chốt trong hội thoại. Trạng thái **Đề xuất/Cần xác nhận** chưa được xem là phạm vi đã duyệt. Implementation status được ghi riêng và không tự thay đổi trạng thái phê duyệt requirement.

## Yêu cầu chức năng

**Trạng thái triển khai:** Identity đăng ký/đăng nhập, Marketplace provider browse/approval, booking cơ bản và review hai chiều đã có implementation/test. Điều này không đồng nghĩa toàn bộ MVP đã hoàn thành; các phần thiếu được đánh dấu trong [DEVELOPMENT_STATUS.md](./DEVELOPMENT_STATUS.md) và tài liệu từng service.

| ID | Yêu cầu | Trạng thái |
|---|---|---|
| ID-FR-001 | Người dùng đăng ký và đăng nhập bằng email hoặc số điện thoại. Email, phone, username, họ tên và mật khẩu bắt buộc. | Đã xác nhận; auth cơ bản đã triển khai |
| ID-FR-002 | Hệ thống phân biệt quyền khách hàng, nhà cung cấp và Admin. | Đã xác nhận trong MVP |
| MARKET-FR-001 | Khách hàng duyệt/tìm dịch vụ, xem giá và nhà cung cấp. | Đã xác nhận trong MVP |
| MARKET-FR-002 | Nhà cung cấp khai báo hồ sơ và dịch vụ. | Đã xác nhận trong MVP |
| MARKET-FR-003 | Admin quản lý/duyệt/khởi tạo hồ sơ nhà cung cấp và quản lý catalog. | Đã xác nhận; quyền tạo hộ còn chi tiết mở |
| BOOK-FR-001 | Khách hàng tạo booking với thời gian, địa chỉ và mô tả công việc. | Đã xác nhận trong MVP |
| BOOK-FR-002 | Nhà cung cấp xử lý booking; chỉ slot trùng mới làm provider không khả dụng. | Đã xác nhận; biên interval/time zone còn mở |
| BOOK-FR-003 | Mỗi khách tối đa hai booking đang mở cùng danh mục; lần hai cảnh báo/Admin review, lần ba bị chặn. | Đã xác nhận; state “đang mở” và review gate còn mở |
| BOOK-FR-004 | Admin tra cứu và xử lý booking theo quyền. | Đã xác nhận trong MVP |
| PAY-FR-001 | Payment service cung cấp nhiều provider adapters có thể hoạt động đồng thời. | Đã xác nhận kiến trúc; gateway/flow cụ thể còn mở |
| FEEDBACK-FR-001 | Khách hàng và provider đánh giá nhau sau khi booking hoàn tất, tối đa một review mỗi bên/booking. | Đã xác nhận; sửa/xóa/moderation còn mở |
| SUPPORT-FR-001 | Khách hàng gửi khiếu nại và theo dõi xử lý. | Chưa xác nhận MVP |
| NOTIFY-FR-001 | Gửi thông báo khi booking đổi trạng thái quan trọng. | Đề xuất; chưa xác nhận kênh |

Chi tiết: [functional requirements](./requirements/functional-requirements.md), [business requirements](./requirements/business-requirements.md), [open questions](./requirements/open-questions.md).

## Phi chức năng và ràng buộc

- Phù hợp quy mô đồ án cá nhân, dễ chạy local, kiểm thử và bảo trì.
- Frontend chỉ gọi Gateway; không truy cập DB hoặc API service nội bộ.
- Mỗi service sở hữu dữ liệu nghiệp vụ của mình; không truy cập DB service khác.
- Admin Portal dùng JWT + role ADMIN; API key chỉ cho server-to-server, không đưa vào browser.
- Payment hỗ trợ adapter/provider độc lập; nhiều phương thức có thể đồng thời hoạt động.
- Kiểm thử quyền, trạng thái booking, slot overlap và payment callback trước khi đánh dấu hoàn tất.
- Không thêm broker/cache hoặc deployable ngoài bốn service đã chọn nếu chưa có lý do.

## Ranh giới domain đã chọn

1. **Identity & Access** — tài khoản, xác thực, vai trò.
2. **Marketplace** — provider profile, catalog/offerings, giá và xét duyệt đối tác.
3. **Booking** — đặt lịch, state machine, slot conflict và giới hạn repeat booking theo category.
4. **Payment** — giao dịch và adapter với nhiều phương thức/provider.
5. **API Gateway** — edge routing, không phải business domain.
6. **Admin** — role/portal; thao tác thuộc service sở hữu dữ liệu, không tạo `admin-service`.

Các service đã được chọn và một phần business behavior đã triển khai. Yêu cầu chi tiết/chưa rõ được liệt kê trong [open questions](./requirements/open-questions.md).
