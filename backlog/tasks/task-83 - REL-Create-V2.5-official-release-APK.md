---
id: task-83
title: 'REL: Create V2.5 official release APK'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-06-10 17:55'
updated_date: '2026-06-10 17:55'
labels:
  - release
  - android
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Package and publish the V2.5 official non-debug Android release after the schedule alternatives direct-overlap defect fix.

As a festival attendee, I want a signed V2.5 APK so that the schedule detail alternatives fix can be installed and validated on Android devices and BlueStacks.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the V2.5 code is complete, when release validation runs, then domain, application, infrastructure, app unit tests, debug compile, and release assemble checks pass.
- [ ] #2 Given the release APK is built, when signing verification runs, then the APK verifies with v1 and v2 signing enabled and minSdk remains compatible with BlueStacks.
- [ ] #3 Given the release is published, when GitHub releases are viewed, then tag v2.5 contains the signed app-release.apk asset and release notes.
- [ ] #4 README documents the V2.5 release notes link and current version metadata where applicable.
- [ ] #5 Business requirements impact is recorded using the canonical wording from delivery-governance.md.
- [ ] #6 ADR and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Bump Android release metadata to versionCode 8/versionName 2.5.
2. Add V2.5 release notes and link them from README.
3. Run full release validation: domain/application/infrastructure/app tests, debug compile, and assembleRelease with release signing.
4. Verify APK signing, minSdk, versionCode/versionName, and SHA-256.
5. Commit and push release prep, create and push git tag v2.5.
6. Publish GitHub release v2.5 with signed app-release.apk and close the release task with validation evidence.
Architecture impact: not architecture-significant; release metadata/documentation only. No ADR needed.
<!-- SECTION:PLAN:END -->
