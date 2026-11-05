# Identity & Access database

Identity sở hữu schema `doan1_identity` (mặc định trong cấu hình local); service khác không đọc trực tiếp schema này.

| Bảng | Dữ liệu chính |
|---|---|
| `accounts` | UUID, email, phone number, username, full name, BCrypt password hash, role (`CUSTOMER`/`PROVIDER`/`ADMIN`), thời điểm tạo. Email, phone và username có unique constraint. |

Hibernate dùng `ddl-auto: update` khi chạy; chưa có migration được quản lý phiên bản. Test dùng H2; schema MySQL cần được tạo/cấu hình trước khi chạy local.
