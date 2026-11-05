# Cấu trúc dự án hiện tại

Tài liệu này mô tả cấu trúc source hiện tại, không phải cấu trúc mục tiêu hay scaffold đề xuất. Backend vẫn là Spring Boot microservices; frontend vẫn là Next.js.

## Cây thư mục

```text
DoAn1/
├── AGENTS.md
├── Readme.md                    # Master prompt/phạm vi khởi đầu; không phải inventory runtime
├── docker-compose.yml
├── docker/
│   └── mysql/init/              # Tạo schema local
├── back-end/
│   ├── pom.xml                  # Maven reactor, Java 21
│   ├── api-gateway/
│   │   └── src/main/java/com/doan1/gateway/
│   │       ├── config/          # GatewayRoutes, GatewayCorsConfiguration
│   │       └── ApiGatewayApplication.java
│   ├── identity-service/
│   │   └── src/main/java/com/doan1/identity/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/request/
│   │       ├── dto/response/
│   │       ├── entity/
│   │       ├── enums/
│   │       ├── exception/
│   │       ├── mapper/
│   │       ├── repository/
│   │       ├── security/
│   │       ├── service/
│   │       └── IdentityServiceApplication.java
│   ├── marketplace-service/
│   │   └── src/main/java/com/doan1/marketplace/
│   │       ├── client/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/request/
│   │       ├── dto/response/
│   │       ├── entity/
│   │       ├── enums/
│   │       ├── exception/
│   │       ├── mapper/
│   │       ├── repository/
│   │       ├── security/
│   │       ├── service/
│   │       └── MarketplaceServiceApplication.java
│   ├── booking-service/
│   │   └── src/main/java/com/doan1/booking/
│   │       ├── client/
│   │       ├── controller/
│   │       ├── dto/request/
│   │       ├── dto/response/
│   │       ├── entity/
│   │       ├── enums/
│   │       ├── mapper/
│   │       ├── repository/
│   │       ├── security/
│   │       ├── service/
│   │       └── BookingServiceApplication.java
│   └── payment-service/
│       └── src/main/java/com/doan1/payment/
│           ├── provider/        # Adapter contract/registry hiện ở mức skeleton
│           └── PaymentServiceApplication.java
├── font-end/
│   └── src/
│       ├── app/                 # Next.js App Router, pages và BFF routes
│       ├── features/            # auth, admin, booking, marketplace
│       ├── shared/components/   # UI dùng chung
│       └── lib/                 # API client/DTO dùng chung
└── docs/
    ├── architecture/
    ├── database/
    ├── frontend/
    ├── requirements/
    └── services/                # API Gateway và 4 business services
```

Mỗi backend module có `src/test/java` riêng. Identity, Marketplace và Booking tách request/response DTO, entity, enum, mapper và nghiệp vụ theo package; Gateway và Payment giữ cấu trúc gọn phù hợp vai trò hiện tại.

## Phân cấp theo trách nhiệm

| Thành phần | Nơi đặt | Sở hữu / trách nhiệm hiện tại |
|---|---|---|
| Web UI và BFF | `font-end/` | Next.js App Router; trình duyệt gọi BFF, BFF chuyển tiếp tới API Gateway và giữ JWT trong cookie HttpOnly. |
| API Gateway | `back-end/api-gateway/` | Điểm định tuyến API; không sở hữu dữ liệu nghiệp vụ. |
| Identity & Access | `back-end/identity-service/` | Tài khoản, mật khẩu đã băm, role, đăng ký/đăng nhập và JWT. Không có `user-service` riêng. |
| Marketplace | `back-end/marketplace-service/` | Provider profile, category code cố định, duyệt provider, review và rating. |
| Booking | `back-end/booking-service/` | Booking, participant snapshots và trạng thái booking cơ bản. |
| Payment | `back-end/payment-service/` | Spring Boot skeleton và adapter registry; chưa có API giao dịch hoặc database nghiệp vụ. |
| MySQL local | `docker-compose.yml`, `docker/mysql/init/` | Schema `doan1_identity`, `doan1_marketplace`, `doan1_booking`; MySQL không publish port host trong Compose. |
| Tài liệu | `docs/` | Requirements, architecture, API/database contracts và implementation status. |

Admin là role/portal, không phải service. Review được lưu ở Marketplace; Marketplace gọi Booking qua API nội bộ để kiểm tra điều kiện review. Các service không dùng chung entity hoặc truy cập schema của service khác.

## Phân biệt hiện trạng và cấu trúc được đề xuất

Các mẫu trong `Readme.md` và sơ đồ do người dùng cung cấp là định hướng tham khảo, không phải cấu trúc repository:

- Frontend hiện là **Next.js/React/TypeScript**, không phải Angular; tên thư mục hiện tại là `font-end/`.
- Backend hiện là Maven reactor gồm **API Gateway và bốn Spring Boot apps**, không phải một backend modular monolith.
- Không có các module/deployable độc lập `user`, `review`, `notification` hoặc `admin`; capability thuộc Identity, Marketplace hoặc service sở hữu workflow.
- Marketplace hiện **chưa có** entity/bảng/API quản lý gói dịch vụ (`ServiceOffering`/`service_offerings`). Yêu cầu provider tạo/sửa giá/xóa gói đã được nêu nhưng chưa có trong source snapshot này.
- Booking hiện nhận provider/category, không nhận offering ID và không lưu price snapshot.
- Payment chưa có API giao dịch, schema hoặc tích hợp cổng thanh toán.
- `db/migration/` chưa có migration scripts; schema được Hibernate cập nhật trong cấu hình local.

Nếu có quyết định chuyển sang Angular hoặc modular monolith, cần xác nhận đó là thay đổi kiến trúc riêng trước khi thay đổi source hoặc sơ đồ triển khai.

## Điểm vào để tra cứu

- [Tổng quan](./PROJECT_OVERVIEW.md)
- [Kiến trúc hiện tại](./ARCHITECTURE.md)
- [Trạng thái phát triển](./DEVELOPMENT_STATUS.md)
- [API hiện có](./API_DOCUMENTATION.md)
- [Sơ đồ dữ liệu](./DATABASE_DESIGN.md)
