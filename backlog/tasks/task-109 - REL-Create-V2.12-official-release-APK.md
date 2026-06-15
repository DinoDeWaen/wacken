---
id: task-109
title: 'REL: Create V2.12 official release APK'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-06-15 08:40'
updated_date: '2026-06-15 08:40'
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
- [ ] #1 Full release validation passes for domain, application, infrastructure, Android unit tests, app compile, and signed release assembly.
- [ ] #2 Android version metadata is bumped to versionCode 15 and versionName 2.12.
- [ ] #3 The release APK is signed, verified, and has package metadata recorded.
- [ ] #4 V2.12 release notes are created and linked from README.
- [ ] #5 Git tag v2.12 is created and pushed.
- [ ] #6 GitHub release v2.12 is published with app-release.apk attached.
- [ ] #7 README, business requirements, diagram, and ADR impact are recorded using canonical delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Bump Android metadata to versionCode 15 / versionName 2.12.
2. Create V2.12 release notes and add README release link.
3. Delete previous local release APK and run full signed release validation build with keychain-backed signing.
4. Verify APK signature, package metadata, and SHA-256.
5. Commit release metadata/task notes, push branch, create/push tag v2.12, and publish GitHub release with app-release.apk.
6. Close task with validation package and canonical impact notes.

Architecture impact: release packaging/versioning only; not architecture-significant. No ADR expected.
<!-- SECTION:PLAN:END -->
