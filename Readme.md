# Nhà Tốt — nền tảng đặt lịch dịch vụ

Website kết nối khách hàng với nhà cung cấp dịch vụ dọn dẹp và sửa chữa. Repository hiện gồm frontend Next.js, API Gateway và các backend service Spring Boot; phạm vi đã triển khai, chưa triển khai và cần xác minh được ghi riêng trong tài liệu trạng thái.

> Đây là README hướng dẫn sử dụng repository. Không dùng README này thay cho đặc tả; chọn tài liệu chi tiết ở mục **Tài liệu**.

## Bắt đầu

### Chạy toàn bộ ứng dụng

Yêu cầu Docker Desktop đang chạy. Tạo khóa RS256 dùng riêng cho local trước khi khởi động, sau đó làm theo [Hướng dẫn triển khai local](docs/DEPLOYMENT_GUIDE.md). Hướng dẫn này có lệnh khởi động, kiểm tra trạng thái, xem log, dừng stack và cấu hình port.

Ứng dụng local:

| Thành phần | URL |
|---|---|
| Frontend | `http://localhost:3000` |
| API Gateway (điểm vào API) | `http://localhost:8080` |
| Prometheus | `http://localhost:9090` |
| Grafana | `http://localhost:3001` |
| Jaeger | `http://localhost:16686` |

Database và RabbitMQ chỉ hoạt động trong Docker network. Các service port được bind loopback trong Compose để hỗ trợ phát triển local; không public các port service ra Internet.

### Phát triển và kiểm thử

- [Hướng dẫn phát triển](docs/DEVELOPMENT_GUIDE.md): công cụ, lệnh build/test, chạy frontend/backend và quy trình thay đổi.
- [Chiến lược kiểm thử](docs/TESTING_STRATEGY.md): phạm vi, tầng kiểm thử và các giới hạn hiện tại.
- [CSV trạng thái testcase](docs/TEST_CASES.csv): kịch bản, kết quả gần nhất, bằng chứng và test còn chưa chạy.
- [Hướng dẫn tạo khóa và cấu hình bảo mật local](docs/SECURITY.md).

## Tích hợp API

Client bên ngoài kết nối qua API Gateway, không gọi trực tiếp microservice. Local base URL là `http://localhost:8080`; khi triển khai, dùng domain HTTPS do reverse proxy/API Gateway công bố. Xem [Hướng dẫn tích hợp API bên ngoài](docs/API_INTEGRATION_GUIDE.md) để biết base URL, đăng nhập, Bearer token, request examples, quyền và luồng nghiệp vụ.

Endpoint catalog:

- [Tổng quan API và bảng endpoint](docs/API_DOCUMENTATION.md)
- [Identity](docs/services/identity-access/API.md) · [Profile](docs/services/profile/API.md) · [Catalog](docs/services/catalog/API.md)
- [Booking](docs/services/booking/API.md) · [Review](docs/services/review/API.md) · [Notification](docs/services/notification/API.md)
- [API Gateway](docs/services/api-gateway/API.md) · [Marketplace migration APIs](docs/services/marketplace/API.md) · [Payment](docs/services/payment/API.md)

Các API `/internal/**` chỉ dành cho giao tiếp service-to-service, không phải API tích hợp client. Payment hiện chưa có API giao dịch; không gửi thông tin thẻ hoặc giả lập thanh toán thành công qua endpoint không tồn tại.

## Phạm vi hiện tại

- Identity phát JWT RS256; refresh token có rotation/revoke.
- Profile sở hữu hồ sơ khách hàng/nhà cung cấp; Catalog sở hữu danh mục và gói dịch vụ.
- Booking lưu snapshot gói/giá; Booking và Review dùng outbox events.
- Review là service riêng; Notification lưu thông báo in-app.
- Payment lifecycle/saga, booking slot/quota, email/push/SMS, Pact và browser E2E chưa hoàn tất hoặc được hoãn. Xem [trạng thái phát triển](docs/DEVELOPMENT_STATUS.md) và [các quyết định](docs/DECISIONS.md).

## Tài liệu

- [Mục lục tài liệu](docs/README.md)
- [Tổng quan dự án](docs/PROJECT_OVERVIEW.md) · [Kiến trúc](docs/ARCHITECTURE.md) · [Yêu cầu](docs/REQUIREMENTS.md)
- [Sơ đồ cấu trúc source](docs/PROJECT_STRUCTURE.md) · [Database](docs/DATABASE_DESIGN.md) · [Deployment](docs/DEPLOYMENT_GUIDE.md)
- [Changelog](docs/CHANGELOG.md) · [Open questions](docs/requirements/open-questions.md)

README và tài liệu mô tả trạng thái quan sát được; một endpoint hoặc cấu hình tồn tại không tự chứng minh toàn bộ workflow đã được kiểm thử.
