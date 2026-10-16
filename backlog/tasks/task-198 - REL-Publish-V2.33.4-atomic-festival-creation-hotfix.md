---
id: TASK-198
title: 'REL: Publish V2.33.4 atomic festival creation hotfix'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-16 20:32'
updated_date: '2026-10-16 20:33'
labels:
  - release
  - android
dependencies:
  - TASK-197
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Publish the completed TASK-197 festival creation repair as an official signed Android update. Include complete CSV/API and Android regression validation, upgrade preservation evidence, release metadata and GitHub APK publication. No additional product behavior or key rotation.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given V2.33.3, the new APK is versionName 2.33.4 and versionCode 40, uses the stable signing certificate, and can update the previous release while retaining app data.
- [ ] #2 Full unit/QA/coverage, real festival creation API gate, Android instrumentation, release lint-vital and fresh signed release build pass on the release source.
- [ ] #3 APK signature, package, SDK metadata and SHA-256 are verified and recorded.
- [ ] #4 The version tag and GitHub release are published with the verified signed app-release.apk and complete release notes.
- [ ] #5 README links the release notes; business requirements, ADR and diagram impact use the canonical wording and the task contains the validation package.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Minimal release packaging from completed TASK-197 in the existing isolated worktree. 1. Bump app/build.gradle to 2.33.4/40, add releases/v2.33.4.md and README release link. 2. Verify stable keystore, run real PostgreSQL/PostgREST gate, clean unit/QA/coverage, Android instrumentation and release lint/build. 3. Verify APK signatures/metadata/digest and upgrade from published V2.33.3 while retaining local data. 4. Commit, tag and publish the signed APK; verify downloaded asset and record complete validation package. Architecture impact: none beyond already approved TASK-197; no production dependency, schema or behavior changes. No new TDD scenarios required for metadata-only packaging; existing behavior suites are mandatory. Business requirements, diagram and ADR impact: none, because packaging delivers the already documented repair. Signing and physical-phone picker limitations will be recorded.
<!-- SECTION:PLAN:END -->
