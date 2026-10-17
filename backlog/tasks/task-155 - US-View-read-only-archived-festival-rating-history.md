---
id: task-155
title: 'US: View read-only archived festival rating history'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-12 07:53'
updated_date: '2026-08-13 05:40'
labels:
  - user-story
  - post-mvp3
  - archive
  - ratings
  - history
dependencies:
  - task-150
  - task-152
  - task-153
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want to open archived festivals and band histories so that I can look back at what we planned and how I actually rated performances.

Business value: Completed festivals remain useful context instead of disappearing once planning moves to the next festival.

Scope: archived festival list when there is no active festival, read-only archived festival detail, band-level history showing planning and personal rating events with festival and created-date context where available.

Out of scope: editing archived festivals, admin-only archive permissions, analytics dashboards.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given no festival is active, when the app starts, then the user sees the archived festival list and can open an archived festival.
- [x] #2 Given a user opens an archived festival, then its lineup, planning ratings, real/personal ratings, schedule context, and imported festival data are displayed read-only where available.
- [x] #3 Given a band has personal rating events from multiple festivals, then the user can see each event with rating value, festival, and created date.
- [x] #4 Given an archived festival is open, then controls that would edit festival data or ratings are unavailable in the first version.
- [x] #5 BDD covers the no-active-festival start state and read-only archive inspection; domain/application tests cover historical rating retrieval.
- [x] #6 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added archived festival history use case/model and retrieval tests.
2. Showed archived festivals in the no-active start state and added a read-only archived festival screen.
3. Displayed archived lineup entries, planning rating count, and personal rating events with festival/date context.
4. Kept archived views read-only with no rating or festival-edit controls.
5. Updated README and business requirements and ran full validation.

Deviation: implemented alongside tasks 151-154 because archive inspection depends on the new lineup, planning rating, and personal history repositories. Architecture approval was previously received for ADR 0011 standard option.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented read-only archived festival inspection. When no festival is active, the start screen shows archived festivals as openable entries plus the add-festival action. ArchivedFestivalActivity displays lineup entries, stored planning rating count, and personal rating history with festival/date context without edit controls.

## Acceptance criteria validation

- AC1: MainActivity renders archived festival buttons in the no-active start state.
- AC2: ViewArchivedFestivalHistoryUseCase and ArchivedFestivalActivity display available lineup, planning, and personal history data read-only.
- AC3: personal rating events from history are displayed with rating value, festival, and created date.
- AC4: ArchivedFestivalActivity includes no rating or festival edit controls.
- AC5: FestivalArchiveQaScenarioTest covers no-active archive state; ArchivedFestivalHistoryUseCaseTest covers historical retrieval.
- AC6: documentation and impact notes recorded here.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac qaTest assembleDebug

### Manual validation

- Archive Wacken, tap the archived Wacken entry on the start screen, and verify the read-only archive screen opens without edit controls.

## TDD / BDD / approval-test evidence

- Added archived history application test and extended QA scenario coverage for the no-active start state.

## Architecture impact

- Architecture-significant change: yes, archive view consumes the approved festival/rating repository model.
- Approval received: yes, user approved ADR 0011 standard option.
- ADR: ADR impact: none, because ADR 0011 already records the approved archive/history direction.

## README impact

README impact: updated current behavior and repository/backend notes.

## Business requirements impact

Business requirements impact: updated implemented post-MVP3 capability status.

## Diagram impact

Diagram impact: none, because existing module diagrams still describe the same container/module boundaries.

## Commits / logical change list

- Add archived festival history use case and Android read-only archive screen.
- Add archive list entries on the no-active start state.

## Risks and follow-up

- Detailed archived schedule context is shown only where existing cached/imported data is available; editing archived festival data remains out of scope.
<!-- SECTION:NOTES:END -->
