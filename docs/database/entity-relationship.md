# Entity relationship (khái niệm)

Quan hệ dưới đây là giả thuyết cho thảo luận, không phải schema:

```text
Account 1 --- 0..1 ProviderProfile
ProviderProfile 1 --- * ServiceOffering
ServiceOffering 1 --- * Booking
Account (customer) 1 --- * Booking
ProviderProfile 1 --- * Booking
Booking 1 --- * BookingStatusHistory
Booking 1 --- 0..* PaymentTransaction (nếu payment được duyệt)
Booking 1 --- 0..1 Review (nếu review được duyệt)
```

Cardinality provider account/profile, giá snapshot, schedule entities, payment attempts và complaint entities phải được chốt trước migration. Cross-service relations là ID references/API, không DB joins.
