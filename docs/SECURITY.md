# Baseline bảo mật

Đây là yêu cầu thiết kế đề xuất; cần kiểm thử trước khi xem là hoàn tất.

- Băm mật khẩu bằng cơ chế chuyên dụng cho password (không mã hóa thuận nghịch/không lưu plaintext).
- Không commit secret, token, credential hoặc dữ liệu cá nhân thật; cấu hình bằng environment/secret store theo môi trường.
- Backend xác thực và phân quyền tại mỗi API; kiểm tra role lẫn ownership của tài nguyên.
- Nếu dùng JWT: xác thực chữ ký, issuer/audience/expiry theo cấu hình; không tin role do client tự gửi.
- Validate dữ liệu vào, giới hạn loại/kích thước upload nếu có; không cho client gửi trạng thái nội bộ tùy ý.
- Không expose database và port service nội bộ ra Internet.
- Không log mật khẩu, token hoặc dữ liệu thanh toán nhạy cảm.
- Admin actions cần giới hạn quyền; audit log có cần thiết và thời hạn lưu nào cần được chốt.
- Admin Portal xác thực bằng JWT với role `ADMIN`. API key dành riêng cho server-to-server, có scope/rotation/revocation; tuyệt đối không nhúng key trong browser.
- Payment callbacks phải xác minh provider signature, có idempotency và không tin client redirect là bằng chứng thanh toán.
- Thiết kế CORS, CSRF/cookie-token, rate limiting, TLS và quản lý session sau khi chọn cách triển khai/xác thực.

Chưa có threat model, security test hoặc production configuration.
