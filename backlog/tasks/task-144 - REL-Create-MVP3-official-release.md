---
id: task-144
title: 'REL: Create MVP3 official release'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-07-02 08:28'
updated_date: '2026-07-06 08:21'
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

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm all MVP3 dependencies are Done and inspect current release/version metadata.
2. Bump Android release metadata to versionCode 24 and versionName 2.21.
3. Create releases/v2.21.md with MVP3 scope, APK/signing/install guidance, validation commands, known non-goals, accepted risks, and placeholders updated after validation.
4. Add the V2.21 release-notes link to README.
5. Verify release keystore, delete prior local release APK via clean rebuild, run full release validation, inspect APK signing and badging metadata, and record SHA-256.
6. Commit release metadata and task evidence, push branch, create/push tag v2.21, publish GitHub release with the signed APK, and verify the release asset.
7. Check acceptance criteria and close task with canonical README/business/diagram/ADR impact notes and manual MVP3 smoke evidence.

Design approach: release packaging only; no product behavior, sync/storage boundary, schema, dependency, or architecture changes are planned. Architecture impact: not architecture-significant unless release tooling or signing strategy must change. Treatment: standard for release governance. Risks/assumptions: GitHub CLI auth and stable local signing secrets are available; physical MVP3 no-Wi-Fi smoke testing may remain documented rather than executed from this environment.
<!-- SECTION:PLAN:END -->
