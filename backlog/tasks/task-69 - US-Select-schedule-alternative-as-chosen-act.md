---
id: task-69
title: 'US: Select schedule alternative as chosen act'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 07:06'
updated_date: '2026-06-10 08:19'
labels:
  - mvp2
  - ui
  - schedule
  - decision
dependencies:
  - task-68
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want to select an alternative act as the one the group is going to, so the schedule can reflect the group's real decision when we disagree with the generated result.

Business rules: BR-066, BR-067.

In scope:
- Add a select-as-act action for alternatives in the schedule decision detail.
- Update the visible schedule result so the selected alternative becomes the chosen act.
- Keep original ratings and generated decision evidence visible; selection must not rewrite ratings.
- Mark manually selected choices clearly.

Out of scope until clarified:
- Supabase schema changes, cross-device sync of manual choices, permissions, and reset behavior unless explicitly decided during implementation planning.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a schedule decision detail shows alternatives, when I select an alternative as the act we are going to, then that act becomes the chosen act in the visible schedule result.
- [x] #2 Given an alternative is selected, then the previously chosen act remains visible as an alternative or prior generated choice.
- [x] #3 Given a manual choice has changed the visible result, then the UI clearly marks it as manually selected and still shows the original rating evidence.
- [x] #4 Before implementation starts, the task records whether manual choices are local-only or synced to Supabase, who may change them, how they are cleared, and whether regeneration preserves them.
- [x] #5 If persistence, Supabase schema, sync contracts, or permissions change, explicit architecture approval is obtained before coding and an ADR is created or updated when required.
- [x] #6 The selected behavior is covered by BDD or focused domain/application tests plus Android compile validation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Recorded the implementation decision before coding: manual schedule choices are local-only visible overrides in ScheduleActivity, not synced to Supabase, not persisted across app restart, and cleared by leaving/regenerating the schedule screen. Any signed-in user can change their local visible result.
2. Confirmed no persistence, Supabase schema, sync contract, or permission change was made, so architecture approval/ADR was not required.
3. Added `ScheduleManualSelections` with focused tests for selected alternative, previous generated choice visibility, and manual status.
4. Updated ScheduleActivity detail dialogs with select-as-act buttons for alternatives and rerendered the visible calendar result from local state.
5. Kept original ratings and generated decision evidence visible in details.
6. Updated README and business requirements, then ran app tests, Android Java compile, and diff checks.

Deviation: manual selections are intentionally local-only for MVP2. Architecture impact: not architecture-significant; Android presentation and package-local helper state only. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added local manual schedule selection. From a schedule decision detail, a user can tap `Select as act` on an alternative. The calendar rerenders with that alternative as the visible selected act and marks the block as `MANUAL CHOICE`.

Manual choices are local-only visible overrides for the current ScheduleActivity session. They are not persisted, not synced to Supabase, not permissioned, and are cleared by leaving or regenerating the schedule screen.

## Acceptance criteria validation

- AC1: Alternatives in the detail dialog now have a `Select as act` button; selecting one changes the visible calendar block to that alternative.
- AC2: The previous generated choice remains visible in details as `GENERATED CHOICE` after an alternative is selected.
- AC3: Manually selected blocks are marked `MANUAL CHOICE`, and details keep original ratings/evidence visible.
- AC4: The task plan records local-only behavior, who can change it, clear/regeneration behavior, and no Supabase sync/persistence.
- AC5: No persistence, Supabase schema, sync contract, permission, dependency, or backend change was made; architecture approval and ADR were not required.
- AC6: Focused app tests cover manual-selection state, and Android Java compile passed.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `git diff --check`

### Manual validation

- Not run on a physical Android device in this task. Install the APK, open the schedule, tap a block with alternatives, select an alternative, and verify the calendar block changes to `MANUAL CHOICE`.

## TDD / BDD / approval-test evidence

Added focused tests for `ScheduleManualSelections` before wiring the UI: selected alternative becomes visible, previous generated choice remains in details, and manual status is marked.

## Architecture impact

- Architecture-significant change: no. This is Android presentation and package-local UI state only. No schema, API, dependency, domain, persistence, backend, sync contract, permission model, or module-boundary change was introduced.
- Approval received: not required.
- ADR impact: none, because no architecture-significant decision was made.

## README impact

README impact: updated basic functionality to mention selecting alternatives as local visible overrides.

## Business requirements impact

Business requirements impact: updated BR-066 and BR-067 to clarify local visible behavior, added BR-068 for local-only manual schedule choices, and collapsed the related open questions into a future shared-decision question.

## Diagram impact

Diagram impact: none, because the system structure and data flow did not change.

## Commits / logical change list

- Add tested `ScheduleManualSelections` local state helper.
- Add select-as-act buttons for alternatives in schedule details.
- Rerender visible calendar blocks from local manual selection state.
- Mark manual choices and keep generated choice evidence visible.
- Update README and business requirements for local-only manual schedule choices.

## Risks and follow-up

- Manual choices do not sync across devices in MVP2. A future shared-decision story should define Supabase persistence, permissions, reset behavior, and conflict handling if group-wide manual choices are needed.
<!-- SECTION:NOTES:END -->
