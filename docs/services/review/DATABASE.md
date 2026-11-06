# Review database

Review sở hữu schema riêng `doan1_review`, Flyway migrations và Hibernate `ddl-auto: validate`. Các ID Booking/account/target là tham chiếu, không có foreign key xuyên service.

| Bảng | Dữ liệu |
|---|---|
| `reviews` | Booking, reviewer/role, target/role, rating, comment, timestamp; unique `(booking_id, reviewer_account_id)`. |
| `review_outbox_events` | Event ID, review aggregate ID, event type/payload, publish status và timestamps. |

Không lưu rating projection trong Review; Profile sở hữu projection riêng và xử lý trùng event theo review ID.
