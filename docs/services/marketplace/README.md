# Marketplace

Marketplace owns legacy provider/review tables only as migration sources. New review traffic is not handled here.

Gateway routes `/api/reviews/**` to Review service. Provider profile routes go to Profile and catalog/package routes go to Catalog. Legacy provider profile/review data can be exported through authenticated internal APIs; destination services never read the Marketplace database directly.

Marketplace uses Flyway with baseline and Hibernate `ddl-auto: validate`. Review moderation/edit/delete are not implemented.

## Java package layout

`src/main/java/com/doan1/marketplace` separates controllers, DTOs, entities, repositories, services, clients and security/configuration. Tests live under `src/test/java/com/doan1/marketplace`.
