# Thiết kế hệ thống

## Luồng thành phần

```text
Browser → Next.js BFF → API Gateway
                           ├─ Identity
                           ├─ Profile ←→ Identity (REST)
                           ├─ Catalog → Profile (REST)
                           ├─ Booking → Catalog/Profile (REST)
                           │      └─ BookingCompleted → RabbitMQ → Notification
                           ├─ Review → Booking eligibility (REST)
                           │      └─ ReviewSubmitted → RabbitMQ → Profile rating projection
                           ├─ Marketplace (legacy migration only)
                           └─ Payment (adapter skeleton; no transaction API)

Identity/Profile/Catalog/Marketplace/Booking/Review/Notification
    └─ independent MySQL schemas; no cross-service foreign keys
```

## Quy tắc liên service

- REST/DTO is used when the caller needs an immediate domain decision; no shared repositories/entities or cross-service database access.
- Booking validates package and provider using Catalog/Profile, then stores immutable snapshots.
- Review calls Booking to verify completed-booking eligibility before saving a review.
- Catalog/Booking/Review persist events to transactional outboxes and publish through RabbitMQ. Profile and Notification consumers must handle duplicate delivery idempotently.
- No distributed database transaction. Payment lifecycle, `PaymentSucceeded`, and payment saga are deferred.
- API Gateway is the client entry point and validates RS256 JWT signature, issuer, and audience; domain services enforce role/ownership.

## Trạng thái

Implemented flows and tests are summarized in [DEVELOPMENT_STATUS.md](./DEVELOPMENT_STATUS.md). API client connection is documented in [API_INTEGRATION_GUIDE.md](./API_INTEGRATION_GUIDE.md). Full Compose runtime, MySQL migration rehearsal and production deployment have not been verified.
