# CATALOG_SERVICE_IMPLEMENTATION.md
# DOAN1 – Catalog Service Implementation Specification

## 1. VAI TRÒ

Bạn là Senior Backend Engineer và Software Architect chuyên về:

- Java Spring Boot
- Spring Security
- Spring Cloud Gateway
- Maven Multi-module
- MySQL
- REST API
- RabbitMQ
- Docker
- Domain-Driven Design (DDD)
- Microservices Architecture

Nhiệm vụ của bạn là đọc repository DoAn1 và triển khai Catalog Service theo kiến trúc Microservices đúng nghĩa.

Repository:
https://github.com/chiendoan77/DoAn1

Lưu ý: Nếu repository đã được mở trong workspace, hãy làm việc trực tiếp trên source hiện tại, không clone hoặc tạo project mới không cần thiết.

## 2. MỤC TIÊU

Xây dựng và hoàn thiện Catalog Service chịu trách nhiệm:

1. Quản lý danh mục dịch vụ.
2. Provider tạo và quản lý gói dịch vụ của mình.
3. Provider gửi gói dịch vụ để Admin kiểm duyệt.
4. Admin duyệt hoặc từ chối gói dịch vụ.
5. Quản lý trạng thái và lịch sử kiểm duyệt.
6. Chỉ công khai gói dịch vụ đã được duyệt.
7. Phát sự kiện để Notification Service gửi thông báo.

Quy tắc nghiệp vụ quan trọng nhất:

- Provider tạo package.
- Provider chỉ quản lý package thuộc quyền sở hữu của mình.
- Provider không có quyền duyệt package.
- Admin là người duyệt hoặc từ chối.
- Provider không thể tự thay đổi status thành APPROVED.
- Customer chỉ xem được package hợp lệ đã được duyệt.
- Mọi hành động quan trọng phải được kiểm tra tại Backend.

## 3. KIẾN TRÚC TỔNG THỂ

Hệ thống có các service:

- api-gateway
- identity-service
- profile-service
- catalog-service
- booking-service
- payment-service
- review-service
- notification-service

Không tạo Admin Service riêng.

Admin là ROLE trong Identity Service.

Profile Service quản lý hồ sơ Customer và Provider.

Catalog Service quản lý Category, Service Package và Approval Workflow.

Không được đưa nghiệp vụ của service khác vào Catalog Service.

## 4. PHÂN QUYỀN

Các Role:

- CUSTOMER
- PROVIDER
- ADMIN

### Provider

Được phép:

- Tạo package.
- Xem package của mình.
- Chỉnh sửa package của mình khi được phép.
- Xóa hoặc ngừng hoạt động package theo quy tắc nghiệp vụ.
- Gửi package để Admin kiểm duyệt.
- Xem kết quả duyệt.
- Xem lý do từ chối.
- Chỉnh sửa package bị từ chối và gửi duyệt lại.

Không được phép:

- Duyệt package.
- Từ chối package.
- Tự chuyển package sang APPROVED.
- Sửa package của Provider khác.
- Thay đổi providerId để chiếm quyền sở hữu package.

### Admin

Được phép:

- Xem tất cả package.
- Xem danh sách package chờ duyệt.
- Xem chi tiết package.
- Duyệt package.
- Từ chối package.
- Ghi lý do từ chối.
- Ngừng hoạt động package vi phạm.
- Xem lịch sử kiểm duyệt.

### Customer

Được phép:

- Xem danh sách package công khai.
- Xem chi tiết package đã duyệt.
- Tìm kiếm và lọc package.

Không được xem package DRAFT, PENDING_APPROVAL hoặc REJECTED.

## 5. XÁC MINH PROVIDER

Khi Provider tạo hoặc gửi package:

1. Xác minh JWT.
2. Lấy userId từ claim sub.
3. Kiểm tra ROLE_PROVIDER.
4. Gọi Profile Service để xác minh Provider Profile.
5. Kiểm tra verificationStatus.
6. Kiểm tra trạng thái tài khoản và Provider không bị đình chỉ.
7. Chỉ tiếp tục nếu Provider đủ điều kiện.

Không nhận providerId từ request để xác định chủ sở hữu.

Catalog phải tự gán providerId từ danh tính đã xác minh.

