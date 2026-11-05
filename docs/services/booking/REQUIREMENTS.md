# Requirements — Booking

> Ranh giới/core rules đã được người dùng chọn. Booking CRUD cơ bản, participant history, state transitions và API eligibility review đã triển khai; các policy còn mở không được suy diễn.

## 1. Giới thiệu

Booking quản lý yêu cầu sử dụng dịch vụ, slot, vòng đời công việc và các thao tác Admin liên quan.

## 2. Mục tiêu và phạm vi

Cho khách tạo/tra cứu booking; provider xử lý booking được gán; Admin tra cứu/xử lý theo quyền. Tạo/lịch sử cơ bản và provider state transitions đã triển khai. Slot overlap và quota booking lặp là yêu cầu được duyệt nhưng chưa triển khai.

## 3. Trách nhiệm nghiệp vụ

- Sở hữu booking; schedule allocation và status history chưa triển khai.
- Kiểm tra state transition và provider/customer ownership; slot conflict chưa triển khai.
- Repeat-category booking limit/Admin review là yêu cầu đã duyệt, chưa triển khai.
- Booking là source of truth cho trạng thái/participants và xác minh review eligibility; Marketplace lưu review.

## 4. Chức năng thuộc phạm vi

Đã triển khai tạo booking, customer/provider history và provider accept/reject/complete. Slot conflict và repeat booking warning/review/quota chưa triển khai. Hủy/đổi chưa có; Marketplace sở hữu review sau eligibility check từ Booking.

## 5. Chức năng ngoài phạm vi

Credential, provider profile/catalog/price source of truth, Payment ledger và DB access sang service khác.

## 6. Actors

Khách hàng, provider được phân công, Admin; Marketplace/Identity/Payment qua API/DTO.

## 7. Functional Requirements

- `BOOK-FR-001` (Đã xác nhận): khách hàng tạo booking với offering, ngày/khung giờ, địa chỉ và mô tả.
- `BOOK-FR-002` (Đã xác nhận): provider xử lý booking được phân công qua state transition hợp lệ.
- `BOOK-FR-003` (Đã xác nhận): khách/provider xem booking thuộc phạm vi quyền; Admin tra cứu/xử lý.
- `BOOK-FR-004` (Đã xác nhận): chỉ slot overlap mới gây provider schedule conflict.
- `BOOK-FR-005` (Đã xác nhận): tối đa hai booking đang mở cùng category/customer; lần hai cảnh báo + Admin review dựa trên complaint context lần trước; lần ba bị chặn.
- `BOOK-FR-006` (Đã xác nhận): customer/provider có thể review nhau sau booking completed, tối đa một review mỗi bên/booking; Booking xác minh eligibility, Marketplace lưu review.
- `BOOK-FR-007` (Đề xuất): đổi/hủy booking theo policy được xác nhận.

## 8. Non-functional Requirements

Không nhận status tùy ý từ client; validate request; tránh lộ booking/complaint ngoài quyền; booking conflict và quota update cần atomicity trong Booking DB. Chưa đặt tải/latency target.

## 9. Business Rules

- Booking phải tham chiếu offering/provider hợp lệ, kiểm tra qua Marketplace API/DTO.
- Provider chỉ bận ở các interval bị overlap; booking interval/timezone/boundaries cần chốt.
- Tối đa hai booking “đang mở” cùng category/customer; định nghĩa open state/terminal state cần chốt.
- Lần hai báo khách và đưa Admin xem complaint context của booking trước; lần ba bị từ chối nếu quota còn đủ điều kiện đếm.
- Không sử dụng transaction database xuyên service.

## 10. Use Cases

- `BOOK-UC-01`: khách tạo booking.
- `BOOK-UC-02`: provider nhận/từ chối/cập nhật/hoàn thành booking.
- `BOOK-UC-03`: customer/provider/Admin xem booking theo quyền.
- `BOOK-UC-04`: phát hiện slot overlap.
- `BOOK-UC-05`: repeat booking cùng category được cảnh báo và chuyển Admin review.
- `BOOK-UC-06`: Booking xác minh điều kiện; hai bên gửi review qua Marketplace sau completed.

