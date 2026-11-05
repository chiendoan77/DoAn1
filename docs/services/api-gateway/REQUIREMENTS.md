# Requirements — API Gateway

Trạng thái: edge component đã được chọn; hiện chỉ có route skeleton, chưa có authentication enforcement hoặc production deployment.

## Mục tiêu/phạm vi

- Định tuyến public API đến Identity, Marketplace, Booking và Payment.
- Là điểm vào API duy nhất từ browser; không thay thế authorization trong từng service.
- Không sở hữu dữ liệu nghiệp vụ.

## Actors và yêu cầu chức năng

- `GW-FR-001`: route request theo path tới đúng service.
- `GW-FR-002`: expose health/info cần thiết, không expose management secrets.
- `GW-FR-003`: không route database/internal service ports ra public.

## API / input / output

Gateway forward HTTP request/response; không chuyển đổi domain contract. Route prefixes được mô tả trong [API.md](./API.md). Error responses từ Gateway cần chuẩn hóa sau này.

## Bảo mật, dependencies, tests

Admin Portal xác thực JWT/ADMIN tại Gateway khi security filter được thêm, và vẫn phải kiểm tra tại domain service. Server-to-server API key không được đưa vào browser. Unit/integration tests cần kiểm tra route destination, forwarding và lỗi upstream.

## Dữ liệu/schema/events

Không có database/schema nghiệp vụ; không phát/nhận event trong skeleton.

## Acceptance / lỗi / chưa triển khai

Acceptance: mỗi route tới đúng service; internal services không public; upstream timeout/error trả lỗi an toàn. Chưa có authentication filter, resilience, rate limit, request logging policy hoặc deployment hardening.
