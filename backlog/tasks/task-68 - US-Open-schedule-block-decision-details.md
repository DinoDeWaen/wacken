---
id: task-68
title: 'US: Open schedule block decision details'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 07:05'
updated_date: '2026-06-10 08:15'
labels:
  - mvp2
  - ui
  - schedule
dependencies:
  - task-67
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want to tap a calendar performance block and see the chosen act plus all alternatives, so I can understand why the group schedule picked that act.

Business rule: BR-065.

In scope:
- Open a detail view or panel from a calendar performance block.
- Show the chosen act clearly.
- Show all alternatives for the same conflict or schedule decision.
- For every shown band, include band name, stage, rating stars, and decision status.

Out of scope:
- Changing the chosen act.
- Persisting manual schedule choices.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a calendar performance block has alternatives, when I tap the block, then a detail view opens for that schedule decision.
- [x] #2 Given the detail view opens, then the chosen act is visibly distinguished from alternatives.
- [x] #3 Given alternatives exist for the decision, then all alternatives are listed with band name, stage, rating stars, and status.
- [x] #4 Given a selected act has no alternatives, then the detail view still opens and clearly shows that no alternatives are available.
- [x] #5 Given I close the detail view, then I return to the same calendar day and scroll position where practical.
- [x] #6 The behavior is covered by focused tests or explicit Android UI validation, and compile validation is run.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected conflict resolution output, `TimelineSlot`, and current ScheduleActivity rendering.
2. Added `ScheduleDecisionCandidate` and extended `TimelineSlot` with immutable decision candidates derived from existing resolution output.
3. Added application tests proving chosen and lost-alternative detail data include band, stage, stars, status, and selected flag.
4. Updated ScheduleActivity so tapping a calendar block opens a detail dialog with chosen act and alternatives.
5. Preserved calendar scroll context by using a modal dialog that closes back to the same schedule screen.
6. Updated README and ran application/app tests, Android Java compile, and diff checks.

Deviation: no physical device UI validation was possible. Architecture impact: not architecture-significant; existing application DTO output and Android presentation only. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Schedule slots now carry read-only decision candidates for the chosen act and alternatives. ScheduleActivity opens a modal detail view when a calendar block is tapped. The detail shows the chosen act first, then alternatives, with band name, stage, time, rating stars, and status.

The generated schedule selection rules were not changed.

## Acceptance criteria validation

- AC1: Calendar performance blocks are clickable and open a detail dialog.
- AC2: The chosen act is shown first and styled distinctly from alternatives.
- AC3: Alternatives are listed with band name, stage, time, rating stars, and status.
- AC4: If a slot has no alternatives, the detail dialog still opens and shows `No alternatives available.`
- AC5: The detail uses a modal dialog, so closing it returns to the same calendar screen/position where practical.
- AC6: Application tests cover detail candidate data; app tests and Android Java compile passed.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `git diff --check`

### Manual validation

- Not run on a physical Android device in this task. Install the APK, open the calendar schedule, tap a block, and verify chosen/alternative details appear.

## TDD / BDD / approval-test evidence

Added application test coverage for the new decision candidate data before relying on it in ScheduleActivity. Existing schedule/conflict tests continue to protect generated schedule behavior.

## Architecture impact

- Architecture-significant change: no. This extends existing application output and Android presentation only. No schema, API, dependency, domain, persistence, backend, or module-boundary change was introduced.
- Approval received: not required.
- ADR impact: none, because no architecture-significant decision was made.

## README impact

README impact: updated basic functionality to mention tapping schedule blocks for chosen act and alternatives.

## Business requirements impact

Business requirements impact: none, because BR-065 already documented this behavior before implementation.

## Diagram impact

Diagram impact: none, because the system structure and data flow did not change.

## Commits / logical change list

- Add `ScheduleDecisionCandidate` application DTO.
- Extend `TimelineSlot` with immutable decision candidates.
- Populate chosen and alternative candidate details from existing conflict resolution output.
- Open a schedule decision detail dialog from calendar blocks.
- Update README basic functionality.

## Risks and follow-up

- Device visual validation remains useful for checking dialog sizing on small screens.
<!-- SECTION:NOTES:END -->
