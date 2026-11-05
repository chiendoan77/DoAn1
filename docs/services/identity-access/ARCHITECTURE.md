# Identity & Access architecture

Spring Boot application on development port 8081. Owns account credentials, role assignment and authentication. Other services consume identity claims/contract and enforce ownership locally; no shared repository or database access.

Admin Portal uses JWT + role ADMIN. API keys are reserved for server-to-server use, scoped and rotated; never exposed in browser code.
