---
id: TASK-202
title: >-
  US: Find verified YouTube and Spotify links from festival settings or band
  detail
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 08:11'
updated_date: '2026-10-17 11:51'
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
  - settingsactivity
  - banddetailactivity
  - syncedbandrepository
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
  - backlog/docs/task-202-verified-links-architecture-proposal.md
  - backlog/decisions/0015-verified-music-link-evidence.md
  - >-
    application/src/test/java/be/wacken/planner/application/FindBandMusicLinksUseCaseTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/MusicLinkEvidence.java
  - >-
    application/src/main/java/be/wacken/planner/application/MusicLinkLookupProvider.java
  - >-
    application/src/main/java/be/wacken/planner/application/FindBandMusicLinksUseCase.java
  - app/src/test/java/be/wacken/planner/VerifiedMusicLinksProviderTest.java
  - app/src/test/java/be/wacken/planner/OfficialMusicPageClientTest.java
  - app/src/main/java/be/wacken/planner/OfficialMusicPageClient.java
  - app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java
  - app/src/main/java/be/wacken/planner/WikidataMetadataProvider.java
  - app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java
  - app/src/androidTest/java/be/wacken/planner/MusicLinksReviewAndroidTest.java
  - app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java
  - app/src/main/AndroidManifest.xml
  - app/src/main/java/be/wacken/planner/SettingsActivity.java
  - app/src/main/java/be/wacken/planner/BandDetailActivity.java
  - >-
    infrastructure/src/test/java/be/wacken/planner/infrastructure/VerifiedMusicLinksSyncTest.java
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
- [x] #1 Given a selected festival, when an admin triggers Find music links from Settings/Admin beside Link bands, then that lineup's missing YouTube and/or Spotify fields are searched with per-band progress and results.
- [x] #2 Given a band detail page, when the action is triggered, then only that band is searched using its name; editing the search term does not rename it or change its identity.
- [x] #3 Candidate review shows band identity, provider, canonical channel/artist URL, clickable verification sources and a verification outcome/rationale that distinguishes this band from namesakes.
- [x] #4 Ambiguous or unsupported matches remain unverified and cannot be saved as verified links. The user can refine the search, select an identity-verified candidate or reject all; no match/rejection leaves missing fields empty.
- [x] #5 Only explicit approval saves verified results to missing golden-source fields and syncs them to other members. Existing links, lineup membership, ratings and history remain unchanged.
- [x] #6 Offline use, missing provider configuration, rate limiting, errors and cancellation give clear per-band status, keep the UI responsive and preserve saved data; retry processes unfinished work without false success.
- [x] #7 Application/provider/adapter and native Android tests cover both entry points, festival scoping, edited names, verified/ambiguous candidates, evidence, approval/rejection, no match, no overwrite, retry and JSON null metadata; manual verification steps are recorded.
- [x] #8 README documents both actions, verification/source rules and provider configuration. Business requirements impact and Diagram impact use canonical delivery-governance wording when implemented.
- [x] #9 Architecture impact is assessed before coding; significant changes to integrations, credentials, evidence persistence, API or schema require explicit approval and ADR handling.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed: integrated published v2.33.5; obtained explicit standard architecture approval and accepted ADR-0015; wrote red application/adapter/native cases; implemented evidence verification, conservative ambiguity/current-profile checks and bounded public HTTPS retrieval; wired both scoped native entries with approval/rejection/edit/retry/cancel and rotation retention; fixed return-to-detail links under a failing regression; passed full unit/QA/coverage, 26 native tests, release lint-vital, fresh signed build and signature/digest checks; updated README and business workflow. Deviation: native regression exposed stale detail links, fixed locally. Full lintDebug retains 14 known pre-existing errors. Real database/API gate and final release version/publication remain TASK-214.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added dedicated Find music links reviews from Settings/Admin for the active festival lineup and from Band Links for one band. Search terms are editable without renaming bands. Review shows identity, canonical profiles, source actions and verification rationale; selection plus explicit approval is required. Rejection, ambiguity and missing evidence leave fields empty.

MusicBrainz/Wikidata identity facts and bounded official-site anchors are evaluated in the application. Existing links must agree with the candidate; saving rechecks current fields and retains biographies, ratings, history and lineup identity. Remote-first shared-band saving remains unchanged. Reviews survive rotation, cancellation stays responsive, and retry reuses completed festival reviews. Returning to detail refreshes the links section without replacing rating controls or scroll.

## Acceptance criteria validation

