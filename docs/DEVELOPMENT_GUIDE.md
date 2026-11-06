# Hướng dẫn phát triển

## Yêu cầu công cụ

- Backend: JDK 21, Maven 3.9+ (hoặc Docker để chạy Maven image).
- Frontend: Node.js 22+ và npm.
- Full local stack: Docker Desktop.
- Tạo khóa RS256 và local environment theo [SECURITY.md](./SECURITY.md).

## Backend

Từ repository root với Maven đã cài:

```powershell
mvn -f back-end/pom.xml test
mvn -f back-end/pom.xml -DskipTests package
```

Chạy một service riêng:

```powershell
mvn -f back-end/pom.xml -pl identity-service spring-boot:run
```

Không có Maven cục bộ thì dùng Maven 3.9.9/JDK 21 Docker image và mount `back-end` vào container. Một ví dụ hiện hành nằm trong [trạng thái kiểm thử](./DEVELOPMENT_STATUS.md). Test reports ở `back-end/<module>/target/surefire-reports/`.

## Frontend

Từ `font-end/`:

```powershell
npm ci
npm run dev
npm run lint
npm run typecheck
npm run build
```

Ứng dụng frontend-only cần `API_GATEWAY_URL` trỏ tới Gateway. Trong Compose, BFF dùng `http://api-gateway:8080`; browser không gọi service port trực tiếp.

## Full stack và API

Khởi động/xem log/dừng stack theo [Hướng dẫn triển khai](./DEPLOYMENT_GUIDE.md). Public API base URL local là `http://localhost:8080`. Tích hợp ngoài cần theo [API Integration Guide](./API_INTEGRATION_GUIDE.md); reference endpoint nằm trong [API_DOCUMENTATION.md](./API_DOCUMENTATION.md).

## Quy trình thay đổi

1. Đọc `AGENTS.md`, yêu cầu, architecture/decision và docs của service bị ảnh hưởng.
2. Xác định requirement ID, owner của business rule/data, API/DB/frontend dependencies và acceptance tests.
3. Nếu yêu cầu/kiến trúc đang mở, làm rõ và cập nhật trạng thái trước khi implement.
4. Thay đổi đúng owner/service; dùng DTO cho API, validate input, không chia sẻ DB/entity.
5. Thêm test cho business logic mới và cập nhật API/database/service documentation.
6. Chạy test/lint/build phù hợp; ghi trạng thái và bằng chứng trong [DEVELOPMENT_STATUS.md](./DEVELOPMENT_STATUS.md) và [TEST_CASES.csv](./TEST_CASES.csv).
7. Không đánh dấu PASS nếu thiếu test report hoặc runtime evidence tương ứng.

Không thêm service theo role/UI, không public internal ports, không đưa secret vào code hoặc browser. Payment lifecycle/saga hiện được hoãn; các policy booking chưa triển khai xem [open questions](./requirements/open-questions.md).
