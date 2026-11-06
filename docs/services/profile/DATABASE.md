# Profile database

Profile owns schema `doan1_profile`, managed by Flyway; Hibernate uses `ddl-auto: validate`.

| Table | Data |
|---|---|
| `provider_profiles` | Identity account reference, display/profile fields, approval status, suspension, rating, review count, demo flag and version. |
| `provider_profile_categories` | Category codes associated with a provider profile. |
| `provider_rating_reviews` | Unique review ID, provider ID, rating and event time; supports idempotent rating projection. |
| `customer_profiles` | Account reference, display name, phone, default address, optional HTTPS avatar URL and update time. |

Account/provider/review IDs are references only; there are no foreign keys into other service schemas. Review content/comment remains in Review service and is not copied into Profile.

Migration V4 adds the optional customer avatar URL without changing existing profile data. Legacy provider data is imported through Marketplace's internal API when Profile has no profiles. Rating projection is updated from RabbitMQ `ReviewSubmitted` events. Profile never queries another service's database.
