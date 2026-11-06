# Profile Service

Profile sở hữu hồ sơ khách hàng, hồ sơ nhà cung cấp, trạng thái duyệt/suspension và rating projection; Admin thao tác qua endpoint của service này, không có `admin-service`. Chạy mặc định ở port 8085 với schema riêng `doan1_profile`.

Provider profile cũ được import từ Marketplace qua API nội bộ khi Profile DB chưa có hồ sơ. Profile không truy cập DB Marketplace. Customer profile lưu tên hiển thị, điện thoại và địa chỉ mặc định.

Profile tra trạng thái account qua Identity và phục vụ provider lookup/eligibility cho Booking/Catalog. Review service phát `ReviewSubmitted`; rating projection được cập nhật theo review ID để xử lý lặp an toàn.

Schema được quản lý bằng Flyway, Hibernate `ddl-auto: validate`. Xem [API](./API.md), [database](./DATABASE.md), [architecture](./ARCHITECTURE.md).
