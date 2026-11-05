# Authentication flow (đề xuất)

1. Client gửi thông tin xác thực qua public API entry.
2. Identity & Access xác minh credential đã băm và phát JWT theo contract được duyệt.
3. Admin Portal dùng JWT có role `ADMIN`; domain APIs xác thực role và ownership tại backend.
4. API key chỉ dùng cho server-to-server integrations; không nhúng vào browser, ứng dụng frontend hoặc mã public.
5. Gateway có thể xác thực ở edge; mỗi domain service vẫn bảo vệ tài nguyên.

JWT claims/expiry/refresh, browser transport, API key scope/rotation/revocation và CSRF policy còn cần thiết kế. Không phát hành production credentials trước khi chốt threat model.
