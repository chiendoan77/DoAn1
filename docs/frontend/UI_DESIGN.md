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

## Trạng thái còn tiếp tục

- Một số trang cũ vẫn dùng CSS module `styleN`; dọn toàn bộ cần từng trang chuyển sang shared Tailwind UI.
- Admin hồ sơ và Catalog chưa đồng bộ lý do/timeline đầy đủ nếu backend chưa có trường tương ứng.
- Playwright/axe/Lighthouse chưa có bằng chứng runtime; browser E2E vẫn nằm trong [TEST_CASES.csv](../TEST_CASES.csv).
- Upload ảnh, dark mode, bảng account quản trị, phân trang/lọc đầy đủ và trang provider detail là phần tiếp theo.
