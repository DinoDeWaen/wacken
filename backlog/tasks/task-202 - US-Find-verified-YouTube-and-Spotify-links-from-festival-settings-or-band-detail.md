---
id: TASK-202
title: >-
  US: Find verified YouTube and Spotify links from festival settings or band
  detail
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 08:11'
updated_date: '2026-10-17 09:24'
labels:
  - administration
  - metadata
  - bands
  - ux
milestone: m-0
dependencies:
  - TASK-179.1
  - TASK-179.5
  - TASK-179.6
references:
  - importcsvactivity
modified_files:
  - .github/workflows/ci.yml
  - app/build.gradle
  - app/src/androidTest/java/be/wacken/planner/FestivalCreationAndroidTest.java
  - app/src/androidTest/java/be/wacken/planner/UnknownBandLinksAndroidTest.java
  - >-
    app/src/androidTest/java/be/wacken/planner/persistence/FestivalDeletionDatabaseTest.java
  - app/src/main/java/be/wacken/planner/AppRepositories.java
  - app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java
  - app/src/main/java/be/wacken/planner/DeleteFestivalDialog.java
  - app/src/main/java/be/wacken/planner/FestivalScopedPerformanceRepository.java
  - app/src/main/java/be/wacken/planner/MainActivity.java
  - app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java
  - app/src/main/java/be/wacken/planner/RoomFestivalCreationCache.java
  - app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java
  - app/src/main/java/be/wacken/planner/SupabaseFestivalDeletionGateway.java
  - app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java
  - app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingClient.java
  - app/src/main/java/be/wacken/planner/SupabaseScheduleLockClient.java
  - app/src/main/java/be/wacken/planner/persistence/RoomFestivalCacheMarker.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomFestivalCacheMarkerDao.java
  - app/src/main/java/be/wacken/planner/persistence/RoomFestivalDao.java
  - app/src/main/java/be/wacken/planner/persistence/RoomFestivalDeletion.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingDao.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingRepository.java
  - app/src/main/java/be/wacken/planner/persistence/RoomFestivalRepository.java
  - app/src/main/java/be/wacken/planner/persistence/RoomPerformance.java
  - app/src/main/java/be/wacken/planner/persistence/RoomPerformanceDao.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomPerformanceRepository.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingEvent.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingEventDao.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingHistoryRepository.java
  - app/src/main/java/be/wacken/planner/persistence/RoomScheduleLock.java
  - app/src/main/java/be/wacken/planner/persistence/RoomScheduleLockDao.java
  - app/src/main/java/be/wacken/planner/persistence/RoomScheduleLockStore.java
  - app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java
  - >-
    app/src/test/java/be/wacken/planner/AddFestivalPersistenceRegressionTest.java
  - >-
    app/src/test/java/be/wacken/planner/FestivalScopedPerformanceRepositoryTest.java
  - app/src/test/java/be/wacken/planner/PendingPlanningRatingRecoveryTest.java
  - app/src/test/java/be/wacken/planner/SupabaseFestivalDeletionGatewayTest.java
  - app/src/test/java/be/wacken/planner/SupabaseMasterDataClientTest.java
  - >-
    app/src/test/java/be/wacken/planner/SyncingFestivalPlanningRatingRepositoryTest.java
  - >-
    app/src/test/java/be/wacken/planner/SyncingPersonalBandRatingHistoryRepositoryTest.java
  - app/src/test/java/be/wacken/planner/WackenDatabaseFestivalMigrationTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/DeleteFestivalUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalCreationGateway.java
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalCsvReader.java
  - >-
    application/src/main/java/be/wacken/planner/application/RecoverPendingPlanningRatingUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ViewArchivedFestivalHistoryUseCase.java
  - >-
    application/src/qaTest/java/be/wacken/planner/application/FestivalArchiveQaScenarioTest.java
  - >-
    application/src/test/java/be/wacken/planner/application/AddFestivalUseCaseTest.java
  - >-
    application/src/test/java/be/wacken/planner/application/DeleteFestivalUseCaseTest.java
  - >-
    application/src/test/java/be/wacken/planner/application/FestivalCsvReaderTest.java
  - >-
    application/src/test/java/be/wacken/planner/application/ShowBandDetailUseCaseTest.java
  - backend/flyway/repairs/task-197-summer-breeze.sql
  - backend/flyway/sql/V013__atomic_festival_creation.sql
  - backend/flyway/tests/festival_creation.sql
  - backend/flyway/tests/festival_deletion.sql
  - backend/flyway/tests/local_auth.sql
  - backend/flyway/verify-festival-deletion.sh
  - backlog/decisions/0013-festival-deletion-and-schedule-ownership.md
  - backlog/decisions/0014-atomic-festival-creation.md
  - backlog/docs/business-requirements.md
  - backlog/docs/release-process.md
  - backlog/tasks/task-191 - REL-Publish-V2.33.1-rating-recovery-hotfix.md
  - >-
    backlog/tasks/task-192 -
    DEF-Discard-a-blocked-pending-rating-from-recovery-dialog.md
  - >-
    backlog/tasks/task-193 -
    US-Delete-a-festival-and-its-schedule-without-deleting-shared-bands.md
  - backlog/tasks/task-194 - REL-Publish-V2.33.2-festival-cleanup-hotfix.md
  - backlog/tasks/task-196 - REL-Publish-V2.33.3-festival-CSV-hotfix.md
  - domain/src/main/java/be/wacken/planner/domain/Performance.java
  - domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingEvent.java
  - releases/v2.33.1.md
  - releases/v2.33.2.md
  - releases/v2.33.3.md
  - releases/v2.33.4.md
  - releases/v2.33.5.md
  - scripts/verify-festival-creation.sh
  - README.md
  - app/src/main/java/be/wacken/planner/ImportCsvActivity.java
  - backlog/decisions/0011-post-wacken-festival-rating-model.md
