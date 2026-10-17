---
id: task-99
title: 'REL: Create V2.11 official release APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-12 15:56'
updated_date: '2026-06-12 16:02'
labels:
  - release
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Create the official signed non-debug Android release for V2.11 after the schedule overlap scratch-rule fixes. The release must include the Saxon chained-overlap regression and the walking-time effective-conflict rule.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Android versionCode is bumped to 14 and versionName is bumped to 2.11.
- [x] #2 Release notes for V2.11 are created and linked from README.md.
- [x] #3 Full release validation passes, including domain/application/infrastructure/app tests and Android debug compilation.
- [x] #4 A signed release APK is built, verified with apksigner, and metadata is checked with aapt.
- [x] #5 The V2.11 Git tag is created and pushed, and the GitHub release is published with the signed app-release.apk asset.
- [x] #6 Release task notes include release URL, APK SHA-256, validation commands, README/business requirements/diagram/ADR impact, and accepted risks.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Bumped Android release metadata to versionCode 14 and versionName 2.11.
2. Created releases/v2.11.md documenting the schedule scratch-rule fixes, validation, signing, install guidance, and risks.
3. Linked V2.11 release notes from README.md above V2.10.
4. Ran full release validation and built a signed release APK with local keychain signing values.
5. Verified APK signatures, package metadata, SHA-256, and diff hygiene.
6. Committed release metadata, pushed the branch, created and pushed tag v2.11, published GitHub release with app-release.apk, and verified the release.
Architecture impact: not architecture-significant; this is release metadata and packaging for already completed behavior. No ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Created the official V2.11 non-debug Android release for the schedule conflict scratch-rule fixes. V2.11 includes the chained visible-overlap regression and the walking-time effective-conflict rule.

GitHub release: https://github.com/DinoDeWaen/wacken/releases/tag/v2.11

## Acceptance criteria validation

- AC1: app/build.gradle now uses versionCode 14 and versionName 2.11.
- AC2: releases/v2.11.md was created and README.md links it above V2.10.
- AC3: Full release validation passed.
- AC4: Signed release APK was built and verified with apksigner and aapt.
- AC5: Tag v2.11 was pushed and the GitHub release was published with app-release.apk.
- AC6: Release URL, APK SHA-256, validation commands, impacts, and risks are recorded here.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease

### Manual validation

- /Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
- /Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk
- shasum -a 256 app/build/outputs/apk/release/app-release.apk
- git diff --check
- gh release view v2.11

APK path: app/build/outputs/apk/release/app-release.apk
APK SHA-256: 9cfaad3332dfe53916a61b23e5f8898bdd74c10e0b4e965f3366fc5665d5fc37
APK metadata: package be.wacken.planner, versionCode 14, versionName 2.11, minSdk 23, targetSdk 36.
APK signing: apksigner verifies v1=true and v2=true.
GitHub release asset: app-release.apk.

## TDD / BDD / approval-test evidence

No new product behavior was added in the release task. The released behavior is covered by the completed defect tests in ScheduleBlockStyleTest.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: updated the release notes list with the V2.11 release notes link.

## Business requirements impact

Business requirements impact: none, because the behavior requirement was already updated in task-98 before this packaging release.

## Diagram impact

Diagram impact: none, because release packaging does not change architecture or workflows.

## Commits / logical change list

- 45110ab Fix schedule overlap scratch rules.
- ed49e60 Prepare V2.11 release.
- v2.11 tag pushed to GitHub.
- GitHub release published at https://github.com/DinoDeWaen/wacken/releases/tag/v2.11.

## Risks and follow-up

- Installed-device UAT still needs to be run on the target Android device or emulator.
- V2.11 uses the same local self-signed V2.9 release-key line; it can install over V2.9/V2.10 same-key releases, but not over older differently signed releases or debug APKs without uninstalling first.
- This is a direct-install APK, not a Play Store release.
<!-- SECTION:NOTES:END -->
