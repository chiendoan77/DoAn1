# Yêu cầu nghiệp vụ và phạm vi

## Mục tiêu nghiệp vụ

**Đề xuất:** giúp khách hàng tìm nhà cung cấp dịch vụ dọn dẹp/sửa chữa và tạo lịch; giúp nhà cung cấp nhận và quản lý yêu cầu. Nội dung được lấy từ mô tả khởi đầu, chưa được xác nhận thành MVP.

## Năng lực/domain ứng viên

| Domain | Trách nhiệm đề xuất | Không đồng nghĩa với |
|---|---|---|
| Identity & Access | Tài khoản, thông tin đăng nhập, vai trò và quyền. | Một portal cho từng vai trò. |
| Marketplace | Hồ sơ/đăng ký nhà cung cấp, dịch vụ, danh mục, giá, khu vực và duyệt đối tác. | `admin-service`; Admin thực hiện quyền quản trị trên dữ liệu marketplace. |
| Booking | Đặt lịch, phân công nhà cung cấp, vòng đời công việc và lịch sử trạng thái. | Quyền sở hữu hồ sơ hoặc danh mục. |
| Payment | Payment transaction và tích hợp provider qua adapter, hỗ trợ nhiều method/provider cùng lúc. | Không để client tự xác nhận paid state; không phải mọi loại thẻ/QR đều là provider. |
| Feedback/Support | Review hai chiều đã nằm trong scope; complaint workflow độc lập chưa chốt. | Mặc định phải có service deploy riêng. |

## Phạm vi tối thiểu được đề xuất

Xác thực, xem dịch vụ/provider, booking, Admin theo domain, review hai chiều và Payment service đã được người dùng chọn. Gateway/provider cụ thể, complaint workflow đầy đủ, thông báo bất đồng bộ và báo cáo nâng cao chưa chốt.

## Quy tắc

- Không booking một dịch vụ/nhà cung cấp không khả dụng hoặc không được phép nhận booking (chính sách cụ thể cần chốt).
- Mọi thay đổi trạng thái booking phải tuân theo state transition được backend kiểm soát.
- Từng service/domain chỉ ghi vào dữ liệu mình sở hữu.
- Chức năng Admin không bỏ qua quy tắc domain; backend phải xác thực quyền và thao tác.

Các quy tắc thời gian, tiền, hủy, hoàn tiền, khu vực, khiếu nại và đánh giá còn mở; xem [open-questions.md](./open-questions.md).
