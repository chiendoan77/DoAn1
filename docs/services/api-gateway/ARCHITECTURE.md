# Gateway architecture

Spring Cloud Gateway reactive application on port 8080. Upstream service URLs are configurable; gateway contains no repositories or domain state. Only Gateway is intended as the public API entry in deployment; business service ports are internal.

Add authentication, rate limiting, tracing and resilience only with approved policies. Domain services remain responsible for authorization and ownership checks.
