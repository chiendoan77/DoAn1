# Payment architecture

```text
Booking / Frontend
        |
   API Gateway
        |
 Payment API
   |      |       |
Adapter A Adapter B Adapter C
   |      |       |
External providers (independent configuration/callback validation)
```

Payment service sở hữu transaction state. Adapter contract chuẩn hóa create/query/refund capability (refund chỉ khi được duyệt). Provider registry/capability routing cho phép nhiều adapter hoạt động cùng lúc; không global kill-switch khi thêm một adapter. Credential được inject qua secret configuration, không commit hoặc trả về client.

REST/DTO cho synchronous flow; callback signature validation và idempotency ở Payment. Chưa dùng broker cho tới khi payment-to-booking consistency contract được chốt.
