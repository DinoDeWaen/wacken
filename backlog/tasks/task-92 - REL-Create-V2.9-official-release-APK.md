---
id: task-92
title: 'REL: Create V2.9 official release APK'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-06-12 09:50'
updated_date: '2026-06-12 09:54'
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
- [ ] #1 Given the V2.9 code is complete, when release validation runs, then domain, application, infrastructure, app unit tests, debug compile, and release assemble checks pass.
- [ ] #2 Given the release APK is built, when signing verification runs, then the APK verifies with v1 and v2 signing enabled and minSdk remains compatible with BlueStacks.
- [ ] #3 Given the release is published, when GitHub releases are viewed, then tag v2.9 contains the signed app-release.apk asset and release notes.
- [ ] #4 README documents the V2.9 release notes link and current version metadata where applicable.
- [ ] #5 Business requirements impact is recorded using the canonical wording from delivery-governance.md.
- [ ] #6 ADR and diagram impact are recorded using the canonical wording from delivery-governance.md.

- [ ] #7 Release process documentation is added or updated so future official APK releases have a standard checklist for signing, validation, tagging, GitHub publication, and signing-failure recovery.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Bump Android version metadata to versionCode 12/versionName 2.9.
2. Add V2.9 release notes for the sparse schedule fix and middle-30-minute overlap rule, and link them from README.
3. Add a reusable release process document covering official signed APK validation, signing credentials, unsigned APK blockers, tagging, GitHub release publication, and task closure evidence.
4. Run full release validation with the existing V2.5+ local release key: domain/application/infrastructure/app tests, debug compile, and assembleRelease.
5. Verify APK signing, minSdk, versionCode/versionName, and SHA-256.
6. Commit and push release prep, create and push git tag v2.9.
7. Publish GitHub release v2.9 with signed app-release.apk, verify the asset, and close the release task with validation evidence.
Current blocker: release signing passwords are not available in this shell, so Gradle currently produces app-release-unsigned.apk. Do not tag or publish until app-release.apk is produced and verified.
Architecture impact: not architecture-significant; release metadata/documentation/packaging only. No ADR needed.
<!-- SECTION:PLAN:END -->
