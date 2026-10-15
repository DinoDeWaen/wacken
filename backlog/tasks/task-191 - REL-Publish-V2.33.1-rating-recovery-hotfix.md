---
id: TASK-191
title: 'REL: Publish V2.33.1 rating recovery hotfix'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-15 19:58'
updated_date: '2026-10-15 20:26'
labels: []
dependencies:
  - TASK-190
modified_files:
  - app/build.gradle
  - releases/v2.33.1.md
  - README.md
priority: high
type: chore
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: deliver TASK-190 as an installable update that preserves the affected device database and lets the user recover the pending Wacken rating. Scope: version bump, signed APK validation, release notes, and GitHub release. Out of scope: changing the rating on the user device without review or clearing app storage.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given V2.33 is installed with a pending rating, when V2.33.1 is installed with the same signing key, then Android accepts the update without clearing app data.
- [x] #2 The official release APK is signed, has versionCode 37 and versionName 2.33.1, and passes the full validation suite.
- [x] #3 Release notes and README document recovery steps, installation, and known risks.
- [ ] #4 The V2.33.1 tag and GitHub release contain the verified signed APK and validation evidence.
- [x] #5 Business requirements, ADR, and diagram impacts are recorded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Prepare V2.33.1 metadata and release notes in this clean worktree from TASK-190.
2. Verify stable release keystore and keychain signing configuration without exposing secrets. Build with JDK 21, run tests, inspect signing, version metadata, and SHA-256.
3. Commit release metadata, push branch/tag, publish signed APK to GitHub, and verify release asset.
4. Record validation package and close TASK-191. No architecture boundary or deployment mechanism changes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
V2.33.1 signed APK built from TASK-190 plus archive fix TASK-189. Full JDK 21 tests, QA, release lint-vital, and assembleRelease passed. apksigner v1/v2 verified; package be.wacken.planner, versionCode 37, versionName 2.33.1, minSdk 23. SHA-256: a2be471a439f440eb28eb1f2d9a1909768d2a32a2cf940c1230c3ad28cd2b6b8. Signing certificate SHA-256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f matches the V2.33 APK. Emulator install V2.33 then adb install -r V2.33.1 succeeded; firstInstallTime remained earlier than lastUpdateTime. Phone rating not accessed.
<!-- SECTION:NOTES:END -->
