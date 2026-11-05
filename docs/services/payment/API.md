# Payment API (dự kiến)

Contract chưa được duyệt; mọi endpoint cần DTO validation, authorization và idempotency policy.

| Method | Path | Actor | Mục tiêu |
|---|---|---|---|
| POST | `/api/payments` | Customer | Tạo transaction cho booking |
| GET | `/api/payments/{paymentId}` | Customer/Admin | Tra cứu trạng thái theo quyền |
| POST | `/api/payments/webhooks/{provider}` | Provider | Callback đã xác minh chữ ký |

Không tin payment success từ client redirect. Không nhận raw card fields. Provider-specific payloads được chuẩn hóa trong adapter; public API không lộ internal provider secrets.
