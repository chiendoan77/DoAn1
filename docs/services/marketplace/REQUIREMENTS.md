# Marketplace requirements and status

## Current ownership

Marketplace retains legacy provider/review tables for migration only. Profile owns provider profiles/rating projections; Catalog owns category codes/packages/prices; Review owns new review records and eligibility orchestration with Booking. Admin is a role, not a separate service.

Legacy provider profile tables and routes remain in the Marketplace app for migration compatibility. Gateway routes provider/profile requests to Profile and catalog/package requests to Catalog. Profile imports legacy data through an internal API rather than querying Marketplace's database.

## Implemented

- Authenticated internal export APIs support one-time migration of legacy profile/review data.

## Not implemented

- Automatic legacy data import and retirement of Marketplace legacy tables.
- Review edit/delete/moderation (owned by Review).
- Complaint workflow, advanced search or profile/catalog CRUD in Marketplace.
- Any direct Marketplace database access by Profile, Catalog or Booking.
