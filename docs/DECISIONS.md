# Quyết định và đề xuất kiến trúc

## DEC-001 — Không tạo Admin service theo mặc định

- **Trạng thái:** Đã xác nhận.
- **Quyết định:** Admin là role/portal; thao tác thuộc domain service sở hữu dữ liệu. Không tạo service CRUD tổng hợp hoặc cho service truy cập database service khác.
- **Lý do:** Giữ ownership nhất quán, giảm deployables và không phân chia chỉ theo role.

## DEC-002 — Bốn business services khởi đầu

- **Trạng thái:** Người dùng đã chọn.
- **Quyết định:** Identity & Access, Marketplace, Booking, Payment; API Gateway là edge component.
- **Lý do:** Giữ domain boundaries rõ; Payment cần hỗ trợ nhiều payment providers.

## DEC-003 — Không thêm broker/cache trước use case

- **Trạng thái:** Đề xuất.
- **Quyết định:** Bắt đầu giao tiếp đồng bộ; chưa thêm Kafka/RabbitMQ/Redis.
- **Lý do:** Chưa có throughput/asynchronous workflow requirement cụ thể.

## DEC-004 — Payment provider adapters

- **Trạng thái:** Người dùng đã chọn ở mức kiến trúc.
- **Quyết định:** Payment service dùng adapter abstraction; có thể bật nhiều provider/method đồng thời. Thêm adapter không yêu cầu tắt toàn bộ module/payment methods đang chạy.
- **Còn mở:** Provider/gateway cụ thể, callback/signature, refund/settlement và rollout.

## DEC-005 — Bảo vệ Admin API

- **Trạng thái:** Người dùng đã chọn.
- **Quyết định:** Admin Portal dùng JWT với role ADMIN; API key chỉ cho server-to-server integration, không đưa vào browser.
- **Còn mở:** Key scope, provisioning, rotation/expiry, audit.

## DEC-006 — Review hai chiều

- **Trạng thái:** Người dùng đã chọn.
- **Quyết định:** Khách hàng và provider đánh giá nhau sau khi booking hoàn tất; mỗi bên tối đa một review cho mỗi booking.
- **Còn mở:** Edit/delete/moderation. Ownership đã được chốt tại DEC-008.

## DEC-007 — Slot overlap và booking lặp

- **Trạng thái:** Core rule đã chọn; semantics một phần còn mở.
- **Quyết định:** Provider chỉ bận ở slot bị trùng. Mỗi khách tối đa hai booking đang mở trong cùng category; lần thứ hai cảnh báo và chuyển Admin review dựa trên complaint context booking trước; lần thứ ba bị chặn.
- **Còn mở:** Định nghĩa open/terminal status, slot boundary/timezone, Admin hold/approval behavior, complaint visibility.

## DEC-008 — Review được lưu ở Marketplace, eligibility thuộc Booking

- **Trạng thái:** Đã triển khai theo lựa chọn của người dùng.
- **Quyết định:** Marketplace lưu review và tổng hợp rating provider. Booking xác minh booking đã `COMPLETED`, reviewer là participant, và trả target ID/role qua endpoint nội bộ được bảo vệ bằng `SERVICE_API_KEY`.
- **Lý do:** Marketplace sở hữu hồ sơ/rating; Booking sở hữu trạng thái và participant của booking. Không chia sẻ database hoặc entity giữa hai service.

## DEC-009 — Provider sửa hồ sơ phải được duyệt lại

- **Trạng thái:** Đã xác nhận.
- **Quyết định:** Provider chỉ cập nhật profile gắn với account của chính mình. Mỗi lần cập nhật đưa profile về `PENDING`; toàn bộ profile bị ẩn khỏi truy vấn public cho tới khi Admin duyệt lại.
- **Lý do:** Không để nội dung mới chưa kiểm duyệt hiển thị và cũng không tiếp tục hiển thị nội dung cũ sau khi provider đã gửi chỉnh sửa.
