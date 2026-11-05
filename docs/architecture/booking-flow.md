# Booking flow (đề xuất)

1. Khách hàng chọn offering/provider và gửi thời điểm, địa chỉ, mô tả qua API.
2. Booking domain xác thực người dùng, tham chiếu offering/provider hợp lệ và kiểm tra slot overlap.
3. Booking tạo bản ghi ở trạng thái ban đầu; trả DTO và mã booking.
4. Provider được gán xem và chấp nhận/từ chối theo quyền/state transition; provider chỉ bận ở slot bị overlap.
5. Booking domain ghi nhận các thay đổi trạng thái và cung cấp lịch sử cho khách hàng/provider/Admin theo authorization.
6. Hủy, thanh toán, hoàn thành, review và khiếu nại đi theo chính sách riêng sau khi được duyệt.

Mỗi khách có tối đa hai booking đang mở cùng category. Lần hai cảnh báo và chuyển Admin review dựa trên complaint context của booking trước; lần ba bị chặn. Chưa định nghĩa status “đang mở”, Admin hold/approval behavior, slot duration/boundaries, provider assignment, payment timing hoặc cancellation/refund.
