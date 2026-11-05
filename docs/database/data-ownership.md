# Data ownership

| Dữ liệu | Owner đề xuất | Không được |
|---|---|---|
| Tài khoản, credential, role | Identity & Access | Domain khác đọc trực tiếp credential storage. |
| Provider profile, offering, category, giá | Marketplace | Booking tự sửa catalog/provider state. |
| Booking, trạng thái và lịch sử | Booking | Marketplace/Identity ghi trực tiếp booking. |
| Payment transaction/provider attempts | Payment | Booking/client tự xác nhận kết quả payment. |
| Review hai chiều | Chưa chốt; Booking là ứng viên vì phụ thuộc booking completion | Ghi chéo vào DB domain khác. |
| Complaint | Chưa chốt; repeat booking review có thể cần complaint context | Lộ complaint/PII qua domain không có quyền. |

ID tham chiếu liên domain không tạo foreign key giữa database. Giao tiếp và snapshot strategy cần quyết định trước khi schema.
