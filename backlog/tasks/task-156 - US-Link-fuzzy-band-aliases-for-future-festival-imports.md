---
id: task-156
title: 'US: Link fuzzy band aliases for future festival imports'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-12 07:53'
updated_date: '2026-10-17 16:49'
labels:
  - user-story
  - bands
  - aliases
  - import
milestone: m-0
dependencies:
  - TASK-151
  - TASK-215
  - TASK-217
references:
  - supabasefestivalcreationgateway
  - apprepositories
  - wackendatabase
  - addfestivalusecase
modified_files:
  - >-
    application/src/test/java/be/wacken/planner/application/ReviewFestivalBandsUseCaseTest.java
  - domain/src/main/java/be/wacken/planner/domain/CanonicalBand.java
  - domain/src/main/java/be/wacken/planner/domain/ConfirmedBandAlias.java
  - domain/src/main/java/be/wacken/planner/domain/ReviewedFestivalBand.java
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalBandCatalog.java
  - >-
    application/src/main/java/be/wacken/planner/application/ReviewFestivalBandsUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalCreationGateway.java
  - >-
    application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java
  - domain/src/test/java/be/wacken/planner/domain/ReviewedFestivalBandTest.java
  - backend/flyway/tests/festival_aliases.sql
  - scripts/verify-rating-state.sh
  - backend/flyway/sql/V018__reviewed_festival_band_aliases.sql
  - app/src/main/java/be/wacken/planner/persistence/RoomBandAlias.java
  - app/src/main/java/be/wacken/planner/persistence/RoomBandAliasDao.java
  - app/src/main/java/be/wacken/planner/persistence/RoomBandAliasRepository.java
  - app/src/main/java/be/wacken/planner/SupabaseFestivalBandCatalog.java
  - app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java
  - app/src/test/java/be/wacken/planner/WackenDatabaseFestivalMigrationTest.java
  - app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java
  - app/src/main/java/be/wacken/planner/RoomFestivalCreationCache.java
  - app/src/main/java/be/wacken/planner/AppRepositories.java
  - app/src/main/java/be/wacken/planner/FestivalBandReviewDialog.java
  - app/src/main/java/be/wacken/planner/ImportCsvActivity.java
  - app/src/androidTest/java/be/wacken/planner/FestivalCreationAndroidTest.java
  - app/src/test/java/be/wacken/planner/ReviewedFestivalCreationGatewayTest.java
  - >-
    app/src/test/java/be/wacken/planner/AddFestivalPersistenceRegressionTest.java
  - >-
    app/src/androidTest/java/be/wacken/planner/FestivalBandReviewAndroidTest.java
  - >-
    app/src/androidTest/java/be/wacken/planner/persistence/BandAliasDatabaseTest.java
  - backlog/decisions/0018-reviewed-canonical-band-aliases.md
  - README.md
  - backlog/docs/business-requirements.md
  - app/src/test/java/be/wacken/planner/FestivalBandCatalogTest.java
priority: low
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want likely existing canonical bands and confirmed aliases proposed before a new festival is created so rating history and metadata reuse the correct identity without duplicates. Release2.34 scope: fuzzy-search proposals, per-row explicit match/no-match choice, optional durable alias storage, and atomic canonical lineup creation following ADR-0016/TASK-217. No match creates a catalog band then links it. Planning prefill copies each member's established band rating once; after-ratings remain festival-specific and are promoted only on archive. Preserve existing identities, metadata, ratings/history and old exact-name clients. No automatic fuzzy merge, rating merge or multi-group alias ownership. TASK-179's post-import linking remains available but does not complete this pre-creation/alias scope.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given an imported band name does not exactly match an existing band, then a future import flow can propose likely matches instead of immediately creating only a duplicate.
- [x] #2 Given a user confirms a proposed match, then the lineup entry links to the existing band and the imported name can be stored as an alias.
- [x] #3 Given no candidate is confirmed, then a new band record is created.
- [x] #4 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
- [x] #5 Given a reviewed canonical band or alias is selected before creation, then metadata is reused and planning prefill comes from that member's established band rating under ADR-0016; linking cannot merge/overwrite ratings, after-ratings or history.
- [x] #6 Existing published exact-name creation remains compatible while the reviewed creation path is introduced; the completed first post-Wacken implementation is retained as baseline rather than being blocked retroactively.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed framework-free canonical/alias/review models and explicit candidate choices; additive V018 member-only alias storage and reviewed request ledger, preserving legacy exact creation while reusing atomic canonical lineup/member snapshots; Room12 additive alias cache and master-data refresh; member-bound fresh review/creation; native staged per-row decisions/final summary/cancel; SQL/HTTP/Room/UI regression cases and ADR0018/README/BR/Mermaid. Full JVM/QA/coverage and signed clean APK/release lintVital pass. Full native55 passed; final affected12 passed including two additional cases (integrated suite57). Real creation16 zero-skip and rating/archive/correction/deletion/alias gates pass. Deviation: catalog locking and strict acknowledgement checks added for stale identity/cache safety. Full lint retains14 preexisting compatibility errors; discovered223 is now mandatory before214. Explicit approved standard architecture applies; no new dependency/service/secret.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

