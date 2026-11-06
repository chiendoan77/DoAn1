# Chiến lược kiểm thử

## Trạng thái

Kịch bản chi tiết và trạng thái gần nhất nằm trong [TEST_CASES.csv](./TEST_CASES.csv). `PASS` chỉ dành cho testcase được chạy và có test report; `NOT_RUN` nghĩa là chưa có bằng chứng thực thi; `NOT_IMPLEMENTED` dùng cho hành vi chưa có implementation. Không coi cấu hình, route, build hoặc test context-load là bằng chứng E2E.

Ngày 2026-11-06 full Maven reactor đã pass: 39 tests/12 suites, không failure/error/skip. CSV hiện có 58 testcase: 42 `PASS`, 11 `NOT_RUN`, 4 `NOT_IMPLEMENTED`, 1 `DEFERRED`. Frontend lint/typecheck/build pass; Compose config cũng đã pass; full runtime Compose, các luồng browser E2E mới, production MySQL migration rehearsal và Pact suite chưa được xác minh. Xem [CSV testcase](./TEST_CASES.csv) để tra trạng thái từng case và bằng chứng.

## Tầng kiểm thử

1. **Unit:** validators, state transitions, mapping và outbox publisher/consumer behavior.
2. **Service integration:** Spring context, H2 persistence, API authorization/ownership, schema startup.
3. **Contract-style:** DTO/client interaction ở ranh giới service. `CatalogClientContractTests` là test hợp đồng nội bộ, không phải Pact provider/consumer suite.
4. **Frontend checks:** lint, TypeScript typecheck và production build; chưa có frontend unit/component test suite.
5. **E2E/runtime:** Compose/MySQL thật, Gateway xuyên services, browser flows; hiện chưa có bằng chứng E2E mới.

## Chạy kiểm thử

Các lệnh và prerequisites nằm trong [Hướng dẫn phát triển](./DEVELOPMENT_GUIDE.md). Backend tests chạy qua Maven reactor với H2; test report được lưu dưới `back-end/<module>/target/surefire-reports/`. Frontend gates là npm lint/typecheck/build. Container build cần Docker Desktop; không dùng `install` thay cho `test` để khẳng định testcase pass.

## Coverage cần có

- Identity: register/login/validation, role, refresh rotation, revoke/replay.
- Gateway: JWT edge validation, protected paths, CORS allow/deny.
- Profile/Catalog: ownership, approval visibility, account/provider eligibility, input validation, idempotent rating projection.
- Booking: package verification/snapshot, history privacy, assigned provider transitions, review eligibility.
- Review/Notification: eligibility, duplicate prevention, outbox publishing/consumer idempotency.
- Payment: adapter registry smoke test; lifecycle/webhook/saga tests chưa áp dụng vì Payment lifecycle được hoãn.
- Database/infrastructure: Flyway upgrades trên MySQL backup, RabbitMQ runtime delivery, Compose health, OTEL/Prometheus/Grafana runtime.
- Client integration: API contract regression và browser flow package → booking → completion → review/notification.

Slot-overlap concurrency, repeat-booking quota/Admin review, Payment, Pact, browser E2E và production observability phải giữ `NOT_IMPLEMENTED` hoặc `NOT_RUN` trong CSV cho đến khi có implementation/test evidence phù hợp.
