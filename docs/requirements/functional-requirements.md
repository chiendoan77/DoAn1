# Yêu cầu chức năng

Trạng thái xác nhận phản ánh quyết định sản phẩm; implementation/test status được theo dõi riêng trong [DEVELOPMENT_STATUS.md](../DEVELOPMENT_STATUS.md). API/acceptance dưới đây mô tả mục tiêu, không mặc nhiên đã hoàn tất.

Đối chiếu source ngày 2026-11-06: provider offering/giá trong MARKET-FR-001/002 chưa triển khai. Booking hiện nhận `providerId`/`categoryId`; việc kết nối booking với offering hoặc lưu price snapshot chưa được xác nhận. Xem [Marketplace API](../services/marketplace/API.md) và [Booking API](../services/booking/API.md).

| ID | Yêu cầu | Actor | Acceptance criteria / kiểm chứng |
|---|---|---|---|
| ID-FR-001 | Đăng ký với email, phone, username, họ tên và mật khẩu bắt buộc. | Khách hàng, provider | Thành công khi hợp lệ; test dữ liệu trùng/sai và role không hợp lệ. |
| ID-FR-002 | Đăng nhập bằng email hoặc số điện thoại và xác định vai trò. | Mọi role | Credential đúng/sai được xử lý; authorization test. |
| ID-FR-003 | Backend kiểm tra role và ownership cho từng API. | Mọi role | User thiếu quyền/không sở hữu tài nguyên bị từ chối. |
| MARKET-FR-001 | Khách hàng duyệt/tìm dịch vụ, xem giá và provider; browse provider hiện giới hạn tối đa 10 theo rating. | Khách hàng | Chỉ dữ liệu public/được duyệt xuất hiện; test giới hạn và thứ tự. |
| MARKET-FR-002 | Provider khai báo hồ sơ và dịch vụ/gói của mình. | Provider | Provider không thể sửa dữ liệu của provider khác. |
| MARKET-FR-003 | Admin tạo/quản lý/duyệt provider và quản lý catalog. | Admin | Backend kiểm tra role; audit chính sách còn chốt. |
| BOOK-FR-001 | Khách hàng tạo booking với provider, ngày/khung giờ, địa chỉ và mô tả. | Khách hàng | Request hợp lệ tạo booking; provider reference sai bị từ chối. |
| BOOK-FR-002 | Provider xử lý booking được phân công; chỉ slot bị overlap mới xung đột. | Provider | Các booking slot không trùng có thể nhận; overlap conflict có test. |
| BOOK-FR-003 | Tối đa hai booking đang mở cùng category cho mỗi khách; lần hai cảnh báo/chuyển Admin review, lần ba bị chặn. | Khách hàng, Admin | Lần hai được đánh dấu review theo complaint context trước đó; lần ba bị từ chối; state/gate policy cần chốt. |
| BOOK-FR-004 | Khách hàng/provider xem booking thuộc quyền; Admin tra cứu/xử lý. | Customer, Provider, Admin | Không lộ booking ngoài quyền. |
| PAY-FR-001 | Payment service khởi tạo và theo dõi thanh toán qua nhiều provider adapters hoạt động đồng thời. | Khách hàng, Admin | Provider adapter lỗi không làm tắt các phương thức khác; callback được xác minh/idempotent. |
| FEEDBACK-FR-001 | Khách hàng và provider đánh giá nhau sau booking hoàn tất; mỗi bên tối đa một đánh giá/booking. | Customer, Provider | Chỉ participant của booking completed được review; test duplicate/unauthorized. |
| SUPPORT-FR-001 | Gửi và theo dõi khiếu nại liên quan booking. | Customer, Admin | Đề xuất; quy trình chưa xác nhận. |
| NOTIFY-FR-001 | Thông báo sự kiện booking quan trọng. | Customer, Provider | Đề xuất; kênh/thời điểm chưa xác nhận. |

## Ghi chú về API/test

Identity, Marketplace và Booking đã có một phần API/tests; chưa có API Payments, migration scripts hoặc full E2E. Xem service API docs để biết contract hiện hành.
