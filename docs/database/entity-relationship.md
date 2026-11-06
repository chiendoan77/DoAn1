# Quan hệ dữ liệu (khái niệm)

Sơ đồ sau biểu diễn các quan hệ logic giữa các service, không phải ERD/FK vật lý. Mỗi service sở hữu schema riêng; các đường nối qua API/event hoặc ID snapshot, không qua join/foreign key liên database.

```text
Identity.Account 1 --- 0..1 Profile.CustomerProfile
Identity.Account 1 --- 0..1 Profile.ProviderProfile
Profile.ProviderProfile 1 --- * Catalog.ServicePackage
Catalog.Category 1 --- * Catalog.ServicePackage
Identity.CustomerAccount 1 --- * Booking.Booking
Profile.ProviderProfile 1 --- * Booking.Booking
Catalog.ServicePackage 1 --- * Booking.Booking (package ID + immutable booking snapshot)
Booking.Booking 1 --- 0..* Review.Review (eligibility via Booking API; record owned by Review)
Review.Review * --- 1 Profile.RatingProjection (ReviewSubmitted event; idempotent by review ID)
Catalog/Booking/Review.OutboxEvent * --- Notification.Inbox (RabbitMQ event ID; no database FK)
```

`BookingStatusHistory`, schedule allocation, Payment transaction/attempt và complaint entities chưa có schema triển khai. Booking giữ package, category và participant snapshots để lịch sử không đổi theo hồ sơ/gói hiện tại. Rating projection thuộc Profile; nội dung review thuộc Review.

Cardinality provider account/profile và các policy booking/payment còn phụ thuộc yêu cầu mở. Không tạo quan hệ vật lý xuyên service.
