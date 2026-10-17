---
id: task-165
title: 'DEF: Fix crash while loading bands after v2.26'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-16 16:04'
updated_date: '2026-08-16 16:11'
labels: []
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user opening the app, I want the band list to finish loading after the Loading bands message, so that startup does not crash before the list is usable.

Scope: diagnose and fix the loadBandList startup path after V2.26, including repository construction, start-state evaluation, cached band loading, and sync-status rendering.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the app shows Loading bands, when loadBandList runs, then repository construction and start-state evaluation do not crash.
- [x] #2 Given cached active or archived festival data exists, when the band list is loaded, then unsupported Android runtime APIs are not used in that path.
- [x] #3 Automated regression tests cover the loadBandList startup crash risk.
- [x] #4 A signed hotfix APK is built and published after validation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected the exact MainActivity loadBandList path reached after Loading bands.
2. Audited AppRepositories and Room migrations/queries; identified an undeclared festival index created by migration 5 -> 6 as a likely Room schema-validation crash when WackenDatabase opens.
3. Updated WackenDatabaseFestivalMigrationTest to assert Room version 8, no invalid index creation, and a 7 -> 8 cleanup migration.
4. Implemented Room schema version 8 and MIGRATION_7_8 with DROP INDEX IF EXISTS idx_festivals_one_active.
5. Ran targeted tests, full clean signed release validation, APK verification, GitHub release publication, and release task closure.

Architecture impact: architecture-significant because local Room schema version/migration changed. User approval received: "proceed" on 2026-08-16. ADR not required because the persistence model did not change; the migration reconciles schema validation with the existing entity model.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published Wacken Planner 2026 V2.27 as a signed Android hotfix for the crash after the app shows Loading bands. The likely failure was Room schema validation during WackenDatabase opening: migration 5 -> 6 created an undeclared unique index, idx_festivals_one_active, on festivals(status), but RoomFestival does not declare that index.

The fix bumps the local Room schema from version 7 to 8, removes creation of the invalid index from the festival migration path, and adds MIGRATION_7_8 to drop idx_festivals_one_active for devices that already created it. This only changes the local Room cache schema; Supabase production data is not changed.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v2.27
APK path: app/build/outputs/apk/release/app-release.apk
SHA-256: 48d7a62d1d35ffd1b1e7e9bf45a0a4f5c591079d3a9df63146626e1c46c148a2

## Acceptance criteria validation

- AC1: Passed. WackenDatabase now migrates to schema version 8 and removes the schema-mismatching festival index that can fail when repositories open after Loading bands.
- AC2: Passed. Existing Android runtime compatibility regression remains in the app test set and full validation passed.
- AC3: Passed. WackenDatabaseFestivalMigrationTest now asserts version 8, no invalid index creation, and a 7 -> 8 drop-index migration.
- AC4: Passed. V2.27 signed APK was built, verified, tagged, pushed, and published to GitHub.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.WackenDatabaseFestivalMigrationTest --tests be.wacken.planner.AndroidRuntimeCompatibilityRegressionTest --tests be.wacken.planner.LegacyRealRatingBackfillRegressionTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug assembleRelease

### Manual validation

- Verified APK signatures with /Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk. V1 and V2 schemes are true.
- Verified APK badging with /Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk: package be.wacken.planner, versionCode 30, versionName 2.27, minSdkVersion 23, targetSdkVersion 36.
- Verified checksum with shasum -a 256 app/build/outputs/apk/release/app-release.apk.
- Verified formatting with git diff --check.
- Verified published GitHub release v2.27 contains asset app-release.apk.
- Emulator logcat/manual launch could not be completed because ADB repeatedly returned waiting for device/protocol fault during this session.

## TDD / BDD / approval-test evidence

- Updated the migration source regression test before release validation to catch the invalid index and required 7 -> 8 cleanup migration.
- Existing Android runtime compatibility and legacy real-rating recovery regression tests pass.

## Architecture impact

- Architecture-significant change: yes, local Room schema version/migration changed.
- Approval received: yes; user explicitly approved with "proceed" on 2026-08-16 after the migration change was described.
- ADR: not required for this hotfix because the persistence model did not change; the migration only reconciles Room schema validation with the existing entity model.

## README impact

README impact: updated with the V2.27 release-notes link.

## Business requirements impact

Business requirements impact: none, because this fixes already required startup/offline cache behavior under BR-080 and does not change product scope.

## Diagram impact

Diagram impact: none, because no architecture or flow diagram changed.

## Commits / logical change list

- 9f8b69d Prepare v2.27 Room migration hotfix
- Tag: v2.27

## Risks and follow-up

- ADB/logcat was unreliable from this workspace, so the crash stack could not be captured. The fix targets the strongest remaining loadBandList-time failure found in code: Room migration schema validation.
- If V2.27 still crashes, the next step must be a crash log from the actual device/BlueStacks instance because this removes the known Room schema mismatch.
<!-- SECTION:NOTES:END -->
