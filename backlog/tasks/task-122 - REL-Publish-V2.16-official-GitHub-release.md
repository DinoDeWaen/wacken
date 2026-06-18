---
id: task-122
title: 'REL: Publish V2.16 official GitHub release'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-06-18 19:29'
updated_date: '2026-06-18 19:29'
labels:
  - release
  - android
  - github
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Publish the already validated V2.16 signed release APK as an official GitHub release. The release includes the reset-button label improvement and all V2.15 changes already committed on the current branch.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given V2.16 release metadata is committed, when release packaging runs, then the signed release APK is verified with versionCode 19 and versionName 2.16.
- [ ] #2 Given the current branch is ready, when publishing runs, then the branch and tag v2.16 are pushed to GitHub.
- [ ] #3 Given the GitHub release is created, when it is viewed, then it contains the signed app-release.apk asset and uses releases/v2.16.md as release notes.
- [ ] #4 Release task notes record the release URL, APK SHA-256, validation commands, README/business/diagram/ADR impact, and signing risk.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm the current worktree is clean except the release task file and inspect V2.16 APK metadata/checksum.
2. Stage and commit the release task tracking file.
3. Push the current branch to GitHub.
4. Create and push tag `v2.16`.
5. Publish GitHub release `v2.16` with `app/build/outputs/apk/release/app-release.apk` and `releases/v2.16.md`.
6. Verify the GitHub release contains the signed APK asset.
7. Close the release task with validation evidence and impact notes.

Architecture impact: not architecture-significant; release publication only.
Documentation impact: README and release notes were already updated in V2.16 commit.
Risk: V2.16 uses the stable local V2.9+ release-key line; devices signed by older missing keys still need uninstall once.
<!-- SECTION:PLAN:END -->
