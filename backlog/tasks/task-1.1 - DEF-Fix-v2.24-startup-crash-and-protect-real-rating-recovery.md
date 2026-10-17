---
id: task-1.1
title: 'DEF: Fix v2.24 startup crash and protect real-rating recovery'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-16 15:45'
updated_date: '2026-08-16 15:52'
labels: []
dependencies: []
parent_task_id: task-1
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user installing v2.24, I want the app to start reliably while preserving recovered real ratings, so that a hotfix cannot block access to my festival data.

Scope: fix the startup crash risk in the v2.24 real-rating recovery path and verify that Supabase personal rating history is not deleted by the app sync path.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given legacy real ratings exist in local storage, when the app repository initializes, then recovery does not throw on Android runtime APIs or duplicate legacy rows.
- [x] #2 Given the user is signed in, when recovered real ratings are backfilled, then rows are created locally for the signed-in user without deleting remote personal rating history.
- [x] #3 Given the user is signed out or the session is unavailable, when the app starts, then the app reaches the existing auth/offline handling instead of crashing in repository construction.
- [x] #4 Automated tests cover the startup recovery regression and confirm Supabase personal rating sync has no delete path for recovered events.
- [x] #5 A signed hotfix APK is built and released after validation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Corrected task metadata and acceptance criteria through Backlog.md CLI.
2. Inspected v2.24 startup recovery, Supabase migration, and sync paths; confirmed production personal_band_rating_events has total=0 and the app has no remote personal-history delete path.
3. Added focused regression coverage for Android-compatible recovery, session-gated Supabase sync wiring, UUID-compatible recovered ids, and no personal-history remote DELETE.
4. Replaced startup recovery Stream.toList usage with Collectors.toList and guarded Supabase syncing repository construction behind session.isPresent().
5. Ran targeted tests, standard validation, clean signed release validation, APK verification, GitHub release publication, and release task closure.

Deviation: emulator logcat/manual launch validation could not be completed because ADB repeatedly returned waiting for device/protocol fault. Architecture impact: not architecture-significant; no approval or ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published Wacken Planner 2026 V2.25 as a signed Android hotfix release for the v2.24 startup regression in archived real-rating recovery. The startup recovery path now avoids Android-runtime-risky Stream.toList usage and only wires Supabase syncing repositories when a valid local session exists.

Production Supabase validation found public.personal_band_rating_events currently has total=0. Airbourne still exists in Supabase planning/group tables for dinodewaen@gmail.com with rating 3, which is a planning/group rating, not a real post-show rating. The app personal-rating sync path was verified to only GET and POST/upsert personal_band_rating_events; it has no remote DELETE path.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v2.25
APK path: app/build/outputs/apk/release/app-release.apk
SHA-256: 3d70a8250345cf96e57a4ee28d10846db090b27d22817fe9b9bec41981673a1d

## Acceptance criteria validation

- AC1: Passed. RoomPersonalBandRatingHistoryRepository recovery no longer uses Stream.toList in the startup path and keeps duplicate legacy handling.
- AC2: Passed. Recovered rows still target the signed-in user when a session exists, and Supabase personal rating sync has no remote delete operation.
- AC3: Passed. AppRepositories only wires remote syncing repositories when session.isPresent() is true; missing sessions fall back to local adapters/no-op syncing instead of dereferencing session group/user ids.
- AC4: Passed. LegacyRealRatingBackfillRegressionTest covers Android-compatible recovery operations, session-gated remote wiring, UUID-compatible ids, stale local duplicate cleanup, and no remote personal-history delete path.
- AC5: Passed. V2.25 signed APK was built, verified, tagged, pushed, and published to GitHub.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.LegacyRealRatingBackfillRegressionTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug
- JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug assembleRelease

### Manual validation

- Verified APK signatures with /Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk. V1 and V2 schemes are true.
- Verified APK badging with /Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk: package be.wacken.planner, versionCode 28, versionName 2.25, minSdkVersion 23, targetSdkVersion 36.
- Verified checksum with shasum -a 256 app/build/outputs/apk/release/app-release.apk.
- Verified formatting with git diff --check.
- Verified published GitHub release v2.25 contains asset app-release.apk.
- Emulator logcat/manual launch could not be completed because ADB repeatedly returned waiting for device/protocol fault during this session.

## TDD / BDD / approval-test evidence

- Added regression assertions before the green run for Android-compatible legacy recovery, session-gated Supabase syncing, and no remote personal-history delete path.
- Existing archive and sync behavior tests remained green in the full validation run.

## Architecture impact

- Architecture-significant change: no; this stays within existing app composition and Room/Supabase adapter wiring.
- Approval received: not required.
- ADR: not required.

## README impact

README impact: updated with the V2.25 release-notes link.

## Business requirements impact

Business requirements impact: none, because BR-080, BR-086, and BR-094 already require cached/offline continuity, read-only archived inspection, and personal rating history sync.

## Diagram impact

Diagram impact: none, because no architecture or flow diagram changed.

## Commits / logical change list

- d0424f6 Prepare v2.25 startup hotfix
- Tag: v2.25

## Risks and follow-up

- Production Supabase does not contain historical real-rating rows in personal_band_rating_events, so remote-only recovery is impossible from that table. Local legacy real_ratings recovery remains the available recovery path for devices that still have those local rows.
- Devices still using an APK signed before the stable key was introduced must uninstall once before installing V2.25.
<!-- SECTION:NOTES:END -->
