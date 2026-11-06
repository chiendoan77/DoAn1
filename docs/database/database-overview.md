# Database overview

Local Docker Compose configures MySQL 8.0 with service-owned schemas:

- `doan1_identity` — Identity.
- `doan1_profile` — Profile.
- `doan1_catalog` — Catalog.
- `doan1_marketplace` — Marketplace.
- `doan1_booking` — Booking.
- `doan1_review` — Review.
- `doan1_notification` — Notification.

Payment has no business schema yet. Identity, Profile, Catalog, Marketplace, Booking, Review and Notification use Flyway with Hibernate `ddl-auto: validate`. Marketplace/Booking have legacy-schema baselines; rehearse migrations against a backup before rollout. Backend tests use H2.

No schema is shared and no foreign key spans service databases. MySQL is not published to the host; services connect over the Docker network. See [data ownership](./data-ownership.md), [entity relationship](./entity-relationship.md) and [database design](../DATABASE_DESIGN.md).
