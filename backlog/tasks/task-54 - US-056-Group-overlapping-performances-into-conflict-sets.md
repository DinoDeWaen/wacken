---
id: task-54
title: 'US-056: Group overlapping performances into conflict sets'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-07 15:36'
updated_date: '2026-06-07 15:54'
labels:
  - mvp2
  - scheduling
  - domain
dependencies:
  - task-53
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner group member, I want overlapping performances to be grouped into conflicts, so the app can reason about which acts cannot all be attended.

In scope:
- Detect overlaps between performances on the same festival day.
- Keep non-overlapping performances separate.
- Return conflict sets that include all mutually relevant overlapping candidates for scheduling.
- Preserve band, stage, date, and time information in each conflict candidate.

Out of scope:
- Deciding the winner of a conflict.
- Travel feasibility, lunch planning, food suggestions, PDF output, and Android UI.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given two performances on the same day overlap in time, when conflicts are detected, then they appear in the same conflict set.
- [x] #2 Given performances on different days have the same time range, when conflicts are detected, then they do not conflict.
- [x] #3 Given performances touch at end/start time without overlap, when conflicts are detected, then they are not grouped as overlapping.
- [x] #4 Given a chain of overlapping performances, when conflicts are detected, then all connected overlapping candidates are available for conflict resolution.
- [x] #5 Automated tests cover overlap, non-overlap, cross-day, and boundary-time cases.
- [x] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [x] #7 Architecture impact is assessed; ADR impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added domain tests for same-day overlap, cross-day non-conflict, boundary end/start non-overlap, independent non-overlapping performances, and chained overlaps.
2. Implemented `PerformanceConflictDetector` to group sorted performances by connected same-day time overlaps.
3. Implemented `PerformanceConflictSet` to preserve original Performance objects and expose whether a set is a real conflict.
4. Ran focused detector tests, full domain/application tests with coverage, and diff whitespace validation.
5. Closed acceptance criteria with validation and impact notes.

Deviation: no application use case was added yet because task-54 is the reusable domain grouping slice for task-55. Architecture impact: not architecture-significant; this adds domain-owned scheduling logic inside the existing domain module with no new dependencies, ports, persistence, schema, external contracts, or module structure. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Added `PerformanceConflictDetector` to group performances into connected same-day overlap sets.
- Added `PerformanceConflictSet` to preserve the candidate `Performance` objects and report whether a set contains an actual conflict.
- Added focused domain tests for overlap grouping, cross-day separation, boundary-touching performances, independent non-overlapping performances, and chained overlaps.

## Acceptance criteria validation

- AC1: Same-day overlapping performances are returned in the same set.
- AC2: Same-time performances on different days are returned in separate non-conflict sets.
- AC3: Performances where one ends exactly when another starts are returned separately.
- AC4: Chained overlaps are returned as one connected conflict set.
- AC5: Automated tests cover overlap, non-overlap, cross-day, and boundary-time cases.
- AC6: README and business requirements impact are recorded below.
- AC7: Architecture and ADR impact are recorded below.

## How to test

### Automated tests

- `/bin/zsh -lc 'JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test'`
- `git diff --check`

### Manual validation

- Not required for this task; the change is internal domain conflict grouping with no Android UI surface yet.

## TDD / BDD / approval-test evidence

- BDD-style acceptance criteria were translated into focused JUnit domain tests before implementation.
- The first focused test run failed because `PerformanceConflictDetector` and `PerformanceConflictSet` did not exist.
- The implementation was added to make those tests pass, then the full domain/application validation passed.
- No approval baseline was needed because this is new domain behavior, not legacy refactoring.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because this task implements already-planned internal MVP2 scheduling behavior without changing setup, commands, architecture, or current user-facing Android behavior.

## Business requirements impact

Business requirements impact: none, because BR-013 to BR-021 already establish the need for overlapping performance conflict handling and this task does not change those rules.

## Diagram impact

Diagram impact: none, because module/container relationships did not change.

## Commits / logical change list

- `domain/src/main/java/be/wacken/planner/domain/PerformanceConflictDetector.java`: connected same-day overlap grouping.
- `domain/src/main/java/be/wacken/planner/domain/PerformanceConflictSet.java`: immutable conflict candidate set model.
- `domain/src/test/java/be/wacken/planner/domain/PerformanceConflictDetectorTest.java`: focused grouping coverage.

## Risks and follow-up

- This task only groups conflicts; it does not choose winners. Winner selection belongs to task-55.
- The detector groups connected overlap chains as one set, which is intended for later conflict resolution but may require careful tie-breaker handling in task-55.
<!-- SECTION:NOTES:END -->
