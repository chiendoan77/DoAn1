# Identity & Access

Identity sở hữu tài khoản, thông tin đăng nhập và role; đây không phải `admin-service`.

Đã triển khai đăng ký/đăng nhập, xác thực JWT cho `/api/me`, BCrypt password hash, unique email/phone/username và bootstrap Admin tùy chọn. Đăng ký công khai chỉ chấp nhận `CUSTOMER` hoặc `PROVIDER`; tài khoản Admin không thể tự đăng ký.

Chưa triển khai API quản trị người dùng, xác minh email/số điện thoại, khôi phục mật khẩu, refresh/revoke token hoặc rate limiting. Schema được Hibernate cập nhật khi chạy (`ddl-auto: update`); chưa có migration.

## Java package layout

`src/main/java/com/doan1/identity` separates HTTP controllers, request/response DTOs, `entity`, `enums`, `mapper`, `service`, `repository` and security/configuration. Tests are isolated in `src/test/java/com/doan1/identity`.
