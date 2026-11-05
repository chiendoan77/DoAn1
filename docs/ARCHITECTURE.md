# Kiến trúc hệ thống

## Trạng thái

Người dùng chọn microservices với bốn business services: Identity & Access, Marketplace, Booking, Payment. API Gateway là edge component. Identity, Marketplace và Booking có các luồng MVP được triển khai; Payment hiện chỉ có adapter registry skeleton.

## Nguyên tắc

- Phân ranh giới theo năng lực nghiệp vụ, quy trình và quyền sở hữu dữ liệu; không theo role/UI.
- Không tạo `admin-service`. Admin Portal dùng JWT + role ADMIN; từng domain service xử lý thao tác trên dữ liệu mình sở hữu.
- API key chỉ dành cho tích hợp server-to-server, không nhúng vào browser.
- Mỗi service sở hữu database/schema riêng; service khác chỉ dùng API/DTO.
- Payment có provider adapter độc lập, nhiều phương thức/provider có thể chạy đồng thời; thêm adapter không tắt toàn bộ Payment module.
- Không thêm broker, Redis hay distributed transaction trước khi có use case và failure semantics cụ thể.

## Thành phần và mức triển khai

1. **Identity & Access** — tài khoản, credential, xác thực, role; đăng ký/đăng nhập và `/api/me` đã triển khai.
2. **Marketplace** — provider profile, category code cố định, duyệt provider, review/rating đã triển khai. Gói dịch vụ và giá thuộc phạm vi nghiệp vụ đã nêu nhưng chưa có model/API trong source hiện tại.
3. **Booking** — tạo/lịch sử booking và chuyển trạng thái cơ bản đã triển khai. Slot conflict và repeat booking policy đã chọn nhưng chưa triển khai. Việc nối booking với offering chưa được xác nhận.
4. **Payment** — adapter abstraction được chọn; hiện chỉ có app skeleton/registry, chưa có giao dịch/database/provider thật.
5. **API Gateway** — public entry, routing và edge policy; không sở hữu nghiệp vụ.
6. **Next.js frontend** — public/customer/provider/Admin flows một phần; mọi API qua Gateway qua BFF.

Review hai chiều nằm trong Marketplace; Booking là source of truth cho booking status/participants, Marketplace hỏi endpoint nội bộ để xác minh eligibility. Complaint workflow đầy đủ, notification service và broker chưa được chốt.

Identity, Marketplace và Booking có API/DTO riêng, schema tách biệt và kiểm thử H2. Tích hợp nội bộ giữa Booking–Marketplace dùng `SERVICE_API_KEY`; Gateway không route `/internal/**`. Compose hiện tạo schema MySQL cho Identity, Marketplace, Booking; Payment chưa có schema. Chi tiết cây source và phân biệt giữa hiện trạng/đề xuất ở [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md).

## Luồng và tài liệu chi tiết

- [Service architecture](./architecture/service-architecture.md)
- [System context](./architecture/system-context.md)
- [Communication flow](./architecture/communication-flow.md)
- [Authentication flow](./architecture/authentication-flow.md)
- [Booking flow](./architecture/booking-flow.md)
- [Deployment architecture](./architecture/deployment-architecture.md)
- [Decisions](./DECISIONS.md)
