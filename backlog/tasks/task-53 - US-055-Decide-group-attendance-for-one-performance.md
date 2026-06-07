---
id: task-53
title: 'US-055: Decide group attendance for one performance'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-07 15:36'
updated_date: '2026-06-07 15:52'
labels:
  - mvp2
  - scheduling
  - domain
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner group member, I want the app to combine all group ratings for a band into a clear attendance decision, so the generated schedule can explain why a band is selected, optional, unrated, or blocked.

In scope:
- Implement the single-performance group decision rules for ratings 0-5, must-see, want-to-see, optional, unrated, and veto handling.
- Apply the lunch-window optional rule for max rating 3 during 12:00-14:00 without inserting lunch blocks yet.
- Expose a decision result that can be used by scheduling and UI code.

Out of scope:
- Conflict resolution between overlapping performances.
- Travel feasibility, lunch planning, food suggestions, PDF output, and multi-group support.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a band has at least one rating of 5, when the group decision is calculated, then the decision is GO.
- [x] #2 Given a band has maximum rating 4, when the group decision is calculated, then it is GO unless there are two or more veto ratings.
- [x] #3 Given a band has maximum rating 3, when the group decision is calculated, then it is GO unless there is any veto, and it is OPTIONAL during 12:00-14:00.
- [x] #4 Given a band has maximum rating 2 or only unrated values, when the group decision is calculated, then it is OPTIONAL or UNRATED according to BR-011 and BR-012.
- [x] #5 Given veto combinations block a band, when the decision is calculated, then the result explains the veto reason.
- [x] #6 Automated tests cover BR-001 to BR-012 and BR-020.
- [x] #7 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [x] #8 Architecture impact is assessed; ADR impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added focused domain tests for BR-001 to BR-012 and BR-020 covering must-see, want-to-see, liked, lunch-window optional, indifferent, unrated, and veto-blocked decisions.
2. Implemented a minimal domain-owned `GroupDecisionPolicy`, `GroupDecision`, and `GroupDecisionStatus` in the `domain` module with no framework or infrastructure dependencies.
3. Kept decision reasons explicit enough for later schedule/UI stories.
4. Ran domain tests/coverage, downstream application tests, and diff whitespace validation.
5. Recorded README impact: none, because the task implements already-documented MVP2 business rules without changing setup, public app behavior, commands, or architecture docs.
6. Recorded business requirements impact: none, because BR-001 to BR-012 and BR-020 already define these rules.

Deviation: no application use case was added yet because task-53 only needs a reusable domain result for later scheduling stories. Architecture impact: not architecture-significant; this keeps business rules in the existing domain module, adds no new boundaries, dependencies, persistence, schema, external contracts, or module structure. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Added `GroupDecisionPolicy` in the domain module to decide single-performance group attendance from group ratings.
- Added `GroupDecisionStatus` values for `GO`, `OPTIONAL`, `UNRATED`, and `BLOCKED`.
- Added `GroupDecision` with explicit reason, max rating, and veto count so later schedule and UI stories can explain decisions.
- Added focused domain tests for the MVP2 single-performance decision rules.

## Acceptance criteria validation

- AC1: Any rating of `5` returns `GO` with a must-see reason.
- AC2: Max rating `4` returns `GO` unless two or more vetoes return `BLOCKED`.
- AC3: Max rating `3` returns `GO`, returns `BLOCKED` with any veto, and returns `OPTIONAL` during the 12:00-14:00 lunch window.
- AC4: Max rating `2` returns `OPTIONAL`; missing or only `0` ratings return `UNRATED`.
- AC5: Veto-blocked decisions include explicit veto reasons and veto counts.
- AC6: Automated tests cover BR-001 to BR-012 and BR-020 through `GroupDecisionPolicyTest` plus existing rating validation tests.
- AC7: README and business requirements impact are recorded below.
- AC8: Architecture and ADR impact are recorded below.

## How to test

### Automated tests

- `/bin/zsh -lc 'JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test'`
- `/bin/zsh -lc 'JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test'`
- `git diff --check`

### Manual validation

- Not required for this task; the change is internal domain decision behavior and has no Android UI surface yet.

## TDD / BDD / approval-test evidence

- BDD-style acceptance criteria from `task-53` were translated into focused JUnit domain tests before implementation.
- The first focused test run failed because `GroupDecisionPolicy`, `GroupDecision`, and `GroupDecisionStatus` did not exist.
- The implementation was added to make those tests pass, then the full domain/application validation passed.
- No approval baseline was needed because this is new domain behavior, not legacy refactoring.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because this task implements already-documented MVP2 decision rules without changing setup, commands, architecture, or current user-facing Android behavior.

## Business requirements impact

Business requirements impact: none, because BR-001 to BR-012 and BR-020 already define the implemented rules.

## Diagram impact

Diagram impact: none, because module/container relationships did not change.

## Commits / logical change list

- `domain/src/main/java/be/wacken/planner/domain/GroupDecisionPolicy.java`: domain decision rules for single performances.
- `domain/src/main/java/be/wacken/planner/domain/GroupDecision.java`: decision result with reason and counts.
- `domain/src/main/java/be/wacken/planner/domain/GroupDecisionStatus.java`: decision status vocabulary.
- `domain/src/test/java/be/wacken/planner/domain/GroupDecisionPolicyTest.java`: focused rule coverage.

## Risks and follow-up

- This task intentionally does not wire the policy into a schedule use case or Android UI. That work belongs to the dependent MVP2 stories.
- The lunch-window rule treats any performance overlapping 12:00-14:00 as lunch-window relevant; later lunch planning can refine that in MVP3 if needed.
<!-- SECTION:NOTES:END -->
