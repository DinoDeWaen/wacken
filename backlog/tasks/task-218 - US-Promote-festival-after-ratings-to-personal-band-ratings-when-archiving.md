---
id: TASK-218
title: 'US: Promote festival after-ratings to personal band ratings when archiving'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 13:18'
updated_date: '2026-10-17 14:42'
labels:
  - ratings
  - festival
  - architecture
  - release-2.34
milestone: m-0
dependencies:
  - TASK-216
  - TASK-217
references:
  - apprepositories
  - supabasemasterdataclient
  - mainactivity
  - addfestivalusecase
modified_files:
  - >-
    application/src/test/java/be/wacken/planner/application/AtomicFestivalArchiveTest.java
  - backend/flyway/tests/festival_archive.sql
  - scripts/verify-rating-state.sh
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalArchiveGateway.java
  - >-
    application/src/main/java/be/wacken/planner/application/ArchiveActiveFestivalUseCase.java
  - >-
    application/src/qaTest/java/be/wacken/planner/application/FestivalArchiveQaScenarioTest.java
  - >-
    application/src/test/java/be/wacken/planner/application/FestivalStartUseCaseTest.java
  - app/src/main/java/be/wacken/planner/ArchiveFestivalAction.java
  - app/src/test/java/be/wacken/planner/ArchiveFestivalActionTest.java
  - backend/flyway/sql/V016__atomic_festival_archive_promotion.sql
  - backend/flyway/tests/festival_creation.sql
  - app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java
  - app/src/main/java/be/wacken/planner/RoomFestivalArchiveCache.java
  - app/src/main/java/be/wacken/planner/AppRepositories.java
  - app/src/main/java/be/wacken/planner/MainActivity.java
  - app/src/test/java/be/wacken/planner/SupabaseFestivalArchiveGatewayTest.java
  - app/src/androidTest/java/be/wacken/planner/FestivalArchiveAndroidTest.java
  - backend/flyway/tests/rating_state_api.py
  - app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java
  - app/src/test/java/be/wacken/planner/SupabaseRatingStateClientTest.java
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/decisions/0016-band-catalog-and-archive-promoted-personal-ratings.md
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member, I want archiving to establish my latest band opinion from that festival's after-rating while preserving the festival record. Scope: one authorized atomic server operation archives the active festival and promotes each applicable member's nonempty final after-rating to that member's current band rating, retaining source festival/event and timestamps. Cache the acknowledged result transactionally and integrate with existing sync. No after-rating retains the previous current band rating; planning never supplies the promotion value. Idempotency, concurrency, pending-write conflict handling and old-client compatibility must prevent status-only archival from bypassing promotion.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given band rating4, festival planning5 and after-rating3, when archive succeeds, then the festival retains5/3, its status is archived and that member's band rating becomes3 with source provenance.
- [x] #2 Given multiple members and bands, then each member's final nonempty after-rating is promoted to the matching personal band rating; missing/reset after-ratings retain prior values.
- [x] #3 Given a denied, offline, interrupted or failed archive, then the UI does not falsely report a complete archive or partially promoted ratings; retries confirm the same result without duplicate effects.
- [x] #4 Given concurrent rating edits, pending local work or an older client, then archiving cannot silently discard edits or bypass promotion; conflicts have actionable recovery and cached data remains usable.
- [x] #5 BDD/TDD, real database transaction-fault/ownership/concurrency/idempotency tests and native archive/failure tests validate the complete operation.
- [x] #6 README/business requirements, accepted ADR-0016, rollout guidance and canonical diagram/ADR impacts are updated.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed red/green archive application contract and SQL scenarios, private V016 atomic promotion/archive/replay and authenticated legacy status guard, owner-only adapter snapshots, Room transaction publication and pending preflight/recovery. Validated full JVM/QA/coverage, real SQL/HTTP concurrency/rollback,13 creation regressions,4 native archive cases and fresh signed task APK. Updated README/BR/ADR0016. Approved standard scope; no new dependency/service. Clarified unavoidable offline-device pending conflict recovery for172. Discovered unrelated intermittent CI music-review test; tracked separately as221 without mixing its implementation.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Archive now delegates through an explicit application transaction port. V016 authorizes a group member, locks lifecycle/festival state against concurrent after-rating writes, rejects recovered lineup values pending review, promotes each current member's final positive after-rating with source provenance, and saves archive status plus replay ledger atomically. Missing/reset values retain prior established opinions; planning remains untouched. Replay cannot re-promote over a later source.

