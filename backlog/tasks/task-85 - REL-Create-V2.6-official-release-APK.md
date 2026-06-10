---
id: task-85
title: 'REL: Create V2.6 official release APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 18:42'
updated_date: '2026-06-10 18:46'
labels:
  - release
  - android
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Package and publish the V2.6 official non-debug Android release after replacing the sync splash with the Dino Metal image.

As a festival attendee, I want a signed V2.6 APK so that the Dino Metal splash update can be installed and validated on Android devices and BlueStacks.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the V2.6 code is complete, when release validation runs, then domain, application, infrastructure, app unit tests, debug compile, and release assemble checks pass.
- [x] #2 Given the release APK is built, when signing verification runs, then the APK verifies with v1 and v2 signing enabled and minSdk remains compatible with BlueStacks.
- [x] #3 Given the release is published, when GitHub releases are viewed, then tag v2.6 contains the signed app-release.apk asset and release notes.
- [x] #4 README documents the V2.6 release notes link and current version metadata where applicable.
- [x] #5 Business requirements impact is recorded using the canonical wording from delivery-governance.md.
- [x] #6 ADR and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Updated Android version metadata to versionCode 9/versionName 2.6.
2. Added V2.6 release notes and README link.
3. Ran full release validation: domain/application/infrastructure/app tests, debug compile, and assembleRelease with the V2.5/V2.6 local release key.
4. Verified APK signing, minSdk, versionCode/versionName, and SHA-256.
5. Committed and pushed release prep, created and pushed git tag v2.6.
6. Published GitHub release v2.6 with signed APK asset and closed the release task with validation evidence.
Architecture impact: not architecture-significant; release metadata/documentation only. No ADR needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

V2.6 is published as an official non-debug Android release with the signed release APK asset.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v2.6

APK: app/build/outputs/apk/release/app-release.apk
SHA-256: 6559c304d0e6bb19011f7ec8f95b306d4d33ca16a8d36dc64c29cbd48c049331

## Acceptance criteria validation

- Release validation passed.
- APK signing verification passed with v1=true and v2=true.
- APK metadata is versionCode=9, versionName=2.6, minSdk=23.
- GitHub release v2.6 contains app-release.apk.
- README links V2.6 release notes.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=/private/tmp/wacken-v2.5-release.jks WACKEN_RELEASE_STORE_PASSWORD=*** WACKEN_RELEASE_KEY_ALIAS=wacken-v2-5 WACKEN_RELEASE_KEY_PASSWORD=*** ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease
- /Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
- /Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk
- shasum -a 256 app/build/outputs/apk/release/app-release.apk
- git diff --check

### Manual validation

- GitHub release was verified with gh release view v2.6 and contains app-release.apk with digest sha256:6559c304d0e6bb19011f7ec8f95b306d4d33ca16a8d36dc64c29cbd48c049331.
- Installed-device UAT remains recommended on Android device and BlueStacks.

## TDD / BDD / approval-test evidence

- Release includes task-84 validation: Android compile/resource packaging and app unit tests for the Dino Metal splash resource update.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: updated with V2.6 release notes link.

## Business requirements impact

Business requirements impact: none, because V2.6 releases the task-84 splash presentation update without changing product rules or scope.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- c17e271 Use Dino Metal splash image
- 1426835 Prepare V2.6 release
- v2.6 tag pushed
- GitHub release published

## Risks and follow-up

- V2.6 uses the same local self-signed V2.5 release key, so it can install over V2.5.
- Devices with V2.4, older releases, or a debug APK installed must uninstall Wacken Planner before installing V2.6 because Android rejects same-package APKs signed by a different certificate.
- APK is signed with a local self-signed release key, not Play App Signing.
- Installed-device visual UAT is still recommended.
<!-- SECTION:NOTES:END -->
