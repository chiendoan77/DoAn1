# Profile architecture

Profile owns customer/provider profiles, provider approval/suspension and the provider-rating projection. Public lookup returns only approved, non-suspended providers; internal eligibility also checks Identity account status.

## Integrations

- Identity account state: synchronous REST.
- Booking provider/category lookup: synchronous REST with dedicated API key.
- Catalog provider eligibility: synchronous REST with dedicated API key.
- Review publishes `ReviewSubmitted` to RabbitMQ; Profile consumes it and updates the rating projection idempotently by review ID.
- Legacy provider profile import calls Marketplace's authenticated migration API; no cross-database reads.

Profile schema is service-owned and managed by Flyway. No database transaction spans Profile, Identity, Catalog, Marketplace or Review. Public provider DTOs do not expose identity account IDs.
