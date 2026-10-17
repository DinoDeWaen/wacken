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
   - Follow `backlog/docs/business-requirements.md`.
   - Use `backlog/docs/business-requirements.md` for project context, business requirements, and roadmap.
   - Follow the active Backlog.md task.

## Which docs to read when

Do not read every linked document by default. Read only the documents listed below for the current task.

- For `backlog/docs/business-requirements.md`, read `Read Me First` first. Then read only the business rules, workflows, edge cases, and open questions relevant to the active task. Do not read the whole file unless the task affects product scope, roadmap, cross-cutting business behavior, or multiple capability areas.
- Starting any implementation task: read `backlog/docs/development-ways-of-working.md`, `backlog/docs/agent-execution-guardrails.md`, and the relevant parts of `backlog/docs/business-requirements.md`.
- Creating or splitting stories: read `backlog/docs/story-writing-guidelines.md`.
- Adding or changing behavior: read `backlog/docs/testing-strategy.md` and `backlog/docs/technical-quality-guidelines.md`.
- Touching architecture, dependencies, persistence, APIs, deployment, security, or module boundaries: read `backlog/docs/architecture-guidelines.md`, `backlog/docs/architecture-decision-rules.md`, and `backlog/docs/delivery-governance.md`.
- Updating diagrams: read `backlog/docs/diagramming-guidelines.md`.
- Reviewing code: read `backlog/docs/code-review-checklist.md`.

## Non-negotiable workflow

### Backlog.md is the task source of truth

- Use `backlog task list --plain` to list tasks.
- Use `backlog task <id> --plain` to read a task.
- Use `backlog search "topic" --plain` to search.
- Use `backlog task create ...` to create tasks.
- Use `backlog task edit ...` to update tasks.
- Never edit task markdown files directly.
- Never manually change task checkboxes, status, frontmatter, plans, or notes.
- Use `backlog/docs/agent-execution-guardrails.md` as the canonical source for Backlog.md CLI details.

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
- Update `README.md` only when setup, public behavior, architecture, commands, configuration, troubleshooting, or diagrams materially change.
- Use Mermaid in Markdown documentation when diagrams improve understanding. Use C4-style Mermaid diagrams for architecture.

### After implementation

- Run relevant tests, linting, formatting, and type checks.
- Update the implementation plan so it reflects what was actually done, including deviations.
- Check acceptance criteria through the CLI only.
- Add PR-ready implementation notes using the CLI.
- Include the validation package defined in `backlog/docs/delivery-governance.md`.
- Use the canonical impact-note wording from `backlog/docs/delivery-governance.md` for README, business requirements, diagram, and ADR impact.
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

`README.md` is the project entry point for humans and agents. Keep it up to date when setup, public behavior, architecture, commands, configuration, troubleshooting, or diagrams materially change.

The README must use Mermaid for diagrams. Architecture diagrams must be C4-style where possible.

The README must contain:

- Project purpose and current MVP.
- Setup from scratch.
- Configuration and local dependencies.
- Build, run, and test commands.
- Basic architecture and package/module map using Mermaid diagrams, preferably C4-style for architecture.
- Links to ways of working, testing strategy, diagramming guidelines, architecture guidelines, and ADRs.
- Troubleshooting notes.

If a task does not require a README change, use the canonical no-impact wording from `backlog/docs/delivery-governance.md`.

## Requirement drift rule

`backlog/docs/business-requirements.md` is the business source of truth. `README.md` describes the current implemented system.

When a task changes public behavior, product scope, architecture, persistence, auth, sync, user workflows, business rules, capabilities, non-goals, or open questions, update the affected source of truth. If `README.md` changes and `business-requirements.md` does not, or vice versa, task notes must explain why using the validation package in `backlog/docs/delivery-governance.md`.

## Architecture decision and approval rule

Before making an architecture-significant change, check existing decisions when present, `backlog/docs/architecture-decision-rules.md`, and `backlog/docs/delivery-governance.md`.

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
