# Catalog architecture

Catalog owns category/package data and moderation invariants. ProviderPackageService asks Profile synchronously for approved/active provider eligibility when creating/submitting or editing an approved package. Public browse only returns APPROVED package data.

Package mutations record history and outbox event transactionally. A scheduled publisher sends durable JSON messages to the durable topic exchange `doan1.events`; correlated publisher confirms and mandatory returns prevent falsely marking an undelivered message as published. Consumer deduplicates by event ID. This is at-least-once delivery; consumer idempotency is required.

Notification currently consumes package lifecycle events only. Booking/payment saga, price snapshot, `BookingCompleted`, and `PaymentSucceeded` are not part of this implementation.
