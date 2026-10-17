---
id: task-119
title: 'DEF: Stop lifecycle sync from blocking cached app usage'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-17 20:26'
updated_date: '2026-06-17 20:51'
labels:
  - android
  - sync
  - offline
  - defect
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The current lifecycle sync path can make the Room cache feel like a bottleneck because app start/reactivation tries to talk to Supabase. Cached screens should render from Room first, while Supabase sync runs in the background and reports a concise offline/sync status without blocking normal use.

Business value: the app remains useful at Wacken when mobile data is poor, while still refreshing from Supabase when the network is available.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given cached data exists on app start or reactivation, when Supabase is slow or unavailable, then the band list and group schedule render from cache before or independently of Supabase sync.
- [x] #2 Given Supabase sync fails, when the user is viewing cached data, then the UI remains usable and shows a concise cached/offline status instead of a blocking error.
- [x] #3 Given Supabase sync succeeds, when cached views are visible, then they refresh without losing the user's current context.
- [x] #4 Automated or focused Android coverage verifies the cached-first lifecycle behavior.
- [x] #5 The release APK is rebuilt and verified after the change.
- [x] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected MainActivity lifecycle sync, AppRepositories sync wiring, ScheduleActivity lock loading, and existing sync tests.
2. Added LifecycleSyncDecision and focused unit coverage for cached-first lifecycle behavior.
3. Changed MainActivity onResume to load/render Room cached bands first, then start Supabase sync in the background without the blocking sync overlay.
4. Preserved sync-and-exit behavior: close still starts sync before finish and keeps the overlay path.
5. Updated README and business requirements to document cached-first lifecycle sync.
6. Bumped Android release metadata to versionCode 16 / versionName 2.13 and added V2.13 release notes.
7. Ran focused and full validation, deleted the previous release APK, rebuilt the signed release APK, and verified signature, metadata, SHA-256, and diff hygiene.

Deviation: the release metadata was bumped to 2.13 because a same-version APK is not useful for reliable device updates.
Architecture impact: not architecture-significant; existing Room cache, Supabase adapters, and repository boundaries were preserved. Task 118 remains the broader offline-first architecture decision.
ADR impact: none, because this task changed Android lifecycle orchestration only and did not introduce a new sync architecture.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- MainActivity now renders cached Room data before lifecycle Supabase sync runs.
- Start/reactivation sync runs in the background without the blocking sync overlay; sync-and-exit still blocks until sync completes or fails.
- Added a focused LifecycleSyncDecision unit test for cached-first behavior.
- Updated README and business requirements to describe cached-first lifecycle sync.
- Bumped the APK to versionCode 16 / versionName 2.13 and added V2.13 release notes.

## Acceptance criteria validation

- AC1: Cached data is rendered before lifecycle Supabase sync starts by loading the band list first in onResume and then starting background sync.
- AC2: Existing failure handling still keeps the cached UI usable and shows a concise cached-data sync failure message.
- AC3: Successful background sync reloads the visible band list from the refreshed Room cache without navigating away.
- AC4: Added LifecycleSyncDecisionTest covering cached-first lifecycle decisions.
- AC5: Deleted the previous release APK and rebuilt a signed V2.13 release APK.
- AC6: README and business requirements were updated with canonical impact notes below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.LifecycleSyncDecisionTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease

### Manual validation

- APK verification: apksigner verified v1 and v2 signatures for app/build/outputs/apk/release/app-release.apk.
- APK metadata: package be.wacken.planner, versionCode 16, versionName 2.13, minSdkVersion 23, targetSdkVersion 36.
- SHA-256: fcc8a6cd87b31366250e102b0886bdea144ac0c0be0a440b570a371991a96e4e.
- git diff --check passed.
- Device install/manual offline validation was not run in this environment.

## TDD / BDD / approval-test evidence

- Regression coverage was added before the lifecycle wiring change through LifecycleSyncDecisionTest.
- No approval test was needed because this was a small lifecycle behavior change, not a legacy structural refactor.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none, because existing Room cache and Supabase repository/adapters were preserved.

## README impact

README impact: updated Basic Functionality and release-note links for V2.13 cached-first lifecycle sync.

## Business requirements impact

Business requirements impact: updated current capabilities and BR-057 to state that cached app data is shown before lifecycle sync completes.

## Diagram impact

Diagram impact: none, because module/container relationships did not change.

## Commits / logical change list

- Add cached-first lifecycle sync decision coverage.
- Render cached band data before lifecycle Supabase sync.
- Document cached-first sync behavior.
- Prepare and verify V2.13 local release APK.

## Risks and follow-up

- Fresh installs without cached data still need one successful sync or CSV import before offline use.
- The broader offline-write conflict policy remains task-118.
- The production Supabase group_schedule_locks schema issue remains task-117.
<!-- SECTION:NOTES:END -->
