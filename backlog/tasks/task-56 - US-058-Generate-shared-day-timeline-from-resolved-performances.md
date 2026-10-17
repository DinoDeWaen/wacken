---
id: task-56
title: 'US-058: Generate shared day timeline from resolved performances'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-07 15:37'
updated_date: '2026-06-07 16:06'
labels:
  - mvp2
  - scheduling
  - application
dependencies:
  - task-55
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner group member, I want the app to generate a day-based shared timeline from the group decisions, so the group has one clear MVP2 plan to review.

In scope:
- Generate one timeline per festival day from non-overlapping selected performances.
- Include selected band, stage, date, start/end time, decision strength, and lost alternative where available.
- Keep optional selected performances visibly marked as optional.
- Preserve chronological ordering within each day.
- Provide an application use case that Android UI can call.

Out of scope:
- Travel feasibility re-runs, travel time annotations, lunch blocks, food suggestions, PDF export, and manual override editing.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given resolved performances for multiple festival days, when a shared schedule is generated, then timeline slots are grouped by day and sorted by start time.
- [x] #2 Given a selected performance has a lost alternative, when the timeline is generated, then the slot includes that lost alternative for review.
- [x] #3 Given a resolved conflict returns OPTIONAL, when the timeline is generated, then the slot is visibly marked optional in the timeline model.
- [x] #4 Given a conflict returns no selected performance because all options are vetoed, when the timeline is generated, then no slot is created for that conflict.
- [x] #5 Given band-only data without performance times, when schedule generation runs, then those bands are ignored with a clear no-scheduled-performance outcome rather than crashing.
- [x] #6 Automated tests cover day grouping, sorting, optional slots, lost alternatives, and no-selection conflicts.
- [x] #7 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [x] #8 Architecture impact is assessed; ADR impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added application tests for day grouping/sorting, lost alternatives, optional slots, vetoed no-selection conflicts, and band-only/no-performance outcome.
2. Implemented `GenerateSharedScheduleUseCase` plus `SharedSchedule`, `ScheduleDay`, `TimelineSlot`, and `SharedScheduleStatus` models using existing performance, rating, conflict detector, and conflict resolver domain objects.
3. Grouped `RatingRepository.findAll()` by band so shared group ratings feed MVP2 decision rules.
4. Kept travel feasibility, lunch blocks, food suggestions, PDF export, and Android UI out of scope.
5. Ran focused schedule tests, full domain/application validation, and diff whitespace validation.

Deviation: no Android screen was added because that belongs to task-57. Architecture impact: not architecture-significant; this adds an application use case over existing domain behavior and repository ports without changing module boundaries, persistence schema, external contracts, or dependencies. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Added `GenerateSharedScheduleUseCase` in the application module.
- Added schedule output models: `SharedSchedule`, `SharedScheduleStatus`, `ScheduleDay`, and `TimelineSlot`.
- The use case reads performances, groups shared ratings by band from `RatingRepository.findAll()`, detects conflict sets, resolves them, and groups selected slots by festival day.
- It skips no-selection conflicts and returns `NO_SCHEDULED_PERFORMANCES` when there are no performances.

## Acceptance criteria validation

- AC1: Timeline slots are grouped by day and sorted by start time.
- AC2: Lost alternatives are included in selected timeline slots when conflict resolution provides them.
- AC3: Optional conflict selections are marked as optional in the timeline model.
- AC4: All-vetoed conflicts create no slots; if nothing remains, schedule status is `NO_SELECTIONS`.
- AC5: Band-only/no-performance data returns `NO_SCHEDULED_PERFORMANCES` with a clear message instead of crashing.
- AC6: Automated tests cover day grouping, sorting, optional slots, lost alternatives, and no-selection conflicts.
- AC7: README and business requirements impact are recorded below.
- AC8: Architecture and ADR impact are recorded below.

## How to test

### Automated tests

- `/bin/zsh -lc 'JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test'`
- `git diff --check`

### Manual validation

- Not required for this task; the change is an application use case with no Android UI surface yet.

## TDD / BDD / approval-test evidence

- BDD-style acceptance criteria were translated into focused application tests before implementation.
- The first focused test run failed because the schedule use case and output models did not exist.
- The implementation was added to make those tests pass, then full domain/application validation passed.
- No approval baseline was needed because this is new application behavior, not legacy refactoring.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because this task implements already-planned MVP2 application behavior without changing setup, commands, architecture, or current user-facing Android behavior.

## Business requirements impact

Business requirements impact: none, because the shared timeline behavior is already covered by the MVP2 roadmap and BR-031/BR-032.

## Diagram impact

Diagram impact: none, because module/container relationships did not change.

## Commits / logical change list

- `GenerateSharedScheduleUseCase.java`: application orchestration for generating shared schedules.
- `SharedSchedule.java`, `SharedScheduleStatus.java`, `ScheduleDay.java`, `TimelineSlot.java`: schedule output model.
- `GenerateSharedScheduleUseCaseTest.java`: focused application coverage.

## Risks and follow-up

- Android display is not included here; it belongs to task-57.
- Travel feasibility, lunch blocks, food suggestions, and PDF export remain out of scope for MVP2/task-56.
<!-- SECTION:NOTES:END -->
