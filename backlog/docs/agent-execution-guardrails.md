# Agent Execution Guardrails

## Purpose

This file contains the non-negotiable safety rules for agent execution. It is
not a step-by-step workflow; use `development-ways-of-working.md` for that.

When instructions conflict, follow the priority order in `AGENTS.md`.

## Golden Rules

### Do

- Read `AGENTS.md`, `README.md`, the active Backlog task, and the relevant docs
  before coding.
- Use the Backlog.md CLI for all task changes.
- Work on one task at a time.
- Keep the implementation plan accurate before and after coding.
- Use BDD/TDD for new behavior.
- Use approval or characterization tests before legacy refactoring.
- Request explicit approval before architecture-significant changes.
- Stop for a user decision when business requirements or acceptance criteria
  would force domain rules, classifications, policy selection, invariants, or
  internal configuration to be supplied by callers or external boundaries.
- Update README, diagrams, ADRs, and task notes when the change affects them.
- Provide validation evidence before marking work done.

### Do Not

- Do not edit files in `backlog/tasks/` directly.
- Do not bypass the Backlog.md CLI for task status, checkboxes, plans, or notes.
- Do not silently expand scope beyond acceptance criteria.
- Do not make architecture-significant changes without explicit approval.
- Do not add dependencies without clear value and approval when architecture-significant.
- Do not refactor legacy code before characterization or approval coverage.
- Do not replace automated behavior tests with manual validation only.
- Do not put business rules in controllers, persistence entities, UI, or
  framework configuration.
- Do not let business wording justify leaking domain categories, updater
  policies, rule engines, invariants, or internal configuration into callers,
  tests, adapters, fixtures, persistence, or UI code without explicit user
  approval.
- Do not let infrastructure dependencies leak into the domain.
- Do not claim validation succeeded if commands were not run.
- Do not mark a task `Done` without validation notes.

## Backlog.md Task Rules

Allowed task operations go through the CLI:

```bash
backlog task list --plain
backlog task <id> --plain
backlog search "topic" --plain
backlog task edit <id> -s "In Progress" -a @agent
backlog task edit <id> --plan $'1. Inspect\n2. Test\n3. Implement\n4. Validate'
backlog task edit <id> --check-ac 1
backlog task edit <id> --notes $'- What changed\n- How it was tested'
```

Never manually edit task markdown files. Direct edits can break Backlog.md
metadata, naming, status tracking, and task relationships.

Use `--plain` whenever viewing tasks, lists, boards, search results, docs, or
decisions so output is AI-readable.

### Backlog.md File Structure

This is read-only context for agents:

- Tasks live in `backlog/tasks/`.
- Drafts live in `backlog/drafts/`.
- Project docs live in `backlog/docs/`.
- Architecture decisions live in `backlog/decisions/`.
- Task files are named like `task-42 - Add GraphQL resolver.md`.

You may inspect files for emergency read-only diagnosis, but never write task
files directly.

### Complete Command Reference

Task discovery:

```bash
backlog task list --plain
backlog task list -s "In Progress" --plain
backlog task list -a @agent --plain
backlog task <id> --plain
backlog search "topic" --plain
backlog search "topic" --type task --plain
backlog search "api" --status "To Do" --plain
backlog search "bug" --priority high --plain
backlog board --plain
```

Task creation:

```bash
backlog task create "Title"
backlog task create "Title" -d "Description"
backlog task create "Title" --ac "Criterion 1" --ac "Criterion 2"
backlog task create "Title" -d "Description" -a @agent -s "To Do" -l backend --priority high
backlog task create "Title" --draft
backlog task create "Subtask title" -p <parent-id>
```

Task modification:

```bash
backlog task edit <id> -t "New title"
backlog task edit <id> -d "New description"
backlog task edit <id> -s "In Progress"
backlog task edit <id> -a @agent
backlog task edit <id> -l backend,api
backlog task edit <id> --priority high
backlog task edit <id> --dep task-1 --dep task-2
backlog task archive <id>
backlog task demote <id>
```

### Acceptance Criteria Edge Cases

Acceptance criteria operations accept repeated flags:

```bash
backlog task edit <id> --ac "New criterion" --ac "Another criterion"
backlog task edit <id> --check-ac 1 --check-ac 2
backlog task edit <id> --uncheck-ac 1 --uncheck-ac 2
backlog task edit <id> --remove-ac 2 --remove-ac 4
backlog task edit <id> --check-ac 1 --uncheck-ac 2 --remove-ac 3 --ac "New criterion"
```

Do not use comma-separated values or ranges:

```bash
# Wrong
backlog task edit <id> --check-ac 1,2,3
backlog task edit <id> --check-ac 1-3
backlog task edit <id> --check 1
```

### Multi-Line CLI Input

Shells do not convert `\n` inside normal quotes. Use real newlines.

Bash/Zsh:

```bash
backlog task edit <id> --desc $'Line 1\nLine 2'
backlog task edit <id> --plan $'1. Inspect\n2. Test\n3. Implement'
backlog task edit <id> --notes $'- Changed X\n- Tested Y'
backlog task edit <id> --append-notes $'- Progress update\n- Next step'
```

POSIX:

```bash
backlog task edit <id> --notes "$(printf 'Line 1\nLine 2')"
```

PowerShell:

```powershell
backlog task edit <id> --notes "Line 1`nLine 2"
```

### Backlog CLI Troubleshooting

| Problem | Fix |
|---|---|
| Task not found | Run `backlog task list --plain` and confirm the task id. |
| Acceptance criterion will not check | Run `backlog task <id> --plain` and use the displayed AC index. |
| Changes are not saved | Confirm you used `backlog task edit`, not direct file edits. |
| Metadata appears out of sync | Re-apply the current value through the CLI, for example `backlog task edit <id> -s "In Progress"`. |

## Architecture Stop Rule

Stop and request approval before implementation if a change affects:

- Architecture style or layer boundaries.
- Module/package structure.
- Ports, adapters, or dependency direction.
- DDD bounded contexts, aggregates, entities, value objects, services, or events.
- Database technology, schema strategy, migrations, or persistence model.
- External APIs, messaging, integrations, security, deployment, or operations.
- Test strategy that affects future development.
- Ownership of domain rules, policy selection, category classification,
  invariants, or configuration, especially when a requirement would make
  callers or external boundaries construct or supply them.

Use `architecture-decision-rules.md` and `delivery-governance.md`.

## Business Requirement Boundary Conflict Rule

Business requirements define what the business must observe, not which layer or
caller owns the domain policy.

If a requirement says or implies that an outside caller, adapter, UI, database,
fixture, or test must provide a category, updater, rule policy, invariant, or
domain configuration object, treat that as a boundary conflict. Stop and ask for
a user decision before implementation.

The decision request must include:

- The observable business behavior being requested.
- The domain logic or configuration that would leak outward.
- A domain-owned alternative that preserves the public contract and dependency
  direction.
- Whether an ADR or public API change would be required.

## Validation Rule

Before a task can be marked done, implementation notes must include:

- What changed.
- Acceptance criteria evidence.
- Automated tests/checks run and result.
- Manual validation when relevant.
- BDD/TDD/approval-test evidence.
- README impact.
- Diagram impact.
- ADR impact.
- Architecture approval status.
- Risks and follow-up.

## README Rule

Update `README.md` when a task changes setup, configuration, commands,
architecture, package/module structure, public behavior, diagrams, or
troubleshooting.

If no README update is needed, say why in implementation notes.
