---
id: task-99
title: 'REL: Create V2.11 official release APK'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-06-12 15:56'
updated_date: '2026-06-12 15:56'
labels:
  - release
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Create the official signed non-debug Android release for V2.11 after the schedule overlap scratch-rule fixes. The release must include the Saxon chained-overlap regression and the walking-time effective-conflict rule.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Android versionCode is bumped to 14 and versionName is bumped to 2.11.
- [ ] #2 Release notes for V2.11 are created and linked from README.md.
- [ ] #3 Full release validation passes, including domain/application/infrastructure/app tests and Android debug compilation.
- [ ] #4 A signed release APK is built, verified with apksigner, and metadata is checked with aapt.
- [ ] #5 The V2.11 Git tag is created and pushed, and the GitHub release is published with the signed app-release.apk asset.
- [ ] #6 Release task notes include release URL, APK SHA-256, validation commands, README/business requirements/diagram/ADR impact, and accepted risks.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Bump Android release metadata to versionCode 14 and versionName 2.11.
2. Create releases/v2.11.md documenting the schedule scratch-rule fixes, validation, signing, install guidance, and risks.
3. Link V2.11 release notes from README.md above V2.10.
4. Run full release validation and build a signed release APK with local keychain signing values.
5. Verify APK signatures, package metadata, and SHA-256.
6. Commit release metadata, push the branch, create and push tag v2.11, publish GitHub release with app-release.apk, then close this task with evidence.
Architecture impact: not architecture-significant; this is release metadata and packaging for already completed behavior. No ADR expected.
<!-- SECTION:PLAN:END -->
