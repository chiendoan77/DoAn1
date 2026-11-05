# Frontend architecture

Tiếp tục tổ chức theo feature/domain (auth, marketplace, booking, admin capabilities), dùng shared UI và API client có typed DTO. Scaffold hiện dùng Next.js App Router; các feature folders và API client sẽ được thêm cùng business flows.

Frontend chỉ gọi public API entry. UI gating theo role chỉ hỗ trợ UX; backend là nơi quyết định authorization. Không đưa secret vào client bundle.
