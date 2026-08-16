---
id: task-163
title: Release v2.24 archived real-rating recovery
status: Done
assignee:
  - '@codex'
created_date: '2026-08-15 08:18'
updated_date: '2026-08-16 15:31'
labels:
  - release
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Prepare and publish Wacken Planner v2.24 as a signed Android hotfix release for archived Wacken real-rating recovery.

Scope: bump Android version metadata to 2.24/versionCode 27, create release notes, update the README release link, run official validation, verify the signed release APK, commit release metadata, tag v2.24, push, and publish the GitHub release if credentials are available.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Full validation passes for domain, application, infrastructure, app unit tests, app Java compilation, and assembleRelease.
- [x] #2 The official signed APK exists at app/build/outputs/apk/release/app-release.apk and apksigner verifies v1/v2 signatures.
- [x] #3 APK metadata reports package be.wacken.planner, versionCode 27, versionName 2.24, and minSdkVersion 23.
- [x] #4 Release notes for v2.24 exist and README links to them above v2.23.
- [x] #5 Git tag v2.24 is created and pushed, and a GitHub release is published with app-release.apk when GitHub credentials are available.
- [x] #6 README impact, business requirements impact, diagram impact, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Bumped app/build.gradle to versionCode 27 and versionName 2.24.
2. Added releases/v2.24.md with scope, APK path, signing, validation, install guidance, non-goals, risks, and SHA-256.
3. Added the v2.24 release notes link to README.md above v2.23.
4. Ran official clean validation with signing configuration and verified APK signing, metadata, and SHA-256.
5. Committed release metadata, pushed the branch, created and pushed tag v2.24, and published the GitHub release with app-release.apk.
6. Closed the release task with validation evidence.

Deviation: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published Wacken Planner 2026 V2.24 as an official signed Android release for archived Wacken real-rating recovery.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v2.24

APK path: `app/build/outputs/apk/release/app-release.apk`

SHA-256: `a008135f689109d6661b8ac7f2a75e75f6149b347cffa44b20afc0c23ea87486`

## Acceptance criteria validation

- AC1: Full clean validation passed with domain, application, infrastructure, app unit tests, app Java compilation, assembleDebug, and assembleRelease.
- AC2: `apksigner verify --verbose app/build/outputs/apk/release/app-release.apk` reports Verifies and v1/v2 signature schemes enabled.
- AC3: `aapt dump badging` reports package `be.wacken.planner`, `versionCode=27`, `versionName=2.24`, and `sdkVersion=23`.
- AC4: `releases/v2.24.md` exists and README links it above v2.23.
- AC5: Tag `v2.24` was created and pushed; GitHub release was published with asset `app-release.apk`.
- AC6: Impact notes are recorded below.

## How to test

### Automated tests

- `scripts/ensure-release-keystore.sh`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `shasum -a 256 app/build/outputs/apk/release/app-release.apk`
- `git diff --check`
- `gh release view v2.24`

### Manual validation

Manual device smoke testing was not executed from this workspace. Install V2.24 over the affected app data, open archived Wacken, open Airbourne, and confirm the Real Rating stars are restored.

## TDD / BDD / approval-test evidence

Release task validation depends on completed defect task-162, including SyncingPersonalBandRatingHistoryRepositoryTest and LegacyRealRatingBackfillRegressionTest.

## Architecture impact

- Architecture-significant change: no; release packaging only updates version metadata and release documentation.
- Approval received: not required.
- ADR: none.

## README impact

README impact: updated release notes index with the V2.24 release link.

## Business requirements impact

Business requirements impact: none, because this release packages a completed defect fix without changing product scope.

## Diagram impact

Diagram impact: none, because release packaging does not change architecture or workflows.

## Commits / logical change list

- `756867f` Recover legacy Wacken real ratings
- `8789d90` Prepare v2.24 release

## Risks and follow-up

Devices still using an APK signed before the stable V2.9+ key must uninstall once before installing V2.24. Devices already on the stable release key can install this APK as an update.
<!-- SECTION:NOTES:END -->
