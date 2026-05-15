# Testing Strategy: BDD, TDD, and Approval Tests

## Testing philosophy

Tests are executable specifications. They should explain what the system does, protect business behavior, and make refactoring safe.

Use the right loop for the type of work:

- **New features/new behavior**: start from BDD-style acceptance scenarios, then use TDD for implementation.
- **Legacy refactoring**: start with approval/characterization tests that capture current behavior, then refactor under that safety net.
- **Bug fixes**: write a failing regression test that proves the bug, then fix it.

For small or early-stage projects, default to the minimal useful test set:

- Approval/characterization tests for legacy behavior changes.
- Cucumber BDD scenarios for business-visible rules.
- Focused unit tests for domain policies and edge cases.

Add adapter, contract, integration, security, or performance tests only when the
project grows beyond a simple in-process application or introduces real external
boundaries.

## New features: BDD outside, TDD inside

For new behavior, the default loop is:

1. Translate the Backlog.md acceptance criteria into BDD-style Given/When/Then scenarios.
2. Pick the smallest scenario or rule.
3. Write the smallest failing test.
4. Implement the smallest production code to pass.
5. Refactor while tests stay green.
6. Repeat until all acceptance criteria are covered.

### TDD loop

1. Red: write a failing test.
2. Green: write the smallest code to pass.
3. Refactor: improve design while tests stay green.

### BDD guidelines

Use Given/When/Then to describe externally visible behavior.

Good scenario:

```gherkin
Scenario: Administrator deactivates a user account
  Given an active user account exists
  When an administrator deactivates the account
  Then the user can no longer log in
  And historical records still show the user's identity
```

BDD scenarios should be:

- Business-readable.
- Focused on behavior, not UI mechanics.
- Stable enough to automate.
- Linked to acceptance criteria.
- Written in domain language.

## Legacy refactoring: approval and characterization tests first

When changing legacy code, do not start by redesigning it. First protect the current behavior.

Required loop:

1. Identify the observable behavior that must not change.
2. Add characterization tests or approval tests around that behavior.
3. Run the tests and approve the current output as the baseline.
4. Refactor in small steps.
5. Run approval/characterization tests after each meaningful step.
6. Only change behavior when a Backlog.md acceptance criterion explicitly requires it.
7. Add new BDD/TDD tests for intentionally changed or newly added behavior.

### When to use approval tests

Use approval tests when:

- Existing behavior is complex or poorly understood.
- Output is large, structured, textual, JSON, XML, HTML, CSV, or report-like.
- The goal is refactoring without changing behavior.
- The current code has low test coverage.
- The safest question is: “Did the observable output change?”

### When to use characterization tests

Use characterization tests when:

- Behavior can be asserted with normal unit/integration tests.
- You need to document the current behavior before refactoring.
- There are known edge cases or suspicious bugs.

### Approval-test rules

Approval tests must:

- Capture stable, meaningful output.
- Normalize volatile values such as timestamps, UUIDs, random numbers, generated IDs, and ordering when ordering is not part of the behavior.
- Be committed only after the baseline is intentionally reviewed.
- Fail loudly when observable behavior changes.
- Be replaced or complemented by clearer domain tests when the design becomes understandable.

Approval tests must not:

- Freeze accidental behavior forever if the task requires a behavior change.
- Approve noisy output without review.
- Hide business decisions inside snapshots without readable explanation.

## Always apply testing, scaled to complexity

### Minimal form

Use for small changes, utilities, simple CRUD, or low-risk behavior.

Required:

- At least one focused automated test before or with the change.
- Test name describes expected behavior.
- Edge case test if the edge case is business-relevant.
- Refactor after passing.
- README test-command update if the command changed.

For legacy code:

- One small characterization test or approval test around the touched behavior.

### Standard form

Use for normal feature work.

Required:

- BDD-style scenarios for acceptance criteria.
- Unit tests for domain behavior.
- Application/use-case tests for orchestration.
- Adapter or integration tests for database/API boundaries when changed.
- Regression test for any bug fix.

For legacy code:

- Approval/characterization baseline before refactoring.
- New TDD tests for newly introduced design seams.

### Maximum form

Use for complex domains, money, risk, security, critical workflows, concurrency, or multi-system behavior.

Required:

- BDD feature scenarios for main happy path and important alternatives.
- Domain unit tests for invariants, policies, edge cases, and state transitions.
- Use-case tests for orchestration.
- Contract tests for ports/adapters or external APIs where useful.
- Integration tests for persistence and messaging.
- Security/performance tests if relevant to acceptance criteria.
- Explicit test data builders or fixtures.

For legacy code:

- Approval tests for full externally visible behavior.
- Characterization tests for risky branches and edge cases.
- Stepwise refactoring with tests run after each step.
- Explicit notes describing what behavior was preserved and what changed intentionally.

## Test pyramid preference

Prefer many fast tests and fewer slow tests:

1. Domain unit tests.
2. Application/use-case tests.
3. Adapter integration tests.
4. API/contract tests.
5. BDD/end-to-end tests for critical flows only.
6. Approval tests for legacy behavior and complex outputs.

## Test quality rules

Tests must:

- Arrange clearly.
- Act once.
- Assert meaningful behavior.
- Avoid testing private implementation details.
- Avoid brittle timing or ordering assumptions unless required.
- Use domain language.
- Separate business behavior from technical setup noise.

## Required final check

Before completing a task, report in implementation notes:

- Which tests were added or updated.
- Whether BDD, TDD, approval tests, or characterization tests were used.
- Which checks were run.
- Any tests not run and why.
- Whether README test instructions were updated or why no update was needed.
