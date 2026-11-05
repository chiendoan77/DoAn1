# Payment service

Service riêng đã được người dùng chọn ở mức kiến trúc; Spring Boot app skeleton và adapter registry đã được tạo nhưng chưa có provider tích hợp thật. Payment adapters phải cho phép nhiều phương thức/provider hoạt động đồng thời và không tắt toàn bộ Payment module khi bổ sung adapter mới.

## Java package layout

The current adapter registry and provider contract are under `com.doan1.payment.provider`; the Spring Boot entry point remains in the root package. Tests remain in the separate `src/test/java` tree.
