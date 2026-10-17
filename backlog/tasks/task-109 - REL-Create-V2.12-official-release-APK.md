---
id: task-109
title: 'REL: Create V2.12 official release APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-15 08:40'
updated_date: '2026-06-15 08:47'
labels:
  - release
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Release Wacken Planner 2026 V2.12 as an official non-debug signed Android APK for the latest schedule filtering and lost-alternative fixes.

Scope includes the completed schedule fixes after V2.11: tied/lost alternative restoration, rating color scheme, hide-barred schedule filter, and selected star-threshold schedule filter.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Full release validation passes for domain, application, infrastructure, Android unit tests, app compile, and signed release assembly.
- [x] #2 Android version metadata is bumped to versionCode 15 and versionName 2.12.
- [x] #3 The release APK is signed, verified, and has package metadata recorded.
- [x] #4 V2.12 release notes are created and linked from README.
- [x] #5 Git tag v2.12 is created and pushed.
- [x] #6 GitHub release v2.12 is published with app-release.apk attached.
- [x] #7 README, business requirements, diagram, and ADR impact are recorded using canonical delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Bumped Android metadata to versionCode 15 / versionName 2.12.
2. Created V2.12 release notes and added README release link.
3. Deleted previous local release APK and ran full signed release validation build with keychain-backed signing.
4. Verified APK signature, package metadata, and SHA-256.
5. Committed release metadata, pushed branch via HTTPS GitHub CLI auth, created/pushed tag v2.12, and published GitHub release with app-release.apk.
6. Closed task with validation package and canonical impact notes.

Deviation: SSH push was unavailable, so branch/tag push used GitHub CLI HTTPS credentials. Architecture impact: release packaging/versioning only; not architecture-significant. No ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Released Wacken Planner 2026 V2.12 as an official non-debug signed Android APK.

GitHub release: https://github.com/DinoDeWaen/wacken/releases/tag/v2.12
Release tag: v2.12
Release APK: app/build/outputs/apk/release/app-release.apk
Published asset: https://github.com/DinoDeWaen/wacken/releases/download/v2.12/app-release.apk
SHA-256: 6a47c5454782e28ffa2e605b4832e6973b5600e87abd29027de8c89e1b6cb488

Android metadata: versionCode 15, versionName 2.12, package be.wacken.planner, minSdk 23, targetSdk 36.

## Acceptance criteria validation

- AC1: Full signed release validation passed.
- AC2: Android metadata is versionCode 15 and versionName 2.12.
- AC3: APK signature and package metadata were verified; v1 and v2 signing are enabled.
- AC4: V2.12 release notes were created and linked from README.
- AC5: Git tag v2.12 was created and pushed.
- AC6: GitHub release v2.12 was published with app-release.apk attached.
- AC7: README, business requirements, diagram, and ADR impacts are recorded below.

## How to test

### Automated tests and release build

- /bin/zsh -lc "JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=*** WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=*** ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease"

### APK verification

- /Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
- /Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk
- shasum -a 256 app/build/outputs/apk/release/app-release.apk
- git diff --check
- gh release view v2.12 --json url,tagName,name,assets

### Manual validation

- Not run on a device in this task. Use backlog/docs/mvp2-android-uat-checklist.md for installed-device UAT.

## TDD / BDD / approval-test evidence

- This release packages already completed and tested behavior. The included feature/defect tasks added unit coverage for tied alternatives, lost alternatives, rating colors, hide-barred filtering, and selected star-threshold filtering.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR impact: none, because this is release packaging/versioning only and does not change architecture, persistence, APIs, dependencies, or module boundaries.

## README impact

README impact: updated with the V2.12 release notes link.

## Business requirements impact

Business requirements impact: none in this release task, because the included feature/defect tasks already updated business requirements for the schedule filter behavior.

## Diagram impact

Diagram impact: none, because no architecture or flow diagram changed.

## Commits / logical change list

- Bumped app version to 2.12 / versionCode 15.
- Added releases/v2.12.md.
- Linked V2.12 release notes from README.
- Built and published signed app-release.apk.

## Risks and follow-up

- Installed-device UAT was not run from this environment.
- V2.12 uses the same local V2.9 release-key line, so it can install over V2.9+ same-key releases. Installing over V2.8 or older/debug builds can fail due to Android signing-certificate mismatch.
<!-- SECTION:NOTES:END -->
