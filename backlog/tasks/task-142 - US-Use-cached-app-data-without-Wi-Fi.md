---
id: task-142
title: 'US: Use cached app data without Wi-Fi'
status: Done
assignee:
  - '@codex'
created_date: '2026-07-02 08:26'
updated_date: '2026-07-06 08:20'
labels:
  - mvp3
  - offline
  - sync
dependencies: []
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Wacken network conditions are unreliable, so the app must remain useful during the festival without Wi-Fi or mobile data.

As a festival attendee, I want the app to work from cached/imported data with no network connection, so that I can still view bands, details, ratings, real ratings, and schedule while at Wacken.

Scope: validate and harden startup, overview, detail, settings, schedule, rating edits, real-rating edits, and manual schedule choices when no network is available after a prior sync/import.

Out of scope: making first install magically contain data when no sync/import has ever happened.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the app has previously synced or imported festival data, when it starts with no network, then cached overview data is shown without blocking on Supabase
- [x] #2 Given the device has no network, when I open band details and schedule, then cached details, ratings, real ratings, and generated schedule remain usable
- [x] #3 Given the device has no network, when I edit planning ratings, real ratings, or manual schedule choices, then changes are saved locally and pending sync state is visible where applicable
- [x] #4 Given the app has no cached/imported festival data and no network, when it starts, then it explains that an initial sync or import is needed
- [x] #5 Automated tests or documented manual checks cover offline startup and offline edits
- [x] #6 Manual no-Wi-Fi smoke-test steps are documented in implementation notes
- [x] #7 Business requirements and README impact use canonical delivery-governance wording
- [x] #8 Architecture impact is assessed before implementation; if sync/storage boundaries change, explicit approval and ADR handling are completed before coding
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected startup, overview, band detail, settings export, schedule generation, planning-rating edits, real-rating edits, and manual schedule-lock paths for hidden Supabase blocking while offline.
2. Confirmed existing automated coverage for cached startup-before-sync, preserved Supabase sessions on offline refresh failure, local pending planning ratings, local real-rating persistence, local schedule-lock fallback, pending-sync summaries, and local ratings CSV export.
3. No production code changes were needed: the prior MVP3 tasks already established the required offline-first behavior and documentation.
4. Ran standard validation from a clean Gradle state, including domain/application/infrastructure tests, Android unit tests, debug and signed release APK builds, diff hygiene, APK signature verification, and APK hashes.
5. Finalized task notes with manual no-Wi-Fi smoke steps, canonical README/business/diagram/ADR impact wording, and acceptance-criteria evidence.

Deviation: this task closed as validation/hardening evidence only after inspection found no implementation gap. Architecture impact: not architecture-significant; sync/storage boundaries, schema, module structure, dependencies, and ADRs were unchanged. Approval and ADR: not required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Validated MVP3 no-Wi-Fi field mode across startup, overview, band detail, settings export, planning-rating edits, real post-show rating edits, generated schedule viewing, and manual schedule-lock edits. No production code changes were needed for this task: task-145 preserved Supabase sessions when refresh fails because the device is offline, task-141 added local real ratings, and task-143 added local cached CSV export.

The current app reads lineup, performance, rating, real-rating, and schedule-lock data from Room/local repositories first. Supabase work happens during explicit/background sync; planning ratings and manual schedule choices are saved locally as pending changes, and real ratings are local by design.

## Acceptance criteria validation

