# Marketplace architecture

Marketplace is retained as a legacy data and migration source. Provider/profile/category/package and new review workflows are owned by Profile, Catalog and Review respectively. API Gateway routes public requests to those owners, not to legacy Marketplace controllers.

Marketplace keeps legacy provider/review tables and authenticated export operations so destination services can import data through internal APIs. It does not own new review writes or rating projection. Migrations do not query another service's database.

Flyway manages the legacy Marketplace schema with a baseline; rehearse the baseline and upgrades against a database backup before rollout. Internal export APIs are not public integration endpoints.
