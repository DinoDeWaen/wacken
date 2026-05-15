# Architecture Guidelines: DDD And Hexagonal Design

## Architecture Philosophy

Protect business logic from technical details.

Use Domain-Driven Design to model business concepts and Hexagonal Architecture
to keep frameworks, databases, messaging, files, UI, and external APIs at the
edges.

Business requirements define observable behavior; they do not override domain
ownership or dependency direction. If a requirement would make callers or
external boundaries construct or supply domain categories, policies, updaters,
invariants, rule engines, or internal configuration, stop and request a user
decision before implementation.

The depth of implementation must match project complexity.

## Project Complexity Scale

At project start, choose and document the current architecture depth: minimal,
standard, or maximum. If the requirements do not yet justify a deeper structure,
default to **minimal but explicit** architecture.

For a new application, record the current scope in `README.md` and
`business-requirements.md`:

- Selected project stack.
- Main business capability.
- External systems, if any.
- Persistence, UI, APIs, jobs, messaging, deployment, and operations needs.
- Constraints that affect architecture.

Default minimal guidance:

- Use clear domain language.
- Keep business rules in plain code in the selected stack.
- Keep tests around behavior.
- Avoid adding packages, ports, adapters, value objects, domain events, or
  infrastructure unless the active task creates a real need.

If the product grows real external boundaries, move to **standard**
architecture:

- Application use cases.
- Domain policies/services where useful.
- Ports for external capabilities.
- Adapters for databases, APIs, messages, files, or UI frameworks.
- Tests at domain, use-case, and adapter boundaries.

Use **maximum** architecture only for a true production system with multiple
bounded contexts, persistence, messaging, operations, security, or high-risk
business workflows.

## Minimal Form

Use by default for new projects until requirements justify more structure.

Required:

- Domain names used consistently.
- Business rules covered by tests.
- No framework or database dependency inside business rules.
- Simple seams only where they clarify behavior or enable testing.

Typical shape:

```text
src/main/java/<package>/
  domain/application classes in the selected stack
```

## Standard Form

Use when the task introduces real external boundaries.

Required:

- Explicit domain model with entities, value objects, policies, or services
  where useful.
- Application/use-case layer orchestrates domain behavior.
- Ports define external capabilities.
- Adapters implement ports for databases, APIs, messaging, files, UI, or
  frameworks.
- DTOs are mapped at boundaries.
- Domain remains framework-independent.

Typical shape:

```text
src/
  domain/
  application/
    ports/in/
    ports/out/
  adapters/
    inbound/
    outbound/
  bootstrap/
```

## Maximum Form

Use only for complex, long-lived, or high-risk production systems.

Required:

- Explicit bounded contexts.
- Aggregates with protected invariants.
- Value objects for meaningful domain concepts.
- Domain events where they clarify business state changes.
- Anti-corruption layers for external systems.
- Contract tests for adapters.
- ADRs for significant architecture choices.
- Clear module boundaries and dependency rules.

## Dependency Rule

Dependencies point inward:

```text
adapters -> application -> domain
```

The domain must not depend on:

- Web frameworks.
- ORM entities.
- Database clients.
- HTTP clients.
- Messaging clients.
- File systems.
- UI frameworks.
- Configuration frameworks.

Outside boundaries must not own domain decisions. Callers, adapters, UI,
persistence, tests, and fixtures may provide business data, but they must not be
required to construct or select domain policies, category rules, updater
strategies, invariants, or internal configuration unless an explicit user
decision and, when architecture-significant, an ADR approve that boundary.

## DDD Tactical Patterns

Use only when they clarify the model:

- Entity: object with identity and lifecycle.
- Value object: immutable concept defined by values.
- Aggregate: consistency boundary around invariants.
- Repository port: collection-like access to aggregates.
- Domain service: business operation that does not naturally belong to one
  entity/value object.
- Domain event: meaningful business fact that happened.
- Policy/specification: named business decision rule.

## Hexagonal Patterns

- Inbound adapters call application use cases.
- Application use cases call domain behavior and outbound ports.
- Outbound adapters implement external dependencies.
- Mapping happens at boundaries.
- Tests can replace outbound ports with fakes.

## Architecture Checklist

Before completing a task, verify:

- Business rules are not hidden in controllers, database queries, UI, or
  framework configuration.
- Business rules, category selection, policy selection, invariants, and internal
  configuration have not leaked into callers or external boundaries without an
  explicit user decision.
- Domain/application logic can be tested without infrastructure.
- External systems are behind ports/adapters when they exist.
- DTOs and persistence entities do not replace the domain model.
- The chosen architecture depth matches this project's current complexity.
- New architecture decisions are documented when significant.
