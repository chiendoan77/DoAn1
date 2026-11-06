# Identity & Access database

Identity sở hữu schema `doan1_identity` (mặc định trong cấu hình local); service khác không đọc trực tiếp schema này.

| Bảng | Dữ liệu chính |
|---|---|
| `accounts` | UUID, email, phone number, username, full name, BCrypt password hash, role (`CUSTOMER`/`PROVIDER`/`ADMIN`), status (`ACTIVE`/`SUSPENDED`), thời điểm tạo. Email, phone và username có unique constraint. |
| `account_status_history` | Account ID, Admin ID, trạng thái cũ/mới, reason và timestamp; tham chiếu ID không tạo foreign key liên service. |
| `refresh_tokens` | Token ID, account ID, family ID, SHA-256 token hash, expiry, revoked time và created time; không lưu raw refresh token. |

Schema `doan1_identity` dùng Flyway và Hibernate `ddl-auto: validate`. Migration V1 tạo accounts; V2 thêm status với default ACTIVE; V3 tạo audit history; V4 tạo refresh-token storage; V5 đổi `refresh_tokens.token_hash` sang `VARCHAR(64)` để khớp mapping JPA. Test dùng H2.
