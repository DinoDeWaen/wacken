# Delivery Governance

## Purpose

This document defines approval, validation, and merge-readiness evidence for
agent work. Use it when closing a task or preparing a change for review.

## Non-Negotiable Rules

- Do not merge directly to `main`.
- Do not make architecture-significant changes without explicit approval.
- Do not mark a task `Done` until the task contains accurate implementation
  notes and validation evidence.
- Keep commits small when commits are part of the workflow, or provide an
  equivalent logical change list.

## Architecture Approval Gate

Before implementing an architecture-significant change, stop and request
approval.

Architecture-significant means a change affects:

- Architecture style or layer boundaries.
- Hexagonal ports and adapters.
- DDD bounded contexts, aggregates, entities, value objects, services, or events.
- Module/package/folder structure.
- Framework choice or major dependency.
- Database schema, persistence model, migrations, or transaction boundaries.
- External API contracts, messaging, integration, security, deployment, or
  operations.
- Test strategy that affects future development.
- Ownership of domain rules, policy selection, category classification,
  invariants, rule engines, or internal configuration, especially when a change
  would make callers or external boundaries construct or supply them.

## Required Architecture Approval Format

```markdown
## Architecture approval request

Task: task-{{id}} - {{title}}

### Proposed architectural change

### Why it is needed

### Alternatives considered

1. Minimal option:
2. Standard option:
3. Maximum option:

### Recommended option

### Impact

- Domain model:
- Application layer:
- Ports/adapters:
- Infrastructure:
- Tests:
- README:
- Diagrams:
- ADR:

### Approval needed

Please approve, reject, or choose an alternative before implementation continues.
```

Implementation may continue only after approval is given.

## Validation Package

Write validation evidence into Backlog.md implementation notes through the CLI.

Use `--append-notes` for progress updates while implementing, and replace with
final PR-ready notes using `--notes` when closing the task.

```bash
backlog task edit <id> --append-notes $'- Implemented X\n- Added tests'
backlog task edit <id> --notes $'## Implementation summary\n\n- Final outcome...'
```

Implementation notes should be human-friendly and PR-ready:

- Lead with the outcome.
- Use short paragraphs or Markdown bullets.
- Include tests, manual validation, README/diagram/ADR impact, and risks.
- Avoid a single long line of text.

Canonical impact-note wording:

- `README impact: updated ...`
- `README impact: none, because ...`
- `Diagram impact: updated ...`
- `Diagram impact: none, because ...`
- `ADR impact: created ...`
- `ADR impact: updated ...`
- `ADR impact: none, because ...`

Required sections:

```markdown
## Implementation summary

## Acceptance criteria validation

## How to test

### Automated tests

### Manual validation

## TDD / BDD / approval-test evidence

## Architecture impact

- Architecture-significant change:
- Approval received:
- ADR:

## README impact

## Diagram impact

## Commits / logical change list

## Risks and follow-up
```

## Implementation Plan Finalization

Before marking a task done, update the task plan so it reflects actual work:

- Final implementation steps.
- Final test strategy.
- Final architecture impact.
- Deviations from the original plan.
- Approval and ADR outcome.

Example:

```bash
backlog task edit <id> --plan $'1. Added approval baseline\n2. Implemented domain change through TDD\n3. Ran automated tests\n4. Updated README\n\nDeviation: adapter wiring was required after inspection. Architecture approval was requested and approved.'
```

## Merge-Readiness Checklist

Do not merge unless:

- Task is linked to the change.
- Acceptance criteria are checked through the CLI.
- Implementation notes contain the validation package.
- Relevant tests pass.
- README impact uses the canonical wording.
- Diagram impact uses the canonical wording.
- ADR impact uses the canonical wording.
- Architecture approval is present when required.
- Risks and follow-up are documented.
