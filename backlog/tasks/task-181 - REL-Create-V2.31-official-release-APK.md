---
id: task-181
title: 'REL: Create V2.31 official release APK'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-08-24 16:12'
labels:
  - release
  - android
  - uat
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: users need the completed reviewed external metadata provider set delivered as a signed installable Android APK, with a clear user acceptance test overview for manual release acceptance.

Scope: bump Android metadata to V2.31, add a V2.31 user acceptance test overview, create release notes, run full release validation, build and verify the signed release APK, publish git tag and GitHub release, and record validation evidence.

Out of scope: Play Store distribution, Supabase schema changes, automatic unreviewed metadata saves, storing production metadata-provider secrets in the repository, and new feature work beyond the completed metadata-provider stories.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the release build runs, then domain, application, infrastructure, Android unit tests, Android compile, debug build, and signed release assembly pass.
- [ ] #2 Given the release APK is built, then signing, package metadata, versionCode 34, versionName 2.31, minSdk, targetSdk, and SHA-256 are verified and recorded.
- [ ] #3 Given release notes are created, then README links V2.31 above older releases and the notes document scope, APK path, build command, metadata, signing, install guidance, validation, user acceptance test overview, non-goals, and risks.
- [ ] #4 Given the user acceptance overview is added, then it covers Settings metadata enrichment, own-catalog-first behavior, reviewed external provider proposals, optional provider configuration behavior, no-overwrite behavior, and imported-band linking regression checks.
- [ ] #5 Given the release is published, then git tag v2.31 and the GitHub release contain the signed app-release.apk asset.
- [ ] #6 Business requirements impact, README impact, diagram impact, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add V2.31 UAT overview documentation for reviewed band linking and metadata enrichment acceptance checks.
2. Bump Android metadata to versionCode 34 / versionName 2.31 and update default metadata-provider User-Agent version strings.
3. Create releases/v2.31.md and add README links for release notes and the UAT overview.
4. Verify release keystore, remove old release outputs, run full signed release validation/build, and verify APK signing/metadata/SHA.
5. Commit release metadata and docs, push branch, create/push v2.31 tag, publish GitHub release with the signed app-release.apk, and verify the release asset.
6. Close the release task with validation evidence and delivery-governance impact notes.
Architecture impact: release packaging and acceptance documentation only; no architecture-significant change.
<!-- SECTION:PLAN:END -->
