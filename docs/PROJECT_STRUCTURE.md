# Cấu trúc dự án hiện tại

Tài liệu mô tả source hiện tại, không phải scaffold mục tiêu. Backend là Spring Boot microservices; frontend là Next.js.

## Cây thư mục rút gọn

```text
DoAn1/
├── docker-compose.yml
├── docker/mysql/init/
├── back-end/
│   ├── pom.xml
│   ├── api-gateway/
│   ├── identity-service/
│   ├── profile-service/
│   ├── catalog-service/
│   ├── marketplace-service/
│   ├── review-service/
│   ├── booking-service/
│   ├── payment-service/
│   └── notification-service/
├── font-end/
│   └── src/
│       ├── app/                 # App Router và BFF routes
│       ├── features/            # auth, admin, booking, catalog, marketplace, notifications
│       ├── shared/components/
│       └── lib/
└── docs/
    ├── architecture/
    ├── database/
    ├── frontend/
    └── services/                # Gateway và business service docs
```

Mỗi backend module có `src/test/java` riêng. Domain services tách controller/DTO/entity/repository/service theo trách nhiệm; Payment hiện giữ cấu trúc gọn cho adapter skeleton.

## Ownership

| Thành phần | Nơi đặt | Sở hữu / trách nhiệm hiện tại |
|---|---|---|
| Web UI/BFF | `font-end/` | Next.js; browser gọi BFF, BFF chuyển tiếp tới Gateway và giữ JWT trong cookie HttpOnly. |
| API Gateway | `back-end/api-gateway/` | Routing và JWT RS256 validation ở edge; không sở hữu dữ liệu nghiệp vụ. |
| Identity & Access | `back-end/identity-service/` | Tài khoản, credential, role, account status/audit, RS256 và refresh-token rotation/revocation. |
| Profile | `back-end/profile-service/` | Customer/provider profiles, approval/suspension, public lookup, eligibility và rating projection. |
| Catalog | `back-end/catalog-service/` | Category, provider packages/prices, moderation, history và event outbox. |
| Marketplace | `back-end/marketplace-service/` | Legacy provider/review tables and authenticated migration export APIs. |
| Booking | `back-end/booking-service/` | Booking, Catalog package lookup, price/name/duration snapshots, state transitions and completion outbox. |
| Review | `back-end/review-service/` | Review data, Booking eligibility, `ReviewSubmitted` outbox and legacy import API. |
| Payment | `back-end/payment-service/` | Adapter registry skeleton; chưa có transaction API/schema. |
| Notification | `back-end/notification-service/` | Package-event consumer và in-app notification inbox. |
| MySQL | `docker-compose.yml`, `docker/mysql/init/` | Schema riêng cho các service có dữ liệu; host port local cấu hình qua `MYSQL_PORT` trong `.env`. |
| RabbitMQ | `docker-compose.yml` | Catalog/Booking/Review outboxes; Notification consumes BookingCompleted and package events, Profile consumes ReviewSubmitted. |
| Observability | `docker-compose.yml`, `docker/prometheus/`, `docker/grafana/` | Prometheus metrics, Grafana and Jaeger OTLP traces; centralized logs not yet integrated. |

Admin là role/portal, không phải service. Marketplace legacy tables remain as migration sources; Review is independently deployed. Các service không dùng chung entity hoặc truy cập schema của service khác.

## Hiện trạng và phần chưa hoàn tất

- Frontend là **Next.js/React/TypeScript**, không phải Angular; tên thư mục là `font-end/`.
- Backend là Maven reactor gồm API Gateway và 8 Spring Boot services, không phải modular monolith.
- Payment chưa có API giao dịch, schema nghiệp vụ hoặc tích hợp cổng thanh toán; Payment events/saga được hoãn.
- Identity, Profile, Catalog, Marketplace, Booking, Review và Notification dùng Flyway; Marketplace/Booking baseline chưa được rehearsal trên bản sao dữ liệu hiện hữu.
- `docker compose config -q` đã pass; full Compose runtime, contract/Pact suite, browser E2E, production deployment và centralized logs chưa được xác minh/hoàn thiện.

## Điểm vào để tra cứu

- [Tổng quan](./PROJECT_OVERVIEW.md)
- [Kiến trúc hiện tại](./ARCHITECTURE.md)
- [Trạng thái phát triển](./DEVELOPMENT_STATUS.md)
- [API hiện có](./API_DOCUMENTATION.md)
- [Sơ đồ dữ liệu](./DATABASE_DESIGN.md)
