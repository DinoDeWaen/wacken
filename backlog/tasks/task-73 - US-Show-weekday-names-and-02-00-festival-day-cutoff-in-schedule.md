---
id: task-73
title: 'US: Show weekday names and 02:00 festival day cutoff in schedule'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 12:17'
updated_date: '2026-06-10 12:27'
labels:
  - ui
  - schedule
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The calendar schedule should match how festival days are experienced on site: a day can continue after midnight and only ends at 02:00, and the day header should be recognizable as Monday, Tuesday, and so on.

As a festival attendee, I want each schedule day to show the weekday and late-night hours through 02:00 so that after-midnight performances stay visible in the correct festival day.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a calendar schedule day is shown, when the header is rendered, then it includes the weekday name and the festival date.
- [x] #2 Given a festival day contains late performances, when the calendar renders the day, then the visible hour range continues through 02:00 instead of stopping at 00:00.
- [x] #3 Given a performance starts after midnight and ends by 02:00, when it belongs to the previous festival day, then it appears in that previous day's calendar view.
- [x] #4 Given the hour lines include late-night slots, when performance blocks are positioned, then they remain aligned to the correct time labels.
- [x] #5 Automated tests cover the festival-day boundary behavior and focused UI validation is documented.
- [x] #6 Business requirements impact: updated for BR-069 and BR-070.
- [x] #7 README impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added application coverage for after-midnight grouping before 02:00.
2. Added app layout coverage for late-night positioning and 02:00 labels.
3. Grouped pre-02:00 performances into the previous festival day.
4. Rendered schedule day headings with English weekday and date.
5. Ran focused and release validation.
Architecture impact: not architecture-significant; existing schedule structures were extended only where needed. No ADR needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Schedule days now render weekday/date headings and the festival-day grouping treats performances before 02:00 as part of the previous festival day. Calendar layout can position late-night blocks against the intended festival day and labels through 02:00.

## Acceptance criteria validation

All acceptance criteria are met by GenerateSharedScheduleUseCaseTest, ScheduleCalendarLayoutTest, and app compile validation.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest
- Full release validation command completed successfully with domain/application/infrastructure/app tests, debug compile, and assembleRelease.

### Manual validation

- Installed-device visual UAT remains recommended for the final calendar screen.

## TDD / BDD / approval-test evidence

- Added regression coverage for a 01:00-02:00 slot grouping into the previous festival day.
- Added layout coverage for late-night hour labels and positioning.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: updated with V2.2 release notes link.

## Business requirements impact

Business requirements impact: updated BR-069 and BR-070 before implementation.

## Diagram impact

Diagram impact: none, because no architecture diagram changed.

## Commits / logical change list

- Updated schedule generation festival-day grouping.
- Updated calendar layout to use a schedule date and 02:00 boundary.
- Updated day heading formatter.

## Risks and follow-up

- Final festival dates still depend on imported performance data.
<!-- SECTION:NOTES:END -->
