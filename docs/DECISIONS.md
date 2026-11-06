# Quyết định và đề xuất kiến trúc

## DEC-001 — Không tạo Admin service theo mặc định

- **Trạng thái:** Đã xác nhận.
- **Quyết định:** Admin là role/portal; thao tác thuộc domain service sở hữu dữ liệu. Không tạo service CRUD tổng hợp hoặc cho service truy cập database service khác.
- **Lý do:** Giữ ownership nhất quán, giảm deployables và không phân chia chỉ theo role.

## DEC-002 — Bốn business services ban đầu

- **Trạng thái:** Baseline ban đầu; được mở rộng bởi DEC-011 và DEC-012.
- **Quyết định:** Identity & Access, Marketplace, Booking, Payment; API Gateway là edge component.
- **Lý do:** Giữ domain boundaries rõ; Payment cần hỗ trợ nhiều payment providers. Profile, Catalog và Notification được thêm sau theo các quyết định mới.

## DEC-003 — Không thêm broker/cache trước use case

- **Trạng thái:** Superseded một phần bởi DEC-012.
- **Quyết định:** Bắt đầu giao tiếp đồng bộ; chưa thêm Kafka/RabbitMQ/Redis.
- **Lý do ban đầu:** Chưa có throughput/asynchronous workflow requirement cụ thể. RabbitMQ hiện chỉ được thêm cho package notification/outbox; không có cache.

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

## DEC-008 — Review là service riêng, eligibility thuộc Booking

- **Trạng thái:** Đã triển khai ở mức API, database riêng và event outbox.
- **Quyết định:** Review service lưu review; Booking xác minh booking đã `COMPLETED`, reviewer là participant và trả target ID/role qua API nội bộ. Review ghi `ReviewSubmitted` vào outbox; Profile cập nhật rating projection idempotent khi nhận event.
- **Lý do:** Review có vòng đời và lưu lượng thay đổi riêng; Profile sở hữu rating projection, Booking sở hữu trạng thái/participant. Không chia sẻ database hoặc entity.

## DEC-009 — Provider sửa hồ sơ phải được duyệt lại

- **Trạng thái:** Đã xác nhận.
- **Quyết định:** Provider chỉ cập nhật profile gắn với account của chính mình. Mỗi lần cập nhật đưa profile về `PENDING`; toàn bộ profile bị ẩn khỏi truy vấn public cho tới khi Admin duyệt lại.
- **Lý do:** Không để nội dung mới chưa kiểm duyệt hiển thị và cũng không tiếp tục hiển thị nội dung cũ sau khi provider đã gửi chỉnh sửa.

## DEC-010 — Provider packages cần Admin duyệt

- **Trạng thái:** Đã xác nhận và triển khai một phần.
- **Quyết định:** Provider tạo/sửa/archive gói dịch vụ; giá là VND nguyên số, phone theo gói được hiển thị sau duyệt; sửa gói APPROVED đưa về `PENDING_APPROVAL`; xóa là archive. Admin moderation thuộc Catalog.
- **Ranh giới:** Catalog sở hữu package, category, giá và moderation; Profile sở hữu provider profile. Booking liên kết package và lưu price snapshot theo DEC-015.
- **Implementation note:** Workflow, API và migration có trong `catalog-service`; xem [Catalog service docs](./services/catalog/README.md) và [development status](./DEVELOPMENT_STATUS.md).

## DEC-011 — Profile sở hữu customer/provider profile và rating projection

- **Trạng thái:** Đã xác nhận và triển khai.
- **Quyết định:** Profile sở hữu hồ sơ khách hàng, hồ sơ provider, trạng thái duyệt/suspension và rating projection. Dữ liệu legacy được import qua API nội bộ, không truy cập DB xuyên service. `ReviewSubmitted` được chiếu idempotent theo review ID.

## DEC-012 — Domain events qua transactional outbox

- **Trạng thái:** Đã triển khai cho Catalog, Booking và Review; Payment hoãn.
- **Quyết định:** Service lưu event trong cùng transaction với thay đổi nghiệp vụ; publisher gửi durable message tới RabbitMQ và đánh dấu published sau publisher confirm. Notification/Profile xử lý event idempotent.
- **Phạm vi:** Catalog phát package lifecycle events; Booking phát `BookingCompleted`; Review phát `ReviewSubmitted`. Payment lifecycle/event và Payment saga chưa triển khai theo phạm vi đã chốt.

## DEC-013 — Flyway migration có baseline

- **Trạng thái:** Đã triển khai cho Identity, Profile, Catalog, Marketplace, Booking, Review và Notification.
- **Quyết định:** Mỗi service sở hữu migration và schema riêng, Hibernate `validate`. Marketplace/Booking dùng baseline cho database đã có dữ liệu và migration versioned cho thay đổi tiếp theo.
- **Giới hạn kiểm chứng:** Chưa chạy nâng cấp thực tế trên một bản sao MySQL production; cần backup và dry-run trước rollout.

## DEC-014 — RS256 và refresh-token rotation

- **Trạng thái:** Đã triển khai.
- **Quyết định:** Identity ký JWT bằng RS256 private key; Gateway/services chỉ cần public key. Token có issuer/audience; refresh token opaque được lưu dưới dạng SHA-256 hash, rotate khi dùng và revoke theo family khi phát hiện replay.
- **Giới hạn:** Access JWT stateless còn hiệu lực đến TTL sau logout; không có denylist/thu hồi tức thời access token.

## DEC-015 — Booking gắn với Catalog package snapshot; Payment hoãn

- **Trạng thái:** Booking/package đã triển khai; Payment lifecycle/saga hoãn theo yêu cầu.
- **Quyết định:** Booking nhận `packageId`, kiểm tra gói APPROVED và provider qua Catalog/Profile, rồi lưu snapshot tên, giá và thời lượng. Booking completion và review submission dùng outbox; không có giao dịch database xuyên service.
- **Ngoài phạm vi hiện tại:** Payment lifecycle/webhooks/refunds/idempotency, `PaymentSucceeded` và saga đặt lịch-thanh toán-xác nhận.
