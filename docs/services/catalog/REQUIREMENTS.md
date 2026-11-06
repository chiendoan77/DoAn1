# Catalog requirements and status

## Đã triển khai

- Danh mục `cleaning`/`repair`, provider package CRUD/archive, VND integer prices and phone validation.
- Provider ownership and Profile eligibility checks.
- Admin approval, rejection reason, deactivation, audit history and optimistic version checks.
- Public browse/filtering of approved packages.
- Flyway schema, transactional outbox, RabbitMQ publisher confirms and Notification event contract.

## Chưa triển khai

- Booking-to-package relationship and price snapshot.
- Package image upload or external image lifecycle.
- Catalog category Admin CRUD; categories are seeded fixed codes.
- Booking/Review/Payment events, distributed saga, contract testing/Pact.
- Additional price/fee/tax/cancellation business rules.
