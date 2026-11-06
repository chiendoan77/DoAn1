# Baseline bảo mật

Đây là baseline bảo mật; các điểm hiện trạng bên dưới chưa được xác minh như production-ready.

- Băm mật khẩu bằng cơ chế chuyên dụng cho password (không mã hóa thuận nghịch/không lưu plaintext).
- Không commit secret, token, credential hoặc dữ liệu cá nhân thật; cấu hình bằng environment/secret store theo môi trường.
- Backend xác thực và phân quyền tại mỗi API; kiểm tra role lẫn ownership của tài nguyên.
- Nếu dùng JWT: xác thực chữ ký, issuer/audience/expiry theo cấu hình; không tin role do client tự gửi.
- Validate dữ liệu vào, giới hạn loại/kích thước upload nếu có; không cho client gửi trạng thái nội bộ tùy ý.
- Không expose database và port service nội bộ ra Internet.
- Không log mật khẩu, token hoặc dữ liệu thanh toán nhạy cảm.
- Admin actions cần giới hạn quyền; audit log có cần thiết và thời hạn lưu nào cần được chốt.
- Hiện Identity ghi audit khi Admin đổi account status; chưa có audit cho mọi Catalog/Profile admin action.
- Identity ký JWT bằng RS256; Gateway và services xác minh bằng public key và kiểm tra issuer/audience. Role/ownership vẫn phải được xác minh tại service sở hữu API.
- RabbitMQ integration hiện dùng API credentials qua environment; mTLS/service tokens và rotation chưa triển khai.
- Admin Portal xác thực bằng JWT với role `ADMIN`. API key dành riêng cho server-to-server, có scope/rotation/revocation; tuyệt đối không nhúng key trong browser.
- Payment callbacks phải xác minh provider signature, có idempotency và không tin client redirect là bằng chứng thanh toán.
- Thiết kế CORS, CSRF/cookie-token, rate limiting, TLS và quản lý session sau khi chọn cách triển khai/xác thực.

Refresh token có rotation/revoke theo token family; access JWT stateless vẫn có hiệu lực tới hết TTL sau logout. Rate limiting, API key rotation, production TLS/secret store, mTLS/service identity, central audit và threat model còn thiếu. Compose credentials chỉ dành cho local.

### Tạo khóa RS256 cho môi trường local

Tại PowerShell ở repository root, tạo keypair RSA 3072-bit và lưu Base64 DER vào `.env` (file được Git ignore):

```powershell
$rsa = [System.Security.Cryptography.RSA]::Create(3072)
$privateKey = [Convert]::ToBase64String($rsa.ExportPkcs8PrivateKey())
$publicKey = [Convert]::ToBase64String($rsa.ExportSubjectPublicKeyInfo())
@("JWT_PRIVATE_KEY_BASE64=$privateKey", "JWT_PUBLIC_KEY_BASE64=$publicKey") |
  Set-Content -Path .env -Encoding ascii
$rsa.Dispose()
```

Identity nhận PKCS#8 private key; Gateway/services chỉ nhận X.509 public key. Không dùng keypair này ở production; dùng secret manager và cơ chế rotation phù hợp.
