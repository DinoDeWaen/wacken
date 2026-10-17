---
id: task-93
title: 'US: Show schedule as stage columns'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-12 11:54'
updated_date: '2026-06-12 11:58'
labels:
  - ui
  - schedule
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The group schedule calendar currently renders all selected acts in one horizontal lane. When acts overlap or are very close together, the blocks visually collide and hide text.

As a festival attendee, I want the schedule to be split into columns per stage, with Louder and Harder kept next to each other and shown first, so that overlapping acts are readable and their stage/time relationship is clear.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given selected acts overlap in time on different stages, when the group schedule is shown, then the acts are rendered in separate stage columns instead of on top of each other.
- [x] #2 Given the schedule contains Louder and Harder stage acts, when stage columns are ordered, then Louder and Harder are adjacent and appear before the other stage columns.
- [x] #3 Given the schedule has walking-time markers, when the column layout is shown, then walking time is still visible between consecutive selected acts without covering performance block text.
- [x] #4 Given a performance block is tapped, when the stage-column layout is used, then the existing schedule detail and manual alternative selection flow still opens.
- [x] #5 Relevant automated tests and Android compile checks pass.
- [x] #6 README, business requirements, ADR, and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected the current ScheduleActivity calendar rendering and layout helper tests.
2. Added focused app unit tests for stage-column ordering and per-stage block placement, including Louder/Harder first and different-stage overlaps in separate columns.
3. Refactored the schedule calendar UI to render a horizontally scrollable stage-lane grid with a fixed time column, stage headers, per-stage block columns, and walking markers kept in the time column so they do not cover block text.
4. Kept block tap behavior wired to the existing detail/manual alternative selection flow.
5. Updated README and business requirements to document stage-column calendar behavior.
6. Ran focused app tests, full domain/application/infrastructure/app validation, Android debug compile, and git diff whitespace checks.
Architecture impact: not architecture-significant; this is Android presentation/layout behavior within the existing ScheduleActivity and app tests. No ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Updated the group schedule UI from a single full-width lane to a horizontally scrollable stage-column calendar. Selected acts now render in the column for their stage, so overlapping or near-overlapping acts on different stages remain readable. Louder and Harder are ordered first and adjacent when present.

Walking-time markers were moved into the fixed time column as compact labels so they remain visible without covering performance block text. Existing block tap behavior still opens the decision detail and manual alternative selection flow.

## Acceptance criteria validation

- AC1: Covered by ScheduleCalendarLayoutTest assigning overlapping different-stage acts to different columns, and implemented in ScheduleActivity slot left positioning by stage column.
- AC2: Covered by ScheduleCalendarLayoutTest ordering Louder and Harder first and adjacent.
- AC3: Implemented by rendering walking markers in the fixed time column instead of across performance blocks.
- AC4: Preserved because slotView still attaches showDecisionDetails(slot) as the block click handler.
- AC5: Automated validation passed.
- AC6: README and business requirements were updated; ADR and diagram impact are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleCalendarLayoutTest --tests be.wacken.planner.ScheduleBlockContentTest` passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac` passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac` passed.
- `git diff --check` passed.

### Manual validation

- Not run on an installed device in this environment. Installed-device visual validation remains recommended after the next APK build/release.

## TDD / BDD / approval-test evidence

- Added focused app unit tests for the externally visible layout rules: stage ordering and different-stage column placement.
- No approval baseline was needed because this is an intentional UI layout change, not legacy refactoring.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: updated the schedule capability wording to describe the stage-column calendar schedule.

## Business requirements impact

Business requirements impact: updated BR-063a to define stage-column calendar behavior and Louder/Harder first-stage ordering.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Added stage-column ordering and column-index calculations to ScheduleCalendarLayout.
- Rendered ScheduleActivity day calendars as horizontally scrollable stage grids.
- Kept walking markers visible in the time column.
- Added layout tests for stage order and overlapping different-stage placement.
- Updated README and business requirements.

## Risks and follow-up

- Horizontal scrolling is required when many stages are present; installed-device UAT should confirm the column width feels right on the target Android devices and BlueStacks.
<!-- SECTION:NOTES:END -->
