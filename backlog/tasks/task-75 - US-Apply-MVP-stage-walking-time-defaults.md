---
id: task-75
title: 'US: Apply MVP stage walking-time defaults'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 12:17'
updated_date: '2026-06-10 12:28'
labels:
  - schedule
  - travel
  - mvp3
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The group schedule and future feasibility rules need explicit MVP walking-time defaults for named stage movements. The user clarified that Heavy and Louder are 5 minutes apart, and that moving between Heavy or Louder and another stage is 15 minutes. Walking between two other stages still needs clarification before implementation.

As a festival attendee, I want the app to use agreed walking-time defaults between stages so that schedule travel information and feasibility decisions are understandable.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the source data does not provide a more specific distance, when travel between Heavy and Louder is needed, then the MVP default walking time is 5 minutes.
- [x] #2 Given the source data does not provide a more specific distance, when travel between Heavy or Louder and any other stage is needed, then the MVP default walking time is 15 minutes.
- [x] #3 Before implementation starts, the product decision for walking between two stages that are neither Heavy nor Louder is clarified and recorded in the task notes or acceptance criteria.
- [x] #4 Architecture impact is assessed before implementation; if an architecture-significant change is needed, explicit approval is requested before coding and an ADR is created or updated if approved.
- [x] #5 Automated tests cover the default walking-time rules.
- [x] #6 Business requirements impact: updated for BR-073.
- [x] #7 README impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Recorded the clarified MVP default that non-Heavy/Louder stages are 5 minutes apart from each other.
2. Added application tests for Heavy/Louder, Heavy-or-Louder-to-other, other-to-other, and stored-distance precedence.
3. Applied defaults in schedule generation through the existing StageDistanceRepository boundary.
4. Ran focused application tests and full release validation.
Architecture impact: not architecture-significant; reused the existing StageDistanceRepository port and did not add persistence, module, or boundary changes. No ADR needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Schedule generation now resolves walking minutes between consecutive selected acts using stored stage distances first, then MVP defaults: Heavy-Louder is 5 minutes, Heavy/Louder to any other stage is 15 minutes, and other-to-other is 5 minutes.

## Acceptance criteria validation

All acceptance criteria are met. The product decision for other-to-other stages is recorded in BR-073 and covered by tests.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest
- Full release validation command completed successfully with domain/application/infrastructure/app tests, debug compile, and assembleRelease.

### Manual validation

- Walking-time defaults are visible through generated schedule data and V2.2 schedule UI.

## TDD / BDD / approval-test evidence

- Added GenerateSharedScheduleUseCaseTest coverage for MVP defaults and stored-distance precedence.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: updated with V2.2 release notes describing walking-time defaults.

## Business requirements impact

Business requirements impact: updated BR-073 with the clarified other-stage default.

## Diagram impact

Diagram impact: none, because no architecture diagram changed.

## Commits / logical change list

- Added walking-minute enrichment to GenerateSharedScheduleUseCase.
- Added tests for default and stored walking distances.

## Risks and follow-up

- Future user-configurable walking speed remains out of scope.
<!-- SECTION:NOTES:END -->
