---
id: TASK-194
title: 'REL: Publish V2.33.2 festival cleanup hotfix'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-16 05:57'
updated_date: '2026-10-16 08:32'
labels: []
milestone: m-1
dependencies:
  - TASK-192
  - TASK-193
modified_files:
  - app/build.gradle
  - README.md
  - releases/v2.33.2.md
priority: high
type: chore
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Deliver the approved pending-rating discard and festival deletion work as a signed Android update over V2.33.1. Scope: versionCode 38, versionName 2.33.2, release notes, full validation, signed APK, tag and GitHub release. Do not publish until TASK-192 and TASK-193 are complete or explicitly accepted for release.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a V2.33.1 installation, when V2.33.2 is installed with the stable signing key, then Android accepts the update without clearing app data.
- [ ] #2 The official release APK is signed, has versionCode 38 and versionName 2.33.2, and passes the required validation.
- [ ] #3 Release notes and README document both changes, deletion consequences, installation, and known risks.
- [ ] #4 The V2.33.2 tag and GitHub release contain the verified signed APK and validation evidence.
- [ ] #5 Business requirements, ADR, and diagram impacts are recorded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Bump Android versionCode to 38 and versionName to 2.33.2; add release notes and README link. 2. Build with JDK 21, full unit/QA checks, and the stable Keychain-backed signing key; verify signature, package metadata, hash, and release lint-vital. 3. Verify an in-place V2.33.1-to-V2.33.2 emulator upgrade and Room migration without clearing data. 4. Commit metadata, push codex/release-2-33-2 and v2.33.2, publish the signed APK on GitHub, and record the validation package. TASK-192 and TASK-193 are complete; the approved V011 migration is already deployed and verified.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Signed APK built successfully with JDK 21; release lint-vital passed. apksigner verified v1/v2 with stable certificate ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. aapt confirmed package be.wacken.planner, versionCode 38, versionName 2.33.2, minSdk 23, targetSdk 36. SHA-256: 2d846f269d5d209c1fb37515424e8804dbbae72bbf36dc79545555dcd8bb8175. Emulator accepted adb install -r over the published V2.33.1 APK and retained firstInstallTime; upgraded app launched successfully.
<!-- SECTION:NOTES:END -->
