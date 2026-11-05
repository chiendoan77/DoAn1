# TÀI LIỆU ĐỊNH HƯỚNG XÂY DỰNG BÁO CÁO ĐỒ ÁN CÔNG NGHỆ PHẦN MỀM 1

## 1. Mục đích tài liệu

Tài liệu này tổng hợp cấu trúc và các nội dung cần có trong một báo cáo Đồ án Công nghệ phần mềm.

Mục đích:
- Làm căn cứ xây dựng mục lục báo cáo.
- Xác định nội dung cần trình bày trong từng chương.
- Định hướng phân tích nghiệp vụ, yêu cầu và thiết kế phần mềm.
- Xác định các sơ đồ UML cần thiết.
- Đảm bảo báo cáo có tính khoa học, logic và thống nhất với sản phẩm thực tế.
- Làm đầu vào để AI đọc tài liệu dự án, phân tích và tổng hợp thành báo cáo hoàn chỉnh.

**Nguyên tắc:** Đây là tài liệu định hướng tham khảo, không phải quy định chính thức của trường. Nếu giảng viên cung cấp mẫu hoặc yêu cầu riêng, phải ưu tiên yêu cầu của giảng viên.

---

# PHẦN I. CẤU TRÚC TỔNG THỂ BÁO CÁO

Một báo cáo hoàn chỉnh gồm ba phần:

1. Phần đầu báo cáo.
2. Nội dung chính.
3. Phần kết thúc.

## 1. Phần đầu báo cáo

Thứ tự đề xuất:

1. Trang bìa chính.
2. Trang bìa phụ.
3. Lời cam đoan.
4. Lời cảm ơn (nếu có).
5. Nhận xét của giảng viên hướng dẫn.
6. Nhận xét của giảng viên phản biện (nếu có).
7. Bảng phân công nhiệm vụ thành viên.
8. Mục lục.
9. Danh mục từ viết tắt.
10. Danh mục bảng.
11. Danh mục hình vẽ và sơ đồ.

## 2. Nội dung chính

Dự kiến gồm 6 chương:

- Chương 1. Tổng quan đề tài.
- Chương 2. Cơ sở lý thuyết và công nghệ sử dụng.
- Chương 3. Khảo sát và phân tích yêu cầu hệ thống.
- Chương 4. Phân tích và thiết kế hệ thống.
- Chương 5. Xây dựng và triển khai hệ thống.
- Chương 6. Kiểm thử và đánh giá hệ thống.

## 3. Phần kết thúc

- Kết luận và hướng phát triển.
- Tài liệu tham khảo.
- Phụ lục.

---

# PHẦN II. NỘI DUNG CHI TIẾT TỪNG CHƯƠNG

# CHƯƠNG 1. TỔNG QUAN ĐỀ TÀI

### 1.1. Đặt vấn đề

- Giới thiệu bối cảnh thực tế.
- Trình bày vấn đề đang tồn tại.
- Nêu nhu cầu cần xây dựng hệ thống phần mềm.
- Phân tích lý do cần giải quyết vấn đề.

### 1.2. Lý do chọn đề tài

- Tính thực tiễn.
- Tính cần thiết.
- Khả năng ứng dụng.
- Giá trị mà hệ thống mang lại.

### 1.3. Mục tiêu đề tài

Chia thành:

- Mục tiêu tổng quát.
- Mục tiêu cụ thể.

Mục tiêu cụ thể cần gắn với những chức năng thực tế mà hệ thống dự kiến triển khai.

### 1.4. Đối tượng sử dụng

Xác định các tác nhân tương tác với hệ thống.

Đối với hệ thống đang nghiên cứu:

- ADMIN: Quản trị hệ thống.
- CUSTOMER: Khách hàng.
- SERVICE PROVIDER: Nhà cung cấp dịch vụ.
- PAYMENT SYSTEM: Hệ thống thanh toán bên ngoài.

