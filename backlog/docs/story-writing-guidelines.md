# Story Writing Guidelines

## Goal

Stories must be small, testable, business-oriented units of work. A story should be small enough for one focused coding session and one pull request.

A good story explains:

- Why the work matters.
- Who benefits from it.
- What observable behavior must exist.
- Which acceptance criteria prove it is complete.
- Which tests or examples should demonstrate the behavior.
- What is explicitly out of scope.

For this project, stories should default to minimal implementation depth. Add
architecture, database, adapter, or production-catalog scope only when the story
explicitly needs it.

## Backlog.md rule

All task changes must go through the Backlog.md CLI.

Do not edit files in `backlog/tasks/` directly.

Task creation belongs to the discovery/planning phase. At creation time, provide
only title, description, acceptance criteria, and optional labels, priority, or
assignee. Do **not** add an implementation plan when creating a task. The plan
is added only after the task is started and assigned.

Use examples such as:

```bash
backlog task create "Allow user to deactivate account" \
  -d "As an administrator, I need to deactivate a user account so that access can be removed without deleting historical records." \
  --ac "Given an active user, when an administrator deactivates the account, then the user can no longer log in" \
  --ac "Given a deactivated user, when historical records are viewed, then the user identity remains visible" \
  --ac "The behavior is covered by automated tests" \
  --ac "README is updated, or implementation notes explain why no README update was needed"
```

## Story template

Use this structure conceptually when creating Backlog.md tasks:

```markdown
# Title

## Business value

Why does this matter?

## User story

As a [user/stakeholder],
I want [capability],
so that [business outcome].

## Scope

In scope:
- ...

Out of scope:
- ...

## Acceptance criteria

- Given ..., when ..., then ...
- Given ..., when ..., then ...
- Error and edge cases are handled.
- Relevant tests are added or updated.
- README is updated when setup, commands, architecture, public behavior, or troubleshooting changes.

## Notes

Additional constraints or context. Avoid implementation design unless necessary.
```

## Phase Discipline

- Creation phase: title, description, acceptance criteria, labels, priority, and
  assignee.
- Implementation phase: move task to `In Progress`, assign it, inspect the
  codebase, then add the implementation plan.
- Wrap-up phase: add implementation notes, check acceptance criteria, document
  validation, and set the task to `Done`.

If implementation requires scope that is not in the acceptance criteria, update
the acceptance criteria first or create a follow-up task.

## Acceptance criteria quality bar

Acceptance criteria must be:

- Observable.
- Testable.
- Specific.
- Business-readable.
- Independent from implementation details where possible.

Prefer:

```text
Given a customer with an expired subscription,
when they try to access premium content,
then access is denied and the renewal message is shown.
```

Avoid:

```text
Add if statement in SubscriptionService.
```

## Required documentation acceptance criteria

Every task that changes setup, test commands, architecture, public behavior, or troubleshooting must include a README-related acceptance criterion.

Use one of these forms:

- `README documents the new setup/test/architecture behavior.`
- `Implementation notes explain why no README update was needed.`

Architecture-significant tasks must also include an ADR-related acceptance criterion.

Tasks that add or change diagrams must include a diagram-impact acceptance
criterion or implementation note.

## BDD connection

Every story with user-visible or business-visible behavior should contain at least one BDD-style acceptance criterion.

For complex flows, create or update executable BDD scenarios in the test suite.

## Legacy refactoring connection

Every story that refactors legacy code must include acceptance criteria for behavior preservation.

Use criteria such as:

- `Existing observable behavior is captured with approval or characterization tests before refactoring.`
- `Approval baselines are reviewed and volatile values are normalized.`
- `Any intentional behavior change is covered by new BDD/TDD tests and explicit acceptance criteria.`

## Task splitting rules

Split stories when:

- The behavior crosses multiple independent workflows.
- The implementation would touch many unrelated areas.
- Acceptance criteria become hard to review.
- There are multiple business outcomes.
- There is a large technical foundation and a separate user-facing feature.

Do not split only by technical layer unless needed. Prefer vertical slices that deliver observable behavior.

## Dependency rules

- Create foundational tasks first.
- Do not make a task depend on a future task.
- Prefer independent vertical slices.
- If dependency is unavoidable, record it via the Backlog.md CLI.

## Story creation checklist

Before creating a task, verify:

- The title is clear.
- The description explains why the task exists.
- The acceptance criteria describe observable outcomes.
- Test expectations are explicit.
- README impact is explicit.
- ADR impact is explicit when architecture is affected.
- The story is small enough for one PR.
- The story does not smuggle in unrelated refactoring.


## Architecture approval acceptance criteria

If a story may require an architecture-significant change, add explicit acceptance criteria for approval and ADR handling.

Example acceptance criteria:

- [ ] Architecture impact is assessed before implementation.
- [ ] If an architecture-significant change is needed, explicit approval is requested before coding.
- [ ] If approved, an ADR is created or updated.
- [ ] README architecture links or explanations are updated when needed.

## Validation acceptance criteria

Every story should include validation expectations.

Recommended acceptance criteria:

- [ ] Automated tests prove the behavior or protect the refactoring.
- [ ] Manual test steps are documented in implementation notes.
- [ ] The implementation plan is updated to reflect what was actually done.
- [ ] README impact is handled.
- [ ] The change is ready for merge to `main` only after the validation package is complete.
