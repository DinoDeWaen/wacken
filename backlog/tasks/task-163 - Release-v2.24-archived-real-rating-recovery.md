---
id: task-163
title: Release v2.24 archived real-rating recovery
status: In Progress
assignee:
  - '@codex'
created_date: '2026-08-15 08:18'
labels:
  - release
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Prepare and publish Wacken Planner v2.24 as a signed Android hotfix release for archived Wacken real-rating recovery.

Scope: bump Android version metadata to 2.24/versionCode 27, create release notes, update the README release link, run official validation, verify the signed release APK, commit release metadata, tag v2.24, push, and publish the GitHub release if credentials are available.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Full validation passes for domain, application, infrastructure, app unit tests, app Java compilation, and assembleRelease.
- [ ] #2 The official signed APK exists at app/build/outputs/apk/release/app-release.apk and apksigner verifies v1/v2 signatures.
- [ ] #3 APK metadata reports package be.wacken.planner, versionCode 27, versionName 2.24, and minSdkVersion 23.
- [ ] #4 Release notes for v2.24 exist and README links to them above v2.23.
- [ ] #5 Git tag v2.24 is created and pushed, and a GitHub release is published with app-release.apk when GitHub credentials are available.
- [ ] #6 README impact, business requirements impact, diagram impact, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Bump app/build.gradle versionCode to 27 and versionName to 2.24.
2. Add releases/v2.24.md with scope, APK path, signing, validation, install guidance, non-goals, risks, and SHA-256 after build.
3. Add the v2.24 release notes link to README.md.
4. Run official release validation with signing configuration and verify APK signing, metadata, and SHA-256.
5. Commit release metadata, tag v2.24, push branch/tag, publish GitHub release when available.
6. Close the release task with validation evidence.
<!-- SECTION:PLAN:END -->
