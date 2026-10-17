# Architecture Decision Rules

## Purpose

Architecture decisions must be explicit when they affect future development, team understanding, coupling, deployability, testing, security, or operations.

Use Architecture Decision Records (ADRs) in `backlog/decisions/`.

## Explicit approval before architecture changes

Before implementing any architecture-significant change, the agent must request explicit human approval.

Do not infer approval from the task existing. Do not proceed only because the implementation seems obvious.

Use the approval-request format in `backlog/docs/delivery-governance.md`.

After approval, create or update the ADR when the decision affects future development.

## When to create or update an ADR

Create or update an ADR when a task introduces or changes:

- Architecture style or boundaries.
- Module/package structure.
- DDD aggregate or bounded-context decisions.
- Hexagonal ports/adapters.
- Database technology or schema strategy.
- External API, messaging, or integration strategy.
- Authentication, authorization, or security design.
- Framework choice or major dependency.
- Testing strategy that affects future work.
- Deployment, observability, or operational model.

Do not create an ADR for trivial implementation details.

## ADR location and naming

Store ADRs in:

```text
backlog/decisions/
```

Use this naming pattern:

```text
0002-short-decision-title.md
```

## ADR template

```markdown
# ADR {{number}}: {{Decision title}}

## Status

Proposed / Accepted / Superseded

## Context

What problem are we solving?
What constraints matter?
What forces are in tension?

## Decision

What did we decide?

## Consequences

Positive:
- ...

Negative / trade-offs:
- ...

## Alternatives considered

- Option A: rejected because ...
- Option B: rejected because ...

## Links

- Related task: task-{{id}}
- Related docs: ...
```

## README link rule

If an ADR changes how the application is understood, set up, tested, or navigated, update `README.md` with a link or short explanation.

If no ADR is needed, record `ADR impact: none, because ...` in the
Backlog.md implementation notes.

## Agent instruction

Before making an architecture-significant change:

1. Check existing ADRs.
2. Prepare options: minimal, standard, maximum.
3. Request explicit human approval.
4. Only implement after approval.

After making an approved architecture-significant change:

1. Create or update an ADR.
2. Update `README.md` if the decision affects navigation, setup, testing, or architecture understanding.
3. Mention approval, ADR impact, and README impact in the Backlog.md implementation notes.
4. Include the decision in the pre-merge validation package.
