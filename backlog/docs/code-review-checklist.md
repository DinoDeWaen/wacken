# Code Review Checklist

Use this checklist only when reviewing changes manually or with Codex. It is not
required reading for normal implementation tasks.

## Task fit

- Does the change satisfy the active Backlog.md task?
- Are all acceptance criteria covered?
- Is there unrelated scope creep?
- Are assumptions or trade-offs documented?

## Tests

- Was TDD followed where meaningful?
- Do tests describe behavior, not implementation details?
- Are important edge cases covered?
- Are BDD scenarios present for business-visible workflows?
- Were relevant tests and checks run?

## DDD and domain model

- Are business concepts named in domain language?
- Are invariants protected in the domain?
- Are value objects/entities used where they clarify behavior?
- Is business logic kept out of controllers, database code, and UI code?
- Is the model too weak, too procedural, or too generic?

## Hexagonal architecture

- Do dependencies point inward?
- Are external systems behind ports/adapters when appropriate?
- Are DTOs mapped at boundaries?
- Is the domain free from framework and persistence concerns?
- Can domain/application behavior be tested without infrastructure?
- Is the architecture depth appropriate for this project, or has the change
  added production structure without a real boundary?

## Clean code

- Are names clear and consistent?
- Are functions/classes small and focused?
- Is nesting controlled?
- Are side effects explicit?
- Is duplication removed where useful?
- Is complexity justified?

## Risk

- Could this break existing behavior?
- Are migration/data/security/performance risks handled?
- Is error handling deliberate?
- Are logs/monitoring needed?
- Is documentation updated where relevant?

## Review output format

When reporting review findings, use:

```markdown
## Summary

## Must fix

## Should fix

## Nice to have

## Missing tests

## Architecture concerns

## Positive observations
```

## README and ADR review

- Does `README.md` still explain how to set up, run, and test the project from scratch?
- Does the README contain a basic architecture map a junior developer can follow?
- Does the README link to ways of working, testing strategy, architecture guidelines, and ADRs?
- If the task changed setup, commands, architecture, behavior, or troubleshooting, was the README updated?
- If no README change was needed, do task notes use `README impact: none, because ...`?
- If the task made an architecture-significant decision, was an ADR created or updated?

## Legacy refactoring review

- Was current behavior protected with approval or characterization tests before refactoring?
- Were volatile outputs normalized in approval tests?
- Is any behavior change intentional and covered by acceptance criteria?
- Are new behaviors covered with BDD/TDD tests rather than only snapshots?


## Delivery governance review

- [ ] The task was updated through the Backlog.md CLI, not by editing task files directly.
- [ ] The implementation plan reflects what was actually done, including deviations from the original plan.
- [ ] The implementation notes explain how to test the change.
- [ ] Automated test commands and results are documented.
- [ ] Manual validation steps are documented when relevant.
- [ ] Architecture-significant changes had explicit approval before implementation.
- [ ] ADRs were created or updated for approved architecture-significant changes.
- [ ] README, diagram, and ADR impact use the canonical wording from `delivery-governance.md`.
- [ ] The branch is made of small, coherent commits or has an equivalent logical change list.
- [ ] Nothing is merged to `main` before the validation package is complete.
