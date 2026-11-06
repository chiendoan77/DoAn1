# Identity & Access requirements and status

## Ownership

Identity owns accounts, credentials, roles, active/suspended account status, and account-status audit history. Profile calls Identity's internal status API; services do not read Identity's database. Admin is a role, not a separate service.

## Implemented

- Register CUSTOMER/PROVIDER and authenticate by email or phone.
- BCrypt password hashing and RS256 access JWT with issuer/audience checks.
- Opaque refresh token rotation; database stores SHA-256 hash and family revocation state.
- `GET /api/me` and optional bootstrap Admin.
- Login rejects non-ACTIVE accounts.
- Admin `PATCH /api/admin/users/{accountId}/status` with required reason and audit record.
- Internal account-status lookup for Profile; Flyway-managed schema.

## Not implemented

- Password recovery, email/phone verification, lockout and rate limiting.
- Immediate access-token revocation (access tokens remain valid until TTL), mTLS/service-token authorization.
- Full Admin user search and role-management workflow.

## Security rules

Registration cannot self-assign ADMIN. Protected APIs check signed identity and role/ownership in the owning service. API keys are server-to-server only. Local Compose RS256 keypair and credentials are for development, not production.
