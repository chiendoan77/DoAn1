# Hướng dẫn phát triển

## Trạng thái hiện tại

Repository ban đầu chưa có mã ứng dụng hay manifest dependency. Hiện có luồng nghiệp vụ MVP một phần; không implement logic phụ thuộc policy còn mở nếu chưa cập nhật requirements/DECISIONS.

## Current commands

- Backend: from repository root, `mvn -f back-end/pom.xml test` (requires Maven 3.9+ and JDK 21).
- Frontend: `cd font-end`; run `npm run lint`, `npm run typecheck`, `npm run build`.
- Frontend dev server: `npm run dev`.

The current backend reactor tests were executed using Maven 3.9.16 from the local cache under JDK 25 with Java release target 21; verify again with JDK 21 in the intended environment.

## Quy trình cho mỗi thay đổi

1. Đọc `AGENTS.md` và tài liệu liên quan trong `docs/`.
2. Xác định requirement ID, domain/service sở hữu, API/DB/Frontend phụ thuộc và acceptance test.
3. Nếu requirement/architecture còn ở trạng thái đề xuất hoặc mở, làm rõ và cập nhật tài liệu trước khi implement.
4. Thực hiện thay đổi nhỏ trong đúng domain; dùng DTO cho API, validate input và giữ business logic ngoài controller.
5. Viết unit/integration/API tests cho hành vi mới.
6. Chạy validation phù hợp; ghi kết quả và cập nhật `DEVELOPMENT_STATUS.md`, API/DB docs khi liên quan.
7. Không đánh dấu done nếu chưa có bằng chứng kiểm tra.

## Quy ước kiến trúc

- Không tạo service theo vai trò, UI portal hoặc CRUD list.
- Admin dùng các API của domain service sở hữu nghiệp vụ.
- Không truy cập DB của domain/service khác.
- Không thêm dependency hoặc middleware hạ tầng nếu không có use case.
