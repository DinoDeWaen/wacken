# ADR 0004: Domain Repository Ports And In-Memory Adapters

## Status

Accepted

## Context

Wacken Planner needs application use cases that can store and retrieve bands, performances, ratings, and stage distances without depending on persistence or Android infrastructure.

Task `task-5` requires repository interfaces without infrastructure details, in-memory implementations for tests and early use cases, and clean dependency direction.

## Decision

Repository interfaces live in the `domain` module:

- `BandRepository`
- `PerformanceRepository`
- `RatingRepository`
- `StageDistanceRepository`

In-memory implementations live in the `infrastructure` module:

- `InMemoryBandRepository`
- `InMemoryPerformanceRepository`
- `InMemoryRatingRepository`
- `InMemoryStageDistanceRepository`

The in-memory adapters are intentionally simple and non-persistent. They are suitable for early application use cases, tests, and local-only MVP behavior until a later task introduces a production persistence decision.

## Consequences

Positive:

- Application code can depend on domain-owned repository contracts.
- Infrastructure details remain outside the domain.
- Early stories can run without selecting a database or storage technology.
- In-memory adapters provide useful fakes for application tests.

Negative / trade-offs:

- In-memory storage does not survive app process restarts.
- Repository keys are minimal and may need refinement when user identity, catalog versioning, and import schemas are finalized.
- A future persistence adapter will need contract-compatible behavior.

## Alternatives considered

- Put repositories in `application`: rejected for this task because the acceptance criteria explicitly says repository interfaces live in the domain layer.
- Implement Android persistence now: rejected because the task asks for in-memory adapters and persistence strategy is not yet decided.
- Add generic repository abstractions: rejected because explicit domain repositories are clearer and easier to evolve.

## Links

- Related task: task-5
- Related docs: `backlog/docs/architecture-guidelines.md`, `backlog/docs/business-requirements.md`
