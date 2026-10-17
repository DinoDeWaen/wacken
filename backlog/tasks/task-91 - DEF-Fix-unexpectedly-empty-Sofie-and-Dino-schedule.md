---
id: task-91
title: 'DEF: Fix unexpectedly empty Sofie and Dino schedule'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-12 08:14'
updated_date: '2026-06-12 09:32'
labels:
  - defect
  - schedule
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The group schedule for Sofie and Dino can show days with no planned acts even though ratings exist for acts on those days. For example, Def Leppard is not scheduled despite both Dino and Sofie having ratings.

As a festival attendee, I want the group schedule selection logic to choose expected rated acts so that the schedule is not incorrectly empty for rated festival days.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given Sofie and Dino have ratings for performances on a festival day, when the shared schedule is generated, then rated eligible acts for that day are considered and the day is not incorrectly empty.
- [x] #2 Given Def Leppard has ratings from both Dino and Sofie and a performance exists, when the shared schedule is generated, then the selection logic either schedules it or records a valid business reason why it was rejected.
- [x] #3 The root cause is validated and documented before the fix is implemented.
- [x] #4 Regression coverage reproduces the empty/incorrect schedule behavior before the fix and passes after the fix.
- [x] #5 Relevant automated tests and Android compile checks pass.
- [x] #6 README, business requirements, ADR, and diagram impact are recorded using the canonical wording from delivery-governance.md.

- [x] #7 Given two acts have full start/end times that touch or overlap only outside their middle 30-minute windows, when conflicts and alternatives are calculated, then they are not treated as overlapping acts.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Validated the root cause in the shared schedule pipeline: conflict detection grouped chained overlaps, and schedule generation chose only one winner for the whole chain.
2. Added the requested business rule that acts overlap for scheduling and alternatives only when their middle 30-minute windows overlap.
3. Added failing regression coverage first: domain coverage for edge overlaps outside the middle 30 minutes and application coverage proving Def Leppard can be selected from a chained conflict when it does not directly overlap the earlier selected act by the middle-window rule.
4. Implemented a domain-owned PerformanceOverlapPolicy and reused it from conflict detection and schedule-detail filtering.
5. Updated shared schedule generation to resolve each connected conflict set iteratively: choose a winner, remove only direct middle-window overlaps, and keep resolving remaining performances.
6. Updated README and business requirements for the new public conflict rule.
7. Ran focused red/green checks and full validation.
Deviation: the focused --tests runs pass their tests but fail module coverage gates because they intentionally execute only narrow test subsets; full module validation passes.
Architecture impact: not architecture-significant; the rule remains in the domain/application core with no persistence, API, dependency, module-boundary, or deployment changes. No ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed the sparse Sofie and Dino group schedule behavior by changing scheduling conflicts to use the middle 30 minutes of each act and by resolving chained conflict sets iteratively instead of selecting only one winner for an entire chain.

Root cause: PerformanceConflictDetector grouped connected overlap chains, then GenerateSharedScheduleUseCase resolved exactly one selected act for the whole connected set. A rated act such as Def Leppard could therefore be rejected even when it did not directly conflict with the selected earlier act; an intermediate act could connect both acts into one large conflict set. This produced days that looked incorrectly empty.

Additional validation: the repository fallback data contains Def Leppard as a band but no local performances.csv row for Def Leppard. The fixed selection logic covers the app case where a Def Leppard performance exists, as shown by the regression test and the synced-data scenario from the screenshots. If Supabase master data also lacks that performance row, that would be a separate data-import correction.

## Acceptance criteria validation

- AC1: Covered by GenerateSharedScheduleUseCaseTest selecting multiple rated eligible acts from a chained conflict instead of leaving only one slot.
- AC2: Covered by GenerateSharedScheduleUseCaseTest with Def Leppard rated by Sofie and Dino and selected when its middle 30-minute window does not directly overlap the earlier winner.
- AC3: Root cause documented above before implementation notes were finalized.
- AC4: Regression tests were added before the fix and failed on the old behavior; they pass after the fix.
- AC5: Full automated validation and Android debug Java compile passed.
- AC6: README and business requirements were updated; diagram and ADR impact are recorded below.
- AC7: Covered by PerformanceConflictDetectorTest for edge overlaps outside the middle 30-minute windows.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac` passed.
- `git diff --check` passed.

### Manual validation

- Not run in the Android UI; this was a scheduling logic defect covered by domain/application regression tests.

## TDD / BDD / approval-test evidence

- Added failing regression tests first for the middle-30-minute overlap rule and the Def Leppard chained-conflict schedule case.
- Focused `--tests` runs failed before implementation on the new regression assertions, then the focused tests passed after implementation. The focused Gradle invocations still fail coverage gates because they intentionally execute only narrow subsets; the full validation command passes coverage.
- No approval/characterization baseline was needed beyond targeted regression tests because the behavior change is intentional and explicitly requested.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required. The scheduling rule remains domain-owned and is reused by application orchestration without changing ports, adapters, persistence, APIs, dependencies, module boundaries, or deployment.

## README impact

README impact: updated the MVP2 schedule description to state that conflict rules use the middle 30 minutes.

## Business requirements impact

Business requirements impact: updated BR-013a to define the middle-30-minute overlap rule for schedule conflicts and alternatives.

## Diagram impact

Diagram impact: none, because the architecture and module relationships did not change.

## Commits / logical change list

- Added `PerformanceOverlapPolicy` for middle-30-minute scheduling overlap checks.
- Updated `PerformanceConflictDetector` and `GenerateSharedScheduleUseCase` to use the shared overlap policy.
- Updated schedule generation to continue selecting non-overlapping remaining acts from connected conflict chains.
- Added/updated domain and application tests for the new rule and Def Leppard regression.
- Updated README and business requirements.

## Risks and follow-up

- If live Supabase master data lacks a Def Leppard performance row, the schedule still cannot include Def Leppard until the imported festival data is corrected. The code now behaves correctly when the performance exists.
<!-- SECTION:NOTES:END -->
