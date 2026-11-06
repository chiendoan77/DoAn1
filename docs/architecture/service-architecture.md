# Service architecture (ứng viên)

| Deployable/domain | Trách nhiệm hiện tại | Dữ liệu sở hữu | Admin liên quan | Trạng thái |
|---|---|---|---|---|
| Identity & Access | Account, credential, role, account status | Account, status audit, hashed refresh tokens | Suspend/activate account | RS256 and refresh rotation/revoke implemented |
| Profile | Customer/provider profile, approval/suspension, rating projection | Customer/provider profiles, rating projection | Duyệt/suspend provider | Implemented |
| Catalog | Category, provider packages/prices, package moderation | Categories, packages, history, outbox | Approve/reject/deactivate package | Đã có Flyway và package workflow |
| Marketplace | Legacy profile/review migration exports | Legacy migration sources | Không còn domain ops mới | Flyway baseline |
| Booking | Booking lifecycle, Catalog package snapshots, completion event | Booking, outbox | Booking admin workflow chưa đủ | Package snapshot and BookingCompleted outbox implemented |
| Review | Review workflow and Booking eligibility | Reviews, outbox | Review moderation chưa có | Separate deployable implemented |
| Payment | Provider adapters, transaction/callback lifecycle | Payment transaction/attempt | Payment operations | Hiện chỉ adapter skeleton |
| Notification | Consume package and booking events, in-app inbox | Notification inbox | Không có admin portal riêng | BookingCompleted and package events |
| API Gateway | Routing, JWT RS256 validation, edge policy | Không sở hữu dữ liệu nghiệp vụ | Không phải admin backend | Routes Review and Profile |

Admin là role/portal, không phải service. Review là deployable riêng; Marketplace còn legacy tables để migration. Xem [PROJECT_STRUCTURE.md](../PROJECT_STRUCTURE.md) và [DEVELOPMENT_STATUS.md](../DEVELOPMENT_STATUS.md).

Catalog/Booking/Review events dùng transactional outbox → RabbitMQ; Notification consumes BookingCompleted, Profile consumes ReviewSubmitted. Payment events/saga và complaint workflow chưa triển khai.
