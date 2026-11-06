# Roadmap

This roadmap reflects implementation status as of 2026-11-06. It is not approval to implement unresolved policies.

## Completed slices

- Identity RS256 auth, refresh/revoke, account status/audit and API Gateway public-key validation.
- Profile customer/provider ownership, rating projection and legacy import path.
- Catalog provider packages, moderation/history, Flyway and RabbitMQ transactional outbox.
- Booking package/price snapshots, Flyway and BookingCompleted outbox.
- Review service, Booking eligibility check and ReviewSubmitted outbox.
- Notification in-app inbox for package and BookingCompleted events.
- Frontend provider/admin package workflows and notification list.
- Prometheus/Grafana/Jaeger Compose configuration and Flyway migrations for Marketplace/Booking.

## Next, subject to acceptance criteria

1. **Data transition:** rehearse Marketplace/Booking Flyway baseline/migrations against a backup and validate data preservation.
2. **Tests:** finish cross-service Pact contracts and browser E2E for package → booking → completion → review/notification.
3. **Operations:** verify Compose runtime, Prometheus scraping and Jaeger traces; add centralized logs, CI/staging and production secrets.
4. **Booking:** confirm interval/timezone, overlap/quota rules, Admin booking workflow and cancel/reschedule behavior, then implement/test.
5. **Payment (deferred):** only resume when explicitly requested; then define provider transaction/refund/webhook lifecycle, idempotency, signature verification, retries and saga.
6. **Notification:** add outbound channel adapters and preferences only after consent, templates and delivery requirements are approved.

Payment methods, booking policies, production infrastructure, notification channels and complaint scope remain open. See [DEVELOPMENT_STATUS](./DEVELOPMENT_STATUS.md) and [open questions](./requirements/open-questions.md).
