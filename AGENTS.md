# Project Instructions

Use the smallest relevant context for each task. Do not read every file in `docs/` by default.

## Read in this order

1. Read this file.
2. Inspect `git status` and locate the requested feature/file.
3. Read [DEVELOPMENT_STATUS.md](docs/DEVELOPMENT_STATUS.md) only to establish current status for the affected area.
4. Read the source, tests, configuration and the owning service's documentation needed for this change.
5. Open requirements, decisions, API, database or architecture docs only when they govern the behavior or files being changed.

Choose the relevant references; do not read all of them automatically:

| Change scope | Read next |
|---|---|
| Backend business behavior | Owning service's `README.md` and `REQUIREMENTS.md`; then relevant source/tests. |
| API contract | Owning service's `API.md`, controller/DTO and callers/clients. |
| Database or migration | Owning service's `DATABASE.md`, migrations/entities/repositories and migration tests. |
| Cross-service workflow or new boundary | Relevant `docs/DECISIONS.md`, `docs/ARCHITECTURE.md` or one matching file under `docs/architecture/`; inspect each involved service's API/client/event code. |
| Frontend | `font-end/AGENTS.md`, the affected feature and its API integration. For Next.js-specific APIs, follow the applicable local Next.js guide required by `font-end/AGENTS.md`. |
| Documentation-only task | Read the target document and the nearest index/status page it updates; source inspection is needed only to verify factual claims. |
| Build/test/configuration | Read the relevant manifest/config and the command-specific guide; do not load unrelated domain documents. |

If inspection reveals a dependency on another service or an unresolved rule, expand the reading set only to that dependency. Do not treat **Proposed**, **Open** or **Not implemented** items as approved behavior.

## Project constraints

- Keep service boundaries aligned with business ownership. Do not create a service solely for a role, portal or CRUD screen.
- Admin is a role/portal, not a separate service by default. Put Admin operations in the service that owns the workflow and data.
- A service must not access another service's database. Use DTO-based APIs, events or an explicitly approved snapshot/projection.
- Keep public API contracts consistent; validate inputs and authorize both role and resource ownership in the owning service.
- Do not add dependencies or expose internal service/database ports without a concrete, approved need.
- Payment lifecycle and saga are deferred. Booking slot-overlap/quota rules remain unimplemented until their open semantics are confirmed.
- Preserve unrelated work and existing user changes. Do not revert changes outside the task.

## Change workflow

1. Identify the affected owner, requirement and directly dependent callers.
2. If a necessary business decision is unresolved, ask before implementing behavior based on an assumption.
3. Make the smallest complete change in the responsible layer; add or update tests for changed behavior.
4. Run the smallest relevant test/build/lint command and report its actual result. Never label unrun behavior as verified.
5. Update only related service/API/database docs; update `docs/DEVELOPMENT_STATUS.md` and `docs/CHANGELOG.md` when the change materially alters project status or scope. Record architecture decisions in `docs/DECISIONS.md` when one is made.

Do not claim completion without verification. For commands and test conventions, see [DEVELOPMENT_GUIDE.md](docs/DEVELOPMENT_GUIDE.md) and [TESTING_STRATEGY.md](docs/TESTING_STRATEGY.md) when relevant.
