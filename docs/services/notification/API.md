# Notification API

| Method | Path | Quyền | Mục đích |
|---|---|---|---|
| GET | `/api/notifications/mine` | Authenticated user | Danh sách notification của account hiện tại; ADMIN nhận notification theo role ADMIN. |

Response fields: `id`, `eventType`, nullable `packageId`, nullable `relatedId`/`relatedType` (`PACKAGE` or `BOOKING`), `message`, `createdAt`. Gateway xác minh JWT; service lấy recipient từ event và không nhận recipient ID từ request.

Không có API đánh dấu đã đọc/xóa notification hoặc gửi email/push/SMS. `BookingCompleted` tạo thông báo cho customer và provider; package lifecycle events giữ nguyên.
