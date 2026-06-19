---
id: task-126
title: 'UX: Add persistent offline and pending-sync status'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-19 06:04'
updated_date: '2026-06-19 18:25'
labels:
  - ux
  - sync
  - offline
dependencies: []
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The app now supports offline-first behavior, but users need clearer confidence while at the festival. Add a compact persistent status area that shows whether data is cached, whether sync is running, and whether local changes are pending.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the app is offline or sync fails, when the user views main screens, then cached/offline state is visible without blocking use.
- [x] #2 Given pending ratings or schedule locks exist, when the user views settings or main screens, then pending sync count/status is visible.
- [x] #3 Given sync succeeds, when pending changes are cleared, then the status updates without requiring app restart.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added BDD-style acceptance coverage through focused pending-sync summary scenarios, using TDD for empty, mixed, and singular pending states.
2. Retained the existing Room-backed pending rating and schedule-lock stores in the app composition root and added a read-only summary; no network call is made to derive status.
3. Added compact status text to main and settings for cached, syncing, offline, and up-to-date states, refreshing after each sync result.
4. Ran the focused Android test and compile check, full multi-module validation, and built and verified a fresh signed release APK.
5. Recorded validation evidence and closed the task.
Architecture impact: not architecture-significant; the existing app composition root reads existing local-store state with no module, schema, API, dependency, or sync-semantics change. No ADR required.
Documentation impact: README, business requirements, and diagrams unchanged because this is an implementation of existing offline-first behavior and requires no setup, workflow, or architecture documentation change.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Added a compact sync status summary to the band overview and Settings screen. It reports cached, syncing, offline, or up-to-date state and includes pending ratings and schedule-choice counts from the existing local Room queues.
- The status refreshes after sync success or failure without requiring an app restart. It uses local data only, so it stays available offline.

## Acceptance criteria validation

- AC1: Main and Settings show `Offline - cached data` after a failed sync while the cached screens remain usable.
- AC2: Both screens show the combined pending rating and schedule-choice count.
- AC3: A successful sync re-reads the local queue and changes the label to `Up to date` without restart.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.PendingSyncSummaryTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease`
- APK verification: `apksigner verify --verbose app/build/outputs/apk/release/app-release.apk` verified v1 and v2 signatures.

### Manual validation

- Build a pending rating or schedule lock while offline, open Bands and Settings, and confirm the queued change count is shown. Sync successfully and confirm the count becomes zero and status becomes `Up to date`.

## TDD / BDD / approval-test evidence

- Added `PendingSyncSummaryTest` first for the acceptance-level empty, mixed, and singular wording scenarios; implemented the smallest pure summary model to make it pass. No legacy refactor occurred.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR impact: none, because no architectural boundary, persistence schema, external API, or dependency changed.

## README impact

- README impact: none, because setup, public workflow, architecture, commands, and troubleshooting instructions did not change.

## Business requirements impact

- Business requirements impact: none, because this implements the existing offline-first behavior without changing product rules.

## Diagram impact

- Diagram impact: none, because the existing system structure is unchanged.

## Commits / logical change list

- Added pending-sync status model and tests.
- Exposed existing local pending queues through `AppRepositories`.
- Rendered and refreshed status on overview and Settings screens.
- Rebuilt signed release APK: `app/build/outputs/apk/release/app-release.apk`, SHA-256 `41f68c0571df5b13de47061da1ceb6627d8cad4fba1200080af186ed6c41ff82`.

## Risks and follow-up

- Status is refreshed on screen load and sync transitions. A future live database observation mechanism could update it while a screen remains open after an edit elsewhere, but that is outside this task.
<!-- SECTION:NOTES:END -->
