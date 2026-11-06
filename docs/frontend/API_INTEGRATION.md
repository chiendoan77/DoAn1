# API integration

Frontend gọi API qua Next.js BFF tại `/api/backend/[...path]`, BFF chuyển tiếp GET/POST/PUT/PATCH/DELETE tới API Gateway và giữ access/refresh tokens trong HttpOnly cookies; token không được lưu trong localStorage hoặc gửi tới client JavaScript. Khi access token hết hạn, BFF rotate refresh token rồi retry request một lần; `/api/session` gọi logout và xóa cookie phiên.

- Trình duyệt không gọi trực tiếp microservice hoặc endpoint `/internal/**`.
- Không truy cập database từ frontend.
- `src/lib/api.ts` tập trung DTO và xử lý lỗi; UI tải lại trạng thái chuẩn từ API sau thao tác.
- BFF cần cấu hình Gateway URL; backend còn cần MySQL, RS256 keypair, RabbitMQ và API keys riêng cho từng tích hợp nội bộ.

Các luồng hiện có: đăng ký/đăng nhập/refresh/logout, provider/customer profiles, browse provider/package, Provider package management, Admin moderation/history, tạo booking từ package, history/review và in-app booking/package notifications. Payment và policy booking nâng cao chưa có UI/API hoàn chỉnh.
