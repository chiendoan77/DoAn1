# Backend

Java 21 / Spring Boot Maven reactor. It contains the API Gateway, shared security modules and eight independently configured services: Identity, Profile, Catalog, Marketplace (legacy migration), Booking, Review, Payment and Notification.

## Architecture and API

The API Gateway is the client entry point. Domain services own their data; service-to-service calls use HTTP DTOs or RabbitMQ events rather than shared database access. Review is a separate service. Payment currently has only an adapter-registry skeleton and no payment lifecycle API.

See [service architecture](../docs/architecture/service-architecture.md), the [API catalog](../docs/API_DOCUMENTATION.md), [external API guide](../docs/API_INTEGRATION_GUIDE.md), [architecture](../docs/ARCHITECTURE.md) and [development status](../docs/DEVELOPMENT_STATUS.md).

## Source layout

Each service has its own `src/main/java` and `src/test/java` trees. Java code is grouped by controller, DTO, entity, repository, security, service, client/configuration and exception responsibilities where applicable.

## Build, test and run

Prerequisites and all local commands are documented in the repository [development guide](../docs/DEVELOPMENT_GUIDE.md) and [deployment guide](../docs/DEPLOYMENT_GUIDE.md). For external consumers, use the [API integration guide](../docs/API_INTEGRATION_GUIDE.md); do not connect clients directly to service ports.

The root `docker-compose.yml` binds development ports to loopback. MySQL and RabbitMQ are not published to the host. Compose credentials and default settings are local-only.
