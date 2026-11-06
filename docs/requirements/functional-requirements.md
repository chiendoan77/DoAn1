# Yêu cầu chức năng

Trạng thái xác nhận phản ánh quyết định sản phẩm; implementation/test status được theo dõi riêng trong [DEVELOPMENT_STATUS.md](../DEVELOPMENT_STATUS.md). API/acceptance dưới đây mô tả mục tiêu, không mặc nhiên đã hoàn tất.

Đối chiếu source ngày 2026-11-06: Booking nhận `packageId`, kiểm tra package đã duyệt và lưu snapshot giá/tên/thời lượng; Payment lifecycle/saga chưa làm theo yêu cầu. Xem [Catalog API](../services/catalog/API.md), [Booking API](../services/booking/API.md), [Review API](../services/review/API.md).

| ID | Yêu cầu | Actor | Acceptance criteria / kiểm chứng |
|---|---|---|---|
| ID-FR-001 | Đăng ký với email, phone, username, họ tên và mật khẩu bắt buộc. | Khách hàng, provider | Thành công khi hợp lệ; test dữ liệu trùng/sai và role không hợp lệ. |
| ID-FR-002 | Đăng nhập bằng email hoặc số điện thoại và xác định vai trò. | Mọi role | Credential đúng/sai được xử lý; authorization test. |
| ID-FR-003 | Backend kiểm tra role và ownership cho từng API. | Mọi role | User thiếu quyền/không sở hữu tài nguyên bị từ chối. |
| ID-FR-004 | Access token dùng RS256; refresh token có rotation và revoke. | Mọi role | Public-key verifiers xác minh token; refresh replay revoke family; access token hiện hữu còn hiệu lực đến TTL. |
| PROFILE-FR-001 | Khách hàng quản lý tên hiển thị, điện thoại và địa chỉ mặc định. | Khách hàng | Profile service sở hữu dữ liệu và kiểm tra account hiện tại. |
| MARKET-FR-001 | Khách hàng duyệt/tìm dịch vụ, xem giá và provider. | Khách hàng | Provider/package browse chỉ trả dữ liệu được duyệt; test giới hạn và thứ tự. |
| MARKET-FR-002 | Provider khai báo hồ sơ và dịch vụ/gói của mình. | Provider | Profile/Catalog backend xác thực role, ownership và approval state. |
| MARKET-FR-003 | Admin quản lý/duyệt provider và catalog. | Admin | Backend kiểm tra role; package moderation history có; provider audit chưa hoàn thiện. |
| BOOK-FR-001 | Khách hàng tạo booking từ package đã duyệt với ngày/giờ, địa chỉ và mô tả. | Khách hàng | Catalog xác nhận package/provider; Booking lưu snapshot tên/giá/thời lượng. |
| BOOK-FR-002 | Provider xử lý booking được phân công; chỉ slot bị overlap mới xung đột. | Provider | Các booking slot không trùng có thể nhận; overlap conflict có test. |
| BOOK-FR-003 | Tối đa hai booking đang mở cùng category cho mỗi khách; lần hai cảnh báo/chuyển Admin review, lần ba bị chặn. | Khách hàng, Admin | Lần hai được đánh dấu review theo complaint context trước đó; lần ba bị từ chối; state/gate policy cần chốt. |
| BOOK-FR-004 | Khách hàng/provider xem booking thuộc quyền; Admin tra cứu/xử lý. | Customer, Provider, Admin | Không lộ booking ngoài quyền. |
| PAY-FR-001 | Payment service khởi tạo và theo dõi thanh toán qua nhiều provider adapters hoạt động đồng thời. | Khách hàng, Admin | Kiến trúc adapter được chốt; lifecycle, callback, refunds, `PaymentSucceeded` và saga hoãn. |
| FEEDBACK-FR-001 | Khách hàng và provider đánh giá nhau sau booking hoàn tất; mỗi bên tối đa một đánh giá/booking. | Customer, Provider | Review service hỏi Booking về eligibility; review và `ReviewSubmitted` outbox commit cùng transaction. |
| SUPPORT-FR-001 | Gửi và theo dõi khiếu nại liên quan booking. | Customer, Admin | Đề xuất; quy trình chưa xác nhận. |
| NOTIFY-FR-001 | Thông báo sự kiện booking quan trọng. | Customer, Provider | `BookingCompleted` và package lifecycle tạo in-app inbox qua RabbitMQ. Email/push/SMS chưa có. |

## Ghi chú về API/test

Identity, Marketplace và Booking đã có một phần API/tests; chưa có API Payments, migration scripts hoặc full E2E. Xem service API docs để biết contract hiện hành.
