# API integration

Frontend gọi API qua Next.js BFF tại `/api/backend/[...path]`, BFF chuyển tiếp tới API Gateway và giữ JWT trong cookie HttpOnly; token không được lưu trong localStorage hoặc gửi tới client JavaScript. `/api/session` xóa cookie phiên.

- Trình duyệt không gọi trực tiếp microservice hoặc endpoint `/internal/**`.
- Không truy cập database từ frontend.
- `src/lib/api.ts` tập trung DTO và xử lý lỗi; UI tải lại trạng thái chuẩn từ API sau thao tác.
- BFF cần cấu hình Gateway URL; backend còn cần MySQL, `JWT_SECRET` và cùng `SERVICE_API_KEY` cho tích hợp Booking–Marketplace.

Các luồng hiện có: đăng ký/đăng nhập, provider profile, duyệt provider, tạo booking, lịch sử hai phía và review. Admin user/booking, payment và các policy booking nâng cao chưa có UI/API hoàn chỉnh.
