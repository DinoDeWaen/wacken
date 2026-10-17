---
id: task-172
title: 'US: Edit archived festival ratings with audit history'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-19 11:02'
updated_date: '2026-10-17 16:18'
labels:
  - user-story
  - future
  - archive
  - ratings
  - audit
milestone: m-0
dependencies:
  - TASK-215
  - TASK-216
  - TASK-218
references:
  - apprepositories
  - addfestivalusecase
  - wackendatabase
  - banddetailactivity
  - settingsactivity
modified_files:
  - >-
    application/src/test/java/be/wacken/planner/application/ArchivedRatingCorrectionUseCaseTest.java
  - domain/src/main/java/be/wacken/planner/domain/ArchivedRatingKind.java
  - domain/src/main/java/be/wacken/planner/domain/ArchivedRatingReview.java
  - domain/src/main/java/be/wacken/planner/domain/RatingCorrection.java
  - >-
    application/src/main/java/be/wacken/planner/application/ArchivedRatingCorrectionGateway.java
  - >-
    application/src/main/java/be/wacken/planner/application/ArchivedRatingCorrectionUseCase.java
  - domain/src/test/java/be/wacken/planner/domain/RatingCorrectionTest.java
  - backend/flyway/tests/rating_corrections.sql
  - scripts/verify-rating-state.sh
  - backend/flyway/sql/V017__audited_archived_rating_corrections.sql
  - backend/flyway/tests/rating_state_api.py
  - >-
    domain/src/main/java/be/wacken/planner/domain/RatingCorrectionRepository.java
  - app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrection.java
  - app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrectionDao.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrectionRepository.java
  - app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java
  - app/src/test/java/be/wacken/planner/WackenDatabaseFestivalMigrationTest.java
  - >-
    app/src/androidTest/java/be/wacken/planner/persistence/RatingCorrectionDatabaseTest.java
  - app/src/test/java/be/wacken/planner/ArchivedRatingCorrectionGatewayTest.java
  - >-
    app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java
  - >-
    app/src/androidTest/java/be/wacken/planner/ArchivedRatingCorrectionCacheAndroidTest.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingEventDao.java
  - app/src/main/java/be/wacken/planner/RoomArchivedRatingCorrectionCache.java
  - app/src/main/java/be/wacken/planner/AppRepositories.java
  - app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java
  - app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java
  - >-
    app/src/test/java/be/wacken/planner/ArchivedFestivalLayoutRegressionTest.java
  - >-
    app/src/androidTest/java/be/wacken/planner/ArchivedRatingCorrectionAndroidTest.java
  - >-
    application/src/test/java/be/wacken/planner/application/ExportRatingCorrectionsCsvUseCaseTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/ExportRatingCorrectionsCsvUseCase.java
  - app/src/main/java/be/wacken/planner/SettingsActivity.java
  - backlog/decisions/0017-audited-archived-rating-corrections.md
  - README.md
  - backlog/docs/business-requirements.md
  - app/src/test/java/be/wacken/planner/ExpectedMemberRequestTest.java
  - app/src/main/java/be/wacken/planner/SupabaseAuthenticatedRequest.java
  - app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java
  - app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Historical ratings can be corrected after a festival without losing trust in what changed.

As a user, I want to correct ratings on archived festivals with change history so that my past festival record stays accurate.

Scope: explicit archived edit mode, planning or real-rating corrections, and audit visibility for who changed what and when.

Out of scope: editing festival master data, re-opening an archived festival as active, and multiple upcoming festival management.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given an archived festival is opened, then it is read-only by default until the user explicitly enters an allowed edit mode.
- [x] #2 Given a rating is changed for an archived festival, when the change is saved, then the old value, new value, user, and timestamp are recorded.
- [x] #3 Given archived ratings are exported or shown in details, then corrected values and historical context remain understandable.
- [x] #4 Relevant tests cover read-only default behavior, allowed corrections, and audit history.
- [x] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
- [x] #6 Given an archived after-rating is corrected, then its audit and any current band-rating update commit atomically only if that festival is still the source; an older correction cannot replace a newer source or refresh existing later-festival planning snapshots. Planning correction never promotes a band rating.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed: framework-free review/correction/audit domain and application contracts; V017 atomic authenticated correction with source guard, audit, revision and legacy-write protection; Room11 pending-aware audit/cache; account-bound transport; native explicit review/confirm, exact-festival detail and separate audit CSV; README/BR/ADR0017. Validated full JVM/QA/coverage, real SQL/HTTP/deletion/creation regressions, 50 native tests and fresh signed APK. Deviations: transport account-switch guard was necessary to preserve owner isolation; obsolete source-shape tests replaced by behavioral native coverage. Explicit accepted architecture approval applies; no new service/dependency. Production deployment and final release/upgrade remain214/220.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Archived festivals remain read-only by default. Members can explicitly review and confirm their own planning (0–5) or after-rating (1–5) correction. Server-owned audit records preserve the previous/new value, actor, correction time and original event date. Corrected after-ratings update an established band opinion only while that festival remains its source. Details and a separate correction CSV explain the history.

