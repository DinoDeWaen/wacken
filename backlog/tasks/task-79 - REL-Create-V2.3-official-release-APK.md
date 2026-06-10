---
id: task-79
title: 'REL: Create V2.3 official release APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 17:18'
updated_date: '2026-06-10 17:28'
labels:
  - release
  - android
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Package and publish the V2.3 official non-debug Android release after the short schedule block readability fix.

As a festival attendee, I want a signed V2.3 APK so that the schedule overview readability fix can be installed and validated on Android devices and BlueStacks.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the V2.3 code is complete, when release validation runs, then domain, application, infrastructure, app unit tests, debug compile, and release assemble checks pass.
- [x] #2 Given the release APK is built, when signing verification runs, then the APK verifies with v1 and v2 signing enabled and minSdk remains compatible with BlueStacks.
- [x] #3 Given the release is published, when GitHub releases are viewed, then tag v2.3 contains the signed app-release.apk asset and release notes.
- [x] #4 README documents the V2.3 release notes link and current version metadata where applicable.
- [x] #5 Business requirements impact is recorded using the canonical wording from delivery-governance.md.
- [x] #6 ADR and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Updated Android version metadata to 2.3.
2. Added V2.3 release notes and README link.
3. Ran full release validation: domain/application/infrastructure/app tests, debug compile, and release assemble.
4. Verified APK signing and version metadata.
5. Committed and pushed release prep, created and pushed git tag v2.3.
6. Published GitHub release with signed APK asset and closed the release task with validation evidence.
Architecture impact: not architecture-significant; release metadata/documentation only. No ADR needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

V2.3 is published as an official non-debug Android release with the signed release APK asset.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v2.3

APK: app/build/outputs/apk/release/app-release.apk
SHA-256: 85f664099a446ac190676656cbab881672c0dac0a48ca997a882895376cce75c

## Acceptance criteria validation

- Release validation passed.
- APK signing verification passed with v1=true and v2=true.
- APK metadata is versionCode=6, versionName=2.3, minSdk=23.
- GitHub release v2.3 contains app-release.apk.
- README links V2.3 release notes.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=/private/tmp/wacken-v2.1-release.jks WACKEN_RELEASE_STORE_PASSWORD=*** WACKEN_RELEASE_KEY_ALIAS=wacken-v2-1 WACKEN_RELEASE_KEY_PASSWORD=*** ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease
- /Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
- /Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk
- git diff --check

### Manual validation

- GitHub release was verified with gh release view v2.3 and contains app-release.apk.
- Installed-device UAT remains recommended on Android device and BlueStacks.

## TDD / BDD / approval-test evidence

- Release includes automated coverage from task-78: ScheduleBlockContentTest and ScheduleCalendarLayoutTest.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: updated with V2.3 release notes link.

## Business requirements impact

Business requirements impact: none, because V2.3 releases the task-78 presentation refinement within existing schedule overview and walking-time requirements.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- 51a5a7c Fix short schedule block readability
- e798fb7 Prepare V2.3 release
- v2.3 tag pushed
- GitHub release published

## Risks and follow-up

- APK is signed with the existing local self-signed release key, not Play App Signing.
- Devices with a previously installed debug APK must uninstall before installing this release APK.
- Installed-device visual UAT is still recommended.
<!-- SECTION:NOTES:END -->
