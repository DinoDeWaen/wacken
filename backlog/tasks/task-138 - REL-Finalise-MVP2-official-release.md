---
id: task-138
title: 'REL: Finalise MVP2 official release'
status: To Do
assignee: []
created_date: '2026-06-30 15:46'
updated_date: '2026-06-30 15:49'
labels:
  - release
  - mvp2
  - apk
dependencies:
  - task-117
  - task-136
  - task-137
  - task-139
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the product owner, I want one final official MVP2 release package after remaining backend, documentation, and UAT work is complete, so that the installable APK represents the completed MVP2 scope.

Scope: run the standard release process after task-117, task-136, task-137, and task-139 are complete; build a signed official APK, verify metadata/signatures/checksum, publish release notes, push the release artifacts/tags to GitHub, and record validation evidence.

Out of scope: adding MVP3/MVP4 capabilities or changing product behavior outside defects found during MVP2 validation.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given all MVP2 closure tasks are complete, when release validation runs, then domain, application, infrastructure, app unit tests, Android compile, and signed release build pass
- [ ] #2 Given the release APK is built, when signature and metadata are inspected, then it is a signed official APK with the expected package, versionCode, versionName, minSdk, targetSdk, and SHA-256 recorded
- [ ] #3 Given the release is published, when GitHub is checked, then the tag, release notes, and APK asset are available
- [ ] #4 Given the release process is complete, then the local previous release APK was deleted before rebuilding according to the documented ways of working
- [ ] #5 README links to the new release notes or README impact is recorded using the canonical wording from delivery-governance.md
- [ ] #6 Business requirements impact is recorded using the canonical wording from delivery-governance.md
- [ ] #7 Manual MVP2 smoke-test evidence is recorded, including schedule open, day filter, schedule locks, ratings detail, and sync/offline status
<!-- AC:END -->
