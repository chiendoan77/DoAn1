# Notification requirements and status

## Đã triển khai

- Consume Catalog package lifecycle events asynchronously.
- Consume `BookingCompleted` and create inbox notifications for customer and provider.
- Retry then DLQ for exhausted event failures.
- Persist in-app notifications with event-ID idempotency.
- Authenticated `/api/notifications/mine` read path.

## Chưa triển khai

- Email, push, SMS, templates, delivery provider adapters and delivery status.
- Read/unread state, pagination and delete/archive API.
- Review/payment event subscriptions and outbound email/push/SMS.
- Outbound notification preferences and channel consent.
