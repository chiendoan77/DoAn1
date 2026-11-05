# System context

```text
Khách hàng / Nhà cung cấp / Admin
                |
                v
          Next.js frontend
                |
                v
 API Gateway
   |          |          |          |
Identity  Marketplace  Booking    Payment
```

Kiến trúc bốn business service đã được chọn. Admin là actor sử dụng frontend và API domain, không phải deployable riêng. Provider thanh toán cụ thể, email/SMS và object storage chưa được chọn.
