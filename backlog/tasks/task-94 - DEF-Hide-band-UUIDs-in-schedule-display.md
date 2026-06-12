---
id: task-94
title: 'DEF: Hide band UUIDs in schedule display'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-12 12:02'
updated_date: '2026-06-12 12:04'
labels:
  - defect
  - ui
  - schedule
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The schedule UI can show raw UUID-like identifiers as part of band names, which makes schedule blocks and details harder to read.

As a festival attendee, I want schedule band names to hide UUIDs or raw identifier suffixes so the calendar and details show clean artist names.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a schedule band name includes a UUID-like identifier, when the schedule block is shown, then the visible band text hides the UUID.
- [x] #2 Given a schedule band name includes a UUID-like identifier, when schedule decision details are shown, then chosen and alternative band labels hide the UUID.
- [x] #3 Given a band detail is opened from the schedule, when the app navigates, then it still uses the underlying band identity needed by the existing detail flow.
- [x] #4 Relevant automated tests and Android compile checks pass.
- [x] #5 README, business requirements, ADR, and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect schedule display paths for blocks, decision details, walking context, and band-detail navigation.
2. Add focused app unit tests for UUID stripping in block and detail-visible schedule names.
3. Implement a UI-only ScheduleBandDisplayName formatter that hides UUID-like identifiers without changing underlying ScheduleDecisionCandidate or TimelineSlot identity.
4. Use the formatter in ScheduleBlockContent and ScheduleActivity visible labels/walking context while keeping openBandDetail on the raw candidate band name.
5. Run focused app tests, Android compile, and relevant validation.
6. Close task with impact notes.
Architecture impact: not architecture-significant; this is Android presentation formatting only. No ADR expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Schedule display labels now hide UUID-like identifiers from band names. This applies to schedule block band text, lost-alternative text, decision detail titles, chosen/alternative labels, and walking-context labels.

The formatter is UI-only: TimelineSlot and ScheduleDecisionCandidate values are not mutated, and opening band detail from the schedule still passes the original candidate band name to the existing detail flow.

## Acceptance criteria validation

- AC1: Covered by ScheduleBlockContentTest; block band labels hide UUIDs.
- AC2: Covered by ScheduleBlockContentTest for lost alternatives and by ScheduleActivity using the same formatter for detail labels.
- AC3: Preserved because openBandDetail still passes candidate.bandName() unchanged.
- AC4: Automated validation passed.
- AC5: Impact statements are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleBandDisplayNameTest --tests be.wacken.planner.ScheduleBlockContentTest` passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac` passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac` passed.
- `git diff --check` passed.

### Manual validation

- Not run on device in this environment.

## TDD / BDD / approval-test evidence

- Added focused app tests for UUID display-name cleanup and schedule block rendering.
- No approval baseline was needed because this is a small intentional presentation fix.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: none, because this is a small display cleanup and does not change setup, architecture, commands, or documented product scope.

## Business requirements impact

Business requirements impact: none, because existing schedule readability requirements already cover clean visible labels.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Added ScheduleBandDisplayName for UI-only UUID cleanup.
- Applied clean display names in schedule blocks, lost alternatives, detail labels, and walking context.
- Preserved raw band names for schedule-to-band-detail navigation.
- Added focused app tests.

## Risks and follow-up

- The formatter intentionally removes standard UUID strings only. If source data includes other non-UUID ID formats in names, add examples and extend the formatter with tests.
<!-- SECTION:NOTES:END -->
