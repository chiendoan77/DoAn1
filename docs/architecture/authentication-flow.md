# Authentication flow (hiện trạng)

1. Browser gửi auth qua Next.js BFF; BFF giữ access/refresh token trong cookie HttpOnly và không trả token cho JavaScript.
2. Identity xác minh BCrypt password, phát access JWT RS256 và refresh token opaque; account không ACTIVE không đăng nhập được.
3. BFF gọi refresh/rotate rồi retry một lần khi upstream trả 401. Logout thu hồi refresh-token family.
4. Gateway xác minh chữ ký, issuer và audience trên protected routes; service domain tiếp tục kiểm tra role và ownership.
5. Admin Portal dùng JWT có role `ADMIN`; API keys riêng chỉ dùng cho server-to-server integrations, không đưa vào browser.

**Giới hạn/còn thiếu:** Access JWT stateless còn hiệu lực đến TTL sau logout. API key rotation/expiry, mTLS/service tokens, rate limit, central audit, production secret management và CSRF/threat-model review chưa hoàn tất. Compose defaults chỉ dành cho local development, không dùng production.
