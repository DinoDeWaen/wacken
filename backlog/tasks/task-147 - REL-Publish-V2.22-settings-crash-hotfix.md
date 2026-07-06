---
id: task-147
title: 'REL: Publish V2.22 settings crash hotfix'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-07-06 13:42'
updated_date: '2026-07-06 13:42'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Users need an installable APK that contains the Settings crash fix, because V2.21 was published before task-146.

Scope: package and publish a signed V2.22 hotfix release from the fixed Settings state, verify metadata/signature/checksum, publish GitHub release notes and APK asset, and record validation evidence.

Out of scope: new feature work or unrelated MVP4 behavior.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Android version metadata is bumped to versionCode 25 and versionName 2.22
- [ ] #2 Full release validation passes, including domain/application/infrastructure/app tests, Android compile, debug build, and signed release build
- [ ] #3 Signed release APK metadata and SHA-256 are recorded
- [ ] #4 GitHub release v2.22 is published with release notes and app-release.apk asset
- [ ] #5 README links to V2.22 release notes and impact notes are recorded
- [ ] #6 Backlog task records validation evidence and Settings crash hotfix scope
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm fixed Settings crash commit is present and current release metadata is V2.21/versionCode 24.
2. Bump Android metadata to versionCode 25 and versionName 2.22.
3. Add releases/v2.22.md with hotfix scope, APK metadata, signing/install guidance, validation commands, and checksum placeholder.
4. Add README link to V2.22 release notes above V2.21.
5. Verify release keystore, run clean full release validation, inspect APK signing/badging, and record SHA-256.
6. Commit release metadata, push branch, create/push tag v2.22, publish GitHub release with signed APK, and verify uploaded asset digest.
7. Close task with checked acceptance criteria and canonical README/business/diagram/ADR impact notes.

Design approach: release packaging only from the fixed task-146 state. Architecture impact: not architecture-significant; no product behavior, persistence, sync, dependency, schema, signing strategy, or module boundary changes beyond version/release metadata. Treatment: standard release governance. Risks/assumptions: GitHub CLI auth and stable local signing secrets remain available.
<!-- SECTION:PLAN:END -->
