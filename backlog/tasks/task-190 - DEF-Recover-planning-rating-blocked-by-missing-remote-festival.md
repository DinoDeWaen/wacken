---
id: TASK-190
title: 'DEF: Recover planning rating blocked by missing remote festival'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-15 19:20'
updated_date: '2026-10-15 19:57'
labels:
  - defect
  - supabase
  - sync
  - festival
dependencies: []
references:
  - syncingratingrepository
  - wackendatabase
  - apprepositories
  - mainactivity
  - settingsactivity
  - supabaseratingclient
  - recoverpendingplanningratingusecase
modified_files:
  - >-
    app/src/test/java/be/wacken/planner/SyncingFestivalPlanningRatingRepositoryTest.java
  - >-
    app/src/main/java/be/wacken/planner/MissingFestivalPlanningRatingException.java
  - >-
    app/src/main/java/be/wacken/planner/SyncingFestivalPlanningRatingRepository.java
  - >-
    app/src/main/java/be/wacken/planner/FestivalPlanningRatingSyncLocalStore.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingDao.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingRepository.java
  - app/src/test/java/be/wacken/planner/PendingPlanningRatingRecoveryTest.java
  - app/src/main/java/be/wacken/planner/AppRepositories.java
  - app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java
  - app/src/main/java/be/wacken/planner/MainActivity.java
  - app/src/main/java/be/wacken/planner/SettingsActivity.java
  - >-
    app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingRemote.java
  - >-
    app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/decisions/0011-post-wacken-festival-rating-model.md
  - >-
    application/src/main/java/be/wacken/planner/application/RecoverPendingPlanningRatingUseCase.java
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: a signed-in user can finish syncing ratings without losing a locally saved change. Observed on 2026-10-15: one pending planning rating repeatedly fails with festival_planning_ratings_festival_id_fkey while Supabase contains only wacken-2026. The pending row's festival ID exists only on the device and is not exposed by the current export. Scope: identify the exact local festival/rating, preserve it, offer a safe repair or explicit recovery path, and prevent repeated opaque sync failure. Out of scope: silently reassigning a rating to another festival or deleting a pending rating without user review.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a pending rating whose festival is absent remotely, when sync runs, then the user can identify the affected festival and band without losing the pending rating.
- [x] #2 Given a recoverable missing festival, when the user completes the supported recovery, then the rating syncs to its intended festival and pending count decreases.
- [x] #3 Given recovery cannot be completed, the app keeps the local rating and explains the next action instead of showing only a database constraint name.
- [x] #4 Automated regression tests cover missing-festival detection, preserved data, and successful recovery.
- [x] #5 README documents any new recovery steps, and architecture approval/ADR impact are recorded when required.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added a typed missing-festival diagnostic before pending planning ratings are pushed, using the refreshed festival cache.
2. Added a review dialog on home and Settings sync. The user sees band, stars, and stored festival ID and can keep the row or explicitly move it to Wacken 2026.
3. Added an application recovery use case. It validates current user, missing source, active Wacken target, pending source, local destination, and exact remote destination. Room rechecks and moves the key in one transaction. Successful local recovery retries the normal sync.
4. Added regression tests for detection, successful move and push, conflicts, archived target, and network failure. Ran full module tests, QA, debug compile/build, lint, and diff check. Updated README, BR-095a, and ADR 0011.

Deviation: remote conflict lookup uses an exact PostgREST key query rather than a full group pull, avoiding pagination misses. Architecture approval was given by the user on 2026-10-15. Device validation remains pending because the affected phone is not attached.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Pending planning ratings whose festival is missing from the refreshed Supabase list now show a review dialog with stored festival ID, band, and stars. The user can keep the rating or explicitly move it to active Wacken 2026. The recovery checks local and exact remote Wacken rating conflicts, then atomically moves only the matching pending Room row and retries sync. Any failure keeps the original pending row.

## Acceptance criteria validation

- Missing festival is detected before push and the pending row is retained (test).
- Confirmed move and subsequent normal push clear the pending count (integrated fake test).
- Local/remote conflict, archived target, and network failure preserve the row (tests).
- README documents phone recovery steps.

## How to test

### Automated tests

- PASS: `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest qaTest :app:assembleDebug`
- PASS: `git diff --check`
- Lint ran but failed on 14 existing `NewApi` errors; no new lint errors in the recovery code.

### Manual validation

- Phone unavailable over ADB; its exact pending band and festival ID could not be inspected. After installing an updated signed build, sync, review the dialog, confirm only if its band/stars match the intended Wacken rating, and check that the pending count decreases. Do not clear Android app storage.

## TDD / BDD / approval-test evidence

- Added a missing-festival regression test before production detection code. Added recovery tests for successful push and preserved data on conflict/offline paths.

## Architecture impact

- Architecture-significant change: application recovery policy and Room transaction boundary. No schema migration.
- Approval received: user explicitly requested the proposed targeted fix on 2026-10-15.
- ADR: updated ADR 0011.

## README impact

- README impact: updated missing-festival rating recovery instructions.

## Business requirements impact

- Business requirements impact: updated BR-095a for explicit recoverability and conflict protection.

## Diagram impact

- Diagram impact: none, because existing application, Room, and Supabase boundaries remain the same.

## Commits / logical change list

- Missing-festival diagnostic and review UI; application recovery use case; exact remote conflict lookup; transactional Room move; regression tests and documentation.

## Risks and follow-up

- The affected rating still exists only on the user phone. Installing a signed build and confirming there is required for actual sync recovery. A remote rating created between conflict check and later sync could still be overwritten by the existing upsert behavior; this small concurrency window is not closed by this local recovery task.
- Existing 14 Android lint `NewApi` errors remain outside this task.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Implemented user-confirmed recovery for a pending Wacken 2026 rating whose stored festival is missing remotely; conflict checks preserve existing ratings and tests pass.
<!-- SECTION:FINAL_SUMMARY:END -->
