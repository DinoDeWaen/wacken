---
id: task-95
title: 'UI: Show compact start and end times inside schedule blocks'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-12 12:10'
updated_date: '2026-06-12 12:14'
labels:
  - ui
  - schedule
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
In the stage-column schedule, the external time labels become hard to read when multiple selected acts overlap or are close together.

As a festival attendee, I want each performance block to show the start time as a small first line and the end time as a small last line, so that the exact time range remains clear inside the block even when the shared time axis is crowded.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a schedule block is rendered, when it is visible in the calendar, then the first line in the block is the compact start time.
- [x] #2 Given a schedule block is rendered, when it is visible in the calendar, then the last line in the block is the compact end time.
- [x] #3 Given the block is short, when the time labels are added, then the band and stage remain readable and text stays single-line with ellipsis where needed.
- [x] #4 Relevant automated tests and Android compile checks pass.
- [x] #5 README, business requirements, ADR, and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add ScheduleBlockContent tests for compact start and end time lines.
2. Extend ScheduleBlockContent to expose startTimeLine and endTimeLine from the visible candidate.
3. Update ScheduleActivity slot rendering so the start time is the first small line and end time is the last small line in each block.
4. Update business requirements for in-block compact times; README impact likely none unless wording needs clarification.
5. Run focused app tests, Android compile, full relevant validation, and git diff checks.
6. Close task with validation and impact notes.
Architecture impact: not architecture-significant; this is Android presentation content only. No ADR expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Each schedule performance block now includes its compact start time as the first line and compact end time as the last line. This makes the time range visible inside each stage-column block when the shared time axis is crowded by overlapping acts.

## Acceptance criteria validation

- AC1: Covered by ScheduleBlockContentTest asserting the first content value is the compact start time.
- AC2: Covered by ScheduleBlockContentTest asserting the end time is exposed and rendered as the final block line in ScheduleActivity.
- AC3: Block text still uses single-line TextViews; band and stage remain single-line with ellipsis.
- AC4: Automated validation passed.
- AC5: Business requirements impact is recorded; README/ADR/diagram impact are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleBlockContentTest` passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac` passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac` passed.
- `git diff --check` passed.

### Manual validation

- Not run on an installed device in this environment. Installed-device visual validation remains recommended.

## TDD / BDD / approval-test evidence

- Added focused app test coverage before implementation for compact in-block start/end time content.
- No approval baseline was needed because this is an intentional schedule presentation improvement.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: none, because this is a small schedule presentation refinement and does not change setup, architecture, commands, or high-level product scope.

## Business requirements impact

Business requirements impact: updated BR-064 to state that calendar performance blocks show compact start and end times inside the block.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Extended ScheduleBlockContent with startTimeLine and endTimeLine.
- Rendered start time first and end time last in ScheduleActivity performance blocks.
- Updated ScheduleBlockContentTest for compact time labels.
- Updated BR-064.

## Risks and follow-up

- On very short blocks, installed-device visual UAT should confirm the extra compact time lines still fit comfortably with the selected column width.
<!-- SECTION:NOTES:END -->
