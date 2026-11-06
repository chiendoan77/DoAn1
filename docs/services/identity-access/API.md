# Identity & Access API

Public API đi qua Gateway. Token được frontend gửi tới BFF; password hash không được trả về client.

| Method | Path | Quyền | Hợp đồng hiện tại |
|---|---|---|---|
| POST | `/api/auth/register` | Public | `{email, phoneNumber, username, fullName, password, role}`; role chỉ `CUSTOMER` hoặc `PROVIDER`. |
| POST | `/api/auth/login` | Public | `{identifier, password}`; identifier là email hoặc số điện thoại. Trả access token, refresh token, TTL và account DTO. |
| POST | `/api/auth/refresh` | Public | `{refreshToken}`; rotate refresh token và phát cặp token mới. |
| POST | `/api/auth/logout` | Public | `{refreshToken}`; thu hồi refresh-token family. |
| GET | `/api/me` | Authenticated | Trả account DTO hiện tại. |
| PATCH | `/api/admin/users/{accountId}/status` | ADMIN | Body `{ "status": "ACTIVE|SUSPENDED", "reason": "..." }`; ghi audit khi status đổi. |
| GET | `/internal/accounts/{accountId}/status` | Profile API key | Trả active status; endpoint không qua Gateway. |

Login từ chối account không ACTIVE. JWT dùng RS256 với `JWT_PRIVATE_KEY_BASE64` ở Identity và `JWT_PUBLIC_KEY_BASE64` ở Gateway/services; token có issuer/audience, access TTL mặc định 900 giây và refresh TTL mặc định 30 ngày. Profile gọi internal status endpoint với API key riêng.

Bootstrap Admin tùy chọn bằng `BOOTSTRAP_ADMIN_ENABLED` và các biến `BOOTSTRAP_ADMIN_*`. Logout không thu hồi access token đã phát; email/phone verification, reset password và chống brute force chưa triển khai.
