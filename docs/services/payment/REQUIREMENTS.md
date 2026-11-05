# Requirements — Payment service

> Service boundary đã được người dùng chọn; payment provider, methods và lifecycle chưa được chốt. Không xem các API/schema minh họa là production contract.

## 1. Giới thiệu

Payment service điều phối giao dịch và tích hợp hệ thống ngoài qua provider adapters.

## 2. Mục tiêu và phạm vi

Cung cấp API thống nhất cho các phương thức/provider được bật, quản lý trạng thái payment độc lập với booking, hỗ trợ nhiều adapters cùng hoạt động.

## 3. Trách nhiệm nghiệp vụ

- Tạo payment intent/transaction liên kết với booking ID.
- Định tuyến tới adapter phù hợp theo method/provider.
- Xác minh callback, ghi nhận trạng thái và cung cấp kết quả qua DTO.
- Quản lý cấu hình/kích hoạt provider adapters an toàn.

## 4. Chức năng thuộc phạm vi

Khởi tạo/xem trạng thái giao dịch, nhận callback provider, kiểm tra idempotency và mở rộng adapters. Refund, reconciliation, settlement và admin manual adjustment chưa được xác nhận.

## 5. Chức năng ngoài phạm vi

Booking lifecycle/price source of truth, tài khoản/credential, catalog/provider profile và truy cập trực tiếp database service khác.

## 6. Actors

Khách hàng, Admin, Booking service (qua API/DTO), payment providers (qua callbacks).

## 7. Functional Requirements

- `PAY-FR-001` (Đã xác nhận kiến trúc): hỗ trợ nhiều payment provider adapters có thể đồng thời bật.
- `PAY-FR-002` (Đề xuất): tạo transaction cho booking hợp lệ.
- `PAY-FR-003` (Đề xuất): cập nhật trạng thái từ callback đã xác minh, có idempotency.
- `PAY-FR-004` (Đề xuất): lỗi hoặc thêm adapter mới không làm ngừng adapter đang hoạt động.
- `PAY-FR-005` (Đề xuất): Admin xem payment transactions theo quyền.

## 8. Non-functional Requirements

Callback authentication, idempotency, auditability, secret protection, safe timeout/error handling và không log payment secrets. PCI/card data handling phải dựa trên gateway hosted/tokenized checkout; không lưu raw card data. Availability/latency target chưa chốt.

## 9. Business Rules

- Payment state không được xác định từ browser redirect; chỉ trusted provider response/callback hoặc approved manual flow.
- Mỗi provider adapter tách biệt; provider failure không làm các adapters khác ngừng.
- Mọi thao tác phải liên kết booking/amount/currency theo dữ liệu được xác thực.
- Amount source, multiple attempts, refund, partial payment và retry policy còn mở.

## 10. Use Cases

- `PAY-UC-01`: khách hàng bắt đầu thanh toán booking.
- `PAY-UC-02`: provider callback cập nhật transaction.
- `PAY-UC-03`: khách/Admin tra cứu trạng thái theo quyền.
- `PAY-UC-04`: thêm/enable adapter mà không tắt các phương thức đang chạy.

## 11. Input / Output

Input dự kiến: booking ID, method/provider selection, idempotency key, callback payload. Output: payment ID, status, next action/checkout URL nếu provider yêu cầu. Amount/currency phải được xác thực với booking/approved source, không tin client.

## 12. API endpoints dự kiến

- `POST /api/payments`
- `GET /api/payments/{paymentId}`
- `GET /api/bookings/{bookingId}/payments`
- `POST /api/payments/webhooks/{provider}`
- Admin query endpoints theo quyền.

Public routes qua Gateway; callback endpoints cần validation riêng.

## 13. Request / Response mẫu

Ví dụ minh họa; amount/currency source và method/provider chưa chốt:

```json
{
  "bookingId": "<booking-id>",
  "paymentMethod": "<enabled-method>",
  "idempotencyKey": "<unique-request-key>"
}
```

```json
{
  "paymentId": "<payment-id>",
  "status": "<pending-or-approved-state>",
  "nextActionUrl": "<provider-checkout-url-or-null>"
}
```

Không gửi/store PAN/CVV. Raw card details không đi qua ứng dụng trừ khi có compliance design được duyệt; ưu tiên hosted/tokenized provider flow.

## 14. Xác thực và phân quyền

Customer chỉ tạo/xem payment thuộc booking của mình; Admin có quyền tra cứu theo policy. Provider callbacks dùng chữ ký/secret riêng per provider; không dùng JWT customer. Server-to-server API key nếu cần phải scoped, rotated và không được gửi xuống browser.

## 15. Dữ liệu service sở hữu

Payment ID, booking reference, amount/currency snapshot (theo policy), method/provider ID, state, provider transaction reference, attempt history và timestamps. Không lưu raw card data.

## 16. Database schema dự kiến

Entity ứng viên: `PaymentTransaction`, `PaymentAttempt`, `ProviderConfiguration` (secrets ở secret manager/env, không lưu plaintext DB), `WebhookEvent`/idempotency record. Chưa có schema/migration.

## 17. Quan hệ với service khác

Booking cung cấp booking ID/amount policy; Payment trả trạng thái qua API/DTO. Không transaction/foreign key xuyên service. Payment không tự thay đổi Booking database.

## 18. Cách giao tiếp

Synchronous REST/DTO cho create/status; callbacks từ provider vào Payment. Chưa thêm broker; nếu cần event payment status, thiết kế outbox/delivery/retry trước.

## 19. Events phát ra/nhận vào

Chưa xác nhận broker. Có thể cần `PaymentSucceeded`/`PaymentFailed` sau này; chưa phát/consume trong MVP cho đến khi consistency contract được duyệt.

## 20. Lỗi và ngoại lệ

Unsupported/disabled method, provider unavailable/timeout, invalid signature, duplicate callback, amount/reference mismatch, declined/cancelled, unauthorized access và invalid state transition. Trả lỗi nhất quán; không lộ credentials/provider secrets.

## 21. Acceptance Criteria

- Nhiều configured adapters có thể đồng thời hoạt động.
- Thêm/lỗi một adapter không tắt adapters khác.
- Payment state chỉ cập nhật theo trusted response/callback hoặc approved manual process.
- Callback được xác thực và idempotent.
- Client không thể trả payment cho booking không thuộc quyền hoặc sửa amount tùy ý.

## 22. Test Cases

- Unit: provider selection/capability registry, state transitions, idempotency.
- Integration: transaction create/status, callback signature valid/invalid, duplicate callback.
- Contract: adapter conformance tests dùng fake provider.
- Failure: provider timeout/decline không ảnh hưởng adapter khác.

## 23. Vấn đề chưa xác nhận

Gateway/providers (card/QR/debit/Visa are payment methods/rails, not provider integrations; mapping to a provider is required), payment timing, amount source, currency, capture/refund, partial payment, retry/timeout, webhook signature contract, admin adjustment, audit/retention, sandbox credentials.

## 24. Chức năng chưa triển khai

Payment APIs, real provider adapters, DB/schema/migrations, webhooks, business tests, Gateway routing and frontend payment UI are not implemented; an adapter registry skeleton and unit tests exist.
