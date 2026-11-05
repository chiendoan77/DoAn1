# Frontend architecture

Frontend dùng Next.js App Router: `src/app/` chứa routes/BFF, `src/features/` nhóm giao diện theo auth, admin, booking và marketplace, `src/shared/components/` chứa UI dùng chung, `src/lib/` giữ API types/client.

UI hiện tại có auth, public provider browsing, booking/history/review và Admin provider approval; offering management chưa có. Cấu trúc này không thay đổi framework hoặc BFF/API boundaries.

Frontend chỉ gọi public API entry. UI gating theo role chỉ hỗ trợ UX; backend là nơi quyết định authorization. Không đưa secret vào client bundle.
