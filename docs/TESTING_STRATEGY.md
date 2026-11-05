# Chiến lược kiểm thử

Backend Maven và frontend npm manifests đã được tạo. Hiện có API/integration tests cho auth, Marketplace provider/review, Booking ownership/status/review eligibility, Gateway context và Payment adapter registry. Frontend hiện được xác minh bằng lint, typecheck và production build; chưa có frontend unit hoặc full E2E tests.

## Mức kiểm thử đề xuất

- Unit tests cho domain rules, validation và chuyển trạng thái booking.
- Integration tests cho persistence và authorization/API boundary.
- API/contract tests cho DTO, status code và quyền truy cập.
- Frontend tests cho form, role-gated navigation và loading/error/empty states.
- E2E tests cho MVP flows sau khi endpoints, test data và environment được tạo.

## Luồng tối thiểu cần kiểm chứng

Đăng ký/đăng nhập; quyền từng role; duyệt dịch vụ; tạo booking; slot overlap concurrency; repeat category quota (lần hai warning/Admin review, lần ba block); provider hoàn tất; hai chiều review; payment adapter/callback isolation; lỗi/booking không thuộc người dùng.

Maven reactor tests dùng H2; MySQL schema/constraints và cross-service behavior chưa được xác minh với database thật. Mỗi requirement ID phải có ít nhất một test hoặc kiểm chứng chấp nhận được. Không tuyên bố coverage/ngưỡng hiệu năng chưa được đo.
