---
id: task-138
title: 'REL: Finalise MVP2 official release'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-06-30 15:46'
updated_date: '2026-07-01 06:24'
labels:
  - release
  - mvp2
  - apk
dependencies:
  - task-136
  - task-137
  - task-139
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the product owner, I want one final official MVP2 release package after remaining documentation and UAT work is complete, so that the installable APK represents the completed MVP2 scope.

Scope: run the standard release process after task-136, task-137, and task-139 are complete; build a signed official APK, verify metadata/signatures/checksum, publish release notes, push the release artifacts/tags to GitHub, and record validation evidence. Task-117 was cancelled because the Supabase schema-cache error no longer reproduces.

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

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm all prerequisite MVP2 closure tasks are Done and the worktree has no unrelated release-blocking changes.
2. Bump Android metadata to versionCode 23 / versionName 2.20.
3. Create `releases/v2.20.md` and add the README release-note link.
4. Delete any existing local release APK output, verify release keystore, and run full release validation with signed `assembleRelease`.
5. Verify APK signing, metadata, and SHA-256.
6. Commit release metadata/task notes, tag v2.20, push branch and tag, publish GitHub release with the signed APK.
7. Record validation evidence, release URL, risks, and close task-138.

Files/areas likely to change: `app/build.gradle`, `README.md`, `releases/v2.20.md`, release task notes.
Test strategy: full Gradle validation required by release process plus APK signer/aapt/SHA checks.
Design approach: release packaging only; no app behavior change expected.
Architecture impact: not architecture-significant; no code architecture changes planned.
Risks/assumptions: signing key must be available locally; GitHub CLI must be authenticated; physical device smoke evidence may be limited to prior user installed-app observation plus backend/API validation unless an emulator/device is available.
Treatment: standard release governance.
README/business/diagram/ADR impact: README release link expected; business requirements, diagrams, ADR expected none.
<!-- SECTION:PLAN:END -->
