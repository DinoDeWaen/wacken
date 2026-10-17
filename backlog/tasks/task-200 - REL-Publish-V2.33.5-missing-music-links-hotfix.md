---
id: TASK-200
title: 'REL: Publish V2.33.5 missing music links hotfix'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 07:10'
updated_date: '2026-10-17 07:10'
labels:
  - release
  - android
dependencies:
  - TASK-199
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Deliver completed TASK-199 to phones as an official signed Android update. Unknown YouTube/Spotify links must remain empty after sync, and known links must be preserved. Package and validate only the completed fix; no unrelated behavior, schema or signing-key change.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 The signed APK is 2.33.5 with versionCode 41 and the stable certificate; it updates published V2.33.4 in place with app data retained.
- [ ] #2 Clean unit/QA/coverage, all 15 Android tests, the real festival creation API gate, release lint-vital and a fresh signed build pass on the release source.
- [ ] #3 APK package, SDK metadata, v1/v2 signature and SHA-256 are verified and recorded.
- [ ] #4 Git tag and GitHub release publish the verified signed app-release.apk with release notes, and the downloaded asset matches.
- [ ] #5 README links the new notes, canonical business requirements/ADR/diagram impact and complete validation evidence are recorded in the visible Backlog task.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Minimal release packaging from completed TASK-199. 1. Work in the existing isolated checkout on codex/release-2-33-5, bump app/build.gradle to 2.33.5/41, add releases/v2.33.5.md and README link. 2. Verify the stable keystore; run the real PostgreSQL/PostgREST gate followed by clean unit/QA/coverage, 15 Android tests, release lint-vital and signed release build. 3. Verify signatures/metadata/digest and update published V2.33.4 in place, preserving install identity and app data marker. 4. Commit, tag, publish, verify downloaded APK, and close the task with validation package. No architecture-significant change, schema, dependency or key rotation; no new ADR or diagram needed. Existing native red/green behavior regressions provide coverage; no metadata-only unit tests. README release index changes; business rules remain as documented by TASK-199.
<!-- SECTION:PLAN:END -->
