---
id: TASK-216
title: 'US: Keep personal band ratings separate from festival after-ratings'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 13:18'
updated_date: '2026-10-17 14:16'
labels:
  - ratings
  - festival
  - architecture
  - release-2.34
milestone: m-0
dependencies:
  - TASK-215
references:
  - apprepositories
  - wackendatabase
  - banddetailactivity
  - supabasemasterdataclient
modified_files:
  - >-
    application/src/test/java/be/wacken/planner/application/FestivalAfterRatingIsolationTest.java
  - domain/src/main/java/be/wacken/planner/domain/FestivalAfterRating.java
  - >-
    domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java
  - domain/src/main/java/be/wacken/planner/domain/PersonalBandRating.java
  - >-
    domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingRepository.java
  - >-
    application/src/main/java/be/wacken/planner/application/RecordFestivalAfterRatingUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ActiveFestivalAfterRatingRepository.java
  - >-
    app/src/androidTest/java/be/wacken/planner/persistence/FestivalAfterRatingDatabaseTest.java
  - app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRating.java
  - app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRating.java
  - app/src/main/java/be/wacken/planner/persistence/RoomRatingStateDao.java
  - app/src/main/java/be/wacken/planner/RatingStateSyncStore.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingRepository.java
  - app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java
  - app/src/test/java/be/wacken/planner/WackenDatabaseFestivalMigrationTest.java
  - backend/flyway/tests/rating_state.sql
  - scripts/verify-rating-state.sh
  - backend/flyway/sql/V014__independent_band_and_festival_after_ratings.sql
  - app/src/test/java/be/wacken/planner/RatingStateSyncTest.java
  - app/src/test/java/be/wacken/planner/SupabaseRatingStateClientTest.java
  - app/src/main/java/be/wacken/planner/RatingStateSync.java
  - app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java
  - app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java
  - app/src/main/java/be/wacken/planner/AppRepositories.java
  - app/src/main/java/be/wacken/planner/BandDetailActivity.java
  - app/src/main/java/be/wacken/planner/persistence/RoomFestivalDeletion.java
  - backend/flyway/tests/rating_state_api.py
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - >-
    app/src/test/java/be/wacken/planner/LegacyRealRatingBackfillRegressionTest.java
  - >-
    domain/src/test/java/be/wacken/planner/domain/PersonalAndFestivalRatingStateTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/RecordPersonalBandRatingUseCase.java
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/decisions/0016-band-catalog-and-archive-promoted-personal-ratings.md
  - .github/workflows/ci.yml
  - scripts/verify-android-smoke.sh
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member, I want a stable personal band rating and a separate after-rating for each festival so my current opinion and festival history cannot overwrite each other. Follow the user-approved archive-promotion architecture in ADR-0016. Scope: canonical band references; current rating per user/band with source provenance; after-rating per user/festival/band; application-owned rules, additive Room/Flyway persistence, existing sync integration and safe legacy migration. Saving an active festival after-rating must not promote it to the current band rating. Preserve active offline editing/pending work. No new service or secret.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a canonical band appears in two festivals, when a member changes one festival after-rating, then the other festival, both planning ratings and the last known band rating remain unchanged.
- [x] #2 Given different members rate one band, then each retains their own current band rating and festival after-ratings; shared metadata exists once.
- [x] #3 Given an active festival after-rating is reset to unrated, then its planning rating and established band rating remain unchanged and archiving cannot promote the empty value.
- [x] #4 Given existing installations, when migrations run, then ratings, event dates, metadata, identity, auth and pending operations are preserved; archived outcomes can backfill with provenance, while ambiguous legacy history is retained without inventing dates or promoting active results.
- [x] #5 BDD/TDD, real database/RLS, Room migration and pending-sync tests validate isolation, ownership, reset, migration and recovery. Existing observable behavior is characterized before refactoring.
- [x] #6 README and business requirements document the implemented model; ADR-0016 and canonical diagram/ADR impacts are recorded, with explicit user approval linked before significant changes.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed characterization and red/green domain/application isolation; additive Room10/FlywayV014, conservative legacy recovery, owner-scoped atomic API, pending-safe sync and active detail wiring. Verified full JVM/QA/coverage, actual SQL/HTTP and34 native tests; built and verified fresh signed task APK. Updated README/BR/ADR0016. Deviation: fixed existing CI offline-fixture assumption using real airplane mode after GitHub exposed no-SIM preference behavior. Architecture approved by user/TASK215; creation/archive promotion intentionally remain217/218.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Separated established personal band ratings from festival after-ratings across domain/application, Room10, authenticated Supabase persistence and sync. Active detail writes only its festival after-rating; reset is a durable zero state, while positive history remains intact. Pending acknowledgements cannot clear newer edits. Shared metadata remains canonical.

