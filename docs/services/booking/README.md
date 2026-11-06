# Booking

Booking sở hữu yêu cầu đặt lịch, snapshot thông tin hai bên và trạng thái công việc.

Đã triển khai tạo booking cho CUSTOMER theo package đã duyệt, lịch sử riêng của customer/provider, kiểm tra quyền provider được gán khi cập nhật, và chuyển trạng thái `REQUESTED → ACCEPTED/REJECTED → COMPLETED`. Booking lấy gói/giá từ Catalog, kiểm tra provider qua Profile và lưu snapshot tên, giá, thời lượng; không truy cập database service khác. Khi hoàn tất, outbox phát `BookingCompleted`.

Booking cung cấp endpoint nội bộ để Review kiểm tra booking hoàn tất và người tham gia trước khi nhận review; endpoint yêu cầu `X-Service-Api-Key`.

Slot conflict, quota booking lặp, Admin booking operations, hủy/đổi, payment và các policy thời gian chưa triển khai. Schema dùng Flyway với baseline và Hibernate `ddl-auto: validate`; cần dry-run nâng cấp trên bản sao dữ liệu hiện hữu trước rollout.

## Java package layout

`src/main/java/com/doan1/booking` separates `controller`, request/response DTOs, `entity`, `enums`, `mapper`, `service`, `repository`, internal clients and security. Tests are isolated in `src/test/java/com/doan1/booking`.
