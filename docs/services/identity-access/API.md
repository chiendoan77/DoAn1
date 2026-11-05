# Identity & Access API

Các endpoint public đi qua Gateway. Token được frontend gửi tới BFF; không trả password hash.

| Method | Path | Quyền | Hợp đồng hiện tại |
|---|---|---|---|
| POST | `/api/auth/register` | Public | `{email, phoneNumber, username, fullName, password, role}`; role chỉ `CUSTOMER` hoặc `PROVIDER`. Trả DTO tài khoản, không có token. |
| POST | `/api/auth/login` | Public | `{identifier, password}`; `identifier` là email hoặc số điện thoại. Trả `{accessToken, expiresIn, account}`. |
| GET | `/api/me` | Bearer JWT | Trả DTO tài khoản hiện tại. |

JWT dùng secret từ `JWT_SECRET` (tối thiểu 32 bytes), TTL mặc định 3600 giây. Bootstrap Admin tùy chọn bằng `BOOTSTRAP_ADMIN_ENABLED`, `BOOTSTRAP_ADMIN_EMAIL`, `BOOTSTRAP_ADMIN_PHONE`, `BOOTSTRAP_ADMIN_USERNAME`, `BOOTSTRAP_ADMIN_PASSWORD` và `BOOTSTRAP_ADMIN_FULL_NAME`.

Request đăng ký sai định dạng trả HTTP 400 với `detail` tổng quát và object `errors` theo tên field, ví dụ:

```json
{
  "status": 400,
  "detail": "Vui lòng kiểm tra lại thông tin đã nhập.",
  "errors": {
    "phoneNumber": "Số điện thoại không đúng định dạng.",
    "password": "Mật khẩu phải có từ 8 đến 128 ký tự."
  }
}
```

Form đăng ký hiển thị hướng dẫn tiếng Việt ngay trên kiểm tra native của trình duyệt; lỗi field từ API cũng được liệt kê thay vì trả một thông báo chung. Password phải đạt validation field và không vượt quá 72 byte UTF-8 do giới hạn BCrypt. Các lỗi nghiệp vụ dùng HTTP 400/409/401 có `detail` giải thích.

`/api/admin/users/**` chưa được triển khai. Thay đổi trạng thái user, verification, reset password, refresh/revocation và chính sách chống brute force còn ngoài phạm vi hiện tại.
