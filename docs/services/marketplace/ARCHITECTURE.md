# Marketplace architecture

Spring Boot application on development port 8082. Owns provider profiles, fixed category IDs, service offerings and their VND prices/approval state, reviews and provider ratings. Offerings reference provider profiles within the Marketplace schema; Identity account IDs remain references, not cross-service foreign keys.

Public queries expose only approved providers and approved offerings belonging to approved providers. Provider/Admin writes check role and ownership in backend. Offering edits return to pending approval; deletion archives the offering. Provider contact phone is supplied for each offering and is public after approval. Booking consumes Marketplace DTO/API, including a service-key-protected internal lookup, never Marketplace database. Offering selection and price snapshot are not yet integrated with Booking. Marketplace asks Booking to verify review eligibility before storing a review.
