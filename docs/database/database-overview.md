# Database overview

Local Docker Compose cấu hình MySQL 8.0 với ba schema:

- `doan1_identity` — Identity.
- `doan1_marketplace` — Marketplace.
- `doan1_booking` — Booking.

Các service trên kết nối tới schema riêng; Payment chưa có schema nghiệp vụ. Hibernate dùng `ddl-auto: update` trong cấu hình local. Chưa có Flyway/Liquibase migration scripts. Backend tests dùng H2.

MySQL không publish port lên host trong Compose; services kết nối qua Docker network. Chi tiết ownership và entity được ghi tại [data ownership](./data-ownership.md), [entity relationship](./entity-relationship.md) và [DATABASE_DESIGN](../DATABASE_DESIGN.md).
