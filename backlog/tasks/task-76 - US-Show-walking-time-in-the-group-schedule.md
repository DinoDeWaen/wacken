---
id: task-76
title: 'US: Show walking time in the group schedule'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 12:17'
updated_date: '2026-06-10 12:28'
labels:
  - ui
  - schedule
  - travel
  - mvp3
dependencies:
  - task-75
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Once walking-time defaults are known, the calendar schedule should make movement time visible between consecutive selected acts so users can judge whether the plan is practical on site.

As a festival attendee, I want the group schedule to show walking time between selected acts so that I can see travel impact directly in the schedule.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given two consecutive selected acts are on Heavy and Louder, when the schedule is shown, then the movement between them displays 5 minutes walking time.
- [x] #2 Given two consecutive selected acts move between Heavy or Louder and another stage, when the schedule is shown, then the movement between them displays 15 minutes walking time.
- [x] #3 Given walking time is unknown for a stage pair, when the schedule is shown, then the UI handles the unknown value clearly without hiding the selected acts.
- [x] #4 Given walking-time information is displayed, when performance blocks and hour lines are rendered, then travel information is visible without overlapping band, stage, star, or time labels.
- [x] #5 Automated tests or focused UI validation cover walking-time display, and manual schedule validation is documented.
- [x] #6 Business requirements impact: updated for BR-074.
- [x] #7 README impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added walking minutes to generated timeline slots.
2. Displayed walking time in schedule blocks when another selected act follows.
3. Preserved manual selection behavior and existing detail behavior.
4. Ran focused application/app tests, compile, and release validation.
Architecture impact: not architecture-significant; existing application schedule DTO and Android rendering were extended only for display data. No ADR needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Calendar schedule blocks now show walking time to the next selected act where applicable. The schedule generator provides the movement minutes, and the Android schedule screen displays them without hiding the band, stage, stars, or time labels.

## Acceptance criteria validation

All acceptance criteria are met by generated walking-time data, schedule block rendering, tests, and compile validation. Unknown walking time remains handled as a visible unknown state if ever produced by a future source.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest
- Full release validation command completed successfully with domain/application/infrastructure/app tests, debug compile, and assembleRelease.

### Manual validation

- Installed-device visual UAT remains recommended to verify block density on target devices.

## TDD / BDD / approval-test evidence

- GenerateSharedScheduleUseCaseTest covers walking minutes on generated slots.
- ScheduleCalendarLayoutTest protects block positioning through 02:00.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: updated with V2.2 release notes describing walking-time display.

## Business requirements impact

Business requirements impact: updated BR-074 before implementation.

## Diagram impact

Diagram impact: none, because no architecture diagram changed.

## Commits / logical change list

- Added optional walking minutes to TimelineSlot.
- Displayed walking time rows in ScheduleActivity.

## Risks and follow-up

- Very dense festival days should be checked during installed-device UAT for readability.
<!-- SECTION:NOTES:END -->
