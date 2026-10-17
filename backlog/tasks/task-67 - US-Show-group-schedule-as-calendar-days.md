---
id: task-67
title: 'US: Show group schedule as calendar days'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 07:05'
updated_date: '2026-06-10 07:40'
labels:
  - mvp2
  - ui
  - schedule
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want the group schedule shown as calendar days with hour lines and performance blocks, so I can quickly understand where the group is going during the festival.

Business rules: BR-063, BR-064.

In scope:
- Replace the current group schedule presentation with a day-based calendar layout.
- Show hour lines for each festival day.
- Position performance blocks by their scheduled time range.
- Show band, stage, and rating stars in each block.

Out of scope:
- Editing the selected act from the calendar block.
- Adding PDF export or travel/lunch MVP3 behavior.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a generated schedule has performances on multiple festival days, when I open the group schedule, then each day is shown in calendar form with hour lines.
- [x] #2 Given a scheduled performance has a start and end time, then its block is positioned in the correct day and approximate time range.
- [x] #3 Given a scheduled performance is shown as a block, then the block shows the band name, stage, and rating stars.
- [x] #4 Given there is no generated schedule or a day has no selected performances, then the screen shows a clear empty state without crashing.
- [x] #5 The calendar layout remains usable on common Android phone widths without overlapping unreadable text.
- [x] #6 Domain/application schedule behavior remains covered by tests, and Android compile or unit validation is run.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected `TimelineSlot`, `ScheduleDay`, `SharedSchedule`, current ScheduleActivity rendering, and existing schedule tests.
2. Added `ScheduleCalendarLayout` with focused app-unit tests for hour range, hour labels, block offsets, and minimum readable block height.
3. Replaced the simple schedule list with per-day calendar sections, hour lines, and time-positioned performance blocks.
4. Kept existing generated/empty/error schedule behavior and conflict selection behavior unchanged.
5. Updated README basic functionality for the calendar-style group schedule.
6. Ran app unit tests, Android Java compile, and diff checks.

Deviation: no device UI validation was possible in this environment. Architecture impact: not architecture-significant; Android presentation and testable UI layout helper only. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Replaced the group schedule list with a day-based calendar layout. Each generated schedule day now renders hour lines and time-positioned performance blocks. Blocks show the selected band, rating stars, stage, time range, decision status, and existing lost-alternative text when present.

Added `ScheduleCalendarLayout` with app-unit tests for hour range, labels, time offsets, and readable minimum block height.

## Acceptance criteria validation

- AC1: Generated schedules render each `ScheduleDay` as a calendar section with hour lines.
- AC2: `ScheduleCalendarLayout` computes top offsets and block durations from start/end times; tests cover minute offsets and duration.
- AC3: Performance blocks show band name, stage, and rating stars.
- AC4: Existing no-schedule/status handling remains in place, and generated schedules with no days show a clear empty message.
- AC5: Blocks use fixed time-grid dimensions, minimum readable height, single-line hour labels, and phone-friendly padding.
- AC6: App unit tests and Android Java compile passed.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `git diff --check`

### Manual validation

- Not run on a physical Android device in this task. Install the APK, open the calendar icon, and verify each schedule day uses hour lines with performance blocks.

## TDD / BDD / approval-test evidence

Added focused tests for the layout calculations before relying on them from ScheduleActivity. Existing application/domain tests continue to protect schedule generation and conflict behavior.

## Architecture impact

- Architecture-significant change: no. This is Android presentation plus a small package-local UI layout helper. No schema, API, dependency, domain, persistence, or module-boundary change was introduced.
- Approval received: not required.
- ADR impact: none, because no architecture-significant decision was made.

## README impact

README impact: updated basic functionality to describe the MVP2 schedule as calendar-style.

## Business requirements impact

Business requirements impact: none, because BR-063 and BR-064 already documented this behavior before implementation.

## Diagram impact

Diagram impact: none, because the system structure and data flow did not change.

## Commits / logical change list

- Add tested `ScheduleCalendarLayout` helper.
- Render generated schedule days as hour-line calendars.
- Render performance blocks by time with band, stage, and stars.
- Update README basic functionality.

## Risks and follow-up

- Device visual validation remains useful for checking exact block readability across Android font/rendering differences.
<!-- SECTION:NOTES:END -->
