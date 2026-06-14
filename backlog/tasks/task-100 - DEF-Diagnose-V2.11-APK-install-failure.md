---
id: task-100
title: 'DEF: Diagnose V2.11 APK install failure'
status: To Do
assignee:
  - '@codex'
created_date: '2026-06-12 16:05'
updated_date: '2026-06-14 19:42'
labels:
  - defect
  - release
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
V2.11 APK install reports App not installed even after deleting the app first. Diagnose the release package and installation constraints before changing code or publishing a replacement release.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 V2.11 package metadata, SDK constraints, and signing certificate are verified.
- [ ] #2 V2.11 signing certificate is compared with V2.10.
- [ ] #3 The likely root cause and install recovery steps are documented.
- [ ] #4 If the APK is defective, a corrected release plan is created before publishing a replacement.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Verify the local V2.11 APK metadata, SDK constraints, and signing certificate.
2. Download the published V2.10 release APK and compare package metadata and signer certificate with V2.11.
3. Check whether the V2.11 GitHub release asset digest matches the local signed APK.
4. Identify likely install failure causes and document concrete recovery steps.
5. If the APK is defective, create a corrected replacement-release plan before changing/publishing anything.
Architecture impact: not architecture-significant; this is release/package diagnosis only. No ADR expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Partial diagnosis before pause

The V2.11 install-failure investigation was interrupted and paused. Findings captured so far:

- Local V2.11 APK verifies with apksigner.
- Package is be.wacken.planner, versionCode 14, versionName 2.11, minSdk 23, targetSdk 36.
- Published V2.10 and V2.11 APKs use the same signing certificate SHA-256: e0f384db6cf96c4c5e4924dc03384f8c7a8130ad2af3772a0ecf36c5a4af50f9.
- Published V2.11 asset SHA-256 matches local app-release.apk: 9cfaad3332dfe53916a61b23e5f8898bdd74c10e0b4e965f3366fc5665d5fc37.
- No Android device was connected over adb at the time, so the concrete package-manager install error was not captured.

Next step when resumed: connect the target device or emulator and run `adb install -r app/build/outputs/apk/release/app-release.apk` after `adb uninstall be.wacken.planner` to capture the real INSTALL_* failure reason.
<!-- SECTION:NOTES:END -->
