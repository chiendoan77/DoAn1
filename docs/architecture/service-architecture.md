# Service architecture (ứng viên)

| Deployable/domain | Trách nhiệm | Dữ liệu sở hữu dự kiến | Admin liên quan | Tách riêng khi |
|---|---|---|---|---|
| Identity & Access | Tài khoản, credential, role/authorization | Account, credentials, role assignments | Khóa/tra cứu user theo quyền | Có domain identity rõ và microservices được yêu cầu |
| Marketplace | Provider profile, service offering/catalog, giá, duyệt đối tác | Provider, category, offering, moderation state | Duyệt provider/danh mục | Ownership, thay đổi hoặc vận hành độc lập |
| Booking | Tạo/điều phối booking và state machine công việc | Booking, status history, schedule allocation | Tra cứu/xử lý booking | Booking cần scale/ownership/deploy độc lập |
| Payment | Payment transaction, provider adapters, callback/status handling | Payment transaction/attempt, provider references | Tra cứu giao dịch theo permission | Đã chọn service riêng để tích hợp nhiều provider |
| API Gateway | Routing và cross-cutting edge policy | Không sở hữu dữ liệu nghiệp vụ | Không phải admin backend | Khi có nhiều backend service và cần edge routing |

Review hai chiều đã vào scope nhưng chưa có service riêng; complaint workflow và notification chưa được chốt. Payment service đã được chọn; provider/method cụ thể và payment lifecycle còn mở.
