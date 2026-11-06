# API Gateway

Edge application duy nhất dự kiến được public. Gateway định tuyến tới business services và xác minh JWT RS256 bằng public key cho API cần đăng nhập; không chứa domain logic hoặc database nghiệp vụ. Authorization theo role/ownership vẫn nằm trong service sở hữu workflow.

## Java package layout

The route configuration is under `com.doan1.gateway.config`; the Spring Boot entry point remains in the root package. Tests remain in the separate `src/test/java` tree.
