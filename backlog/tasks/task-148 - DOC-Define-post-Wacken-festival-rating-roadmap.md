---
id: task-148
title: 'DOC: Define post-Wacken festival rating roadmap'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-12 07:47'
updated_date: '2026-08-12 07:55'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: The next product phase needs clear business requirements and implementation stories for archived festivals, synced personal band ratings, and future festival prefilling.

Scope: update business-requirements.md with the validated one-group multi-festival direction, move clarified open questions out of scope, keep real-rating sync in scope, and create the needed implementation user stories.

Out of scope: implementing the new festival/rating behavior in code during this task.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Business requirements document the validated festival archive, personal band rating, planning rating, and prefill rules
- [x] #2 Open questions 1-7 are moved out of active open questions or marked out of scope, while real-rating sync remains in scope
- [x] #3 Implementation-ready Backlog.md user stories are created through the CLI
- [x] #4 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Read current business requirements around scope, workflows, business rules, terminology, reporting, edge cases, and open questions.
2. Updated business-requirements.md with the validated post-Wacken direction: one group only, archive-first next festival flow, read-only archives, synced personal band ratings, synced festival planning ratings, synced real rating events, exact-name band reuse, and future fuzzy alias linking as out of first-version scope.
3. Moved open questions 1-7 out of active open questions by recording the validated out-of-scope decisions, while keeping real-rating sync in the post-MVP3 personal band rating scope.
4. Created Backlog.md tasks task-149 through task-157 through the CLI, including one architecture decision task, five first-version post-MVP3 implementation stories, and two future-scope stories.
5. Validated Markdown/task state with focused rg checks, backlog task list, and git diff --check.

Deviation: the final story set includes a foundational architecture task because the requested implementation will change persistence, Supabase contracts, sync, and domain model boundaries. Architecture impact for task-148 itself remains not architecture-significant because only requirements and backlog tasks changed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Updated the business requirements to reflect the validated post-Wacken direction and created the needed Backlog.md implementation stories.

Created tasks: task-149 through task-157. The first implementation step is task-149 because the upcoming work changes festival/rating persistence and Supabase contracts.

## Acceptance criteria validation

- AC1: Business requirements now document festival archive, active/no-active start behavior, reusable bands, personal band rating history, festival planning ratings, and prefill rules in BR-081 through BR-102.
- AC2: Open questions 1-7 are moved into resolved out-of-scope decisions; real post-show rating sync is explicitly in post-MVP3 personal band rating history scope.
- AC3: Implementation-ready Backlog.md stories were created through the CLI: task-149 through task-157.
- AC4: Impact notes are recorded below using delivery-governance wording.

## How to test

### Automated tests

- Not run, because this task changed requirements and backlog metadata only.

### Manual validation

- Ran focused rg checks for post-MVP3 requirements, out-of-scope decisions, and stale scraping/invite language.
- Ran backlog task list --plain to verify created tasks appear on the board.
- Ran git diff --check with no whitespace errors.

## TDD / BDD / approval-test evidence

No code behavior was implemented in this task. The created implementation stories require BDD and domain/application tests where appropriate.

## Architecture impact

- Architecture-significant change: no, task-148 only updates requirements and creates backlog tasks.
- Approval received: not required for this documentation/planning task.
- ADR: not required for this task; task-149 requires an ADR before implementation changes persistence/Supabase contracts.

## README impact

README impact: none, because the README describes the implemented application and this task only defines future post-MVP3 scope.

## Business requirements impact

Business requirements impact: updated backlog/docs/business-requirements.md with the validated post-Wacken festival archive, rating history, planning rating, prefill, and out-of-scope decisions.

## Diagram impact

Diagram impact: none, because no implemented architecture or workflow diagram changed in this documentation/planning task.

## Commits / logical change list

- Updated backlog/docs/business-requirements.md.
- Created task-149 through task-157 via Backlog.md CLI.

## Risks and follow-up

- Follow-up implementation must start with task-149 and receive explicit architecture approval before changing Room, Supabase, migrations, sync, or domain model contracts.
- task-156 and task-157 are future scope and should not block the first post-Wacken implementation.
<!-- SECTION:NOTES:END -->