Nếu Profile Service không phản hồi, thao tác cần xác minh phải thất bại an toàn. Không tự động cho phép thao tác.

## 6. PACKAGE STATUS WORKFLOW

Sử dụng các trạng thái:

- DRAFT
- PENDING_APPROVAL
- APPROVED
- REJECTED
- INACTIVE

Luồng:

DRAFT → PENDING_APPROVAL → APPROVED

DRAFT → PENDING_APPROVAL → REJECTED → DRAFT → PENDING_APPROVAL

APPROVED → INACTIVE

INACTIVE → DRAFT khi Provider chỉnh sửa và gửi duyệt lại.

Các quy tắc:

1. Package mới mặc định DRAFT.
2. Chỉ package hợp lệ mới được gửi duyệt.
3. Provider không được sửa package đang PENDING_APPROVAL.
4. Admin chỉ duyệt hoặc từ chối package PENDING_APPROVAL.
5. REJECTED phải có reason.
6. Package sửa nội dung quan trọng sau khi APPROVED phải trải qua duyệt lại.
7. Không được hiển thị nội dung chưa duyệt.
8. Không cho phép chuyển trạng thái tùy ý từ request.
9. Kiểm tra version để chống cập nhật đồng thời.
10. Không cho phép hai Admin đồng thời xử lý một package thành công.

Phải triển khai State Transition rõ ràng trong Service Layer.

Không cho phép Controller trực tiếp thay đổi status.

## 7. DATABASE

Tạo database riêng:

doan1_catalog

Các bảng chính:

### service_packages

- id
- provider_id
- category_id
- name
- description
- price
- duration
- status
- version
- created_at
- updated_at

### categories

- id
- name
- description
- status
- created_at
- updated_at

### package_images

- id
- package_id
- image_url
- display_order

### package_approvals

- id
- package_id
- admin_id
- action
- reason
- package_version
- created_at

### outbox_events

- id
- aggregate_id
- event_type
- payload
- status
- created_at
- published_at

Có thể điều chỉnh schema nếu source hiện tại có cấu trúc phù hợp hơn.

Yêu cầu:

- Mỗi service sở hữu database riêng.
- Không tạo foreign key đến database của service khác.
- provider_id, admin_id chỉ là ID tham chiếu.
- Không sử dụng Entity của Identity hoặc Profile.
- Sử dụng Flyway hoặc Liquibase để quản lý migration.
- Tạo index phù hợp cho provider_id, category_id, status và created_at.
- Sử dụng transaction khi cập nhật package và ghi lịch sử duyệt.
- Dùng optimistic locking hoặc conditional update.

## 8. API CẦN TRIỂN KHAI

### Provider API

POST /api/provider/packages

GET /api/provider/packages/my

GET /api/provider/packages/{id}

PUT /api/provider/packages/{id}

DELETE /api/provider/packages/{id}

POST /api/provider/packages/{id}/submit

### Admin API

GET /api/admin/packages/pending

GET /api/admin/packages/{id}

POST /api/admin/packages/{id}/approve

POST /api/admin/packages/{id}/reject

GET /api/admin/packages/{id}/history

POST /api/admin/packages/{id}/deactivate

### Public API

GET /api/catalog/packages

GET /api/catalog/packages/{id}

GET /api/catalog/categories

GET /api/catalog/categories/{id}

### Internal API

GET /internal/catalog/packages/{id}

API nội bộ dành cho Booking Service lấy thông tin package đã được duyệt.

Các API phải có:

- Request DTO.
- Response DTO.
- Validation.
- Exception handling.
- HTTP status phù hợp.
- Pagination.
- Sorting.
- Filtering khi cần.
- Swagger/OpenAPI.

Không expose Entity trực tiếp ra API.

## 9. API GATEWAY

Cập nhật routing:

