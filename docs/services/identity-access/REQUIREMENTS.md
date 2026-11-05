# Requirements — Identity & Access

> Ranh giới và các identity fields/role đã được chốt. API auth, BCrypt, JWT và persistence đã triển khai; quản trị tài khoản, verification và recovery chưa có.

## 1. Giới thiệu

Identity & Access quản lý danh tính tài khoản, xác thực và quyền truy cập cơ bản.

## 2. Mục tiêu và phạm vi

Hỗ trợ đăng ký/đăng nhập và xác định vai trò khách hàng, nhà cung cấp, Admin. Hiện role là một giá trị trên tài khoản; Admin được bootstrap cấu hình, không đăng ký công khai.

## 3. Trách nhiệm nghiệp vụ

- Sở hữu tài khoản, credential và gán role.
- Cung cấp identity/role claims hoặc contract xác thực cho API.
- Thực thi thao tác quản trị tài khoản thuộc domain Identity.

## 4. Chức năng thuộc phạm vi

Đã triển khai đăng ký, đăng nhập email/phone, truy vấn danh tính hiện tại, BCrypt, JWT và bootstrap Admin. Quản trị tài khoản theo quyền, email verification, reset password, lockout và nhiều role trên một tài khoản chưa triển khai/chưa được xác nhận.

## 5. Chức năng ngoài phạm vi

Provider business profile, catalog/service offering, booking, payment, review, và truy cập trực tiếp database domain khác.

## 6. Actors

Khách hàng, nhà cung cấp, Admin và các API domain cần xác minh identity.

## 7. Functional Requirements

- `ID-FR-001` (Đã xác nhận): người dùng có thể đăng ký theo identity fields/role đã được chốt.
- `ID-FR-002` (Đã xác nhận): người dùng có thể đăng nhập bằng credential hợp lệ.
- `ID-FR-003` (Đã xác nhận): backend xác định role/quyền; không tin role client tự gửi.
- `ID-FR-004` (Đã xác nhận): Admin quản lý user theo quyền; chi tiết action/audit còn mở.

## 8. Non-functional Requirements

Credential phải được băm bằng password hashing chuyên dụng; không log secret; API validate input và không để lộ thông tin nhạy cảm trong lỗi. Token/session policy, rate limit và ngưỡng vận hành còn cần xác nhận.

## 9. Business Rules

- Tài khoản không được tạo trùng định danh đăng nhập theo chính sách được duyệt.
- Chỉ cấp quyền theo danh sách role hợp lệ phía server.
- Thay đổi trạng thái tài khoản/role yêu cầu quyền phù hợp; audit requirement còn mở.

## 10. Use Cases

- `ID-UC-01`: đăng ký tài khoản.
- `ID-UC-02`: đăng nhập.
- `ID-UC-03`: xem danh tính hiện tại.
- `ID-UC-04`: Admin tra cứu/khóa/mở tài khoản nếu chức năng này được duyệt.

## 11. Input / Output

Đăng ký nhận email, phoneNumber, username, fullName, password và role. Login nhận identifier (email hoặc phone) và password. Response tài khoản không gồm password/hash; login trả accessToken, expiresIn và account.

## 12. API endpoints dự kiến

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/me`
- `/api/admin/users/...` chưa triển khai.

Public request đi qua Gateway.

## 13. Request / Response mẫu

Ví dụ minh họa, không phải contract đã duyệt:

```json
{
  "identifier": "<email-or-approved-identifier>",
  "password": "<provided-at-runtime>"
}
```

```json
{
  "accessToken": "<issued-token>",
  "tokenType": "<approved-scheme>",
  "expiresIn": "<approved-duration>"
}
```

Không ghi credential thật trong tài liệu hoặc log. Tên field, role registration, token scheme/expiry phải được xác nhận.

## 14. Xác thực và phân quyền

Các endpoint đăng ký/đăng nhập là public nhưng cần chống lạm dụng theo policy. `GET /me` yêu cầu xác thực. Admin Portal xác thực bằng JWT + role `ADMIN`. API key chỉ dành cho server-to-server integration, có scope/rotation và không gửi xuống browser. Các service vẫn kiểm tra ownership tài nguyên của mình.

## 15. Dữ liệu service sở hữu

Account ID, login identifier, credential hash, account state, role assignments và timestamps ở mức tối thiểu. PII cần thu thập/lưu bao nhiêu chưa được chốt.

## 16. Database schema dự kiến

Entity `Account` đang lưu tại bảng `accounts`, role dùng enum; email, phone và username có unique constraints. Hibernate `ddl-auto: update`; chưa có migration.

## 17. Quan hệ với service khác

Marketplace lưu ProviderProfile; Booking lưu ID khách hàng/provider; các domain không sở hữu credential và không truy cập DB Identity.

## 18. Cách giao tiếp

REST/DTO hoặc token claims theo contract đã duyệt. Nếu microservices độc lập, Gateway có thể xác thực ở edge nhưng domain services cần bảo vệ tài nguyên. Không dùng entity/repository dùng chung.

## 19. Events phát ra/nhận vào

Chưa đề xuất broker/event trong MVP. Nếu sau này cần sự kiện account lifecycle, phải xác định schema, consistency, delivery/retry và consumer trước khi thêm.

## 20. Lỗi và ngoại lệ

Các trường hợp cần xử lý: dữ liệu không hợp lệ, identifier trùng, credential sai, tài khoản không hoạt động, chưa xác thực, không đủ quyền và dependency unavailable. Error format/status code chưa được chốt; phản hồi đăng nhập không nên tiết lộ account tồn tại.

## 21. Acceptance Criteria

- Đăng ký/đăng nhập thành công với dữ liệu hợp lệ theo policy được duyệt.
- Dữ liệu sai/trùng hoặc credential sai bị từ chối an toàn.
- API bảo vệ không truy cập được khi thiếu/sai quyền.
- Response không lộ credential/hash.

## 22. Test Cases

- Unit: validation, credential hashing/verification, role policy.
- API/integration: registration success/duplicate, login success/failure, unauthenticated `/me`, role denied/allowed.
- Security: không trả credential và không tin role từ client.

## 23. Vấn đề chưa xác nhận

Refresh/revocation; password recovery; email/phone verification; lockout; Admin audit; PII retention.

## 24. Chức năng chưa triển khai

Đã triển khai: đăng ký/đăng nhập bằng email hoặc phone, account fields bắt buộc, BCrypt hash, JWT, `/api/me`, cấm tự đăng ký ADMIN, bootstrap Admin tùy chọn, persistence và integration tests. Chưa triển khai: Admin user APIs, verification, recovery, refresh/revocation và migration scripts.