Lưu ý: PAYMENT SYSTEM là hệ thống bên ngoài, không phải vai trò người dùng thông thường.

### 1.5. Phạm vi đề tài

- Phạm vi nghiệp vụ.
- Phạm vi chức năng.
- Phạm vi người dùng.
- Phạm vi kỹ thuật.
- Những chức năng chưa thực hiện.

### 1.6. Phương pháp thực hiện

- Khảo sát nghiệp vụ.
- Thu thập và phân tích yêu cầu.
- Phân tích và thiết kế hệ thống.
- Xây dựng cơ sở dữ liệu.
- Lập trình.
- Kiểm thử và đánh giá.

### 1.7. Bố cục báo cáo

Giới thiệu ngắn gọn nội dung của từng chương.

**Yêu cầu:** Chương 1 cần súc tích, không trình bày quá nhiều lý thuyết hoặc nội dung kỹ thuật chi tiết.

---

# CHƯƠNG 2. CƠ SỞ LÝ THUYẾT VÀ CÔNG NGHỆ SỬ DỤNG

### 2.1. Tổng quan về lĩnh vực nghiệp vụ

- Giải thích lĩnh vực mà hệ thống hướng đến.
- Các khái niệm nghiệp vụ quan trọng.
- Những quy trình nghiệp vụ cơ bản.

### 2.2. Kiến trúc phần mềm

- Khái niệm kiến trúc phần mềm.
- Mô hình kiến trúc được lựa chọn.
- Nguyên tắc phân lớp và phân chia module.
- Vai trò của kiến trúc đối với hệ thống.

### 2.3. Công nghệ Backend

Ví dụ nếu sử dụng Spring Boot:

- Java.
- Spring Boot.
- Spring MVC.
- Spring Data JPA.
- Spring Security.

### 2.4. Công nghệ Frontend

Ví dụ:

- Angular.
- TypeScript.
- HTML/CSS.
- Angular Material.

### 2.5. Hệ quản trị cơ sở dữ liệu

- MySQL hoặc PostgreSQL.
- Mô hình quan hệ.
- Khóa chính và khóa ngoại.
- Các ràng buộc dữ liệu.

### 2.6. Công nghệ xác thực và bảo mật

- Authentication.
- Authorization.
- JWT.
- Role-Based Access Control (RBAC).

### 2.7. Công nghệ tích hợp hệ thống

- RESTful API.
- JSON.
- HTTP.
- Payment Gateway.
- Webhook/Callback.

### 2.8. Công cụ phát triển

Ví dụ:

- IntelliJ IDEA.
- Visual Studio Code.
- Git/GitHub.
- Postman.
- Docker.
- MySQL Workbench.

**Yêu cầu trình bày mỗi công nghệ:**

1. Khái niệm.
2. Vai trò trong hệ thống.
3. Lý do lựa chọn.

Không biến chương này thành tài liệu hướng dẫn sử dụng framework. Chỉ trình bày kiến thức liên quan trực tiếp đến đề tài.

---

# CHƯƠNG 3. KHẢO SÁT VÀ PHÂN TÍCH YÊU CẦU HỆ THỐNG

Đây là chương quan trọng, thể hiện quá trình tìm hiểu nghiệp vụ và xác định hệ thống cần xây dựng.

### 3.1. Khảo sát nghiệp vụ

- Mô tả nghiệp vụ hiện tại.
- Xác định quy trình hoạt động.
- Xác định các đối tượng tham gia.
- Xác định khó khăn và hạn chế.
- Đề xuất giải pháp.

Phân biệt rõ:
- Khảo sát thực tế.
- Khảo sát tài liệu.
- Khảo sát các hệ thống tương tự.
- Phân tích và giả định nghiệp vụ của nhóm.

Không được trình bày những giả định của nhóm như kết quả khảo sát thực tế.

### 3.2. Khảo sát các hệ thống tương tự

