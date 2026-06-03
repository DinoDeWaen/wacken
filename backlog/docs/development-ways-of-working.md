# Development Ways Of Working

## Purpose

Use this file when implementing a Backlog.md task. It describes the task
lifecycle. Safety rules live in `agent-execution-guardrails.md`; task closure
evidence lives in `delivery-governance.md`.

## Standard Task Workflow

1. Read `AGENTS.md`.
2. Read `README.md`.
3. Read the active task with `backlog task <id> --plain`.
4. Read `business-requirements.md`.
5. Read only the extra docs relevant to the task.
6. Inspect the codebase.
7. Set the task to `In Progress` and assign it to yourself through the CLI.
8. Check that planned tools and commands are available in the environment.
9. Add an implementation plan through the CLI.
10. Assess architecture impact.
11. If architecture-significant, stop and request explicit approval.
12. Implement using the right test loop.
13. Run relevant checks.
14. Update README, diagrams, ADRs, and docs when the change affects them.
15. Finalize the implementation plan.
16. Check acceptance criteria through the CLI.
17. Add implementation notes with validation evidence.
18. Set the task to `Done` only when the Definition of Done is met.

## Start-Work Command Pattern

```bash
backlog task edit <id> -s "In Progress" -a @codex
```

Then add a plan:

```bash
backlog task edit <id> --plan $'1. Inspect\n2. Test\n3. Implement\n4. Validate'
```

Before writing the plan, quickly verify that expected tools are available, for
example build tools, test runners, package managers, or CLIs referenced by the
task.

## Implementation Plan Requirements

The plan must include:

- Files or areas likely to change.
- Test strategy.
- Design approach.
- Architecture boundary impact.
- Risks or assumptions.
- Whether the task needs minimal, standard, or full treatment.
- Whether legacy behavior needs approval or characterization coverage.
- README, business requirements, diagram, and ADR impact.
- Whether architecture approval is required before coding.

Before marking the task done, update the plan so it describes what actually
happened, including deviations.

## Complexity Scale For The Current Project

Default to **minimal but explicit** until the current project requirements prove
that more structure is useful:

- Domain/application logic in the selected project stack.
- Clear domain names.
- Business rules covered by tests.
- No framework, persistence, or adapter layers unless a task explicitly adds
  them.

Use **standard** depth when a task introduces a real boundary such as
persistence, external APIs, adapters, background jobs, UI workflows, or
cross-module orchestration.

Use **maximum** depth only for a production product with multiple bounded
contexts, high-risk workflows, persistence, messaging, operations, security, or
compliance concerns.

## One-Task Rule

Work on one Backlog.md task per session unless the user explicitly instructs
otherwise.

If extra work is discovered:

- Add acceptance criteria before doing it, or
- Create a follow-up task, or
- Mention it in implementation notes as future work.

## Test Loops

### New Behavior

Use BDD outside and TDD inside:

1. Derive BDD-style scenarios from acceptance criteria.
2. Pick the smallest scenario or business rule.
3. Write the smallest failing test.
4. Make it pass with the smallest useful implementation.
5. Refactor while tests stay green.
6. Repeat until acceptance criteria are covered.

### Legacy Refactoring

Protect behavior first:

1. Identify observable behavior that must not change.
2. Add approval or characterization tests.
3. Review/approve the current baseline.
4. Refactor in small steps.
5. Re-run tests after each meaningful step.
6. Add BDD/TDD tests only for intentional behavior changes.

## Stop Conditions

Stop and ask or document the trade-off when:

- Acceptance criteria conflict.
- Scope would expand beyond the task.
- Architecture approval may be required.
- A new production dependency is needed.
- The task is much larger than expected.
- A business rule is ambiguous.
- Existing architecture prevents a safe implementation.

## Documentation Updates

Update README and docs when setup, commands, architecture, package structure,
public behavior, diagrams, troubleshooting, or important business rules change.

Use:

- `diagramming-guidelines.md` for Mermaid/C4 diagrams.
- `architecture-decision-rules.md` for ADR decisions.
- `delivery-governance.md` for final validation notes and canonical README, business requirements, diagram, and ADR impact wording.

## Completion Checklist

Before marking done:

- Acceptance criteria are met and checked through the CLI.
- Tests were added/updated where meaningful.
- Legacy refactoring is protected by approval/characterization tests.
- Relevant checks pass.
- README, business requirements, diagram, and ADR impact use the canonical wording from `delivery-governance.md`.
- Architecture approval is handled when required.
- Implementation plan reflects actual work.
- Implementation notes explain what changed, how to test it, and remaining risk.
