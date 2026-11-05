# Booking

Booking sở hữu yêu cầu đặt lịch, snapshot thông tin hai bên và trạng thái công việc.

Đã triển khai tạo booking cho CUSTOMER, lịch sử riêng của customer/provider, kiểm tra quyền provider được gán khi cập nhật, và chuyển trạng thái `REQUESTED → ACCEPTED/REJECTED → COMPLETED`. Booking dùng API nội bộ Marketplace để xác minh provider đã duyệt và lấy identity account ID; không truy cập DB Marketplace.

Booking cung cấp endpoint nội bộ để Marketplace kiểm tra booking hoàn tất và người tham gia trước khi nhận review; endpoint yêu cầu `X-Service-Api-Key`.

Slot conflict, quota booking lặp, Admin booking operations, hủy/đổi, payment và các policy thời gian/giá chưa triển khai. Schema dùng Hibernate `ddl-auto: update`, chưa có migration.

## Java package layout

`src/main/java/com/doan1/booking` separates `controller`, request/response DTOs, `entity`, `enums`, `mapper`, `service`, `repository`, internal clients and security. Tests are isolated in `src/test/java/com/doan1/booking`.