- Giới thiệu các giải pháp hiện có.
- Phân tích chức năng.
- Ưu điểm.
- Hạn chế.
- So sánh với hệ thống đề xuất.

### 3.3. Xác định tác nhân

Phân tích các tác nhân:

| Tác nhân | Trách nhiệm |
|---|---|
| ADMIN | Quản lý và giám sát hệ thống |
| CUSTOMER | Tìm kiếm, đặt và sử dụng dịch vụ |
| SERVICE PROVIDER | Quản lý và cung cấp dịch vụ |
| PAYMENT SYSTEM | Xử lý giao dịch thanh toán |

### 3.4. Yêu cầu chức năng

Phân nhóm theo nghiệp vụ:

- Quản lý tài khoản.
- Đăng ký, đăng nhập và đăng xuất.
- Quản lý khách hàng.
- Quản lý nhà cung cấp.
- Quản lý dịch vụ.
- Quản lý danh mục.
- Tìm kiếm và lọc dịch vụ.
- Quản lý đặt dịch vụ.
- Quản lý thanh toán.
- Quản lý đánh giá.
- Quản lý thông báo.
- Quản trị và thống kê.

Mỗi yêu cầu cần có mã định danh, tên, mô tả và tác nhân liên quan nếu phù hợp.

### 3.5. Yêu cầu phi chức năng

- Bảo mật.
- Hiệu năng.
- Tính ổn định.
- Khả năng mở rộng.
- Khả năng bảo trì.
- Tính dễ sử dụng.
- Tính toàn vẹn dữ liệu.
- Khả năng tương thích.

### 3.6. Biểu đồ Use Case

Cần xây dựng:

- Use Case tổng quát.
- Use Case phân rã theo ADMIN.
- Use Case phân rã theo CUSTOMER.
- Use Case phân rã theo SERVICE PROVIDER.
- Use Case có liên quan đến PAYMENT SYSTEM.

Yêu cầu:
- Sử dụng đúng ký pháp UML.
- Xác định đúng Actor.
- Xác định đúng System Boundary.
- Sử dụng quan hệ Include và Extend khi phù hợp.
- Không đưa các bước xử lý nội bộ nhỏ thành Use Case độc lập nếu không có giá trị nghiệp vụ.

### 3.7. Đặc tả Use Case

Mỗi Use Case quan trọng cần mô tả:

| Thuộc tính | Nội dung |
|---|---|
| Mã Use Case | Mã định danh |
| Tên Use Case | Tên chức năng |
| Tác nhân | Actor tham gia |
| Mô tả | Mục đích chức năng |
| Tiền điều kiện | Điều kiện trước khi thực hiện |
| Luồng chính | Các bước xử lý chính |
| Luồng thay thế | Những trường hợp khác |
| Ngoại lệ | Các tình huống lỗi |
| Hậu điều kiện | Trạng thái sau khi hoàn thành |

Không cần đặc tả quá dài các chức năng CRUD đơn giản.

### 3.8. Biểu đồ hoạt động (Activity Diagram)

Mô tả các quy trình nghiệp vụ quan trọng:

- Đăng ký tài khoản.
- Đặt dịch vụ.
- Nhà cung cấp tiếp nhận yêu cầu.
- Thanh toán.
- Hủy dịch vụ.
- Hoàn tiền.

Yêu cầu:
- Sử dụng đúng ký pháp UML.
- Có điểm bắt đầu và kết thúc.
- Thể hiện các bước xử lý và điều kiện rẽ nhánh.
- Có thể sử dụng Swimlane để phân biệt Actor và System.
- Không ghi các bước xử lý kỹ thuật quá chi tiết nếu mục đích là mô tả nghiệp vụ.

### 3.9. Biểu đồ tuần tự (Sequence Diagram)

Mô tả sự tương tác theo thời gian giữa:

- Actor.
- Frontend.
- Controller.
- Service.
- Repository.
- Database.
- Hệ thống bên ngoài.

