# API Gateway

Edge application duy nhất dự kiến được public. Gateway định tuyến tới bốn business services; không chứa domain logic hoặc database nghiệp vụ.

## Java package layout

The route configuration is under `com.doan1.gateway.config`; the Spring Boot entry point remains in the root package. Tests remain in the separate `src/test/java` tree.
