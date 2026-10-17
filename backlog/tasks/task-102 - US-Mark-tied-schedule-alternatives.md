---
id: task-102
title: 'US: Mark tied schedule alternatives'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-14 19:54'
updated_date: '2026-06-14 20:11'
labels:
  - schedule
  - ui
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
When conflict resolution selects a winner only by the final fallback order after the normal rating/tie-break criteria are equal, the tied alternative should be visible as the lost alternative, listed first in the decision details, and marked with a clear tie icon. The winner logic itself must stay unchanged.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given two directly overlapping selectable acts tie on the existing conflict criteria before input order, when the schedule is generated, then the non-selected tied act is the lost alternative.
- [x] #2 Given a tied lost alternative exists, when schedule detail candidates are shown, then it appears immediately after the chosen act and before other alternatives.
- [x] #3 Given a tied lost alternative exists, when schedule detail candidates are shown, then its status includes a tie icon and tie wording.
- [x] #4 Given one act has broader group support than another, such as one extra 3-star rating, when the schedule is generated, then it is not marked as a tie.
- [x] #5 Automated tests cover tied alternatives and non-tied broader-support alternatives.
- [x] #6 Business requirements, README, diagram, and ADR impact are recorded using canonical delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added application-level regression tests for true tied alternatives and non-tied broader-support alternatives.
2. Extended PerformanceConflictResolution with a lostAlternativeTied flag while preserving the existing winner comparator.
3. Updated GenerateSharedScheduleUseCase so visible lost alternatives are listed first after the chosen act and tied alternatives use the status ⚖ TIED ALTERNATIVE.
4. Updated business requirements with BR-032a for tied lost-alternative visibility.
5. Ran domain/application tests, full relevant validation, and diff whitespace validation.
Architecture impact: not architecture-significant; this changes existing domain/application schedule output semantics without new dependencies, ports, persistence, or module boundaries. No ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added tied lost-alternative support without changing the current winner logic. A tie means the selected winner and runner-up are equal on the existing conflict criteria before the final input-order fallback. In that case, the runner-up is still the lost alternative, appears first after the chosen act in decision details, and is marked `⚖ TIED ALTERNATIVE`.

Also created follow-up UI ticket task-103 for the schedule rating color scheme.

## Acceptance criteria validation

- AC1: `marksTiedLostAlternativeAndListsItFirstAfterChosenAct` verifies a true tie makes Grand Magus the lost alternative.
- AC2: The same test verifies candidate order is chosen act, tied lost alternative, then other alternatives.
- AC3: The same test verifies status `⚖ TIED ALTERNATIVE`.
- AC4: `doesNotMarkLostAlternativeAsTieWhenWinnerHasBroaderGroupSupport` verifies Any given Day vs Danko Jones is not marked as tied because Any given Day has broader support.
- AC5: Automated tests cover tied and non-tied alternatives.
- AC6: Impact notes are recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac

### Manual validation

- git diff --check passed.

## TDD / BDD / approval-test evidence

Used TDD: added the failing tied-alternative application regression first, then implemented the domain/application changes to pass it.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because setup, commands, architecture, and high-level capability descriptions did not change.

## Business requirements impact

Business requirements impact: updated BR-032a to define tied lost-alternative visibility and ordering.

## Diagram impact

Diagram impact: none, because this changes schedule output behavior without changing architecture or workflow diagrams.

## Commits / logical change list

- Add tied lost-alternative regression coverage.
- Carry `lostAlternativeTied` from domain resolution to schedule details.
- Mark tied alternatives with `⚖ TIED ALTERNATIVE`.
- Add task-103 for the schedule rating color scheme.

## Risks and follow-up

The color-scheme UI change is tracked separately in task-103 and was not implemented here.
<!-- SECTION:NOTES:END -->
