# Entity relationship (khái niệm)

Các quan hệ hiện có và mục tiêu phải được phân biệt; đây không phải ERD sinh từ migration.

```text
Account 1 --- 0..1 ProviderProfile
Account (customer) 1 --- * Booking
ProviderProfile 1 --- * Booking
Booking 1 --- * BookingStatusHistory
Booking 1 --- 0..* PaymentTransaction (nếu payment được duyệt)
Booking 1 --- 0..* ProviderReview (Marketplace lưu review; Booking xác minh eligibility)
```

`Account`, `ProviderProfile`, `Booking` và `ProviderReview` phản ánh các domain đã có một phần implementation. `ServiceOffering`, `BookingStatusHistory` và `PaymentTransaction` là entity mục tiêu/chưa có schema. Việc `Booking` tham chiếu `ServiceOffering` chưa được xác nhận; không vẽ quan hệ này như một contract đã chốt.

Cardinality provider account/profile, schedule entities, payment attempts và complaint entities cần được chốt trước migration. Cross-service relations là ID references/API, không DB joins.
