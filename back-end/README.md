# Backend

Java 21 / Spring Boot 3 Maven reactor containing the API Gateway and four independent business applications.

## Modules

- `api-gateway`
- `identity-service`
- `marketplace-service`
- `booking-service`
- `payment-service`

Each application exposes Actuator health. Identity, Marketplace and Booking implement the current authentication, provider, booking and review flows with service-owned persistence. Payment remains an adapter registry without payment APIs or a real provider. See `../docs/services/` for the implemented contracts and limitations.

## Java source layout

Business classes are organized by responsibility under each module's `src/main/java` package:

- `controller`: HTTP endpoints
- `dto`: API and inter-service request/response types
- `model`: persistence entities and domain enums
- `repository`: persistence access
- `security`: JWT and principal handling
- `service`: business operations
- `client`: inter-service HTTP clients
- `config` and `exception`: application configuration and error handling

Only applicable packages are present in each module; application entry points stay in the root package for component scanning. Tests remain in each module's separate `src/test/java` source tree, following Maven conventions. Payment's current provider adapters are under `provider`; Gateway routing is under `config`.

## Build and run

To run the entire local stack (frontend, Gateway, all services and MySQL), start Docker Desktop and run from the repository root:

```powershell
docker compose up --build
```

Open the frontend at `http://localhost:3000`. Gateway and development-only service ports are bound to `127.0.0.1`; MySQL remains internal to Docker. See `../docs/DEPLOYMENT_GUIDE.md` for port overrides, logs, shutdown, data persistence and local-only credentials.

For running or testing a backend module directly, JDK 21 and Maven 3.9+ are required:

```powershell
mvn -f back-end/pom.xml test
mvn -f back-end/pom.xml -pl identity-service spring-boot:run
```

Business schemas use Hibernate `ddl-auto: update`; versioned migrations are not included. The local Compose defaults are not for production.
