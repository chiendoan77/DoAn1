# Notification Service

Notification consumes Catalog package lifecycle and `BookingCompleted` events, stores an idempotent in-app inbox, and exposes the current user's messages. It runs on port 8087 and owns schema `doan1_notification`. It does not own package, profile, review, booking, or payment data.

The RabbitMQ consumers use outbox-backed events and idempotent event IDs. Current notifications are in-app only; email, push, SMS and notification templates are not implemented. Flyway manages the schema and Hibernate uses `ddl-auto: validate`.
