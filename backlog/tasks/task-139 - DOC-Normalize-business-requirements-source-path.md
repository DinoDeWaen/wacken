---
id: task-139
title: 'DOC: Normalize business requirements source path'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-30 15:48'
updated_date: '2026-06-30 15:58'
labels:
  - documentation
  - requirements
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a developer or agent working on MVP2, I want the repository instructions and links to point to the active business requirements file, so that future work uses the correct source of truth.

Scope: resolve the path drift between `backlog/docs/business-requirements.md` and `backlog/docs/business-requirements-wacken.md`; update repository instructions and references so there is one clear canonical business requirements document.

Out of scope: changing product behavior or adding new requirements.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given AGENTS.md and README are reviewed, when they reference business requirements, then they point to the canonical current file
- [x] #2 Given the canonical file path is resolved, when git status is reviewed, then the business requirements file is tracked consistently without an accidental delete/untracked pair
- [x] #3 README impact is recorded using the canonical wording from delivery-governance.md
- [x] #4 Business requirements impact is recorded using the canonical wording from delivery-governance.md
- [x] #5 Documentation validation commands are recorded in implementation notes
- [x] #6 Given backlog docs and active task references are searched for stale business-requirements.md paths, then active instructions do not point to a missing file
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm the intended canonical business requirements path from AGENTS.md and README.
2. Move the current MVP2-only requirements content back to the tracked canonical path.
3. Search active docs for stale references to missing business requirement paths.
4. Record validation and close the task.

Architecture impact: not architecture-significant; documentation path hygiene only.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Resolved the business requirements path drift by restoring the current MVP2-only requirements content to the canonical tracked file `backlog/docs/business-requirements.md`. This matches `AGENTS.md` and the development docs, and removes the previous deleted-file plus untracked-file state.

## Acceptance criteria validation

- AC1: `AGENTS.md` and development docs reference `backlog/docs/business-requirements.md`, which now exists and contains the current MVP2-only requirements. README does not point to a conflicting requirements path.
- AC2: `git status --short` now shows `backlog/docs/business-requirements.md` as modified instead of a deleted `business-requirements.md` plus untracked `business-requirements-wacken.md`.
- AC3: README impact: none, because README had no stale business requirements path to update for this task.
- AC4: Business requirements impact: none, because this task only normalized the file path for the already-updated requirements content.
- AC5: Documentation validation commands are recorded below.
- AC6: Active docs/instructions now point to an existing canonical requirements file. Historical completed task notes may still mention the temporary filename as past validation evidence, but active instructions do not depend on it.

## How to test

### Automated tests

Not run, because this is documentation-path hygiene only.

### Manual validation

- `git status --short`
- `rg -n "business-requirements-wacken|business-requirements\.md|business requirements" AGENTS.md README.md backlog/docs backlog/tasks/task-136* backlog/tasks/task-137* backlog/tasks/task-138* backlog/tasks/task-139*`
- `rg -n "MVP 3|MVP 4|PDF|printable|travel feasibility|food suggestions|12:00-14:00" backlog/docs/business-requirements.md README.md`

## TDD / BDD / approval-test evidence

Not applicable; no executable behavior changed.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: ADR impact: none, because no architecture decision changed.

## README impact

README impact: none, because README did not contain the stale business requirements path.

## Business requirements impact

Business requirements impact: none, because the task only restored the canonical path for the existing MVP2-only requirements content.

## Diagram impact

Diagram impact: none, because no diagram changed.

## Commits / logical change list

- Restored current requirements content at `backlog/docs/business-requirements.md`.
- Removed the temporary untracked `backlog/docs/business-requirements-wacken.md` path by moving it back to the canonical file.

## Risks and follow-up

None.
<!-- SECTION:NOTES:END -->
