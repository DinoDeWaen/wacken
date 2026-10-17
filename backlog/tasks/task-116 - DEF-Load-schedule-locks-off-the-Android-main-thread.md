---
id: task-116
title: 'DEF: Load schedule locks off the Android main thread'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-17 19:55'
updated_date: '2026-06-17 20:16'
labels:
  - defect
  - schedule
  - android
  - sync
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The Group Schedule screen shows `Locked schedule choices could not be synced... NetworkOnMainThreadException` because schedule lock sync is performed while rendering the Activity on the Android UI thread.

Business value: the schedule must remain usable and responsive. Lock sync is useful but must not block or fail the screen because Android forbids network calls on the main thread.

Observed evidence: screenshot `/Users/dino/Desktop/Screenshot 2026-06-17 at 21.54.04.png` shows `NetworkOnMainThreadException` in the schedule warning.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the Group Schedule screen opens, when group schedule locks need to sync, then lock loading runs off the Android main thread.
- [x] #2 Given lock loading is still in progress or fails, when the schedule is shown, then the generated schedule remains visible and the UI does not show NetworkOnMainThreadException.
- [x] #3 Given lock loading succeeds, when the locks are available, then locked choices are applied to the visible schedule without requiring a manual restart.
- [x] #4 Regression tests or focused Android coverage prove lock-loading state/message behavior and prevent main-thread network regression where practical; Android compile validation passes.
- [x] #5 The signed release APK is deleted, rebuilt, and verified after the fix.
- [x] #6 Business requirements and README impact are recorded using the canonical wording from delivery-governance.md.

- [x] #7 Given I horizontally scroll the schedule timeline, when the time columns move, then the stage names remain fixed on the left.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected the schedule lock loading path in `ScheduleActivity` and the schedule error-message coverage.
2. Added focused regression coverage so `NetworkOnMainThreadException` is not exposed as schedule UI text.
3. Moved schedule lock pulling to a background thread; the generated schedule renders immediately with empty locks while lock sync is pending.
4. Applied loaded locks on the UI thread and rerender automatically; on failure, the generated schedule remains visible with a non-technical warning.
5. Split the schedule layout so stage labels stay fixed on the left while only the timeline scrolls horizontally.
6. Updated README and business requirements for fixed stage labels, ran validation, deleted and rebuilt the signed release APK, and verified the APK.

Architecture impact: not architecture-significant; this is Android threading/error handling and layout composition around existing schedule data and adapter calls. No domain, schema, API, or dependency changes were made.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed the schedule lock sync error by moving group schedule lock loading off the Android main thread. The schedule now renders immediately with generated choices while locks sync in the background. When locks load successfully, the activity applies them and refreshes automatically. If lock loading fails, the generated schedule remains visible and the UI shows a non-technical warning instead of `NetworkOnMainThreadException`.

Also updated the schedule layout so stage names remain fixed on the left while the timeline scrolls horizontally.

## Acceptance criteria validation

- AC1: `ScheduleActivity` now starts a `schedule-lock-loader` background thread for `pullGroupLocks()` instead of calling it during render.
- AC2: Pending/failed lock loading keeps the generated schedule visible; `ScheduleErrorMessage` maps `NetworkOnMainThreadException` to non-technical UI text.
- AC3: Successful lock loading updates `manualSelections` on the UI thread and rerenders automatically.
- AC4: Added focused Android unit coverage for the NetworkOnMainThread message path; Android compile validation passed.
- AC5: Deleted the previous signed APK, rebuilt `app-release.apk`, and verified signatures, metadata, and checksum.
- AC6: README and business requirements impact are recorded below.
- AC7: The schedule layout now keeps stage labels outside the horizontal timeline scroller so stage names stay fixed on the left.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `git diff --check`
- Deleted previous `app/build/outputs/apk/release/app-release.apk` and rebuilt signed release APK.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `shasum -a 256 app/build/outputs/apk/release/app-release.apk`

APK metadata: package `be.wacken.planner`, `versionCode=15`, `versionName=2.12`, minSdk `23`, targetSdk `36`.

APK SHA-256: `5df93c809d8c1e98ae6b24cb22b46b0f2dff71a92588328dda17f97b58cc195a`.

### Manual validation

Not run on a physical Android device. Manual UAT should install the new APK, open Group Schedule, verify no `NetworkOnMainThreadException` warning appears, verify locked choices apply after sync, and verify stage names stay fixed while horizontally scrolling the timeline.

## TDD / BDD / approval-test evidence

Used focused regression coverage for the schedule warning text path and preserved existing Android schedule tests for layout, filters, locks, details, and content behavior.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: updated schedule wording to document fixed stage labels with horizontally scrollable time columns.

## Business requirements impact

Business requirements impact: updated calendar schedule capability and BR-063 to document fixed stage rows and a horizontally scrollable time axis.

## Diagram impact

Diagram impact: none, because system structure and data flow did not change.

## Commits / logical change list

- Move schedule lock loading to a background thread.
- Apply loaded locks on the UI thread and rerender automatically.
- Hide technical main-thread network exception text from the schedule warning.
- Keep stage labels fixed while the timeline scrolls horizontally.
- Update README/business requirements and rebuild signed APK.

## Risks and follow-up

The background lock loader starts one request per Activity instance and rerenders when complete. If a lock sync failure persists, the schedule still remains usable with generated choices and a warning.
<!-- SECTION:NOTES:END -->
