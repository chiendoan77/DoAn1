# System context

```text
Customer / Provider / Admin
            |
            v
      Next.js BFF
            |
            v
      API Gateway
       /  |  \
 Identity Profile Catalog Marketplace Booking Review Payment Notification
           \      |       /       |
             MySQL schemas       RabbitMQ
        Catalog/Booking/Review outboxes → Notification/Profile
```

Admin là role/portal, không phải deployable. Profile sở hữu customer/provider profiles và rating projection; Catalog sở hữu categories/packages/prices; Review sở hữu review records; Booking sở hữu booking state; Payment hiện chỉ có adapter skeleton; Notification lưu in-app package/booking notifications.

Gateway xác minh JWT RS256/issuer/audience tại edge cho route cần auth; mỗi service vẫn xác minh role/ownership. Internal APIs không route qua Gateway. Chi tiết trạng thái triển khai xem [DEVELOPMENT_STATUS](../DEVELOPMENT_STATUS.md).