Các quy trình nên ưu tiên:

- Đăng nhập.
- Đặt dịch vụ.
- Xác nhận yêu cầu.
- Thanh toán.
- Nhận callback từ Payment Gateway.

Yêu cầu:
- Đúng thứ tự tương tác.
- Thể hiện Request và Response.
- Thể hiện các nhánh điều kiện nếu có.
- Phân biệt rõ thành phần nội bộ và hệ thống bên ngoài.

---

# CHƯƠNG 4. PHÂN TÍCH VÀ THIẾT KẾ HỆ THỐNG

Đây là chương thể hiện tư duy thiết kế và khả năng chuyển đổi yêu cầu nghiệp vụ thành kiến trúc phần mềm.

### 4.1. Thiết kế kiến trúc tổng thể

Trình bày:

- Mô hình kiến trúc tổng thể.
- Frontend.
- Backend.
- Database.
- Các module nghiệp vụ.
- Hệ thống bên ngoài.
- Cơ chế giao tiếp giữa các thành phần.

Nếu sử dụng Modular Monolith:
- Giải thích nguyên tắc phân chia module.
- Mô tả trách nhiệm từng module.
- Mô tả cách các module giao tiếp.
- Giải thích vì sao lựa chọn kiến trúc này.

### 4.2. Thiết kế phân rã module và Service

Phân chia theo nghiệp vụ, không phân chia Service chỉ dựa trên vai trò người dùng.

Ví dụ:

| Module | Service |
|---|---|
| Identity | AuthService, JwtService, AuthorizationService |
| User | UserService, CustomerService, ProviderService |
| Service Management | ServiceManagementService, CategoryService |
| Booking | BookingService, BookingStatusService, BookingAssignmentService |
| Payment | PaymentService, PaymentGatewayService, PaymentCallbackService |
| Review | ReviewService |
| Notification | NotificationService |
| Admin | AdminManagementService, DashboardService |

Đây là danh sách tham khảo, cần điều chỉnh theo mã nguồn và nghiệp vụ thực tế.

### 4.3. Thiết kế cơ sở dữ liệu

Bao gồm:

1. Mô hình ERD.
2. Danh sách thực thể.
3. Mô tả các bảng.
4. Thuộc tính từng bảng.
5. Kiểu dữ liệu.
6. Khóa chính.
7. Khóa ngoại.
8. Quan hệ giữa các bảng.
9. Các ràng buộc dữ liệu.

Ví dụ các nhóm bảng:

- Tài khoản: users, roles, user_roles.
- Khách hàng: customers.
- Nhà cung cấp: providers.
- Dịch vụ: services, categories.
- Đặt dịch vụ: bookings, booking_details.
- Thanh toán: payments, transactions.
- Đánh giá: reviews.
- Thông báo: notifications.

Không mặc định sử dụng toàn bộ các bảng trên. Cần dựa trên thiết kế và cơ sở dữ liệu thực tế.

### 4.4. Thiết kế API

Trình bày:

- Nguyên tắc thiết kế REST API.
- Các nhóm API.
- HTTP Method.
- Endpoint.
- Request.
- Response.
- Authentication.
- Authorization.
- HTTP Status Code.

Ví dụ:

| Method | Endpoint | Chức năng |
|---|---|---|
| POST | /api/auth/login | Đăng nhập |
| GET | /api/services | Danh sách dịch vụ |
| POST | /api/bookings | Tạo đặt dịch vụ |
| GET | /api/bookings/{id} | Chi tiết đặt dịch vụ |
| POST | /api/payments | Khởi tạo thanh toán |
| POST | /api/payments/callback | Nhận kết quả thanh toán |

### 4.5. Thiết kế giao diện

- Sơ đồ điều hướng.
- Wireframe.
- Mockup.
- Giao diện ADMIN.
- Giao diện CUSTOMER.
- Giao diện SERVICE PROVIDER.
- Giao diện dùng chung.