New festival creation now reviews nonexact band identities before saving. Exact canonical names retain reuse; fuzzy/confirmed-alias suggestions require explicit choice. Members may remember a selected spelling or choose a separate new band. A final summary confirms the creation; cancel writes no festival or alias.

V018 stores confirmed aliases and a reviewed request payload, retains the published exact-name RPC and reuses its atomic canonical lineup/every-member established planning snapshot. Duplicate canonical choices yield one lineup member. Alias conflicts/stale identities fail atomically. No metadata/opinion/history merge occurs. Room12 caches aliases with acknowledged creation; master-data refresh includes aliases.

## Acceptance criteria validation

1. Application tests and native review show likely canonical/known alias candidates without selecting nonexact matches.
2. Stable canonical IDs and optional remembered aliases are persisted atomically; real SQL/API assertions verify shared identity and original spelling.
3. Explicit no-match creates a separate exact-name band even when an alias suggestion exists.
4. README/requirements/workflow/C4 descriptions/ADR0018 updated.
5. Real API tests retain metadata and each member's established planning snapshot; no after-ratings or established/history writes arise from linking.
6. All16 real creation API tests passed with zero skips, including the original Summer Breeze63/exact-name/concurrency/replay/rollback baseline.

## How to test

### Automated tests

- JDK21 ./gradlew test qaTest :app:lintVitalRelease: passed, including domain80%/application70% coverage gates.
- JDK21 sh scripts/verify-festival-creation.sh:16 real PostgreSQL/PostgREST tests passed, zero skipped.
- bash scripts/verify-rating-state.sh: migrations throughV018 and real SQL/HTTP rating/archive/correction/deletion/alias gates passed. Alias assertions include stale identity, conflicting confirmation, outsider denial and injected later band-write rollback.
- Native full suite55 passed, zero failures/skips. After adding cache-last failure and multi-row independent-decision cases, the affected12 native tests passed, zero failures/skips. Final integrated suite is57 tests.
- Gateway regressions first failed when an incomplete/wrong-festival acknowledgement was accepted, then passed with strict validation. Catalog tests cover501 rows (pagination) and failed alias fetch retaining cache.
- git diff --check passed. Full lintDebug reports the same14 preexisting NewApi errors, with no new errors; TASK223 now blocks final release pending their resolution.

### Manual validation

No human acceptance claimed. Actual Android Activity/dialog and real Room are exercised by native automation; the backend is tested separately through authenticated real PostgREST. Review path: archive current festival, select a named bands CSV, choose each nonexact identity/new band, optionally remember alias, inspect summary, create. Cancel must create nothing. Repeat an uncertain creation with the same choices.

## TDD / BDD / approval-test evidence

Application tests initially failed for missing canonical/alias/review contracts; database tests failed for the missing reviewed RPC before implementation. Domain invariants and Given/When/Then scenarios cover explicit suggestions, no-match, validation and preservation. Published exact-name tests remain unchanged in behavior. Native fixture fixes account for posted Android button callbacks and correctly recognize valid band_id,name CSV headers. The complete native suite was rerun green and added final cases passed.

## Architecture impact

- Architecture-significant change: reviewed creation/catalog port, V018 aliases/ledger payload and Room12 additive cache.
- Approval received: explicit standard remaining2.34 architecture approval under TASK215/ADR0016.
- ADR impact: created backlog/decisions/0018-reviewed-canonical-band-aliases.md.
- Reuses existing lifecycle lock, creation transaction, member-bound authentication and master-data sync. A catalog table lock prevents direct legacy inserts/renames changing reviewed identity during creation. No new service/dependency/secret or offline alias-write queue.

## README impact

README impact: updated pre-creation choices, confirmation/cancel/retry, alias cache and migration-before-APK setup.

## Business requirements impact

Business requirements impact: updated implemented increment and BR092 for explicit durable aliases, conflicts, canonical deduplication and preserved ratings/history.

## Diagram impact

Diagram impact: updated C4 responsibilities and added Mermaid reviewed-creation workflow.

## Commits / logical change list

1. Canonical/alias/review models, application proposals and reviewed gateway extension with BDD/unit tests.
2. V018 authenticated alias/RLS/ledger/atomic reviewed creation and real SQL/API tests.
3. Room12 migration/cache and paginated catalog adapter/master-data sync.
4. Native staged decisions/final summary/cancel, validation/fault tests and documentation.

## Risks and follow-up

V014–V018 are tested on disposable databases only; final release214 must apply migrations before APK rollout. Current task APK retains baseline2.33.5/code41 and is not published2.34. Review requires connectivity; cancellation does not persist aliases. Multiple imported spellings selecting one canonical identity retain one deterministic lineup display name and all choices in the reviewed request ledger. Exact canonical names take precedence over alias suggestions. TASK169/219/220/223 and final214 remain. Full lint compatibility is tracked as requiredTASK223; no suppression/minSdk increase was introduced.

Fresh task build: JDK21 ./gradlew :app:clean test qaTest :app:lintVitalRelease :app:assembleRelease passed. APK SHA256 ebbe77f921a5b7a5c50b265818155fd2f7c58264e651a9b28e26ff02f5f29e2e; apksigner verifies published certificate SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. No2.34 publication claimed.
<!-- SECTION:NOTES:END -->
