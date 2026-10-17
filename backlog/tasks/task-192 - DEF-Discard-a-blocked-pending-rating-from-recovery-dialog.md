---
id: TASK-192
title: 'DEF: Discard a blocked pending rating from recovery dialog'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-16 05:56'
updated_date: '2026-10-16 06:26'
labels: []
milestone: m-1
dependencies: []
references:
  - recoverpendingplanningratingusecase
  - wackendatabase
  - apprepositories
  - missingfestivalratingdialog
modified_files:
  - app/src/test/java/be/wacken/planner/PendingPlanningRatingRecoveryTest.java
  - >-
    app/src/test/java/be/wacken/planner/SyncingFestivalPlanningRatingRepositoryTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/RecoverPendingPlanningRatingUseCase.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingRepository.java
  - app/src/main/java/be/wacken/planner/AppRepositories.java
  - app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java
  - README.md
  - backlog/docs/business-requirements.md
priority: high
type: bug
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: a signed-in user who decides not to keep an orphaned local planning rating can explicitly clear that one blocked pending change and resume sync. The popup shows the band, value, and stored festival ID. Scope: discard only the displayed pending rating after a second confirmation; leave every other pending change, synced rating, band, and festival untouched. Out of scope: clearing all app data or all pending changes at once.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a blocked pending planning rating, when the recovery popup opens, then it offers a clearly labeled discard action alongside keep and move.
- [x] #2 Given the user confirms discard, when the exact displayed pending row is unchanged, then only that row is removed and the pending count decreases; a later sync no longer retries it.
- [x] #3 Given cancellation or a changed row, no rating is deleted and the user sees an accurate result.
- [x] #4 Tests cover exact-row deletion, preservation of other pending changes, and retry behavior.
- [x] #5 README explains this irreversible per-rating action and architecture impacts are recorded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implemented the confirmed Discard pending action, exact pending-row application/Room deletion, and retry sync. Added focused tests for changed rows, other pending changes, and post-discard sync. Updated README and BR-095a. No ADR or diagram change was needed because this extends the existing rating recovery port and Room query.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
- Added a second confirmation showing band, stars, and stored festival ID before discarding one unsynced rating. Cancellation preserves it.
- Enforced signed-in group/user ownership and an exact PENDING row/value check. A changed row fails without deleting other changes; success retries normal sync.

## Validation
- Red: focused test compilation failed until discard use case was added. Green: focused test and `./gradlew test` passed on JDK 21.
- `git diff --check` passed. `:app:lintDebug` reports 14 pre-existing NewApi errors; none are in new lines.
- UI button rendering was reviewed in code; device interaction remains for release verification.

## Architecture impact
- Approved by user on 2026-10-16: application port and Room exact deletion. No schema migration.
- README impact: updated pending-rating recovery and irreversible discard instructions.
- Business requirements impact: updated BR-095a to cover confirmed discard.
- Diagram impact: none, because the existing rating recovery boundary remains in place.
- ADR impact: none, because this extends the existing approved recovery and Room transaction pattern.

## Risks
- The discarded unsynced rating cannot be recovered from Supabase because it was never uploaded. The second confirmation states this.
<!-- SECTION:NOTES:END -->
