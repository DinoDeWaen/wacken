---
id: task-high.4
title: 'REL: Create V2.10 official release APK'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-06-12 14:24'
updated_date: '2026-06-12 14:24'
labels:
  - release
  - android
dependencies: []
parent_task_id: task-high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Release V2.10 as the next official non-debug Android APK after the completed schedule UI and walking-time refinements.

Scope:
- Bump Android versionCode to 13 and versionName to 2.10.
- Create V2.10 release notes and link them from README.
- Run full validation and build the signed release APK.
- Verify APK signature, metadata, and SHA-256.
- Commit release metadata, tag v2.10, push branch/tag, and publish the GitHub release with the signed APK asset.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Full release validation passes for domain, application, infrastructure, app unit tests, Android compile, and assembleRelease.
- [ ] #2 The signed release APK is verified with apksigner and reports package be.wacken.planner, versionCode 13, and versionName 2.10.
- [ ] #3 README links to the V2.10 release notes and releases/v2.10.md records scope, APK path, build command, metadata, signing, install guidance, validation, non-goals, and risks.
- [ ] #4 Git tag v2.10 is created and pushed.
- [ ] #5 GitHub release v2.10 is published with app-release.apk as the asset.
- [ ] #6 Business requirements, diagram, and ADR impact are recorded using the canonical wording from delivery-governance.md.
- [ ] #7 The release task records the release URL, APK SHA-256, validation commands, signing evidence, and residual risks before being marked Done.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Update app/build.gradle to versionCode 13 and versionName 2.10.
2. Create releases/v2.10.md using the standard release-note sections for the schedule axis, stage-column block, and walking-time refinements.
3. Add the V2.10 release-note link to README above V2.9.
4. Run full release validation with signing variables from the macOS keychain and build app/build/outputs/apk/release/app-release.apk.
5. Verify APK signature, package metadata, min/target SDK, versionCode/versionName, and SHA-256.
6. Commit release metadata, push the branch, tag v2.10, push the tag, and publish the GitHub release asset.
7. Close the release task with validation evidence, URL, checksum, and impact notes.
Architecture impact: not architecture-significant; release packaging only. No ADR expected.
<!-- SECTION:PLAN:END -->