The Android path syncs pending work first and publishes acknowledged festival/current/after snapshots in one Room transaction. New pending edits prevent cache publication and are retained with recovery guidance. A private transaction and invoker status guard prevent authenticated older clients from performing status-only archive. No new service/dependency/secret.

## Acceptance criteria validation

1. Real SQL and native cache prove established4/planning5/after3 becomes established3 with archived5/3 and provenance.
2. Multiple members promote independently; reset and missing outcomes retain prior opinions.
3. SQL final-write fault rolls back promotion/status; HTTP concurrent retries confirm one operation. JVM/native failure paths never publish false success.
4. Row/lifecycle locks serialize edit/archive; legacy status-only writes are denied, recovered values require review, local pending work is synced/retained. Remote offline devices cannot be inspected: later unsent edits remain pending for explicit archived correction under172.
5. Application TDD and real SQL/HTTP/native tests pass.
6. README/BR/ADR0016 and rollout guidance updated.

## How to test

### Automated tests

- Initial application tests failed for the missing archive transaction contract; all application tests/QA then passed with coverage gates.
- bash scripts/verify-rating-state.sh passed real migration/RLS/archive fault, review, reset/missing, member ownership, replay and concurrent edit/archive HTTP checks.
- JDK21 bash scripts/verify-festival-creation.sh passed13 scenarios,0 skips, including Summer Breeze63 and the new guarded archive path.
- sh scripts/verify-android-smoke.sh emulator-5554 be.wacken.planner.FestivalArchiveAndroidTest:4 passed,0 skips; actual Room atomicity, injected local fault, arriving pending edits and worker/main-thread failure handling.
- Fresh :app:clean test qaTest :app:lintVitalRelease :app:assembleRelease passed. Domain/application coverage gates passed;13 real creation fixtures skip only outside their mandatory disposable runner.
- Fresh task APK digest a530652d84591580a4b5e7563b2ffcbd1cb61aaaf3f2f0845b2ea5fa4d502b44; signature verified against existing stable SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. Baseline2.33.5/code41 task artifact, not published2.34.
- git diff --check passed.

### Manual validation

No production database writes or separate human acceptance claimed. Native instrumentation validates the real Room/action path; the complete actual-screen suite remains a final220/214 gate.

## TDD / BDD / approval-test evidence

Preserved existing start-state/worker/UI failure characterizations while replacing status-only save with a transaction port. New application tests initially failed; SQL archive scenarios initially failed for the missing RPC. Green tests cover user-visible archived state, current/source values, rollback, race, retry and pending retention.

## Architecture impact

- Architecture-significant change: accepted explicit archive port and additive transactional/RLS persistence.
- Approval received: user2026-10-17, TASK215/ADR0016.
- ADR impact: updated ADR0016 for V016, legacy guard and pending conflict handling.

## README impact

README impact: updated archive behavior, migration-before-APK rollout, older-client error and offline-device conflict limits.

## Business requirements impact

Business requirements impact: updated implemented archive capability and correction/view/upgrade work remaining.

## Diagram impact

Diagram impact: none, because existing lifecycle application and Supabase/Room boundaries are unchanged.

## Commits / logical change list

Application archive port/orchestration; private atomic archive RPC/status guard; authenticated adapter and atomic Room cache; pending sync preflight/recovery; SQL/HTTP/native/JVM tests; documentation.

## Risks and follow-up

V016 tested only against disposable local services. Apply additive migrations before final release; older APK status-only archive now requires updating. TASK172 must provide explicit correction/recovery before shipping. TASK219/220/214 still gate final views, integrated upgrade and release. CI217 exposed intermittent music-review return validation; TASK221 was added to2.34 and release214 dependencies rather than ignored. No main merge/tag/publication.
<!-- SECTION:NOTES:END -->
