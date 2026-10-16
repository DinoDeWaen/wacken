---
id: TASK-193
title: 'US: Delete a festival and its schedule without deleting shared bands'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-16 05:57'
updated_date: '2026-10-16 08:13'
labels: []
milestone: m-1
dependencies: []
references:
  - performance
  - rating
  - wackendatabase
  - apprepositories
  - supabasemasterdataclient
  - supabaseratingclient
  - mainactivity
  - archivedfestivalactivity
  - listactivefestivalbandsusecase
  - importcsvactivity
modified_files:
  - >-
    application/src/test/java/be/wacken/planner/application/DeleteFestivalUseCaseTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/DeleteFestivalUseCase.java
  - domain/src/main/java/be/wacken/planner/domain/Performance.java
  - domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingEvent.java
  - backend/flyway/sql/V011__festival_deletion_and_schedule_scope.sql
  - app/src/main/java/be/wacken/planner/persistence/RoomPerformance.java
  - app/src/main/java/be/wacken/planner/persistence/RoomPerformanceDao.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomPerformanceRepository.java
  - app/src/main/java/be/wacken/planner/persistence/RoomScheduleLock.java
  - app/src/main/java/be/wacken/planner/persistence/RoomScheduleLockDao.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingDao.java
  - app/src/main/java/be/wacken/planner/persistence/RoomFestivalDao.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingEvent.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingEventDao.java
  - app/src/main/java/be/wacken/planner/persistence/RoomFestivalCacheMarker.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomFestivalCacheMarkerDao.java
  - app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java
  - app/src/main/java/be/wacken/planner/persistence/RoomFestivalRepository.java
  - app/src/main/java/be/wacken/planner/persistence/RoomScheduleLockStore.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingHistoryRepository.java
  - app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java
  - app/src/main/java/be/wacken/planner/SupabaseScheduleLockClient.java
  - app/src/main/java/be/wacken/planner/FestivalScopedPerformanceRepository.java
  - app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingClient.java
  - >-
    application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ViewArchivedFestivalHistoryUseCase.java
  - app/src/main/java/be/wacken/planner/persistence/RoomFestivalDeletion.java
  - app/src/main/java/be/wacken/planner/SupabaseFestivalDeletionGateway.java
  - app/src/test/java/be/wacken/planner/SupabaseFestivalDeletionGatewayTest.java
  - app/src/main/java/be/wacken/planner/AppRepositories.java
  - app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java
  - app/src/main/java/be/wacken/planner/DeleteFestivalDialog.java
  - app/src/main/java/be/wacken/planner/MainActivity.java
  - app/src/test/java/be/wacken/planner/WackenDatabaseFestivalMigrationTest.java
  - >-
    app/src/test/java/be/wacken/planner/FestivalScopedPerformanceRepositoryTest.java
  - app/build.gradle
  - >-
    app/src/androidTest/java/be/wacken/planner/persistence/FestivalDeletionDatabaseTest.java
  - backend/flyway/tests/festival_deletion.sql
  - backend/flyway/verify-festival-deletion.sh
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/decisions/0013-festival-deletion-and-schedule-ownership.md
  - >-
    application/src/test/java/be/wacken/planner/application/ShowBandDetailUseCaseTest.java
  - >-
    app/src/test/java/be/wacken/planner/SyncingPersonalBandRatingHistoryRepositoryTest.java
  - app/src/main/java/be/wacken/planner/ImportCsvActivity.java
