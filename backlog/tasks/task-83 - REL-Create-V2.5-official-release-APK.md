---
id: task-83
title: 'REL: Create V2.5 official release APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 17:55'
updated_date: '2026-06-10 18:01'
labels:
  - release
  - android
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Package and publish the V2.5 official non-debug Android release after the schedule alternatives direct-overlap defect fix.

As a festival attendee, I want a signed V2.5 APK so that the schedule detail alternatives fix can be installed and validated on Android devices and BlueStacks.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the V2.5 code is complete, when release validation runs, then domain, application, infrastructure, app unit tests, debug compile, and release assemble checks pass.
- [x] #2 Given the release APK is built, when signing verification runs, then the APK verifies with v1 and v2 signing enabled and minSdk remains compatible with BlueStacks.
- [x] #3 Given the release is published, when GitHub releases are viewed, then tag v2.5 contains the signed app-release.apk asset and release notes.
- [x] #4 README documents the V2.5 release notes link and current version metadata where applicable.
- [x] #5 Business requirements impact is recorded using the canonical wording from delivery-governance.md.
- [x] #6 ADR and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Updated Android version metadata to versionCode 8/versionName 2.5.
2. Added V2.5 release notes and README link.
3. Ran full release validation: domain/application/infrastructure/app tests, debug compile, and assembleRelease with a newly generated local V2.5 release key because the V2.1-V2.4 keystore passwords were unavailable in this shell.
4. Verified APK signing, minSdk, versionCode/versionName, and SHA-256.
5. Committed and pushed release prep, created and pushed git tag v2.5.
6. Published GitHub release with signed APK asset and closed the release task with validation evidence.
Architecture impact: not architecture-significant; release metadata/documentation only. No ADR needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

V2.5 is published as an official non-debug Android release with the signed release APK asset.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v2.5

APK: app/build/outputs/apk/release/app-release.apk
SHA-256: e17978073e806cca7583ade7e8d32fdc6270e47a921b5147452399718e1ef82c

## Acceptance criteria validation

- Release validation passed.
- APK signing verification passed with v1=true and v2=true.
- APK metadata is versionCode=8, versionName=2.5, minSdk=23.
- GitHub release v2.5 contains app-release.apk.
- README links V2.5 release notes.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=/private/tmp/wacken-v2.5-release.jks WACKEN_RELEASE_STORE_PASSWORD=*** WACKEN_RELEASE_KEY_ALIAS=wacken-v2-5 WACKEN_RELEASE_KEY_PASSWORD=*** ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease
- /Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
- /Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk
- shasum -a 256 app/build/outputs/apk/release/app-release.apk
- git diff --check

### Manual validation

- GitHub release was verified with gh release view v2.5 and contains app-release.apk with digest sha256:e17978073e806cca7583ade7e8d32fdc6270e47a921b5147452399718e1ef82c.
- Installed-device UAT remains recommended on Android device and BlueStacks.

## TDD / BDD / approval-test evidence

- Release includes automated coverage from task-82: a regression test that first reproduced the chained-overlap alternatives bug and now proves schedule detail candidates are limited to direct overlaps.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: updated with V2.5 release notes link.

## Business requirements impact

Business requirements impact: none, because V2.5 releases the task-82 implementation correction within existing schedule alternatives requirements.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- 89da6e0 Fix schedule alternatives to direct overlaps
- d578bce Prepare V2.5 release
- v2.5 tag pushed
- GitHub release published

## Risks and follow-up

- The V2.1-V2.4 release keystore file existed locally but its passwords were unavailable in this shell. V2.5 is signed with a newly generated local self-signed V2.5 release key.
- Devices with V2.4, a previous release, or a debug APK installed must uninstall Wacken Planner before installing V2.5 because Android rejects same-package APKs signed by a different certificate.
- APK is signed with a local self-signed release key, not Play App Signing.
- Installed-device visual UAT is still recommended.
<!-- SECTION:NOTES:END -->