### 4.6. Thiết kế bảo mật

- Đăng nhập và đăng xuất.
- Xác thực JWT.
- Phân quyền RBAC.
- Bảo vệ API.
- Kiểm soát quyền truy cập dữ liệu.
- Xác thực callback thanh toán.
- Kiểm soát các trường hợp truy cập trái phép.

---

# CHƯƠNG 5. XÂY DỰNG VÀ TRIỂN KHAI HỆ THỐNG

### 5.1. Môi trường phát triển

- Phần cứng.
- Hệ điều hành.
- Ngôn ngữ lập trình.
- Framework.
- IDE.
- Công cụ quản lý mã nguồn.
- Hệ quản trị cơ sở dữ liệu.

### 5.2. Xây dựng Backend

- Cấu trúc thư mục.
- Phân chia module.
- Controller.
- Service.
- Repository.
- Entity.
- DTO.
- Mapper.
- Exception Handling.
- Security Configuration.

### 5.3. Xây dựng Frontend

- Cấu trúc thư mục.
- Component.
- Routing.
- Service.
- Model.
- Guard.
- Interceptor.
- Giao diện và tương tác.

### 5.4. Xây dựng cơ sở dữ liệu

- Khởi tạo database.
- Tạo bảng.
- Thiết lập quan hệ.
- Ràng buộc dữ liệu.
- Dữ liệu mẫu.

### 5.5. Tích hợp hệ thống

- Frontend và Backend.
- Backend và Database.
- Authentication và Authorization.
- Tích hợp các module nghiệp vụ.

### 5.6. Tích hợp thanh toán

- Khởi tạo giao dịch.
- Gửi yêu cầu đến Payment Gateway.
- Nhận kết quả thanh toán.
- Xác thực callback/webhook.
- Cập nhật trạng thái giao dịch.
- Xử lý thất bại hoặc hoàn tiền nếu có.

### 5.7. Triển khai hệ thống

- Cấu hình môi trường.
- Cấu hình Database.
- Cấu hình Backend.
- Cấu hình Frontend.
- Docker nếu có.
- Hướng dẫn chạy hệ thống.

### 5.8. Kết quả giao diện

Trình bày ảnh chụp từ chính sản phẩm đã xây dựng.

Mỗi ảnh cần có:
- Tên giao diện.
- Mục đích.
- Mô tả chức năng.
- Tác nhân sử dụng.

Không sử dụng ảnh minh họa trên Internet để thay thế ảnh giao diện sản phẩm thực tế.

---

# CHƯƠNG 6. KIỂM THỬ VÀ ĐÁNH GIÁ HỆ THỐNG

### 6.1. Mục tiêu kiểm thử

- Kiểm tra chức năng.
- Kiểm tra dữ liệu.
- Kiểm tra phân quyền.
- Kiểm tra tích hợp.
- Kiểm tra thanh toán.
- Kiểm tra độ ổn định.

### 6.2. Phương pháp kiểm thử

| Loại kiểm thử | Nội dung |
|---|---|
| Unit Testing | Kiểm thử từng thành phần |
| Integration Testing | Kiểm thử sự phối hợp giữa module |
| API Testing | Kiểm tra API |
| UI Testing | Kiểm tra giao diện |
| Security Testing | Kiểm tra bảo mật |
| System Testing | Kiểm thử toàn bộ hệ thống |

### 6.3. Kịch bản kiểm thử

Bảng kiểm thử nên có:

- Mã Test Case.
- Tên chức năng.
- Mô tả.
- Dữ liệu đầu vào.
- Các bước thực hiện.
- Kết quả mong đợi.
- Kết quả thực tế.
- Trạng thái Pass/Fail.

Ví dụ:

