# Roadmap

## Phase 0 — Domain/MVP

Đã chốt architecture bốn business services + Gateway, MVP core, Admin auth, review hai chiều, slot overlap và giới hạn repeat booking ở mức high-level. Các semantics còn mở nằm trong `requirements/open-questions.md`.

## Phase 1 — Skeleton và local environment (đang thực hiện)

- Tạo API Gateway, Identity & Access, Marketplace, Booking, Payment và Next.js frontend.
- Tạo health endpoints, cấu hình môi trường và build/test/lint commands tối thiểu.
- **Đã xác minh:** Maven reactor build/test; frontend lint/typecheck/build; local frontend page render và không tràn ngang ở viewport 390px.
- **Còn lại:** runtime health/routing integration test, Java 21 runtime validation, Docker Compose/local service orchestration.

## Phase 2 — Identity & access

- Signup/login, role và authorization.
- **Acceptance:** auth tests, role/ownership tests, Admin Portal dùng JWT + ADMIN; server API keys không expose cho browser.

## Phase 3 — Marketplace

- Provider profile, service category/offering, giá và Admin/provider onboarding.
- **Acceptance:** provider ownership, approval visibility, Admin privileges được test.

## Phase 4 — Booking

- Create/view/process booking, slot overlap, repeat booking warning/Admin review/quota, two-way reviews.
- **Acceptance:** state machine, concurrency, ownership, third-open-booking block và review eligibility có test; các policy còn mở phải được xác nhận.

## Phase 5 — Payment

- Payment service/provider abstraction, transaction lifecycle, webhooks/idempotency.
- **Acceptance:** nhiều adapters có thể đồng thời bật; lỗi adapter không ảnh hưởng adapter khác; callback xác minh được.

## Phase 6 — Integration/demo/deployment

- API Gateway routing, frontend integration, Docker/Compose phù hợp, seed/test data và smoke/E2E.
- **Acceptance:** setup từ môi trường sạch theo tài liệu; database/service internal không public ngoài ý muốn.

## Thứ tự bắt đầu

Bắt đầu Phase 1 với skeleton và health/build checks; không chờ các policy booking/payment chưa cần cho skeleton. Không viết feature logic dựa trên các câu hỏi còn mở.