priority: medium
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a festival admin, I want to find the official YouTube channel and Spotify artist profile using a band's name, either for the selected festival from Settings/Admin beside Link bands or for one band from its detail page, so that I can verify and approve missing music links.

## Scope
Extend the existing TASK-179 reviewed metadata framework/providers. Use the stored band name by default and allow editing the search term without renaming the band. Festival mode searches missing links for that festival's lineup; band mode searches only the selected band.

Show candidate identity, canonical channel/artist URL, verification evidence and source links before approval. Establish identity using official band/label website cross-links, official platform artist/channel information and corroborating authoritative music-identity sources as appropriate. Search rank, a name match, a badge or a generated URL alone is not verification. Unresolved candidates stay unverified and missing fields stay empty.

The Spotify equivalent is an artist profile, not a playlist/user account. YouTube results must be band channels, not videos/fan accounts. Preserve existing nonempty metadata, lineup identity, ratings and history. No automatic merging, unreviewed saves, guessed links or unrelated enrichment. Follow existing provider boundaries and obtain approval before significant integration, configuration or persistence changes. Preserve TASK-199's empty-link behavior.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a selected festival, when an admin triggers Find music links from Settings/Admin beside Link bands, then that lineup's missing YouTube and/or Spotify fields are searched with per-band progress and results.
- [ ] #2 Given a band detail page, when the action is triggered, then only that band is searched using its name; editing the search term does not rename it or change its identity.
- [ ] #3 Candidate review shows band identity, provider, canonical channel/artist URL, clickable verification sources and a verification outcome/rationale that distinguishes this band from namesakes.
- [ ] #4 Ambiguous or unsupported matches remain unverified and cannot be saved as verified links. The user can refine the search, select an identity-verified candidate or reject all; no match/rejection leaves missing fields empty.
- [ ] #5 Only explicit approval saves verified results to missing golden-source fields and syncs them to other members. Existing links, lineup membership, ratings and history remain unchanged.
- [ ] #6 Offline use, missing provider configuration, rate limiting, errors and cancellation give clear per-band status, keep the UI responsive and preserve saved data; retry processes unfinished work without false success.
- [ ] #7 Application/provider/adapter and native Android tests cover both entry points, festival scoping, edited names, verified/ambiguous candidates, evidence, approval/rejection, no match, no overwrite, retry and JSON null metadata; manual verification steps are recorded.
- [ ] #8 README documents both actions, verification/source rules and provider configuration. Business requirements impact and Diagram impact use canonical delivery-governance wording when implemented.
- [ ] #9 Architecture impact is assessed before coding; significant changes to integrations, credentials, evidence persistence, API or schema require explicit approval and ADR handling.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Standard scope. First integrate published v2.33.5 fixes into codex/release-2-34 so new metadata work retains festival/import/null-link regressions. Inspect accepted ADR-0012 and existing search/proposal/provider/save contracts, then define the smallest verification-evidence and scope extension. Before architecture-significant provider/API/evidence changes, prepare a concrete approval request per repository rules. After approval, use BDD/TDD for band/festival scoping, edited search term, canonical verified links, approval/reject/no-overwrite and failure/retry; extend native coverage. Update README/business requirements, ADR where needed, and full validation package before Done. No 3.0 redesign in this task.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Integrated the published v2.33.5 baseline before new development. Resolved three merge conflicts by retaining the released atomic festival gateway, creation recovery wording and accepted ADR-0014; preserved the new design-study entry points. This integration reuses already published architecture and adds no new architectural decision.
<!-- SECTION:NOTES:END -->
