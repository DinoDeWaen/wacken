# ADR 0002: JVM Test And Coverage Gates

## Status

Accepted

## Context

Wacken Planner 2026 requires TDD for new behavior and early coverage gates for the inner architecture modules. Task `task-2` requires:

- JUnit 5 tests for the `domain` and `application` modules.
- Mockito or fakes available for test code.
- Coverage gates that fail below 80% for `domain` and 70% for `application`.

The current scaffold has no business behavior yet, so the first test setup should prove the tooling without adding premature production abstractions.

## Decision

Configure the `domain` and `application` Java modules with:

- JUnit 5 for JVM unit tests.
- Mockito for tests that need mocks, while simple fakes remain acceptable.
- JaCoCo coverage verification.
- Coverage thresholds:
  - `domain`: 80% instruction coverage.
  - `application`: 70% instruction coverage.

Wire module `test` tasks so coverage verification runs after tests, and wire `check` to coverage verification for CI-friendly validation.

## Consequences

Positive:

- Future domain/application behavior has a ready TDD path.
- Coverage gates protect the inner architecture modules from untested business logic.
- Mockito is available without forcing mocks where fakes are clearer.

Negative / trade-offs:

- Placeholder proof tests exist until real domain/application behavior replaces them.
- Coverage gates may need exclusions or refinement when generated code or non-business scaffolding appears.
- Dependency resolution requires Gradle cache/network access.

## Alternatives considered

- Configure only JUnit 5 without coverage: rejected because task `task-2` explicitly requires coverage gates.
- Add a full shared Gradle convention plugin now: rejected because the build is still small and the minimal root configuration is easier to understand.
- Configure Android instrumentation coverage now: rejected because current acceptance criteria target domain/application JVM tests.

## Links

- Related task: task-2
- Related docs: `backlog/docs/testing-strategy.md`, `backlog/docs/technical-quality-guidelines.md`, `README.md`