Conservative migration retains original events and raw legacy values. Only dated acknowledged archived outcomes establish a band rating with provenance. Recovered after-ratings require confirmation; uncertain ownership/dates are flagged without assigning them to the current user.

## Acceptance criteria validation

1. Application and native tests demonstrate festival/user isolation, unchanged planning and established opinions.
2. User-keyed stores and real RLS/API tests prove ownership isolation with canonical band references.
3. Reset persists independently; the established store accepts only positive outcomes. Archive promotion remains TASK-218.
4. Actual SQLite9→10 and PostgreSQL migrations preserve history/pending data and provenance; ambiguous legacy facts remain retained.
5. Domain/application TDD, adapter, real SQL/RLS/PostgREST and native suites cover isolation, reset, replay, rollback and recovery.
6. README, business requirements and accepted ADR0016 describe implemented scope and remaining creation/archive work.

## How to test

### Automated tests

- Full test/qaTest and coverage gates passed: domain55, application125, infrastructure24, QA3; app debug/release213 each (203 passed,10 existing fixture tests skipped outside their database runner).
- bash scripts/verify-rating-state.sh passed: real PostgreSQL migrations/RLS/rollback and PostgREST concurrency, ownership, reset and retry scenarios.
- Existing festival-creation runner passed all10 scenarios with V014 applied.
- sh scripts/verify-android-smoke.sh emulator-5554 all:34 passed,0 failed/skipped, isolated API36 ARM64. Includes actual touch/reset and seven Room persistence/migration cases.
- Fresh :app:clean test qaTest :app:lintVitalRelease :app:assembleRelease passed. APK signature verified against existing certificate SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. Task-validation APK SHA25625c362e401fee898d7a1c2c8c8e6980094c28ce5d1695b5d882a7da01cf431fc. This intermediate build retains baseline version2.33.5/code41 and is not published as2.34.
- git diff --check and shell syntax checks passed.
- Full lintDebug remains blocked by14 preexisting NewApi errors; release lintVital passes.
- Prior GitHub run39036503808 exposed a no-SIM emulator fixture assumption: mobile_data preference stayed1 after svc data disable. Runner now enables actual airplane mode plus Wi-Fi/data off, asserts airplane state, and restores original settings. Local full suite passed; new GitHub validation remains required.

### Manual validation

No production writes or production rollout. Native instrumentation exercises actual controls; no claim of separate human acceptance.

## TDD / BDD / approval-test evidence

Characterized legacy read/preservation behavior before rewiring. Initial domain/application tests failed for missing scoped records/use case, then passed. Added meaningful migration, pending-race, reset/replay, injected transaction failure and authenticated HTTP scenarios. Native acceptance covers active detail touch/reset without promotion.

## Architecture impact

- Architecture-significant change: approved additive rating model, Room10/FlywayV014 and authenticated transactional after-rating API within existing hexagonal adapters.
- Approval received: user2026-10-17 explicitly adopted this architecture for all stories; TASK215 and ADR0016 record approval.
- ADR impact: updated ADR0016 with implemented storage, provenance, RLS and narrow private write boundary. No new service, secret or runtime dependency.

## README impact

README impact: updated implemented rating roles, conservative migration, sync/testing commands and offline emulator setup.

## Business requirements impact

Business requirements impact: updated implemented isolation and legacy recovery status; creation/archive targets remain explicitly pending217/218.

## Diagram impact

Diagram impact: updated existing C4 component/container descriptions for personal and festival rating responsibilities; topology unchanged.

## Commits / logical change list

Domain/application isolation; additive Room migration and atomic pending cache; owner-scoped Supabase RPC/RLS and replay log; existing sync/UI integration; SQL/HTTP/native regression gates; documentation.

## Risks and follow-up

V014 has been validated only against disposable local services; deploy migrations before the final APK. Creation snapshots217, archive promotion218, archived correction172, contextual views219 and integrated upgrade220 must complete before release214. No2.34 tag/publication or main merge in this task. Unrelated user changes were excluded.
<!-- SECTION:NOTES:END -->
