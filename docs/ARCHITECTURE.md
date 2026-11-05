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

## Thành phần

1. **Identity & Access** — tài khoản, credential, xác thực, role.
2. **Marketplace** — provider profile, category, offering/price và duyệt provider.
3. **Booking** — booking state machine, slot conflict, customer/provider/Admin workflows; repeat booking limit theo category.
4. **Payment** — giao dịch và adapter với payment providers.
5. **API Gateway** — public entry, routing và edge policy; không sở hữu nghiệp vụ.
6. **Next.js frontend** — public/customer/provider/Admin portals; mọi API qua Gateway.

Review hai chiều nằm trong Marketplace; Booking là source of truth cho booking status/participants, Marketplace hỏi endpoint nội bộ để xác minh eligibility. Complaint workflow đầy đủ, notification service và broker chưa được chốt.

Identity, Marketplace và Booking có API/DTO riêng, schema tách biệt và kiểm thử H2. Tích hợp nội bộ giữa Booking–Marketplace dùng `SERVICE_API_KEY`; Gateway không route `/internal/**`. Payment hiện mới có adapter registry, chưa có transaction API/provider thật.

## Luồng và tài liệu chi tiết

- [Service architecture](./architecture/service-architecture.md)
- [System context](./architecture/system-context.md)
- [Communication flow](./architecture/communication-flow.md)
- [Authentication flow](./architecture/authentication-flow.md)
- [Booking flow](./architecture/booking-flow.md)
- [Deployment architecture](./architecture/deployment-architecture.md)
- [Decisions](./DECISIONS.md)
