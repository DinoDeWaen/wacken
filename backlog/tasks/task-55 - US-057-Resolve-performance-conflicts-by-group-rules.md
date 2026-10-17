---
id: task-55
title: 'US-057: Resolve performance conflicts by group rules'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-07 15:37'
updated_date: '2026-06-07 15:58'
labels:
  - mvp2
  - scheduling
  - domain
dependencies:
  - task-53
  - task-54
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner group member, I want overlapping performances to be resolved according to the group decision rules, so the generated plan selects the best candidate and shows what was lost.

In scope:
- Resolve each conflict set using group ratings, must-see priority, want-to-see counts, veto counts, and optional outcomes.
- Use existing stage-distance data only as a tie-breaker where BR-014 and BR-017 require shortest/closest travel context.
- Return the selected performance, decision strength, rejected alternatives, and the lost alternative/runner-up.
- Select no performance when all overlapping options are vetoed.

Out of scope:
- Re-running conflicts for infeasible travel paths, lunch insertion, food suggestions, PDF output, and Android UI.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given overlapping performances include a band with any rating of 5, when the conflict is resolved, then a must-see option is preferred over lower-rated alternatives.
- [x] #2 Given overlapping performances both have a rating of 5, when distance context is available, then the option with the better travel position is selected and the other is recorded as the lost alternative.
- [x] #3 Given overlapping options only have ratings of 4, when the conflict is resolved, then the option with the most 4 ratings wins, then fewer vetoes wins, then shorter distance wins.
- [x] #4 Given overlapping options only have ratings of 3, when the conflict is resolved, then the result is OPTIONAL and the option with the most 3 ratings is chosen.
- [x] #5 Given overlapping options only have ratings of 2, when the conflict is resolved, then the result remains OPTIONAL.
- [x] #6 Given all overlapping options are vetoed, when the conflict is resolved, then no performance is selected.
- [x] #7 Automated tests cover BR-013 to BR-021 and runner-up/lost-alternative output.
- [x] #8 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [x] #9 Architecture impact is assessed; ADR impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added focused domain tests for must-see priority, must-see distance tie-break with lost alternative, rating-4 count/veto/distance tie-breaks, optional rating-3 and rating-2 conflicts, and all-vetoed no-selection output.
2. Implemented `PerformanceConflictResolver`, `PerformanceConflictResolution`, and `ConflictDistanceContext` using existing `PerformanceConflictSet`, `GroupDecisionPolicy`, `Rating`, and `StageDistance`.
3. Kept distance data as business input for tie-breaking only; MVP3 travel feasibility and scheduling re-runs were not implemented.
4. Ran focused resolver tests, full domain/application validation, and diff whitespace validation.
5. Closed acceptance criteria with validation and impact notes.

Deviation: no application use case or Android UI was added because task-55 is domain-only and feeds task-56/task-57. Architecture impact: not architecture-significant; this continues domain-owned scheduling rules inside the existing domain module with no new dependencies, ports, persistence, schema, external contracts, or module structure. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Added `PerformanceConflictResolver` to choose a selected performance from an overlap set using MVP2 group rules.
- Added `PerformanceConflictResolution` with selected performance, decision status, lost alternative, rejected performances, and reason.
- Added `ConflictDistanceContext` so existing `StageDistance` data can break ties without adding MVP3 travel feasibility behavior.
- Added focused resolver tests for must-see priority, 5-vs-5 distance tie-breaks, 4-rated count/veto/distance tie-breaks, optional 3/2-rated conflicts, all-vetoed conflicts, and lost-alternative output.

## Acceptance criteria validation

- AC1: A must-see candidate wins over lower-rated alternatives.
- AC2: Multiple must-see candidates use distance context to choose the better travel position and record the other as lost alternative.
- AC3: Rating-4 conflicts choose by most 4 ratings, then fewer vetoes, then shorter distance.
- AC4: Rating-3 conflicts return `OPTIONAL` and choose the candidate with the most 3 ratings.
- AC5: Rating-2 conflicts remain `OPTIONAL`.
- AC6: All veto-blocked conflicts select no performance.
- AC7: Automated tests cover BR-013 to BR-021 and runner-up/lost-alternative output.
- AC8: README and business requirements impact are recorded below.
- AC9: Architecture and ADR impact are recorded below.

## How to test

### Automated tests

- `/bin/zsh -lc 'JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test'`
- `git diff --check`

### Manual validation

- Not required for this task; the change is internal domain conflict resolution with no Android UI surface yet.

## TDD / BDD / approval-test evidence

- BDD-style acceptance criteria were translated into focused JUnit domain tests before implementation.
- The first focused test run failed because `PerformanceConflictResolver`, `PerformanceConflictResolution`, and `ConflictDistanceContext` did not exist.
- The implementation was added to make those tests pass, then full domain/application validation passed.
- No approval baseline was needed because this is new domain behavior, not legacy refactoring.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because this task implements already-planned internal MVP2 scheduling behavior without changing setup, commands, architecture, or current user-facing Android behavior.

## Business requirements impact

Business requirements impact: none, because BR-013 to BR-021 already define the implemented conflict-resolution rules.

## Diagram impact

Diagram impact: none, because module/container relationships did not change.

## Commits / logical change list

- `domain/src/main/java/be/wacken/planner/domain/PerformanceConflictResolver.java`: conflict winner selection and runner-up output.
- `domain/src/main/java/be/wacken/planner/domain/PerformanceConflictResolution.java`: resolution result model.
- `domain/src/main/java/be/wacken/planner/domain/ConflictDistanceContext.java`: distance tie-break input model.
- `domain/src/test/java/be/wacken/planner/domain/PerformanceConflictResolverTest.java`: focused rule coverage.

## Risks and follow-up

- The resolver only chooses within a conflict set. Building a day timeline belongs to task-56.
- Distance context is used only for tie-breaking. Full reachability/travel feasibility re-runs remain MVP3 scope.
<!-- SECTION:NOTES:END -->
