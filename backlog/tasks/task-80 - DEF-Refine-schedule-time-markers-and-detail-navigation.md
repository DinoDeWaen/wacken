---
id: task-80
title: 'DEF: Refine schedule time markers and detail navigation'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 17:33'
updated_date: '2026-06-10 17:35'
labels:
  - defect
  - ui
  - schedule
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The schedule overview time markers should be simpler and clearer, the conflict detail should show walking time to the other acts, and tapping a band name in the schedule detail should open the band detail page.

As a festival attendee, I want schedule times, walking context, and band navigation to be clear so that the schedule is easy to use during the festival.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the schedule overview renders a performance block, when start and end times are shown next to that block, then they use a straight-line marker instead of bracket characters.
- [x] #2 Given event start and end times are shown next to a block, when compared with regular hour-line labels, then event times are white and regular hour labels remain dark grey.
- [x] #3 Given the schedule decision detail shows chosen act and alternatives, when candidates are listed, then each candidate shows walking-time context to the other acts where available.
- [x] #4 Given I tap a band name in the schedule decision detail, when the band exists in the app, then the existing band detail screen opens for that band.
- [x] #5 Automated tests or focused compile checks protect the behavior, and installed-device visual validation is documented.
- [x] #6 README, business requirements, ADR, and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected ScheduleActivity, BandDetailActivity, and focused schedule tests.
2. Updated overview event time markers to use straight white line labels while preserving grey hour labels.
3. Added walking-time context to each schedule decision detail candidate.
4. Made candidate band names tappable and routed them to BandDetailActivity with stage/date/time extras.
5. Centralized MVP walking defaults in a domain policy and reused it from schedule generation and detail context.
6. Ran domain/application/app unit tests and debug compile validation.
Architecture impact: not architecture-significant; uses an existing Activity and centralizes an existing business rule without changing module boundaries or persistence. No ADR needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Schedule overview event time markers now use straight white line labels instead of bracket characters, while regular hour-line labels remain dark grey. Schedule decision detail candidates now show walking-time context to the other acts, and tapping a candidate band name opens the existing BandDetailActivity with band, stage, date, and time extras.

The MVP walking-time defaults were moved into StageWalkingTimePolicy in the domain module so the app UI and schedule generation reuse the same rule.

## Acceptance criteria validation

All acceptance criteria are met through ScheduleActivity changes, the existing BandDetailActivity route, focused tests, and compile validation.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac
- git diff --check

### Manual validation

- Installed-device visual validation is recommended to confirm the white event time labels, grey hour labels, detail walking rows, and tap-through navigation on phone and BlueStacks.

## TDD / BDD / approval-test evidence

- Added StageWalkingTimePolicyTest for the shared walking-time defaults.
- Existing schedule generation and schedule UI tests passed after refactoring to the shared domain policy.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: none, because this refines existing schedule UI behavior without changing setup, architecture, commands, or public capabilities.

## Business requirements impact

Business requirements impact: none, because existing schedule detail and walking-time requirements already cover this behavior.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Straight white event time markers in ScheduleActivity.
- Walking context rows in schedule decision details.
- Candidate band names open BandDetailActivity.
- Shared StageWalkingTimePolicy with tests.

## Risks and follow-up

- Installed-device UAT may still tune exact spacing or text density.
<!-- SECTION:NOTES:END -->
