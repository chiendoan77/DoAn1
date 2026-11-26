# UI design

Phân khu public/customer/provider/admin là UX điều hướng, không tạo ranh giới backend domain mới.

## Đã triển khai

- Design tokens Tailwind cho thương hiệu, trạng thái, khoảng cách, bán kính, bóng và focus.
- Shared UI: `Button`, `Badge`, `Dialog`, `ConfirmDialog`, `Toast`, `Skeleton`, `EmptyState`.
- `lucide-react` thay icon ký tự Unicode.
- `SessionProvider` dùng chung dữ liệu `/me`; Header hỗ trợ điều hướng theo vai trò và menu di động.
- ConfirmDialog cho ngừng gói, duyệt/từ chối gói/hồ sơ, cập nhật lịch hẹn và gửi đánh giá.
- Luồng đặt lịch bổ sung địa chỉ thực hiện, mô tả công việc và tóm tắt trước gửi.
- Route fallback `/account`, `/provider/profile`, `/admin/accounts` chuyển về trang tài khoản/quản trị phù hợp.

## Auth desktop

- `/register` và `/login` dùng cùng bố cục hai cột, 50/50 trên desktop, panel xanh sát mép phải.
- Form auth căn giữa trong cột trái với chiều rộng tối đa 480px.
- Register dùng 2 cột cho cặp Họ tên/Tên đăng nhập và Email/Số điện thoại; role card 68px và trạng thái chọn có viền xanh 2px cùng dấu tick.
- Input cao 48px, icon nằm trong ô, mật khẩu có nút hiện/ẩn bên trong.
- AuthLayout có header tối giản, tiêu đề panel, 3 benefit cards và trust strip dưới.
- Target desktop: 1366×768, 1440×900 và 1920×1080; không mở rộng layout sang trang Phase 2.
- Input dùng chung bọc icon trong wrapper `relative`, icon giữa theo trục ô, padding trái đồng nhất và autofill không đổi nền.
- `SafeImage` dùng chung fallback icon nhà/dụng cụ khi thiếu ảnh hoặc lỗi tải; card dùng tỉ lệ 16/10 và modal/provider dùng tỉ lệ phù hợp.
- Trang chủ: bỏ khu vực danh mục và nhà cung cấp nổi bật; không đặt ô tìm kiếm bên dưới hero. Ô tìm kiếm và điều hướng danh mục trong header vẫn được giữ. Hỗ trợ mở email `support@nhatot.vn`.
- Trang danh mục: nút chọn nhà cung cấp mở popup đặt lịch, không mở rộng card. Popup hiển thị thông tin provider, các gói dạng card để chọn, ngày/giờ, địa chỉ và mô tả công việc; luồng gửi booking hiện có được giữ nguyên. Khách chưa đăng nhập được nhắc đăng nhập hoặc đăng ký trước khi mở form đặt lịch.
- Popup đặt lịch dùng SafeImage, tóm tắt provider/gói, ô datetime-local theo locale Việt, trạng thái chọn gói, helper trường thiếu, khóa cuộn nền, đóng bằng X/Esc/nền.

## Trạng thái còn tiếp tục

- Một số trang cũ vẫn dùng CSS module `styleN`; dọn toàn bộ cần từng trang chuyển sang shared Tailwind UI.
- Admin hồ sơ và Catalog chưa đồng bộ lý do/timeline đầy đủ nếu backend chưa có trường tương ứng.
- Playwright/axe/Lighthouse chưa có bằng chứng runtime; browser E2E vẫn nằm trong [TEST_CASES.csv](../TEST_CASES.csv).
- Upload ảnh, dark mode, bảng account quản trị, phân trang/lọc đầy đủ và trang provider detail là phần tiếp theo.
