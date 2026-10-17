# Technical Quality Guidelines

## General directive

Write code that humans can read, reason about, test, and safely modify.

Clarity beats cleverness. Design must earn its keep.

For a new application, "clean enough" means minimal but explicit: code in the
selected stack, clear domain language, behavior-focused tests, and no production
infrastructure unless the active task requires it.

## Core principles

Prefer:

- Simple, explicit code.
- Clear domain language.
- Short functions and small classes.
- Explicit data flow.
- Narrow interfaces.
- Tests that describe behavior.
- Refactoring in small safe steps.

Avoid:

- Magic behavior.
- Hidden side effects.
- Pattern-for-pattern's-sake.
- Overly generic abstractions.
- Framework leakage into the domain.
- Anemic procedural code when business behavior belongs in the model.
- Mixing API DTOs, persistence entities, and domain objects.
- Large unrelated refactors inside feature tasks.
- Production-style structure that is not justified by the current task.

## Code That Fits In Your Head

Design code so a developer can understand the changed area without loading too
many concepts at once.

Prefer:

- A small number of consistent patterns.
- Explicit data flow.
- Small interfaces.
- Local reasoning over hidden global state.
- Predictable naming and structure across similar features.

Avoid:

- Hidden side effects.
- Deep dependency chains.
- Clever abstractions that require remembering many moving parts.
- Mixing unrelated concepts in one function or class.

## Naming

Names must reveal intent.

Use:

- Domain terms for business concepts.
- Verbs for behavior.
- Nouns for entities, values, policies, and services.
- Consistent names across code, tests, docs, and stories.

Avoid abbreviations unless universally understood.

## Function and class design

A function should do one clear thing.

A class/module should have one clear reason to change.

Prefer:

- Early returns over deep nesting.
- Functions that are short enough to scan comfortably. As a guideline, prefer
  5-20 lines when practical, but do not split code mechanically when it would
  reduce clarity.
- Small pure functions for business rules.
- Explicit dependencies.
- Immutable value objects where useful.

## Error handling

Errors must be deliberate and testable.

- Domain errors should use domain language.
- Infrastructure errors should not leak into domain rules.
- User-facing errors should be understandable.
- Exceptional cases should be covered by tests when business-relevant.

## Dependencies

- Keep high-level policy independent from low-level details.
- Hide databases, HTTP clients, queues, files, and external services behind ports/adapters.
- Avoid global mutable state.
- Avoid deep dependency chains.
- Do not add production dependencies without clear value.

## Refactoring rule

Refactor after tests pass.

Refactoring should improve:

- Naming.
- Duplication.
- Boundaries.
- Simplicity.
- Testability.

Do not refactor unrelated areas unless the task explicitly allows it.

Follow the Boy Scout Rule inside the touched area: leave the code slightly
cleaner than you found it by improving names, clarity, duplication, or structure
without expanding scope.

## Definition of clean enough

Code is clean enough when:

- A new developer can follow the flow without hidden context.
- Business rules are visible and test-covered.
- Framework code is kept at the edges.
- The solution is no more complex than the problem requires.
- Tests protect the behavior and enable safe change.
