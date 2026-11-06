# Identity & Access

Identity sở hữu tài khoản, thông tin đăng nhập và role; đây không phải `admin-service`.

Đã triển khai đăng ký/đăng nhập, JWT RS256, refresh-token rotation/revocation, BCrypt password hash, unique email/phone/username, account ACTIVE/SUSPENDED, Admin status change/audit và bootstrap Admin tùy chọn. Đăng ký công khai chỉ chấp nhận `CUSTOMER` hoặc `PROVIDER`; tài khoản Admin không thể tự đăng ký. Profile kiểm tra account status qua internal API.

Identity giữ private signing key; các service khác chỉ nhận public key. Refresh token raw chỉ được trả khi đăng nhập/refresh, database lưu hash SHA-256 và family để rotate; replay thu hồi family. Logout thu hồi refresh token, nhưng access JWT còn hiệu lực đến TTL. Chưa triển khai xác minh email/số điện thoại, khôi phục mật khẩu hoặc rate limiting. Schema được quản lý bằng Flyway, Hibernate `ddl-auto: validate`.

## Java package layout

`src/main/java/com/doan1/identity` separates HTTP controllers, request/response DTOs, `entity`, `enums`, `mapper`, `service`, `repository` and security/configuration. Tests are isolated in `src/test/java/com/doan1/identity`.
