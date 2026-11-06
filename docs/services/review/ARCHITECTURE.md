# Review architecture

Review owns review content and submission workflow. It synchronously calls Booking for eligibility because the decision is required before accepting a review, then commits the review and `ReviewSubmitted` outbox row atomically.

The outbox publisher delivers events to RabbitMQ and marks them published only after broker confirmation. Profile owns the rating projection and handles duplicate review IDs idempotently. Legacy records are moved by internal API; no service reads another service's database.

The current scope does not include moderation, edits/deletion, automated legacy migration, or Payment events/saga.
