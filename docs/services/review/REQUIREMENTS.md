# Review requirements status

## Implemented

- Customer/provider may submit one review per completed booking after Booking verifies participation and target.
- Review content is stored only in Review's database.
- Review submission and `ReviewSubmitted` outbox record commit atomically.
- Profile consumes the event idempotently for rating projection.
- Legacy review export/import is available through authenticated internal APIs.

## Not implemented

- Review moderation, edits, deletion, or dispute workflow.
- Automatic orchestration of legacy import and post-import retirement of Marketplace legacy tables.
- Pact contract suite and browser end-to-end coverage.
- Payment-related events and saga (deferred by user).
