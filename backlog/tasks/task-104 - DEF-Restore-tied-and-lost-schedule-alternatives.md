---
id: task-104
title: 'DEF: Restore tied and lost schedule alternatives'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-15 05:44'
updated_date: '2026-06-15 07:04'
labels:
  - defect
  - schedule
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The schedule regression after the rating color/veto changes means tied alternatives are not visible in the schedule details and many selected blocks no longer show a lost alternative. Restore BR-031, BR-032, and BR-032a behavior while preserving the 1-star veto filtering rule.

Observed by user: ties were not implemented in the schedule and almost all blocks lost the lost alt.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A tied alternative is included first after the chosen act in schedule decision details and keeps the tied marker.
- [x] #2 A non-tied lost alternative that overlaps the selected act remains visible in the timeline slot and decision details.
- [x] #3 1-star vetoed alternatives are still hidden from selected acts, lost alternatives, and decision details.
- [x] #4 Regression tests cover tied alternatives, normal lost alternatives, and veto filtering together.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected resolver, schedule generation, and Android detail-candidate mapping.
2. Added an application regression test for chained conflicts where the global runner-up does not directly overlap the selected act.
3. Added an Android mapper regression test proving tied alternatives remain first after the generated choice in details.
4. Updated GenerateSharedScheduleUseCase to compute visible lost alternatives from the selected act plus directly overlapping, non-vetoed alternatives.
5. Ran focused and broader validation.

Deviation: the defect was in application-layer visible-lost-alternative selection, not in the domain resolver. Architecture impact: not architecture-significant; no approval or ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Restored schedule lost alternatives by separating the resolver global runner-up from the visible runner-up for a selected schedule block. The app now recomputes the visible lost alternative from the selected act and its directly overlapping, non-vetoed alternatives, so chained conflict sets no longer clear the block lost alternative when the global runner-up is outside the selected timeslot.

The tied-alternative marker remains preserved into schedule details, and 1-star veto filtering is still applied before visible alternatives are exposed.

## Acceptance criteria validation

- AC1: Tied alternatives remain first after the chosen act in schedule details and keep the ⚖ TIED ALTERNATIVE marker.
- AC2: Non-tied directly overlapping lost alternatives remain visible in the timeline slot and decision details even when the resolver global runner-up is outside the selected act timeslot.
- AC3: 1-star vetoed alternatives remain hidden from selected acts, lost alternatives, and detail candidates.
- AC4: Regression tests cover tied detail mapping, normal visible lost alternatives in chained conflicts, and the existing veto filtering behavior.

## How to test

### Automated tests

- /bin/zsh -lc "JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest"
- /bin/zsh -lc "JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac"

### Manual validation

- Not run; the regression is covered by application and Android unit tests.

## TDD / BDD / approval-test evidence

- Added a failing regression test for the missing lost alternative in chained conflicts before changing production code.
- Added detail-mapper coverage for tied alternatives.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR impact: none, because this keeps existing domain/application/UI boundaries and changes only application orchestration of existing resolver output.

## README impact

README impact: none, because setup, commands, architecture, and public usage instructions did not change.

## Business requirements impact

Business requirements impact: none, because this restores existing BR-031, BR-032, and BR-032a behavior rather than changing requirements.

## Diagram impact

Diagram impact: none, because no architecture or flow diagrams changed.

## Commits / logical change list

- GenerateSharedScheduleUseCase now calculates visible lost alternatives from direct overlaps of the selected act.
- Added application regression coverage for chained conflict visible lost alternatives.
- Added Android detail-mapper coverage for tied alternative ordering and status.

## Risks and follow-up

- No known follow-up. If a specific real-world block still lacks an expected lost alternative, capture the band names and times so the exact conflict set can be added as another fixture.
<!-- SECTION:NOTES:END -->
