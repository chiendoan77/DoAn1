# Marketplace database

Marketplace sở hữu schema `doan1_marketplace`; Identity/Booking/Profile IDs chỉ lưu tham chiếu, không có foreign key xuyên service. Flyway quản lý schema bằng baseline và Hibernate `ddl-auto: validate`.

| Bảng | Dữ liệu |
|---|---|
| `provider_reviews` | Bảng review legacy; giữ làm nguồn export để import một lần sang Review service. |
| `provider_profiles`, `provider_profile_categories` | Legacy provider-profile data retained for migration; Profile is the current owner. |

Review mới không được ghi vào Marketplace. Migration export dùng internal API và API key; việc import dữ liệu legacy phải được vận hành chủ động và kiểm tra số lượng trước/sau. Package, category và giá thuộc Catalog, không nằm trong schema Marketplace.
