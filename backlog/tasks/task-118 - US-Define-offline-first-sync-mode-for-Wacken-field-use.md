---
id: task-118
title: 'US: Define offline-first sync mode for Wacken field use'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-17 20:23'
updated_date: '2026-06-18 18:54'
labels:
  - offline
  - sync
  - architecture
  - mvp
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user at the festival, I want the app to remain useful with poor or no network, so that I can view the lineup, ratings, and schedule without waiting on Supabase.

Recommendation: keep Room, but make the architecture explicitly offline-first. UI reads should always use local Room data first. Supabase should sync in the background and never block schedule/list/detail viewing. User edits should be queued locally as pending changes and synced when a connection is available, with clear pending/synced status.

This story is a design/decision story because it affects sync policy, user expectations, and potentially architecture/ADR documentation.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the app starts without network, when cached festival data exists, then the band list, band details, and schedule are usable without waiting for Supabase.
- [x] #2 Given the user changes ratings or schedule locks while offline, when offline edits are allowed, then the changes are stored locally as pending and synced later when network returns.
- [x] #3 Given offline edits could conflict with remote group changes, when sync resumes, then a conflict policy is documented and implemented or split into follow-up stories.
- [x] #4 Given this changes sync policy, when implementation starts, then architecture impact is assessed and an ADR is created or updated if required.
- [x] #5 Business requirements and README impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirmed the current failure mode: task-117 tracks backend schema availability; task-118 owns the approved standard offline-first sync architecture.
2. Received explicit user approval for option 2: standard offline-first sync boundary.
3. Inspected AppRepositories, SyncingRatingRepository, ScheduleLockStore, Supabase lock adapter, Room persistence, and Activity sync call sites.
4. Added SyncingScheduleLockStore with local-first pending UPSERT/DELETE operations and remote sync flushing.
5. Added Room schedule_locks persistence, DAO, store, and Room migration 3->4.
6. Wired schedule-lock sync into lifecycle/manual/sync-and-exit flows.
7. Added ADR 0010 and updated README/business requirements for offline-first sync behavior and conflict policy.
8. Added focused tests for offline schedule lock edits, failed remote sync retention, successful flush, queued clears, and pending-local-wins conflict handling.
9. Ran full validation, deleted the previous release APK, built and verified the signed V2.14 APK.

Architecture impact: architecture-significant and approved by the user on 2026-06-18. ADR 0010 was created.
Conflict policy: local pending schedule-lock operations win over remote pulls until they sync; Supabase remains last-write-wins after successful remote writes.
Deviation: task-117 backend migration could not be completed from this environment because the configured Supabase database host is unreachable; V2.14 still includes app-side offline-first queueing and schema-cache warning hardening.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Implemented the approved standard offline-first sync boundary for group schedule locks.
- Added local Room persistence for schedule locks with pending/synced state and UPSERT/DELETE operations.
- Added SyncingScheduleLockStore so manual schedule choices are saved locally first and synced with Supabase when available.
- Added schedule-lock sync to lifecycle sync, manual settings sync, and sync-and-exit.
- Added ADR 0010 documenting the offline-first sync boundary and conflict policy.
- Updated README and business requirements.
- Prepared and verified V2.14 release APK.

## Acceptance criteria validation

- AC1: Cached festival data remains usable without waiting for Supabase; schedule locks now read local Room locks first through SyncingScheduleLockStore.
- AC2: Offline schedule-lock edits are stored locally as pending operations and synced later. Ratings already had pending local sync behavior.
- AC3: Conflict policy is documented in ADR 0010: local pending schedule-lock operations win over remote pulls until synced; Supabase remains last-write-wins after remote writes.
- AC4: Architecture impact was assessed as architecture-significant; user approved option 2; ADR 0010 was created.
- AC5: README and business requirements were updated with canonical impact notes below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleErrorMessageTest --tests be.wacken.planner.SupabaseScheduleLockClientTest`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.SyncingScheduleLockStoreTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease`

### Manual validation

- APK verification: apksigner verified v1 and v2 signatures for `app/build/outputs/apk/release/app-release.apk`.
- APK metadata: package `be.wacken.planner`, versionCode `17`, versionName `2.14`, minSdkVersion `23`, targetSdkVersion `36`.
- SHA-256: `613d9e15c78e91487d441c3f984737303d829c5e507461ff55969f9c02c9cbf8`.
- `git diff --check` passed.
- Device install/manual offline validation was not run in this environment.

## TDD / BDD / approval-test evidence

- Added focused TDD-style unit tests in SyncingScheduleLockStoreTest before and during implementation.
- Covered pending offline saves, remote failures, successful sync flushes, pending clears, and pending-local-wins conflict behavior.

## Architecture impact

- Architecture-significant change: yes.
- Approval received: yes, user approved option 2 on 2026-06-18.
- ADR: created `backlog/decisions/0010-offline-first-sync-boundary.md`.

## README impact

README impact: updated Basic Functionality, architecture ADR links, repository responsibility text, backend troubleshooting, and V2.14 release-note link.

## Business requirements impact

Business requirements impact: updated current capabilities, BR-057, and BR-068 for offline-first schedule-lock sync.

## Diagram impact

Diagram impact: none, because the existing module/container boundaries remain the same.

## Commits / logical change list

- Sanitized schedule-lock schema-cache UI warning and added PostgREST schema refresh migration.
- Added offline-first schedule-lock sync boundary and Room pending-operation storage.
- Documented ADR 0010 and V2.14 release notes.

## Risks and follow-up

- Task-117 remains open for production Supabase migration verification because this environment cannot reach the configured database host.
- Fresh devices still need one successful sync/import before offline use.
- Concurrent offline edits to the same group lock use pending-local-wins locally and last-write-wins after remote sync; richer conflict UI remains deferred.
<!-- SECTION:NOTES:END -->