| Mã | Chức năng | Đầu vào | Kết quả mong đợi |
|---|---|---|---|
| TC01 | Đăng nhập | Tài khoản hợp lệ | Đăng nhập thành công |
| TC02 | Đăng nhập | Sai mật khẩu | Hiển thị lỗi |
| TC03 | Đặt dịch vụ | Dữ liệu hợp lệ | Tạo đơn thành công |
| TC04 | Phân quyền | CUSTOMER gọi API ADMIN | Từ chối truy cập |
| TC05 | Thanh toán | Giao dịch hợp lệ | Cập nhật trạng thái |
| TC06 | Đánh giá | Đơn hoàn thành | Lưu đánh giá |

### 6.4. Kết quả kiểm thử

- Tổng số trường hợp kiểm thử.
- Số trường hợp thành công.
- Số trường hợp thất bại.
- Những lỗi đã phát hiện.
- Những lỗi đã khắc phục.
- Những vấn đề còn tồn tại.

### 6.5. Đánh giá hệ thống

- Mức độ hoàn thành chức năng.
- Mức độ đáp ứng yêu cầu.
- Ưu điểm.
- Hạn chế.
- Khả năng ứng dụng.

Không được ghi kết quả kiểm thử khi chưa thực hiện kiểm thử thực tế.

---

# PHẦN III. KẾT LUẬN VÀ HƯỚNG PHÁT TRIỂN

## 1. Kết luận

- Tóm tắt nội dung đề tài.
- Kết quả xây dựng hệ thống.
- Các chức năng hoàn thành.
- Những mục tiêu đã đạt được.

## 2. Hạn chế

- Chức năng chưa hoàn thiện.
- Hạn chế về hiệu năng.
- Hạn chế về bảo mật.
- Hạn chế về giao diện.
- Hạn chế trong tích hợp hệ thống.

## 3. Hướng phát triển

- Mở rộng chức năng.
- Phát triển ứng dụng Mobile.
- Tích hợp thêm Payment Gateway.
- Thêm thông báo thời gian thực.
- Cải thiện hiệu năng.
- Ứng dụng AI nếu phù hợp.

---

# PHẦN IV. TÀI LIỆU THAM KHẢO VÀ PHỤ LỤC

## 1. Tài liệu tham khảo

Có thể chia thành:

1. Giáo trình và sách chuyên ngành.
2. Bài báo và tài liệu học thuật.
3. Tài liệu chính thức của framework.
4. Tài liệu API và thư viện.
5. Các nguồn tham khảo khác.

Ví dụ:

- Spring Boot Documentation: https://docs.spring.io/spring-boot/
- Angular Documentation: https://angular.dev/
- MySQL Documentation: https://dev.mysql.com/doc/
- UML Specification: https://www.omg.org/spec/UML/

Cần ghi nguồn đầy đủ, thống nhất một kiểu trích dẫn và chỉ đưa vào tài liệu đã thực sự tham khảo.

## 2. Phụ lục

Có thể bao gồm:

- Danh sách API chi tiết.
- Cấu trúc database.
- Đặc tả Use Case bổ sung.
- Hướng dẫn cài đặt.
- Hướng dẫn sử dụng.
- Các đoạn mã quan trọng.
- Tài liệu khảo sát.
- Các biểu mẫu liên quan.

---

# PHẦN V. DANH SÁCH SƠ ĐỒ UML VÀ HÌNH ẢNH CẦN CÂN NHẮC

| STT | Sơ đồ | Mục đích | Chương |
|---|---|---|---|
| 1 | Use Case Diagram | Xác định tác nhân và chức năng | 3 |
| 2 | Activity Diagram | Mô tả luồng nghiệp vụ | 3 |
| 3 | Sequence Diagram | Mô tả tương tác theo thời gian | 3 |
| 4 | Class Diagram | Mô hình lớp đối tượng | 4 |
| 5 | ERD | Thiết kế dữ liệu | 4 |
| 6 | Component Diagram | Phân chia module | 4 |
| 7 | Deployment Diagram | Mô tả triển khai | 4 hoặc 5 |
| 8 | System Architecture Diagram | Mô tả kiến trúc tổng thể | 4 |
| 9 | Navigation Diagram | Mô tả điều hướng giao diện | 4 |