## Acceptance criteria validation

1. Native tests verify read-only default, explicit edit, cancel and separate confirmation.
2. SQL/HTTP and domain tests verify immutable audit context, dates and actor ownership.
3. Details show scoped corrected values and audit; deterministic owner-scoped audit CSV retains the existing normal export contract.
4. Native suite: 50 passed, zero failures/skips. Full JVM/QA/coverage, real SQL/HTTP and creation regression gates passed.
5. README, business requirements, diagrams and ADR0017 updated.
6. SQL/HTTP prove source-conditional atomic promotion, stale review rejection, replay, rollback, owner isolation and preservation of newer opinions/later planning. Planning correction never promotes.

## How to test

### Automated tests

- JDK21 ./gradlew :app:clean test qaTest :app:lintVitalRelease :app:assembleRelease: passed.
- bash scripts/verify-rating-state.sh: real PostgreSQL migrations through V017, rating/archive/correction/deletion SQL and authenticated PostgREST concurrency/RLS tests passed.
- Festival creation API regression: 13 passed, zero skipped, including existing exact-name/Summer Breeze behavior.
- sh scripts/verify-android-smoke.sh: full native suite 50 passed, zero skipped; includes actual dialog flow, Room10→11 pending preservation, cache rollback and scoped archive detail.
- apksigner verify: stable release signer SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f.
- Fresh task APK SHA256 94e15e8da85632df192977a626ec9b2eae4f8cc0ddcefb6de353befaadc6a452. This is a task validation build, not a published 2.34 release.
- git diff --check passed. Full lintDebug retains 14 previously documented NewApi baseline errors; release lintVital passes.

### Manual validation

No human acceptance claimed. Native automation exercises the actual Activity/dialog and real Room cache; authenticated backend behavior is exercised separately through real PostgREST. Suggested review: open an archived band, choose Correct my archived rating, reload current value, stage a correction, confirm, inspect history, then export the audit CSV.

## TDD / BDD / approval-test evidence

Domain/application and transport tests were red for missing correction contracts/classes and expected-member request support before implementation. SQL tests exercise business scenarios and deliberate transactional failure. Existing read-only behavior was retained; obsolete source-shape assertions were replaced by native exact-festival behavior coverage. Native fixture window/spinner lookups were corrected and the complete suite rerun green. Review uncovered an account-switch race; expected-member request binding now checks each actual send and refresh retry, with three passing regression tests.

## Architecture impact

- Architecture-significant change: approved correction port, additive V017 audit/revision/guards and Room11 migration; remote-first transaction and pending-aware atomic cache publication.
- Approval received: explicit user approval of the standard remaining 2.34 architecture under TASK215/ADR0016.
- ADR: ADR impact: created backlog/decisions/0017-audited-archived-rating-corrections.md.
- Domain/application remain framework-free; existing Supabase authentication and sync are reused. No new service, secret or dependency.

## README impact

README impact: updated archived correction workflow, audit export, member-bound transport, migration-before-APK requirement and implemented architecture links.

## Business requirements impact

Business requirements impact: updated BR116 and archive workflow for own explicit corrections, audit/date/source semantics, and clarified the unrestricted archived-edit non-goal.

## Diagram impact

Diagram impact: updated C4 container responsibilities and Mermaid correction workflow in README.

## Commits / logical change list

1. Domain correction/review/audit contracts and application use cases/CSV tests.
2. V017 authenticated atomic audited correction and SQL/HTTP regression gates.
3. Room11 audit, pending-aware cache and member-bound transport.
4. Explicit native review/confirmation, scoped archive detail/audit/export and native tests.
5. README, requirements and accepted ADR0017.

## Risks and follow-up

V014–V017 have only been applied to disposable test databases; production migration must precede final APK rollout in TASK214. Corrections require connectivity and fresh review; no offline correction queue is introduced. Explicit reviewed correction can resolve its captured local pending target, while newer/unrelated pending work is retained. A remote save with failed cache publication is reported for reload/recovery. TASK156/169/219/220 and final release214 remain. Full exact-source CI and upgrade validation are final release gates.
<!-- SECTION:NOTES:END -->
