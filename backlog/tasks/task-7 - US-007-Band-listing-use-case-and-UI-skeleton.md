---
id: task-7
title: 'US-007: Band listing use case and UI skeleton'
status: Done
assignee:
  - '@codex'
created_date: '2026-01-06 16:57'
updated_date: '2026-05-15 10:05'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-007: Band listing use case and UI skeleton

**As an** attendee
**I want** to browse the band lineup with stage and time
**So that** I can plan who to see
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given imported data When I run the band listing use case Then it returns bands with stage and time sorted by start time
- [x] #2 Given the Android app When I open the band list screen Then I see band name, stage, and time without infrastructure details leaking into the UI layer
- [x] #3 Given no imported data When I open the band list Then I see an empty state message
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added application `ListBandsUseCase` that reads performances and returns `BandListItem` values sorted by start time.
2. Added application tests first for sorting by start time and for empty imported data.
3. Updated Android `MainActivity` to render a simple band list screen from application-facing listing items, with an empty state when no performances are available.
4. Kept infrastructure details out of UI; the Activity currently renders application DTOs and does not know repository implementations.
5. Validated with `./gradlew :application:test :application:jacocoTestCoverageVerification`, `./gradlew test`, and `./gradlew assembleDebug`.
6. README update was not needed because no setup or command changed.

Deviation: final date/time display format is unresolved, so the use case currently uses `LocalDateTime.toString()` as a stable placeholder. The Android screen shows the empty state because no production data wiring exists yet.
Architecture impact: not architecture-significant; uses existing application/domain boundaries and keeps UI at the edge. No ADR added.
README impact: not needed.
Diagram impact: not needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added `ListBandsUseCase` and `BandListItem` in the application module. The use case returns performance-backed band list items sorted by start time.

Updated `MainActivity` from the scaffold text to a minimal band list screen that renders application-facing list items or the empty state `No bands imported yet.`

## Acceptance criteria validation

- AC1: `ListBandsUseCaseTest.returnsBandsWithStageAndTimeSortedByStartTime` verifies band name, stage, start time, end time, and sorting by start time.
- AC2: `MainActivity` renders band name, stage, and time from `BandListItem` values and does not reference infrastructure classes.
- AC3: `MainActivity` renders `No bands imported yet.` when the list is empty.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :application:jacocoTestCoverageVerification`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`

### Manual validation

- Inspected `MainActivity` to confirm it renders application DTOs and does not reference infrastructure details.

## TDD / BDD / approval-test evidence

Used TDD for application listing behavior. The first application test run failed because `ListBandsUseCase` and `BandListItem` did not exist. Then the minimal implementation and UI skeleton were added and validation passed.

## Architecture impact

- Architecture-significant change: no; this uses existing module boundaries.
- Approval received: not required.
- ADR: not needed.

## README impact

Not needed because setup and commands did not change.

## Diagram impact

Not needed because module boundaries did not change.

## Commits / logical change list

- `b24ab48` Add band listing use case and UI skeleton

## Risks and follow-up

- The date/time display uses `LocalDateTime.toString()` until date/time formatting is refined.
- The Activity currently shows the empty state because production data wiring comes with later tasks.
<!-- SECTION:NOTES:END -->
