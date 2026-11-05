# Payment database (khái niệm)

Payment service sở hữu database/schema riêng.

Entity ứng viên:

- `payment_transaction`: internal payment ID, booking reference, amount/currency, status, chosen method/provider, timestamps.
- `payment_attempt`: transaction ID, provider reference, attempt status, safe failure code, timestamps.
- `webhook_event` hoặc idempotency record: provider event ID/hash, processing result, deduplication timestamps.

Không lưu PAN/CVV hoặc plaintext provider secrets. Amount/currency source, transaction state machine, retention, indexes và migration cần được chốt theo gateway/requirements. Chưa có schema/migration triển khai.
