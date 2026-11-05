# Marketplace architecture

Spring Boot application on development port 8082. Currently owns provider profiles, fixed category IDs, reviews and provider ratings. Provider service offerings and their VND prices/approval state are approved requirements but are not present in the current source/schema.

Public queries expose only approved providers. Provider/Admin writes check role and ownership in backend. Booking consumes Marketplace DTO/API, including a service-key-protected internal lookup, never Marketplace database. Marketplace asks Booking to verify review eligibility before storing a review. Offering CRUD, offering moderation, and booking price snapshot are not implemented.
