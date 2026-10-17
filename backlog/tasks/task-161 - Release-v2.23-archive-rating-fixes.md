---
id: task-161
title: Release v2.23 archive rating fixes
status: Done
assignee:
  - '@codex'
created_date: '2026-08-14 20:00'
updated_date: '2026-08-14 20:12'
labels:
  - release
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Prepare and publish the Wacken Planner v2.23 Android release for the archived festival layout and Wacken real-rating regression fixes.

Scope: bump Android version metadata to 2.23/versionCode 26, create release notes, update the README release link, run official validation, verify the signed release APK, commit release metadata, tag v2.23, push, and publish the GitHub release if credentials are available.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Full validation passes for domain, application, infrastructure, app unit tests, app Java compilation, and assembleRelease.
- [x] #2 The official signed APK exists at app/build/outputs/apk/release/app-release.apk and apksigner verifies v1/v2 signatures.
- [x] #3 APK metadata reports package be.wacken.planner, versionCode 26, versionName 2.23, and minSdkVersion 23.
- [x] #4 Release notes for v2.23 exist and README links to them above v2.22.
- [x] #5 Git tag v2.23 is created and pushed, and a GitHub release is published with app-release.apk when GitHub credentials are available.
- [x] #6 README impact, business requirements impact, diagram impact, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Bumped app/build.gradle to versionCode 26 and versionName 2.23.
2. Added releases/v2.23.md with scope, APK path, signing, validation, install guidance, non-goals, risks, and SHA-256.
3. Added the v2.23 release notes link to README.md above v2.22.
4. Ran official clean validation with signing configuration and verified APK signing, metadata, and SHA-256.
5. Committed release metadata, pushed the branch, created and pushed tag v2.23, and published the GitHub release with app-release.apk.
6. Closed the release task with validation evidence.

Deviation: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published Wacken Planner 2026 V2.23 as an official signed Android release for the archive layout and Wacken real-rating preservation fixes.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v2.23

APK path: `app/build/outputs/apk/release/app-release.apk`

SHA-256: `5f0c56a19956c6428fa5c1d5a31440f49c86800d307b849be5d0ab4b37d82dfa`

## Acceptance criteria validation

- AC1: Full clean validation passed with domain, application, infrastructure, app unit tests, app Java compilation, assembleDebug, and assembleRelease.
- AC2: `apksigner verify --verbose app/build/outputs/apk/release/app-release.apk` reports Verifies and v1/v2 signature schemes enabled.
- AC3: `aapt dump badging` reports package `be.wacken.planner`, `versionCode=26`, `versionName=2.23`, and `sdkVersion=23`.
- AC4: `releases/v2.23.md` exists and README links it above v2.22.
- AC5: Tag `v2.23` was created and pushed; GitHub release was published with asset `app-release.apk`.
- AC6: Impact notes are recorded below.

## How to test

### Automated tests

- `scripts/ensure-release-keystore.sh`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `shasum -a 256 app/build/outputs/apk/release/app-release.apk`
- `git diff --check`
- `gh release view v2.23`

### Manual validation

Manual device smoke testing was not executed from this workspace. Install V2.23, open the Wacken archive, confirm the active-style band list, open Airbourne, and confirm the 4-star Real Rating is visible.

## TDD / BDD / approval-test evidence

Release task validation depends on the completed implementation stories task-high.8 and task-160, including ArchivedFestivalLayoutRegressionTest, ArchivedFestivalHistoryUseCaseTest, and WackenDatabaseFestivalMigrationTest.

## Architecture impact

- Architecture-significant change: no; release packaging only updates version metadata and release documentation.
- Approval received: not required.
- ADR: none.

## README impact

README impact: updated release notes index with the V2.23 release link.

## Business requirements impact

Business requirements impact: none, because this release packages already completed defect fixes without changing product scope.

## Diagram impact

Diagram impact: none, because release packaging does not change architecture or workflows.

## Commits / logical change list

- `65bbbc5` Fix archived festival rating views
- `c96406b` Prepare v2.23 release

## Risks and follow-up

Devices still using an APK signed before the stable V2.9+ key must uninstall once before installing V2.23. Devices already on the stable release key can install this APK as an update.
<!-- SECTION:NOTES:END -->
