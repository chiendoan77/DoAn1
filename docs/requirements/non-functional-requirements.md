# Yêu cầu phi chức năng

Tất cả nội dung dưới đây là baseline đề xuất; các mục không có ngưỡng đo được không được diễn giải thành SLA.

| ID | Yêu cầu | Trạng thái / xác minh |
|---|---|---|
| NFR-001 | Kiến trúc và cách chạy phải vừa sức đồ án cá nhân, ưu tiên số thành phần vận hành nhỏ. | Định hướng; review kiến trúc và hướng dẫn local. |
| NFR-002 | Frontend gọi API qua một điểm vào công khai; không truy cập DB hoặc service nội bộ. | Đề xuất; kiểm tra cấu hình và E2E. |
| NFR-003 | Backend kiểm tra xác thực, vai trò và quyền sở hữu tài nguyên cho mọi thao tác nhạy cảm. | Đề xuất; security/integration tests. |
| NFR-004 | Mật khẩu phải được lưu bằng thuật toán băm mật khẩu phù hợp; secrets không commit vào source. | Yêu cầu nền tảng bảo mật. |
| NFR-005 | Các API có đầu vào phải validate và trả lỗi nhất quán, không làm lộ thông tin nhạy cảm. | Đề xuất; API tests. |
| NFR-006 | Các luồng booking có test đơn vị và tích hợp cho chuyển trạng thái, phân quyền và lỗi nghiệp vụ. | Đề xuất; test report. |
| NFR-007 | Giao diện hỗ trợ desktop, tablet và mobile. | Đề xuất; breakpoint/tiêu chí UX cần chốt. |
| NFR-008 | Cấu hình môi trường tách khỏi source code; local setup có thể lặp lại. | Đề xuất; hướng dẫn chạy và kiểm tra thực tế. |
| NFR-009 | Chỉ thêm message broker, Redis, object storage hoặc service mới khi có use case và lợi ích được xác nhận. | Ràng buộc kiến trúc. |

Chưa xác định: tải đồng thời, latency, uptime, backup/retention, thời gian lưu ảnh, khả năng truy cập, logging/monitoring mục tiêu. Không đặt ngưỡng số liệu khi chưa có yêu cầu.
