# Profile requirements and status

## Đã triển khai

- Provider profile, category associations, application/update, Admin approval.
- Provider suspension và public/internal availability checks.
- Provider eligibility API cho Catalog và Booking provider lookup.
- Customer profile CRUD for display name, phone and default address.
- Rating projection consumes `ReviewSubmitted` idempotently by review ID.
- Import legacy provider profiles qua API thay vì database access chéo service.

## Chưa triển khai

- Automated recovery/monitoring for event processing beyond RabbitMQ retries and DLQ.
- mTLS/service-specific auth token.
- Audit history cho mọi thao tác quản trị Profile (chỉ account status có audit trong Identity).