priority: high
type: feature
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: a group can remove an unwanted festival and its festival-owned planning schedule while keeping reusable bands in the central catalog. Scope: visible delete action with explicit confirmation, remote-first group-wide deletion, cleanup of festival-owned lineup/performances/schedule locks/planning ratings and local cache, and prevention of deleted-festival reseeding. Preserve the shared bands catalog. The current schema does not scope performances or schedule locks by festival; Supabase permits only platform-admin festival deletion and personal rating history loses its festival reference on hard delete. Resolve these data and authorization choices through explicit architecture approval before coding. Out of scope: silently deleting a production festival during this task.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given an authorized user and a selected festival, when deletion is confirmed, then that festival disappears for all group members after sync and its own schedule no longer appears.
- [x] #2 Deleting a festival never deletes a band from the central bands catalog or another festival lineup, schedule, or rating.
- [x] #3 The user sees the affected data and receives clear success or failure feedback; a remote denial leaves the local festival intact.
- [x] #4 The app does not recreate an intentionally deleted default Wacken festival on restart, and tests cover deletion plus cross-festival preservation.
- [x] #5 README, business requirements, migration/authorization decision, ADR, and diagram impacts are recorded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Implemented festival-owned performances and schedule locks with Room 9 and Flyway V011 backfills; preserved personal-history names with snapshots. Added the DeleteFestivalUseCase/gateway, remote absence verification, transactional local cleanup, active/archive confirmation buttons, progress/error feedback, sync cleanup on other devices, and a persisted initialization marker. Scoped schedule reads and preserved pending-lock sync across retained festivals. Used existing full-suite behavior as the baseline, added use-case/adapter tests and real Android migration/deletion tests, then updated README, BR-086/102/105b/105c and ADR 0013. User architecture approval was received on 2026-10-16. Deviation: AndroidX runner 1.6.2 and an explicit performance-import target were needed for real Room tests and safe scoped reads.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
- Group members can confirm deletion from the active or archived festival screen. Supabase deletion is verified before local cleanup; denial preserves the local festival. Progress and failure feedback are visible.
- Festival-owned lineup, planning ratings, performances, and schedule locks are removed. Central bands, other festivals, and personal event values/dates/name snapshots remain.
- Room initialization and legacy-history guards prevent deleted Wacken data from being recreated. A successful festival sync cleans deletions from another device; pending locks for retained festivals continue syncing.

## Validation
- Initial delete behavior tests preceded the use case. Full JDK 21 command passed: ./gradlew test qaTest :app:connectedDebugAndroidTest.
- Four real Android database tests passed: exact pending-row discard, selected-festival cleanup/preservation, remote deletion cache convergence, and Room 8-to-9 upgrade preservation.
- Final :app:testDebugUnitTest passed after explicit import wiring.
- Flyway V011 applied successfully to wacken2026. bash backend/flyway/verify-festival-deletion.sh passed member deletion, non-member denial, cascade isolation, central-band preservation, and history preservation; all synthetic rows rolled back.
- Production counts remain 1 festival, 186 performances, 1 schedule lock, 36 personal rating events, and 166 bands. Every existing event with a festival has a name snapshot.
- bash -n passed; git diff --check passed. No formatter is configured.
- :app:lintDebug still reports 14 pre-existing NewApi errors, plus English-text/dependency warnings. Release lint-vital is checked in TASK-194.
- Supabase advisors found existing issues in legacy functions and Flyway metadata, not the new invoker trigger: [search path](https://supabase.com/docs/guides/database/database-linter?lint=0011_function_search_path_mutable), [Flyway RLS](https://supabase.com/docs/guides/database/database-linter?lint=0013_rls_disabled_in_public), [anonymous definer access](https://supabase.com/docs/guides/database/database-linter?lint=0028_anon_security_definer_function_executable), [authenticated definer access](https://supabase.com/docs/guides/database/database-linter?lint=0029_authenticated_security_definer_function_executable).

## Architecture impact
- Approval received: user approved the standard design on 2026-10-16 before coding.
- Domain/application ports remain framework-free. Room and Supabase adapters own storage and transport.
- README impact: updated deletion behavior, migration/test commands, C4 labels, and ADR link.
- Business requirements impact: updated BR-086 and BR-102 and added BR-105b/BR-105c.
- Diagram impact: updated the README C4 container labels for festival ownership and deletion.
- ADR impact: created 0013-festival-deletion-and-schedule-ownership.md.

## Risks
- Confirmed deletion is irreversible and removes festival planning ratings.
- The lock conflict contract now includes festival_id; all group devices should update to V2.33.2.
- If remote deletion succeeds but local cleanup fails, feedback directs the user to sync again; remote festival reconciliation retries local cleanup.
<!-- SECTION:NOTES:END -->
