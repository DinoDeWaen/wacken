---
id: task-144
title: 'REL: Create MVP3 official release'
status: To Do
assignee: []
created_date: '2026-07-02 08:28'
updated_date: '2026-07-02 08:45'
labels:
  - mvp3
  - release
  - apk
dependencies:
  - task-140
  - task-141
  - task-142
  - task-143
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: MVP3 should finish with a verified installable APK and GitHub release, matching the release governance used for MVP2.

Scope: after MVP3 scope, real post-show ratings, no-Wi-Fi use, and ratings CSV export are complete, build an official signed APK, verify metadata/signature/checksum, publish release notes and GitHub release, and record validation evidence.

Out of scope: adding new MVP4 capabilities during release packaging.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given all MVP3 implementation tasks are Done, when release validation runs, then domain, application, infrastructure, app unit tests, Android compile, QA checks, and signed release build pass
- [ ] #2 Given the MVP3 release APK is built, when signing and metadata are inspected, then it is an official signed APK with package, versionCode, versionName, minSdk, targetSdk, and SHA-256 recorded
- [ ] #3 Given the release is published, when GitHub is checked, then the tag, release notes, and APK asset are available
- [ ] #4 Given release packaging is complete, then the previous local release APK was deleted before rebuilding
- [ ] #5 README links to the new release notes or README impact is recorded using canonical delivery-governance wording
- [ ] #6 Business requirements impact is recorded using canonical delivery-governance wording
- [ ] #7 Manual MVP3 smoke-test evidence is recorded for CSV export, real rating, and no-Wi-Fi cached use
<!-- AC:END -->