- AC1: Cached overview is rendered before lifecycle Supabase sync; `LifecycleSyncDecisionTest` covers cache-first startup/reactivation.
- AC2: Band detail, real ratings, settings export, and schedule generation read from local repositories; `RateRealBandUseCaseTest`, `ExportRatingsCsvUseCaseTest`, and schedule/sync tests cover the offline-readable paths.
- AC3: Planning ratings save locally as pending changes, real ratings save locally, and manual schedule locks save locally as pending changes; `SyncingRatingRepositoryTest`, `RateRealBandUseCaseTest`, `SyncingScheduleLockStoreTest`, and `PendingSyncSummaryTest` cover those behaviors.
- AC4: Empty startup uses `R.string.empty_band_list`: `No bands synced or imported yet. Sync from Supabase or import festival data first.`
- AC5: Automated tests cover the offline startup/edit paths; manual smoke steps are listed below.
- AC6: Manual no-Wi-Fi smoke-test steps are documented below.
- AC7: README and business-requirements impact use canonical delivery-governance wording below.
- AC8: Architecture was assessed before closure; no architecture-significant implementation change was made.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug assembleRelease`
- `git diff --check`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `shasum -a 256 app/build/outputs/apk/debug/app-debug.apk app/build/outputs/apk/release/app-release.apk`

Debug APK SHA-256: `7b3ac0341bba626609205aa55d0ce4747c334956edbeed70b615149f61a8ca53`
Release APK SHA-256: `bfff02183f563db7410241a5635c8738403f40bb6168d46b01b23cc377df77ae`

APK signature verification result: verifies with v1 and v2 schemes; apksigner reported Android packaging META-INF warnings only.

### Manual validation

Manual no-Wi-Fi smoke-test steps for a physical device:

1. With network enabled, sign in, sync or import festival data, open the band overview, and confirm bands are visible.
2. Disable Wi-Fi and mobile data or enable airplane mode. Force-close and reopen the app. Expected: the cached band overview appears; the app does not log out only because Supabase is unreachable; sync status reports cached/offline data.
3. Open a band detail from the overview. Expected: cached band details, planning rating, group ratings where previously cached, real rating, and schedule metadata remain visible.
4. Change a planning rating, reset it, change a real post-show rating, and reset it. Expected: values update immediately; planning-rating pending sync count is visible in the overview/settings sync status where applicable.
5. Open Settings and export ratings CSV. Expected: Android share/save sheet opens using locally cached rating data.
6. Open Group Schedule. Expected: generated schedule uses cached performances/ratings/distances and warns only if locked schedule choices cannot refresh.
7. Select an alternative act in a schedule conflict, then return to the schedule. Expected: the chosen act is shown locally with pending schedule choice count visible.
8. Re-enable network and run manual sync. Expected: pending planning ratings and manual schedule choices sync, and pending count returns to no pending changes after success.
9. Optional empty-cache check: clear app data, sign in state absent or no imported/synced festival data, disable network, and start the app. Expected: the empty overview explains that initial Supabase sync or CSV import is needed.

## TDD / BDD / approval-test evidence

This task used validation-first coverage rather than new code. Existing BDD/TDD-style tests from the MVP3 tasks cover the acceptance scenarios: cache-first startup, preserved session on offline Supabase refresh failure, offline pending planning ratings, local real ratings, local schedule locks, pending-sync status, and local CSV export. No approval/characterization baseline was needed because no legacy refactor was performed.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required; existing offline-first sync boundary and Room local-cache architecture remained unchanged.

## README impact

README impact: none, because README already documents the current offline-first cached behavior, local real ratings, CSV export, pending sync behavior, and Room/Supabase architecture from the preceding MVP3 stories.

## Business requirements impact

Business requirements impact: none, because BR-056 and BR-080 already describe preserving the Supabase session on network failure and keeping cached lineup, ratings, real ratings, settings, and schedule usable without Wi-Fi or mobile data after data is cached/imported.

## Diagram impact

Diagram impact: none, because no architecture, module, dependency, or data-flow diagram changed.

## Commits / logical change list

- Logical validation-only change: task-142 backlog plan, acceptance criteria, and implementation notes.
- Related enabling commits already present on this branch: task-145 offline Supabase session handling, task-141 real post-show ratings, and task-143 ratings CSV export.

## Risks and follow-up

- Manual physical no-Wi-Fi smoke test was documented but not executed in this environment.
- First install with no synced/imported data remains out of scope and correctly requires initial Supabase sync or CSV import.
<!-- SECTION:NOTES:END -->
