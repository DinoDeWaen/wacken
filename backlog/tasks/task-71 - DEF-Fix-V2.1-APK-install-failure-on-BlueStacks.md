---
id: task-71
title: 'DEF: Fix V2.1 APK install failure on BlueStacks'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 08:46'
updated_date: '2026-06-10 08:52'
labels:
  - defect
  - release
  - android
  - bluestacks
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a user installing Wacken Planner V2.1 on BlueStacks, I want the official release APK to install cleanly, so I can validate the app without Android rejecting the package.

Observed issue:
- APKMirror Installer reports: Unfortunately, this app could not be installed. Please verify the installation file before trying again.

In scope:
- Inspect V2.1 release APK metadata/signing compatibility.
- Adjust release packaging/signing/minimum compatibility only as needed.
- Rebuild and verify the release APK.
- Update release notes/task evidence and replace the GitHub V2.1 asset if fixed.

Out of scope:
- Play Store distribution and changing app business behavior.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the V2.1 release APK is inspected, then likely install blockers such as min SDK, signing schemes, and package metadata are recorded.
- [x] #2 Given a packaging issue is found, then the release APK is rebuilt with a compatible release configuration.
- [x] #3 Given the APK is rebuilt, then automated tests, assembleRelease, and APK signature verification pass.
- [x] #4 Given the release asset is updated, then GitHub V2.1 release notes and Backlog evidence document the correction.
- [x] #5 README/business requirements/diagram/ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected the V2.1 release APK with aapt/apksigner.
2. Found likely install blockers: the original release APK required Android 8+ (`minSdk 26`), used v2 signing only, and could not be installed over an already installed debug APK due Android signing-certificate mismatch.
3. Updated release packaging for broader BlueStacks compatibility: `minSdk 23`, Java time desugaring, and explicit v1+v2 release signing.
4. Rebuilt the signed release APK, verified metadata/signatures, and replaced the GitHub V2.1 release asset.
5. Updated V2.1 release notes with BlueStacks/install guidance and closed the task with validation evidence.

Architecture impact: release packaging/deployment compatibility only. No domain, application behavior, persistence, backend, or business rule change. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed the V2.1 APK packaging for broader BlueStacks compatibility. The original V2.1 release APK was valid, but inspection showed likely install blockers for BlueStacks/APKMirror Installer: it required Android 8+ (`minSdk 26`), verified only with APK Signature Scheme v2, and could not be installed over an existing debug APK with the same package name because Android rejects signature mismatches.

The corrected V2.1 release APK now supports Android 6.0+ (`minSdk 23`) with core library desugaring enabled and verifies with both APK Signature Scheme v1 and v2. The GitHub `v2.1` release asset was replaced with the corrected `app-release.apk`, and release notes now explain that users must uninstall any previous debug build before installing the official release.

## Acceptance criteria validation

- AC1: APK inspection recorded package `be.wacken.planner`, `versionCode 4`, `versionName 2.1`, original `minSdk 26`, v2-only signing, and the debug-to-release signing mismatch risk.
- AC2: Release packaging was rebuilt with `minSdk 23`, Java time desugaring, and explicit v1+v2 release signing.
- AC3: Full automated validation, `assembleRelease`, and APK signature verification passed. Corrected APK verifies with v1=true and v2=true.
- AC4: GitHub V2.1 release asset was replaced and `releases/v2.1.md` documents the correction and install guidance.
- AC5: README, business requirements, diagram, and ADR impact are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease --rerun-tasks`
- `/Users/dino/Library/Android/sdk/build-tools/36.1.0-rc1/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.1.0-rc1/apksigner verify --verbose --print-certs app/build/outputs/apk/release/app-release.apk`
- `git diff --check`

### Manual validation

- Not run on BlueStacks from this environment. On BlueStacks, uninstall any existing Wacken Planner debug build first, then install the updated V2.1 `app-release.apk` from GitHub.

## TDD / BDD / approval-test evidence

This is a release packaging defect, not product behavior. Existing domain/application/infrastructure/app tests were rerun, and APK metadata/signature verification provides packaging validation.

## Architecture impact

- Architecture-significant change: no. This is release packaging compatibility only. No domain, application behavior, persistence, backend, schema, API, or module-boundary change was introduced.
- Approval received: not required beyond the existing release-signing approval used for V2.1.
- ADR impact: none, because no architecture-significant decision was made.

## README impact

README impact: none, because release-specific BlueStacks/install guidance belongs in `releases/v2.1.md` and does not change setup, architecture, or normal development commands.

## Business requirements impact

Business requirements impact: none, because this fixes release packaging compatibility without changing product scope or business rules.

## Diagram impact

Diagram impact: none, because the system structure and data flow did not change.

## Commits / logical change list

- Lower Android `minSdk` to 23 for older BlueStacks images.
- Enable core library desugaring for Java time support below Android 8.
- Enable explicit v1 and v2 release signing.
- Update V2.1 release notes with debug-uninstall guidance and Android 6+ compatibility.
- Replace the GitHub V2.1 `app-release.apk` asset.

## Risks and follow-up

- Installing over a previously installed debug APK will still fail by Android design; uninstall the debug build first.
- BlueStacks must run Android 6.0 or newer.
- Installed-emulator validation remains to be run by the user because BlueStacks is not available in this environment.
<!-- SECTION:NOTES:END -->
