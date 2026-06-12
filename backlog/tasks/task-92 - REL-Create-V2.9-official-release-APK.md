---
id: task-92
title: 'REL: Create V2.9 official release APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-12 09:50'
updated_date: '2026-06-12 10:01'
labels:
  - release
  - android
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Package and publish the V2.9 official non-debug Android release after fixing sparse Sofie and Dino group schedule selection and the middle-30-minute overlap rule.

As a festival attendee, I want a signed V2.9 APK so that the corrected group schedule logic can be installed and validated on Android devices and BlueStacks.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the V2.9 code is complete, when release validation runs, then domain, application, infrastructure, app unit tests, debug compile, and release assemble checks pass.
- [x] #2 Given the release APK is built, when signing verification runs, then the APK verifies with v1 and v2 signing enabled and minSdk remains compatible with BlueStacks.
- [x] #3 Given the release is published, when GitHub releases are viewed, then tag v2.9 contains the signed app-release.apk asset and release notes.
- [x] #4 README documents the V2.9 release notes link and current version metadata where applicable.
- [x] #5 Business requirements impact is recorded using the canonical wording from delivery-governance.md.
- [x] #6 ADR and diagram impact are recorded using the canonical wording from delivery-governance.md.

- [x] #7 Release process documentation is added or updated so future official APK releases have a standard checklist for signing, validation, tagging, GitHub publication, and signing-failure recovery.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Bumped Android version metadata to versionCode 12/versionName 2.9.
2. Added V2.9 release notes for the sparse schedule fix and middle-30-minute overlap rule, and linked them from README.
3. Added a reusable release process document covering official signed APK validation, signing credentials, unsigned APK blockers, tagging, GitHub release publication, and task closure evidence.
4. Created a new local self-signed V2.9 release key because the previous V2.5 release-key passwords were unavailable; stored the V2.9 signing values in the local macOS keychain for future releases.
5. Ran full release validation with the V2.9 local release key: domain/application/infrastructure/app tests, debug compile, and assembleRelease.
6. Verified APK signing, minSdk, versionCode/versionName, and SHA-256.
7. Committed and pushed release prep, created and pushed git tag v2.9.
8. Published GitHub release v2.9 with signed app-release.apk, verified the asset, and closed the release task with validation evidence.
Deviation: V2.9 starts a new signing-certificate line. Devices with V2.8 or older installed must uninstall before installing V2.9.
Architecture impact: not architecture-significant; release metadata/documentation/packaging only. No ADR needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

V2.9 is published as an official non-debug Android release with the signed release APK asset.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v2.9

APK: app/build/outputs/apk/release/app-release.apk
SHA-256: 3042d2f000f97149f2efab65c696661dccc6a47e46eba1575947eb82e2d3e4f7

## Acceptance criteria validation

- Release validation passed.
- APK signing verification passed with v1=true and v2=true.
- APK metadata is versionCode=12, versionName=2.9, minSdk=23.
- GitHub release v2.9 contains app-release.apk.
- README links V2.9 release notes and the standardized release process.
- Release process documentation was added at backlog/docs/release-process.md.

## How to test

### Automated tests

- STORE_FILE=$(security find-generic-password -w -s WACKEN_RELEASE_STORE_FILE 2>/dev/null) STORE_PASSWORD=$(security find-generic-password -w -s WACKEN_RELEASE_STORE_PASSWORD 2>/dev/null) KEY_ALIAS=$(security find-generic-password -w -s WACKEN_RELEASE_KEY_ALIAS 2>/dev/null) KEY_PASSWORD=$(security find-generic-password -w -s WACKEN_RELEASE_KEY_PASSWORD 2>/dev/null) JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=*** WACKEN_RELEASE_STORE_PASSWORD=*** WACKEN_RELEASE_KEY_ALIAS=*** WACKEN_RELEASE_KEY_PASSWORD=*** ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease
- /Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
- /Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk
- shasum -a 256 app/build/outputs/apk/release/app-release.apk
- git diff --check

### Manual validation

- GitHub release was verified with gh release view v2.9 and contains app-release.apk.
- Installed-device UAT remains recommended on Android device and BlueStacks.

## TDD / BDD / approval-test evidence

- Release includes task-91 regression coverage for middle-30-minute overlap conflict detection and Def Leppard chained-conflict schedule selection.
- Release process documentation was reviewed through markdown/diff inspection and git diff whitespace validation.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: updated with V2.9 release notes and release-process links.

## Business requirements impact

Business requirements impact: none in this release task, because task-91 already updated BR-013a for the middle-30-minute schedule conflict rule being released here.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- 4135f4e Fix sparse group schedule selection
- 7f795e4 Prepare V2.9 release
- v2.9 tag pushed
- GitHub release published

## Risks and follow-up

- V2.9 uses a new local self-signed V2.9 release key because the previous V2.5 release-key passwords were unavailable. Devices with V2.8 or any older build installed must uninstall Wacken Planner before installing V2.9.
- The V2.9 signing values are stored in the local macOS keychain for future releases and documented in backlog/docs/release-process.md.
- APK is signed with a local self-signed release key, not Play App Signing.
- Installed-device visual/UAT validation is still recommended.
- If live Supabase master data lacks a performance row for a rated band such as Def Leppard, the app cannot schedule that band until master data is corrected.
<!-- SECTION:NOTES:END -->
