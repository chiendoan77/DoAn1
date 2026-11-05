# AGENTS.md

## Mandatory Project Instructions

Before making any code changes, you MUST:

1. Read this file completely.
2. Read docs/README.md.
3. Read docs/PROJECT_OVERVIEW.md.
4. Read docs/REQUIREMENTS.md.
5. Read docs/ARCHITECTURE.md.
6. Read docs/DEVELOPMENT_STATUS.md.
7. Read docs/DECISIONS.md.
8. Read the documentation of the affected service under docs/services/.
9. Inspect the actual source code before modifying it.

## Development Rules

- Follow the approved requirements and architecture.
- Analyze business domains, capabilities, workflows, and data ownership before proposing or creating services.
- Treat service boundaries and requirements marked "Proposed" or "Open" in docs as unapproved; do not implement them as confirmed scope.
- Prefer the smallest architecture that fits this individual project. Do not create a service solely for a user role, portal, or CRUD screen.
- Admin is a role and portal, not automatically a separate domain/service. Put each admin operation in the service that owns the corresponding business workflow and data unless an independent domain is justified and approved.
- Do not invent business requirements.
- Do not modify unrelated services.
- Do not access another service's database directly.
- Keep API contracts consistent.
- Use DTOs for API communication.
- Write tests for new business logic.
- Do not introduce unnecessary dependencies.
- Do not expose internal microservice ports publicly.

## Documentation Rules

After every meaningful change:

- Update affected service documentation.
- Update API documentation when API contracts change.
- Update database documentation when schemas change.
- Update DEVELOPMENT_STATUS.md.
- Update CHANGELOG.md when appropriate.
- Record important architectural decisions in DECISIONS.md.

## Task Execution

For each task:

1. Identify the affected service.
2. Identify related requirements.
3. Inspect dependencies.
4. Explain the implementation plan.
5. Implement the smallest coherent change.
6. Run relevant tests.
7. Report test results.
8. Update documentation.

If requirements are ambiguous or conflict with existing architecture, ask for clarification before making significant changes.

Never claim a task is complete without verifying it.

The initial repository inventory found no application source code. Read docs/DEVELOPMENT_STATUS.md and docs/DECISIONS.md for the current proposal and approval state before starting implementation. Do not treat the proposed architecture as approved until the user confirms it.