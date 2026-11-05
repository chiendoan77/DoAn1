# Tài liệu dự án

Tài liệu ghi nhận yêu cầu đã xác nhận, quyết định kiến trúc và implementation status. Những chi tiết chưa được xác nhận được đánh dấu **Đề xuất** hoặc **Cần xác nhận**; xem `DEVELOPMENT_STATUS.md` và service docs để phân biệt yêu cầu với chức năng đã kiểm thử.

## Tài liệu chính

- [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md): cây thư mục và phân cấp source code đang có trong repository.
- [PROJECT_OVERVIEW.md](./PROJECT_OVERVIEW.md): mục tiêu và hiện trạng repository.
- [REQUIREMENTS.md](./REQUIREMENTS.md): yêu cầu tổng hợp và trạng thái xác nhận.
- [ARCHITECTURE.md](./ARCHITECTURE.md): kiến trúc bốn service đã chọn.
- [SYSTEM_DESIGN.md](./SYSTEM_DESIGN.md): luồng và thành phần ở mức hệ thống.
- [DATABASE_DESIGN.md](./DATABASE_DESIGN.md): schema hiện tại và giới hạn migration.
- [API_DOCUMENTATION.md](./API_DOCUMENTATION.md): API đã triển khai và phần còn dự kiến.
- [SECURITY.md](./SECURITY.md): baseline bảo mật đề xuất.
- [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md): quy trình phát triển.
- [TESTING_STRATEGY.md](./TESTING_STRATEGY.md): chiến lược kiểm thử.
- [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md): hướng triển khai dự kiến.
- [DEVELOPMENT_STATUS.md](./DEVELOPMENT_STATUS.md): trạng thái đã kiểm kê.
- [ROADMAP.md](./ROADMAP.md): các phase đề xuất.
- [DECISIONS.md](./DECISIONS.md): quyết định và đề xuất kiến trúc.
- [CHANGELOG.md](./CHANGELOG.md): lịch sử thay đổi tài liệu.

## Phân tích

- `requirements/`: yêu cầu chức năng, phi chức năng, nghiệp vụ và câu hỏi mở.
- `architecture/`: ngữ cảnh, ranh giới service, giao tiếp, xác thực, đặt lịch và triển khai.
- `database/`: ownership và mô hình dữ liệu khái niệm.
- `frontend/`: phạm vi và tích hợp giao diện.
- `services/`: tài liệu domain-service; Identity, Marketplace, Booking có một phần business API, Payment còn ở mức adapter registry.

## Quy ước trạng thái

- **Đã xác nhận**: người dùng đã chấp thuận rõ ràng.
- **Đề xuất**: phương án suy ra từ mô tả dự án, cần chấp thuận trước khi triển khai.
- **Cần xác nhận**: thiếu quyết định nghiệp vụ hoặc kỹ thuật.
- **Chưa triển khai**: feature/domain behavior chưa có implementation evidence; app skeleton hoặc smoke test không đồng nghĩa feature đã hoàn tất.

Khi tài liệu và source code khác nhau, kiểm tra trạng thái thực tế, không xóa hoặc sửa code chỉ để khớp tài liệu.