## 11. Input / Output

Tạo booking nhận providerId, categoryId, scheduledAt, address và description. Response gồm booking ID, snapshot category/provider, thời gian/địa chỉ/mô tả và status. Warning/review-required result cho booking lặp chưa được triển khai.

## 12. API endpoints dự kiến

- `POST /api/bookings`
- `GET /api/bookings/mine`
- `PATCH /api/provider/bookings/{bookingId}/status`
- `GET /internal/bookings/{bookingId}/review-eligibility` (API key; Marketplace gọi)
- Admin query/review actions theo quyền.

## 13. Request / Response

Request hiện dùng `providerId`, `categoryId`, `scheduledAt`, `address`, `description`; status ban đầu là `REQUESTED`. Các policy quota, warning, Admin hold và complaint context chưa có contract/implementation.

## 14. Xác thực và phân quyền

Tạo booking yêu cầu CUSTOMER; provider actions yêu cầu provider được gán; Admin APIs yêu cầu JWT/ADMIN. Server-to-server API key nếu cần chỉ cho internal integrations, scoped và không expose cho browser. Complaint context chỉ cho role được duyệt.

## 15. Dữ liệu service sở hữu

Booking ID, customer/provider/category references, scheduledAt, address/request snapshot và current status. Không lưu review/admin-review reference trong schema hiện tại.

## 16. Database schema dự kiến

Entity hiện tại: `Booking` trong bảng `bookings`. `BookingStatusHistory`/`ScheduleAllocation`, unique/locking cho slot/quota và migrations chưa có.

## 17. Quan hệ với service khác

Identity cung cấp claims; Marketplace xác thực provider/offering/category; Payment sở hữu payment state. Không có FK/join trực tiếp sang DB khác.

## 18. Cách giao tiếp

REST/DTO đồng bộ với Marketplace để xác minh provider và kiểm tra review eligibility; không transaction xuyên service. Gateway là public entry, internal endpoints không qua Gateway. Payment integration và broker chưa có.

## 19. Events phát ra/nhận vào

Chưa dùng broker. Booking state events có thể thêm khi Notifications/Payment integration yêu cầu và có idempotency/retry contract.

## 20. Lỗi và ngoại lệ

Validation error, unauthenticated, forbidden, not found, invalid transition, slot conflict, repeat quota reached, pending Admin review, dependency unavailable. Không để lộ complaint/tài nguyên ngoài quyền.

## 21. Acceptance Criteria

- Booking hợp lệ tạo đúng status và quyền sở hữu.
- Overlap slot bị xử lý conflict; slot không trùng không bị khóa toàn provider.
- Booking category lần hai cảnh báo và chuyển Admin review; lần ba bị chặn theo open-state policy đã duyệt.
- Chuyển status trái phép không thay đổi state.
- Review eligibility chỉ trả true sau completion cho đúng customer/provider participant; Marketplace giới hạn một review mỗi reviewer/booking.

## 22. Test Cases

- Unit: state machine, interval overlap, category quota/warning.
- Integration: simultaneous requests không vượt quota/slot uniqueness, ownership, Admin review transition.
- API: create/read/accept/reject/complete/forbidden.
- Review: incomplete booking denied, each side once, duplicate rejected.

## 23. Vấn đề chưa xác nhận

Provider selection/assignment; open/terminal statuses; slot duration/timezone/boundary; Admin review hold/approve/reject; complaint visibility/content; category ID and quota release after cancel/reject/complete; cancellation/reschedule; Review edit/delete/moderation; pricing/payment timing.

## 24. Chức năng chưa triển khai

Đã triển khai: tạo booking sau khi Marketplace xác minh provider; lịch sử theo customer/provider; provider status transitions có kiểm tra ownership; endpoint eligibility review yêu cầu API key, COMPLETED và participant; persistence/tests. Chưa triển khai: slot conflict, repeat-category quota/Admin review, admin booking APIs, cancel/reschedule, status history và migration scripts.
