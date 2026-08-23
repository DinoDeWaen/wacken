---
id: task-180
title: 'REL: Create V2.30 official release APK'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-08-23 14:31'
updated_date: '2026-08-23 14:31'
labels:
  - release
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: users need the latest festival administration and reviewed metadata improvements delivered as a signed installable Android APK.

Scope: bump Android metadata to V2.30, create release notes, run full release validation, build and verify the signed release APK, publish git tag and GitHub release, and record validation evidence.

Out of scope: adding more metadata providers beyond the completed MusicBrainz provider, Play Store distribution, Supabase schema changes, and unfinished task-179 provider follow-ups.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the release build runs, then domain, application, infrastructure, Android unit tests, Android compile, debug build, and signed release assembly pass.
- [ ] #2 Given the release APK is built, then signing, package metadata, versionCode 33, versionName 2.30, minSdk, targetSdk, and SHA-256 are verified and recorded.
- [ ] #3 Given release notes are created, then README links V2.30 above older releases and the notes document scope, APK path, build command, metadata, signing, install guidance, validation, non-goals, and risks.
- [ ] #4 Given the release is published, then git tag v2.30 and the GitHub release contain the signed app-release.apk asset.
- [ ] #5 Business requirements impact, README impact, diagram impact, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Verify release inputs: latest tag, included commits, GitHub auth, signing setup, and unrelated worktree changes.
2. Bump Android metadata to versionCode 33 / versionName 2.30.
3. Create releases/v2.30.md and add the README release-note link.
4. Verify release keystore, remove prior release APK, run full validation and signed release build.
5. Verify APK signatures, metadata, SHA-256, and diff hygiene.
6. Commit release metadata/notes/task, push branch, tag v2.30, push tag, publish GitHub release, and verify asset.
7. Record release evidence, check acceptance criteria, and mark task Done.

Architecture impact: release packaging only; no architecture change expected.
README impact: update release-note link.
Business requirements impact: none expected; released feature tasks already updated requirements.
Risk: existing unrelated .idea/workspace.xml remains unstaged and will not be included.
<!-- SECTION:PLAN:END -->