## Nguyên tắc vẽ UML

- Tuân thủ đúng ký pháp UML.
- Sơ đồ phải phản ánh đúng nghiệp vụ.
- Không vẽ quá nhiều thành phần không cần thiết.
- Tên Actor, Use Case và thành phần phải thống nhất.
- Sơ đồ phải khớp với nội dung mô tả và mã nguồn.
- Mỗi sơ đồ phải có tên, số thứ tự và phần giải thích.

Không nhất thiết đưa tất cả các sơ đồ vào báo cáo nếu chúng không có giá trị đối với phạm vi hệ thống.

---

# PHẦN VI. NGUYÊN TẮC TRÌNH BÀY BÁO CÁO

## 1. Bố cục

- Sử dụng hệ thống tiêu đề phân cấp rõ ràng.
- Đánh số chương, mục, tiểu mục thống nhất.
- Có mục lục tự động.
- Có danh mục hình và bảng tự động.
- Mỗi chương nên bắt đầu ở trang mới.

## 2. Bảng biểu và hình ảnh

- Mỗi bảng có số thứ tự và tên bảng.
- Mỗi hình có số thứ tự và tên hình.
- Hình và bảng cần được nhắc đến trong nội dung.
- Hình ảnh phải rõ ràng, có độ phân giải tốt.
- Không để sơ đồ quá nhỏ hoặc khó đọc.
- Ưu tiên hình ảnh, sơ đồ do nhóm tự xây dựng.

## 3. Văn phong

- Sử dụng văn phong học thuật.
- Viết khách quan, rõ ràng, súc tích.
- Tránh văn nói và từ ngữ thiếu chính xác.
- Không lặp lại nội dung giữa các chương.
- Không trình bày lý thuyết không liên quan.
- Giải thích các thuật ngữ chuyên ngành khi cần thiết.

## 4. Nội dung và tính trung thực

- Nội dung báo cáo phải thống nhất với sản phẩm.
- Không tự tạo số liệu khảo sát.
- Không tự tạo kết quả kiểm thử.
- Không mô tả chức năng chưa triển khai như đã hoàn thành.
- Phân biệt rõ nội dung thực tế và nội dung đề xuất.
- Trích dẫn các nguồn tài liệu sử dụng.

---

# PHẦN VII. NHỮNG LỖI CẦN TRÁNH

1. Chương 1 quá dài và chứa nhiều lý thuyết.
2. Chương 2 chỉ liệt kê công nghệ mà không nêu vai trò.
3. Không phân biệt yêu cầu chức năng và phi chức năng.
4. Use Case không đúng ký pháp UML.
5. Có sơ đồ nhưng không có giải thích.
6. Activity Diagram không thể hiện rõ luồng nghiệp vụ.
7. Sequence Diagram không khớp với kiến trúc thực tế.
8. Database không có ERD hoặc thiếu mô tả quan hệ.
9. Chỉ liệt kê API mà không giải thích cách hoạt động.
10. Mô tả kiến trúc khác với source code.
11. Ảnh giao diện không phải sản phẩm thực tế.
12. Kiểm thử không có Test Case.
13. Không có kết quả kiểm thử cụ thể.
14. Trùng lặp nội dung giữa các chương.
15. Trích dẫn tài liệu không đầy đủ.

---

# PHẦN VIII. ĐỊNH HƯỚNG ĐỘ DÀI VÀ MỨC ĐỘ CHI TIẾT

Đối với Đồ án Công nghệ phần mềm 1, nên tập trung vào chất lượng phân tích và thiết kế thay vì kéo dài báo cáo.

Mức độ ưu tiên:

| Nội dung | Mức độ ưu tiên |
|---|---|
| Tổng quan đề tài | Vừa |
| Cơ sở lý thuyết | Vừa |
| Khảo sát nghiệp vụ | Cao |
| Phân tích yêu cầu | Rất cao |
| Use Case và đặc tả | Rất cao |
| Activity và Sequence | Cao |
| Thiết kế kiến trúc | Rất cao |
| Thiết kế cơ sở dữ liệu | Rất cao |
| Thiết kế API | Cao |
| Thiết kế giao diện | Cao |
| Xây dựng hệ thống | Theo yêu cầu môn học |
| Kiểm thử | Theo yêu cầu môn học |
| Kết luận | Vừa |

Không đặt số trang cứng khi chưa có quy định của giảng viên.

Nếu môn học tập trung vào phân tích và thiết kế, ưu tiên Chương 3 và Chương 4.

Nếu môn học yêu cầu sản phẩm hoàn chỉnh, bổ sung đầy đủ Chương 5 và Chương 6.

---

# PHẦN IX. HƯỚNG DẪN AI SỬ DỤNG TÀI LIỆU NÀY

Khi sử dụng tài liệu này làm đầu vào, AI cần thực hiện theo các nguyên tắc sau:

1. Đọc toàn bộ tài liệu định hướng.
2. Đọc README, source code, cấu trúc thư mục và các tài liệu hiện có của dự án.
3. Xác định công nghệ thực tế đang sử dụng.
4. Xác định nghiệp vụ thực tế của hệ thống.
5. Xác định các Actor và quyền hạn.
6. Phân tích các module, Service, API và database.
7. Đối chiếu nội dung hiện có với cấu trúc báo cáo đề xuất.
8. Xác định những phần đã có, còn thiếu hoặc cần chỉnh sửa.
9. Đề xuất mục lục cuối cùng phù hợp với dự án và học phần.
10. Tổng hợp nội dung báo cáo dựa trên bằng chứng thực tế từ dự án.

## Các nguyên tắc bắt buộc đối với AI

- Không tự suy diễn nghiệp vụ khi chưa có dữ liệu.
- Không bịa chức năng, bảng dữ liệu, API hoặc công nghệ.
- Không tự nhận định một chức năng đã hoàn thành nếu chưa có bằng chứng.
- Không tạo sơ đồ UML không khớp với nghiệp vụ.
- Không sao chép nguyên mẫu báo cáo của dự án khác.
- Không trình bày nội dung chung chung chỉ để làm báo cáo dài hơn.
- Không đưa kiến trúc đề xuất vào báo cáo như thể đó là kiến trúc đã triển khai.
- Khi thiếu thông tin, phải ghi rõ thông tin cần bổ sung.
- Phân biệt nội dung đã triển khai, nội dung đang phát triển và nội dung đề xuất.
- Ưu tiên sự chính xác, nhất quán và khả năng bảo vệ trước giảng viên.

## Kết quả AI cần tạo ra

1. Mục lục báo cáo hoàn chỉnh.
2. Giải thích nhiệm vụ của từng chương.
3. Nội dung cần lấy từ source code cho từng mục.
4. Danh sách sơ đồ UML cần xây dựng.
5. Danh sách bảng biểu cần có.
6. Danh sách thông tin còn thiếu.
7. Đề xuất điều chỉnh để báo cáo phù hợp với phạm vi Đồ án Công nghệ phần mềm 1.

**Mục tiêu cuối cùng:** Tạo ra một báo cáo có cấu trúc khoa học, phản ánh đúng sản phẩm phần mềm thực tế, có đầy đủ cơ sở phân tích và thiết kế, không dư thừa lý thuyết và có thể sử dụng để trình bày, bảo vệ trước giảng viên.

---

*Tài liệu định hướng tham khảo. Mẫu báo cáo, quy định định dạng và yêu cầu chính thức của giảng viên/Khoa/Trường luôn được ưu tiên.*