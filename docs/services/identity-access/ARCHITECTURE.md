# Identity & Access architecture

Spring Boot application on development port 8081. Owns account credentials, role assignment, account status and status audit. Other services consume signed identity claims or call the internal account-status API; no shared repository or database access. Login rejects non-ACTIVE accounts.

Identity signs JWTs with an RSA private key; Gateway and services verify with a public key and issuer/audience constraints. Refresh tokens are opaque, stored hashed, rotated on use and revoked by family. Admin Portal uses JWT + role ADMIN. Access tokens remain valid until TTL after logout; scoped service credentials, key/API-key rotation, mTLS and rate limiting remain future work.
