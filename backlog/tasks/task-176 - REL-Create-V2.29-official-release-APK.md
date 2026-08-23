---
id: task-176
title: 'REL: Create V2.29 official release APK'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-08-23 09:09'
updated_date: '2026-08-23 09:09'
labels:
  - release
  - apk
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: publish the latest workspace state as an official signed Android APK release so the current app version and import data are available from GitHub.\n\nScope: bump Android metadata from V2.28 to V2.29, include the Summer Breeze 2027 band import CSV in the release commit, create release notes, update README, run release validation, verify the signed APK, push the commit and tag, publish the GitHub release, and record validation evidence.\n\nOut of scope: Play Store distribution, new app behavior, Supabase schema changes, and schedule/performance data for Summer Breeze.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the latest releasable workspace state is packaged, when release validation runs, then domain, application, infrastructure, app unit tests, Android compile, debug build, and signed release build pass.
- [ ] #2 Given the V2.29 APK is built, when signing and metadata are inspected, then it is an official signed APK with package, versionCode, versionName, minSdk, targetSdk, and SHA-256 recorded.
- [ ] #3 Given the release is published, when GitHub is checked, then tag v2.29, release notes, and the signed APK asset are available.
- [ ] #4 Given release packaging is complete, then README links to V2.29 release notes and release notes document scope, APK path, validation, signing, installation guidance, known non-goals, and accepted risks.
- [ ] #5 Business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm latest published release and local Android version metadata; choose V2.29/versionCode 32.
2. Update release metadata files: app/build.gradle, README release link, and releases/v2.29.md.
3. Include the Summer Breeze 2027 band-only CSV data and SOURCE note in the release commit.
4. Verify CSV parse/row counts and run the full signed Android release validation.
5. Verify APK signing, badging metadata, SHA-256, and git diff hygiene.
6. Commit release metadata/data, push the branch, tag v2.29, push the tag, publish the GitHub release with the signed APK, and verify it.
7. Close task-176 with checked acceptance criteria and delivery-governance validation notes.

Design approach: minimal release packaging only; no app behavior or architecture changes.
Test strategy: full Gradle test/compile/debug/release build plus APK signing and metadata inspection.
Architecture impact: not architecture-significant; no ADR or approval needed.
README impact: update release-notes link.
Business requirements impact: none expected, because release packaging and data-file addition stay within the existing CSV import path.
Diagram impact: none expected.
Risks/assumptions: existing unrelated .idea/workspace.xml change remains untouched; release signing secrets are expected in the local keychain.
<!-- SECTION:PLAN:END -->
