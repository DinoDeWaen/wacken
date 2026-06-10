---
id: task-82
title: 'DEF: Limit schedule alternatives to directly overlapping acts'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 17:47'
updated_date: '2026-06-10 17:51'
labels:
  - defect
  - schedule
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The schedule decision detail can show a very long alternatives list because chained conflict groups include acts that do not overlap the chosen act. For example, Def Leppard should only compare against bands in the same overlapping timeslot, not every act connected through a chain of partial overlaps.

As a festival attendee, I want schedule alternatives to include only acts that overlap the selected act so that the decision detail remains relevant and readable.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a chosen act has alternatives from a chained conflict group, when the schedule detail candidates are built, then only rejected acts that overlap the chosen act's own time range are shown as alternatives.
- [x] #2 Given a rejected act overlaps another rejected act but not the chosen act, when the chosen act detail is opened, then that indirectly connected act is not listed as an alternative.
- [x] #3 Given the filtered candidates are shown in schedule detail, when walking context is displayed, then it is calculated only for the candidates visible in that same detail.
- [x] #4 A failing regression unit test is added before the fix and passes after the fix.
- [x] #5 Relevant automated tests and compile checks pass.
- [x] #6 README, business requirements, ADR, and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added a regression unit test in GenerateSharedScheduleUseCaseTest for a selected act with one direct overlap and one indirect chained overlap.
2. Confirmed the regression test failed before the fix because the indirect chained act appeared in detail candidates.
3. Filtered TimelineSlot detail candidates and visible lost-alternative metadata in GenerateSharedScheduleUseCase to rejected acts that directly overlap the selected performance.
4. Verified walking context remains limited by the same filtered detail candidate list used by ScheduleActivity.
5. Ran focused red/green validation, full relevant module tests, Android debug compile, and git diff checks.

Deviation: none. Architecture impact: not architecture-significant; this preserves domain conflict-set grouping and changes only application-level presentation candidates.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed the schedule detail alternatives bug by keeping domain connected-conflict grouping intact while filtering the selected slot detail candidates to rejected performances that directly overlap the selected act. Visible lost-alternative metadata now follows the same direct-overlap filter, so schedule blocks and detail walking context do not pull in indirectly chained acts.

## Acceptance criteria validation

- AC1: Covered by the new chained-overlap regression test; only the direct overlap remains in the selected act candidates.
- AC2: Covered by the same regression test; the indirectly connected act is excluded.
- AC3: ScheduleActivity calculates walking context from manualSelections.detailCandidates(slot), which is derived from the now-filtered slot.candidates() list, so walking context is limited to visible candidates.
- AC4: The regression test failed before implementation and passed after the fix.
- AC5: Relevant automated tests and compile checks passed.
- AC6: README, business requirements, ADR, and diagram impact are recorded below.

## How to test

### Automated tests

- RED before fix: `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test --tests be.wacken.planner.application.GenerateSharedScheduleUseCaseTest` failed at `limitsDetailCandidatesToPerformancesThatDirectlyOverlapTheSelectedAct`.
- GREEN focused after fix: same focused command had all tests pass; the command still failed the module-wide JaCoCo threshold because only one test class was selected.
- Full relevant validation: `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac` passed.
- `git diff --check` passed.

### Manual validation

- Not run on device; this is covered by application-level regression tests and unchanged UI data flow.

## TDD / BDD / approval-test evidence

- TDD regression loop followed: wrote failing unit test first, confirmed red, implemented the minimal use-case filter, then confirmed green via focused and full validation.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: not required.

## README impact

README impact: none, because this is a defect fix inside existing schedule-detail behavior and does not change setup, commands, architecture, or documented public capability.

## Business requirements impact

Business requirements impact: none, because the existing MVP2 schedule rules already require relevant conflict alternatives; this corrects implementation drift.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Added chained-overlap regression coverage for schedule detail candidates.
- Filtered visible lost alternatives and detail candidates to direct overlaps with the selected act.

## Risks and follow-up

- Existing domain behavior still groups chained overlaps for conflict resolution; this fix only limits what the selected act detail presents to users. No follow-up is required.
<!-- SECTION:NOTES:END -->
