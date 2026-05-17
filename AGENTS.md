# Agent Instructions

## Purpose

This repository uses **Codex + Backlog.md** to deliver software in small, testable, business-oriented increments.

The agent must always separate three concerns:

1. **How to manage stories and tasks**
   - Follow `backlog/docs/story-writing-guidelines.md`.
   - Use the Backlog.md CLI for all task operations.

2. **How to develop**
   - Follow `backlog/docs/development-ways-of-working.md`.
   - Follow `backlog/docs/technical-quality-guidelines.md`.
   - Follow `backlog/docs/testing-strategy.md`.
   - Follow `backlog/docs/architecture-guidelines.md`.
   - Follow `backlog/docs/architecture-decision-rules.md`.
   - Follow `backlog/docs/delivery-governance.md`.
   - Follow `backlog/docs/diagramming-guidelines.md`.

3. **What to develop**
   - Follow `README.md`.
   - Follow `backlog/docs/project-context.md`.
   - Follow `backlog/docs/business-requirements.md`.
   - Follow `backlog/docs/mvp-roadmap.md`.
   - Follow the active Backlog.md task.

## Non-negotiable workflow

### Backlog.md is the task source of truth

- Use `backlog task list --plain` to list tasks.
- Use `backlog task <id> --plain` to read a task.
- Use `backlog search "topic" --plain` to search.
- Use `backlog task create ...` to create tasks.
- Use `backlog task edit ...` to update tasks.
- Never edit task markdown files directly.
- Never manually change task checkboxes, status, frontmatter, plans, or notes.

### Before implementation

1. Read this `AGENTS.md`.
2. Read `README.md`.
3. Read the active task using `backlog task <id> --plain`.
4. Read the relevant files in `backlog/docs/`.
5. Inspect the codebase.
6. Set the task to `In Progress` and assign it to yourself.
7. Add an implementation plan to the task using the CLI.
8. Check whether the task requires an architecture-significant change.
9. If architecture-significant, request explicit human approval before coding.
10. Only then start changing code.

### During implementation

- Work on one task at a time.
- Stay inside the acceptance criteria.
- For new features and new behavior, use BDD for externally visible behavior and TDD for implementation: red, green, refactor.
- For legacy refactoring, first add approval/characterization tests that capture current behavior before changing structure.
- Keep the domain model protected from frameworks and infrastructure.
- Use hexagonal architecture: domain/application inside, adapters outside.
- Prefer the smallest design that preserves clean boundaries.
- Do not make architecture-significant changes without explicit human approval.
- Use multiple small commits when the environment allows commits, and commit completed tasks promptly before starting unrelated follow-up work.
- Update `README.md` when setup, test commands, architecture, public behavior, troubleshooting, or diagrams change.
- Use Mermaid in Markdown documentation when diagrams improve understanding. Use C4-style Mermaid diagrams for architecture.

### After implementation

- Run relevant tests, linting, formatting, and type checks.
- Update the implementation plan so it reflects what was actually done, including deviations.
- Check acceptance criteria through the CLI only.
- Add PR-ready implementation notes using the CLI.
- Include a validation package: what changed, how to test it, automated checks run, manual validation, README impact, ADR impact, approval status, and risks.
- Mention README impact in implementation notes: either updated, or not needed with a reason.
- Mention diagram impact in implementation notes: either updated, or not needed with a reason.
- Do not merge to `main` until the validation package is complete.
- Set the task to `Done` only when all Definition of Done items are satisfied.

## Engineering baseline

All applications must use:

- **TDD** as the default implementation loop for new code and behavior.
- **BDD** for acceptance criteria and externally visible behavior.
- **Approval/characterization tests** for safe legacy refactoring before behavior is changed.
- **DDD** for language, business rules, invariants, and domain modeling.
- **Hexagonal architecture** to isolate domain/application logic from infrastructure.

The depth of each practice depends on complexity:

- Simple application: minimal but explicit form.
- Medium application: standard form.
- Complex application: full form.

See:

- `backlog/docs/testing-strategy.md`
- `backlog/docs/architecture-guidelines.md`
- `backlog/docs/architecture-decision-rules.md`
- `backlog/docs/delivery-governance.md`
- `backlog/docs/diagramming-guidelines.md`
- `backlog/docs/technical-quality-guidelines.md`

## Living README rule

`README.md` is the project entry point for humans and agents. Keep it up to date after every completed task.

The README must use Mermaid for diagrams. Architecture diagrams must be C4-style where possible.

The README must contain:

- Project purpose and current MVP.
- Setup from scratch.
- Configuration and local dependencies.
- Build, run, and test commands.
- Basic architecture and package/module map using Mermaid diagrams, preferably C4-style for architecture.
- Links to ways of working, testing strategy, diagramming guidelines, architecture guidelines, and ADRs.
- Troubleshooting notes.

If a task does not require a README change, implementation notes must explicitly say why.

## Architecture decision and approval rule

Before making an architecture-significant change, check `backlog/decisions/`, `backlog/docs/architecture-decision-rules.md`, and `backlog/docs/delivery-governance.md`.

Architecture-significant changes require explicit human approval before implementation.

After making an approved architecture-significant change, create or update an ADR in `backlog/decisions/` and link or summarize it from `README.md` when it helps future developers.

## Conflict rule

If product scope, architecture quality, delivery speed, or task acceptance criteria conflict, stop and explain the trade-off. Propose the smallest safe option before implementing.

## Review behavior

When asked to review code, review against:

- The active Backlog.md task acceptance criteria.
- `README.md` accuracy.
- `backlog/docs/code-review-checklist.md`.
- `backlog/docs/technical-quality-guidelines.md`.
- `backlog/docs/testing-strategy.md`.
- `backlog/docs/architecture-guidelines.md`.
- `backlog/docs/architecture-decision-rules.md`.
- `backlog/docs/diagramming-guidelines.md`.

Focus on correctness, missing tests, business-rule placement, unnecessary complexity, hidden coupling, regression risk, README drift, missing architecture decisions, missing architecture approval, stale implementation plans, and missing pre-merge validation evidence.
