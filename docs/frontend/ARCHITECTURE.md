# Frontend architecture

Frontend dùng Next.js App Router: `src/app/` chứa routes/BFF, `src/features/` nhóm giao diện theo auth, admin, booking, catalog, marketplace và notifications, `src/shared/components/` chứa UI dùng chung, `src/lib/` giữ API types/client.

UI hiện tại có auth, public provider/package browse, Provider package management, Admin provider/package moderation, booking/history/review và in-app notification list. Booking chưa đặt package cụ thể hoặc hiển thị price snapshot.

Frontend chỉ gọi public API entry. UI gating theo role chỉ hỗ trợ UX; backend là nơi quyết định authorization. Không đưa secret vào client bundle.
