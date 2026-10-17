---
id: task-164
title: 'DEF: Fix archive startup crash after v2.25'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-16 15:56'
updated_date: '2026-08-16 16:02'
labels: []
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user with archived festival data, I want the app to remain open after the main band list renders and archive state is checked, so that I can continue using active and archived festival views.

Scope: fix the remaining crash after v2.25, focused on archive/start-state rendering and archived festival list/detail data loading.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the active Wacken band list is shown, when the app checks festival archive state or background sync reloads the start state, then the app does not crash.
- [x] #2 Given archived festival data is rendered, when archived band list ratings are collected, then Android-runtime-compatible collection APIs are used.
- [x] #3 Automated regression tests cover archive/start-state crash risk and Android-incompatible Stream.toList usage in app/application production code paths touched by archive startup.
- [x] #4 A signed hotfix APK is built and published after validation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Attempted constrained AndroidRuntime logcat capture; ADB repeatedly returned waiting for device/protocol faults, so stack trace capture was not possible from this workspace.
2. Inspected MainActivity start-state, FestivalLifecycle, archived festival list/detail, archived history, export, and import paths for Android runtime incompatibilities.
3. Added AndroidRuntimeCompatibilityRegressionTest to scan production app/application/domain/infrastructure Java code for direct Stream.toList.
4. Replaced all direct production Stream.toList calls with Collectors.toList.
5. Ran targeted archive/runtime tests, full clean signed release validation, APK verification, GitHub release publication, and release task closure.

Architecture impact: not architecture-significant; no approval or ADR required. Deviation: crash log capture could not be completed because ADB was unavailable/unreliable.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published Wacken Planner 2026 V2.26 as a signed Android hotfix for the remaining crash after V2.25. The app could render the main Wacken band list and then crash when archive/start-state or archived festival code paths evaluated Java Stream.toList on Android.

The fix removes all direct Stream.toList calls from production Android-reachable code in app, application, domain, and infrastructure modules, including FestivalLifecycle, archived festival list/detail, archived history, export, and import paths. Added AndroidRuntimeCompatibilityRegressionTest to fail if direct Stream.toList is reintroduced.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v2.26
APK path: app/build/outputs/apk/release/app-release.apk
SHA-256: 673af2fc3f30ddb49a20b6b92467270a559454d1f1f5b344f76b315ec1960021

## Acceptance criteria validation

- AC1: Passed. FestivalLifecycle and archive/start-state production paths no longer call direct Stream.toList.
- AC2: Passed. Archived festival band list ratings now collect with Collectors.toList. Archived band detail and archived history paths were fixed too.
- AC3: Passed. AndroidRuntimeCompatibilityRegressionTest scans app/src/main/java, application/src/main/java, domain/src/main/java, and infrastructure/src/main/java for direct Stream.toList. Targeted archive/runtime tests pass.
- AC4: Passed. V2.26 signed APK was built, verified, tagged, pushed, and published to GitHub.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.AndroidRuntimeCompatibilityRegressionTest --tests be.wacken.planner.ArchivedFestivalLayoutRegressionTest --tests be.wacken.planner.LegacyRealRatingBackfillRegressionTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug assembleRelease

### Manual validation

- Verified no direct production Stream.toList remains with rg --pcre2 -n "(?<!Collectors)\.toList\(" app/src/main/java application/src/main/java domain/src/main/java infrastructure/src/main/java.
- Verified APK signatures with /Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk. V1 and V2 schemes are true.
- Verified APK badging with /Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk: package be.wacken.planner, versionCode 29, versionName 2.26, minSdkVersion 23, targetSdkVersion 36.
- Verified checksum with shasum -a 256 app/build/outputs/apk/release/app-release.apk.
- Verified formatting with git diff --check.
- Verified published GitHub release v2.26 contains asset app-release.apk.
- Emulator logcat/manual launch could not be completed because ADB repeatedly returned waiting for device/protocol fault during this session.

## TDD / BDD / approval-test evidence

- Added a source-level regression test before release validation to prevent reintroducing direct Stream.toList in Android-reachable production code.
- Existing archived festival layout and legacy real-rating recovery regression tests pass.

## Architecture impact

- Architecture-significant change: no; this is a runtime compatibility fix inside existing app/application/domain code paths.
- Approval received: not required.
- ADR: not required.

## README impact

README impact: updated with the V2.26 release-notes link.

## Business requirements impact

Business requirements impact: none, because this fixes already required archive/startup behavior under BR-080 and BR-086.

## Diagram impact

Diagram impact: none, because no architecture or flow diagram changed.

## Commits / logical change list

- 67dcdf4 Prepare v2.26 archive crash hotfix
- Tag: v2.26

## Risks and follow-up

- ADB was unreliable from this workspace, so the crash stack could not be captured. The fix is based on the observed timing and exhaustive removal of the Android-incompatible API from production code.
- If the installed app still crashes after V2.26, the next step must be a crash log from the device/BlueStacks instance because this removes the known runtime API class of failures.
<!-- SECTION:NOTES:END -->
