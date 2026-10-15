---
id: TASK-188
title: 'REL: Publish V2.33 signed Android release'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-15 18:03'
updated_date: '2026-10-15 18:19'
labels:
  - release
  - android
milestone: m-1
dependencies:
  - TASK-185
  - TASK-186
  - TASK-187
priority: high
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Ship the completed Release 2.33 fixes as a verifiable, installable Android APK for the group.

Scope: After TASK-185, TASK-186, and TASK-187 are Done or explicitly accepted, bump Android metadata to 2.33, write release notes, run the documented validation build with the existing signing key, verify the APK, publish the v2.33 Git tag and GitHub release asset, and record evidence.

Out of scope: Play Store distribution, key rotation, unrelated features, and publishing an unsigned APK.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the completed Release 2.33 defect tasks, when release packaging starts, then Android versionCode is 36 and versionName is 2.33.
- [ ] #2 Release notes describe the three fixes, known risks, install guidance, and validation, and README links to them.
- [ ] #3 Full documented release validation and a signed assembleRelease build pass; the APK has verified v1/v2 signatures, package/version/minSdk metadata, and a recorded SHA-256.
- [ ] #4 Tag v2.33 is pushed and the GitHub release is published with the verified app-release.apk asset; the release URL and asset are checked.
- [ ] #5 Unrelated pre-existing worktree changes are excluded from release commits and artifact provenance.
- [ ] #6 README, business requirements, diagram, and ADR impact are recorded using canonical delivery-governance wording before task completion.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Treatment: standard release packaging with no architecture-significant change. TASK-185, TASK-186, and approved TASK-187 are Done.
1. Commit this release task and milestone metadata, then use a clean checkout from codex/release-2-33 to exclude unrelated dirty workspace files.
2. Verify prior release v2.32 versionCode 35; set app/build.gradle to versionCode 36 and versionName 2.33. Write releases/v2.33.md and link it in README.
3. Delete the prior local APK, run scripts/ensure-release-keystore.sh, full documented Gradle validation and signed assembleRelease using local Keychain values. Verify v1/v2 signing, package/version/minSdk, SHA-256, git diff --check, and clean provenance.
4. Record validation and impact notes in TASK-188, commit release metadata and task, push branch, tag v2.33, publish GitHub release with verified APK, then verify URL and asset. Close task with final evidence.
Likely files: app/build.gradle, releases/v2.33.md, README.md, TASK-188, milestone m-1. No business rules, module boundaries, APIs, persistence, or security design changes, so architecture approval and new ADR are not required. Business requirements and diagrams should need no release-only changes. Risks: signed-key lineage requires uninstall/reinstall for devices on the missing old key; full lintDebug has pre-existing NewApi findings; device install test may be unavailable.
<!-- SECTION:PLAN:END -->
