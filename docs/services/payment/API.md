# Payment API status

**Hiện chưa có public Payment API contract.** Payment service chỉ có adapter registry skeleton; không có transaction schema, payment lifecycle, webhook, refund hay `PaymentSucceeded` event. Dù Gateway có route prefix `/api/payments/**`, client không được xem route đó là endpoint đã hỗ trợ.

Contract/payment saga được hoãn. Khi được yêu cầu lại, cần chốt request/response, ownership/authorization, idempotency, callback signature verification và lifecycle trước khi public endpoint.

Không gửi thẻ/raw payment credentials hoặc coi redirect phía client là bằng chứng thanh toán.