1–2: Native tests follow both real Activity entry points and edit a search term; application tests isolate festival lineup scope and selected band identity.
3–4: Provider/HTML tests expose source facts and canonical links; application tests reject namesakes, mismatched identifiers and unsupported video/user URLs. Native review asserts evidence actions, disabled unverified save and rejection.
5: Explicit native approval fills a verified field. Real SyncedBandRepository composition tests show another member receives it on sync and remote failure leaves the cache unchanged. Current-profile mismatch and no-overwrite regressions pass.
6: Native tests exercise cancellation, error/retry and no match. Provider tests exercise evidence failure and JSON null; configuration warnings retain public-source lookup. Application progress and retry tests preserve completed work.
7–9: Automated layers, manual steps, documentation, architecture approval and accepted ADR recorded below.

## How to test

### Automated tests

- JDK21: ./gradlew :app:clean test qaTest :app:lintVitalRelease :app:assembleRelease with existing Keychain signing values — passed, fresh signed task-validation APK.
- Domain: 51; application: 120; QA: 3; infrastructure: 24 — all passed, zero skipped. Domain/application coverage gates passed.
- App debug and release unit suites: 204 tests each, 194 passed, 10 existing database/API scenarios skipped without the isolated PostgreSQL/PostgREST fixture. The mandatory real API gate belongs to final TASK-214; it was not run for this metadata-only task.
- sh scripts/verify-android-smoke.sh emulator-5554 all — 26 native tests passed, zero skipped, isolated API36 ARM64 emulator. Includes seven new review tests and existing import/null-link/rating/schedule flows.
- Full lintDebug: 14 unchanged, previously documented NewApi errors remain in older code. The new provider URL-encoder finding was fixed; release lintVital passed. No lint baseline/suppression added.
- git diff --check passed.
- apksigner verified v1/v2 signatures. APK app/build/outputs/apk/release/app-release.apk; SHA-256 20e65fa36006204cee8fd85660ca09e77e3a9a82885cb424c5ce6affe4e572f6. Task-validation artifact retains baseline version 2.33.5/code41; not published as release2.34.

### Manual validation

Reproducible steps: open Settings/Admin beside Link imported bands, choose Find music links, edit one term, search, open identity/official-site sources, and approve only a verified intended artist. Repeat from a band page. Reject or cancel and confirm missing fields remain empty. Return after approval and confirm the new music action appears; sync a second member to observe shared metadata. Disable connectivity and retry unfinished work.

No live-provider or physical-phone walkthrough is claimed. Native actions and deterministic source/transport fixtures were executed; public service availability is external.

## TDD / BDD / approval-test evidence

BDD scenarios are executable Given/When/Then-style acceptance tests across application, provider, sync-composition and native layers. Initial application/adapter/native tests failed before the implementations existed. The stale-review identity regression failed before current-band revalidation; the return-to-detail test failed before links refresh. Both are green. Existing general enrichment behavior remains covered by the regression suite; no legacy provider behavior was refactored.

## Architecture impact

- Architecture-significant change: source-evidence port and bounded official-site retrieval within existing reviewed enrichment.
- Approval received: explicit user approval on 2026-10-17 of the standard task-202 proposal before implementation.
- ADR: accepted ADR-0015. No schema, service, dependency or new secret added.

## README impact

README impact: updated both actions, evidence policy, limits, configuration, retry/lifecycle behavior, testing and ADR link.

## Business requirements impact

Business requirements impact: updated Workflow11c with scoped discovery, evidence verification, explicit approval and preservation/failure rules.

## Diagram impact

Diagram impact: none, because system/container boundaries and existing provider/sync relationships are unchanged; the new evidence policy is documented in ADR-0015.

ADR impact: created 0015-verified-music-link-evidence.md following approved proposal.

## Commits / logical change list

Published v2.33.5 baseline integration; evidence/application policy; provider and bounded HTTPS adapters; two native entry points/review lifecycle; regression and shared-save tests; documentation.

## Risks and follow-up

Conservative evidence requirements intentionally leave poorly documented artists unresolved. Unsupported profile formats, identity conflicts, blocked sites or absent cross-links cannot be approved. Evidence is transient and must be retrieved again after process loss. Public services and server permissions may reject lookups/saves. Existing minimum-Android compatibility lint debt is unchanged; no lower-API walkthrough is claimed. Final2.34 versioning, API gate, upgrade and publication remain TASK-214.
<!-- SECTION:NOTES:END -->
