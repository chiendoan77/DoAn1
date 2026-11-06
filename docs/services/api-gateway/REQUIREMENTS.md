# Requirements — API Gateway

Trạng thái: routing và JWT RS256/issuer/audience validation đã triển khai; production hardening còn thiếu.

## Mục tiêu/phạm vi

- Định tuyến public API đến Identity, Profile, Catalog, Marketplace (legacy only), Booking, Review, Payment và Notification.
- Là điểm vào API duy nhất từ browser; không thay thế authorization trong từng service.
- Không sở hữu dữ liệu nghiệp vụ.

## Actors và yêu cầu chức năng

- `GW-FR-001`: route request theo path tới đúng service.
- `GW-FR-002`: expose health/info cần thiết, không expose management secrets.
- `GW-FR-003`: không route database/internal service ports ra public.

## API / input / output

Gateway forward HTTP request/response; không chuyển đổi domain contract. Route prefixes được mô tả trong [API.md](./API.md). Error responses từ Gateway cần chuẩn hóa sau này.

## Bảo mật, dependencies, tests

Gateway xác minh JWT RS256/issuer/audience trên route cần auth; domain service vẫn kiểm tra role/ownership. Server-to-server API key không được đưa vào browser. Rate limiting, resilience và production hardening chưa hoàn tất.

## Dữ liệu/schema/events

Không có database/schema nghiệp vụ; không phát/nhận event trong skeleton.

## Acceptance / lỗi / chưa triển khai

Acceptance hiện đã test routing và JWT allow/deny cơ bản. Internal services không route công khai. Resilience, rate limit, request logging policy và deployment hardening chưa triển khai.