/api/provider/packages/** → catalog-service

/api/admin/packages/** → catalog-service

/api/catalog/** → catalog-service

/internal/catalog/** → catalog-service

Giữ tương thích với API hiện tại nếu frontend đang sử dụng.

Nếu phải thay đổi endpoint, cập nhật frontend tương ứng và lập bảng API cũ/mới.

Gateway xác thực JWT nhưng Catalog Service vẫn phải kiểm tra quyền nghiệp vụ.

Không chỉ dựa vào Gateway hoặc việc ẩn nút trên Frontend.

## 10. NOTIFICATION VÀ RABBITMQ

Sử dụng RabbitMQ để phát sự kiện bất đồng bộ.

Các event:

- PackageSubmitted
- PackageApproved
- PackageRejected
- PackageResubmitted
- PackageDeactivated

Ví dụ:

{
  "eventId": "uuid",
  "eventType": "PackageApproved",
  "packageId": 101,
  "providerId": 1001,
  "adminId": 5001,
  "occurredAt": "ISO-8601"
}

Yêu cầu:

1. Catalog ghi event vào Outbox cùng transaction thay đổi package.
2. Outbox Publisher gửi event đến RabbitMQ.
3. Notification Service nhận event.
4. Notification Service tạo thông báo tương ứng.
5. Consumer xử lý idempotent dựa trên eventId.
6. Có retry và Dead Letter Queue.
7. Không để lỗi Notification làm rollback package.
8. Không gửi password, JWT hoặc thông tin nhạy cảm qua event.

Nếu Notification Service hiện chưa tồn tại, hãy xây dựng phần tích hợp theo từng giai đoạn, không tạo một hệ thống giả lập chỉ để báo cáo đã hoàn thành.

## 11. NOTIFICATION CONTENT

PackageSubmitted:

Thông báo cho Admin rằng có package mới cần kiểm duyệt.

PackageApproved:

Thông báo cho Provider rằng package đã được duyệt và có thể xuất hiện công khai.

PackageRejected:

Thông báo cho Provider kèm lý do từ chối.

PackageResubmitted:

Thông báo cho Admin rằng Provider đã gửi lại package.

PackageDeactivated:

Thông báo cho Provider rằng package đã ngừng hoạt động.

## 12. FRONTEND

Nếu frontend hiện tại có các trang liên quan, cập nhật và giữ nguyên giao diện phù hợp.

Provider Portal:

- Danh sách package của mình.
- Tạo package.
- Chỉnh sửa package.
- Xem trạng thái duyệt.
- Xem lý do từ chối.
- Gửi duyệt lại.

Admin Portal:

- Danh sách package chờ duyệt.
- Xem chi tiết.
- Duyệt package.
- Từ chối package.
- Nhập lý do từ chối.
- Xem lịch sử kiểm duyệt.

Customer Portal:

- Chỉ hiển thị package đã APPROVED và đang hoạt động.
- Không cho truy cập package chưa duyệt bằng cách gọi API trực tiếp.

Không được chỉ sửa UI mà bỏ qua Backend Security.

## 13. CẤU TRÚC SOURCE CATALOG SERVICE

Đề xuất:

catalog-service/
├── pom.xml
├── Dockerfile
└── src/
    ├── main/
    │   ├── java/com/doan1/catalog/
    │   │   ├── CatalogApplication.java
    │   │   ├── config/
    │   │   ├── security/
    │   │   ├── controller/
    │   │   │   ├── provider/
    │   │   │   ├── admin/
    │   │   │   ├── publicapi/
    │   │   │   └── internal/
    │   │   ├── dto/
    │   │   ├── entity/
    │   │   ├── enums/
    │   │   ├── repository/
    │   │   ├── service/
    │   │   │   ├── PackageService.java
    │   │   │   ├── ApprovalService.java
    │   │   │   └── CategoryService.java
    │   │   ├── client/
    │   │   │   └── ProfileClient.java
    │   │   ├── event/
    │   │   │   ├── publisher/
    │   │   │   ├── outbox/
    │   │   │   └── dto/
    │   │   ├── exception/
    │   │   └── mapper/
    │   └── resources/
    │       ├── application.yml
    │       └── db/migration/
    └── test/

Có thể tổ chức package theo feature/domain nếu phù hợp hơn với source hiện tại.

## 14. TESTING BẮT BUỘC

### Authentication và Authorization

- Customer không tạo được package.
- Provider không duyệt được package.
- Provider A không sửa được package của Provider B.
- Admin có thể xem danh sách chờ duyệt.
- Admin ID phải lấy từ JWT đã xác minh.
- Request giả mạo providerId phải bị từ chối.

### Business Workflow

- Provider VERIFIED tạo package thành công.
- Provider chưa VERIFIED không thể tạo hoặc gửi package.
- Package mới có status DRAFT.
- Package hợp lệ chuyển PENDING_APPROVAL.
- Admin duyệt thành công.
- Admin từ chối và bắt buộc nhập reason.
- Provider sửa package REJECTED và gửi lại được.
- Package APPROVED xuất hiện trong Public API.
- Package chưa duyệt không xuất hiện trong Public API.
- Package đang chờ duyệt không thể bị sửa bởi Provider.

### Concurrency

- Hai Admin không thể duyệt một package hai lần.
- Package thay đổi version phải bị từ chối khi Admin duyệt theo version cũ.
- Không có lịch sử duyệt sai lệch khi transaction thất bại.

### Event

- PackageApproved tạo đúng event.
- PackageRejected tạo đúng event.
- RabbitMQ lỗi không làm mất event trong Outbox.
- Event trùng không tạo thông báo trùng.
- Consumer retry khi xử lý thất bại.

## 15. QUY TRÌNH LÀM VIỆC

Không được thay đổi toàn bộ repository ngay lập tức.

Thực hiện lần lượt:

### PHASE 1 – AUDIT

Đọc repository hiện tại.

Phân tích:

- Cấu trúc Maven.
- Marketplace Service.
- Entity.
- Repository.
- Controller.
- Service.
- Security.
- Database.
- API Gateway.
- Docker Compose.
- Frontend API.

Lập bảng mapping code cũ sang code mới.

Báo cáo các vấn đề trước khi chỉnh sửa.

### PHASE 2 – CATALOG BACKEND

- Tách nghiệp vụ Catalog khỏi Marketplace.
- Xây dựng package management.
- Xây dựng approval workflow.
- Xây dựng authorization.
- Xây dựng database và migration.
- Xây dựng REST API.
- Viết test.

### PHASE 3 – INTEGRATION

- Kết nối Profile Service.
- Cập nhật API Gateway.
- Kết nối RabbitMQ.
- Tích hợp Notification Service.
- Cập nhật Docker Compose.

### PHASE 4 – FRONTEND

- Cập nhật Provider Portal.
- Cập nhật Admin Portal.
- Cập nhật Customer Portal.
- Kiểm tra tương thích API.

### PHASE 5 – VALIDATION

- Build backend.
- Chạy migration.
- Chạy unit tests.
- Chạy integration tests.
- Chạy các luồng nghiệp vụ đầu cuối.
- Kiểm tra Docker Compose.
- Kiểm tra bảo mật.

## 16. NGUYÊN TẮC BẮT BUỘC

1. Không xóa chức năng hiện có khi chưa xác định tác động.
2. Không viết lại toàn bộ dự án nếu không cần.
3. Không tạo code giả để báo cáo hoàn thành.
4. Không tạo Entity dùng chung giữa các service.
5. Không truy cập trực tiếp database của service khác.
6. Không hardcode password, JWT secret hoặc API key.
7. Không bỏ qua kiểm tra quyền trong Backend.
8. Không cho Provider tự duyệt package.
9. Không công khai package chưa được duyệt.
10. Không báo cáo build/test thành công nếu chưa chạy thực tế.
11. Nếu thiếu service phụ thuộc, phải ghi rõ và đề xuất giải pháp.
12. Nếu có thay đổi API, phải cập nhật frontend và tài liệu.
13. Bảo toàn dữ liệu và tạo migration an toàn.
14. Không tự ý thay đổi nghiệp vụ không liên quan.

## 17. BÁO CÁO CUỐI CÙNG

Sau khi thực hiện, báo cáo:

1. Kiến trúc trước và sau.
2. Danh sách file tạo mới.
3. Danh sách file chỉnh sửa.
4. Danh sách file xóa và lý do.
5. Database và migration.
6. API đã triển khai.
7. Quy tắc phân quyền.
8. Approval Workflow.
9. Event và Notification.
10. Kết quả build.
11. Kết quả test.
12. Những vấn đề chưa hoàn thành.

Yêu cầu cuối cùng:

Hãy xây dựng Catalog Service đúng ranh giới nghiệp vụ Microservices, có dữ liệu độc lập, kiểm soát quyền chặt chẽ, Approval Workflow rõ ràng và khả năng tích hợp với các service còn lại.

Không được chỉ tạo folder và class rỗng để tuyên bố đã hoàn thành.