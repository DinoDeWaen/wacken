---
id: task-85
title: 'REL: Create V2.6 official release APK'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-06-10 18:42'
updated_date: '2026-06-10 18:42'
labels:
  - release
  - android
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Package and publish the V2.6 official non-debug Android release after replacing the sync splash with the Dino Metal image.

As a festival attendee, I want a signed V2.6 APK so that the Dino Metal splash update can be installed and validated on Android devices and BlueStacks.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the V2.6 code is complete, when release validation runs, then domain, application, infrastructure, app unit tests, debug compile, and release assemble checks pass.
- [ ] #2 Given the release APK is built, when signing verification runs, then the APK verifies with v1 and v2 signing enabled and minSdk remains compatible with BlueStacks.
- [ ] #3 Given the release is published, when GitHub releases are viewed, then tag v2.6 contains the signed app-release.apk asset and release notes.
- [ ] #4 README documents the V2.6 release notes link and current version metadata where applicable.
- [ ] #5 Business requirements impact is recorded using the canonical wording from delivery-governance.md.
- [ ] #6 ADR and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Bump Android release metadata to versionCode 9/versionName 2.6.
2. Add V2.6 release notes and README link.
3. Run full release validation: domain/application/infrastructure/app tests, debug compile, and assembleRelease with the V2.5/V2.6 local release key.
4. Verify APK signing, minSdk, versionCode/versionName, and SHA-256.
5. Commit and push release prep, create and push git tag v2.6.
6. Publish GitHub release v2.6 with signed app-release.apk and close the release task with validation evidence.
Architecture impact: not architecture-significant; release metadata/documentation only. No ADR needed.
<!-- SECTION:PLAN:END -->
