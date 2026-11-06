# Notification architecture

Notification listens to durable queue `notifications.catalog`, bound to `doan1.events` with `catalog.package.#`. Package consumer retries failures three times, then routes exhausted messages to `doan1.events.dlx` / `notifications.catalog.dlq`.

Consumer stores event ID uniquely in the service database; duplicate deliveries do not create duplicate inbox rows. Current event set: `PackageSubmitted`, `PackageResubmitted`, `PackageApproved`, `PackageRejected`, `PackageDeactivated`.

Notification is asynchronous from Catalog's perspective. No synchronous notification API is called during package moderation. Email/push/SMS, `BookingCompleted`, `ReviewSubmitted`, `PaymentSucceeded`, and notification read state are not implemented.
