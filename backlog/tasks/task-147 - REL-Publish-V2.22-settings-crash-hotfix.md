---
id: task-147
title: 'REL: Publish V2.22 settings crash hotfix'
status: Done
assignee:
  - '@codex'
created_date: '2026-07-06 13:42'
updated_date: '2026-07-06 13:45'
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
- [x] #1 Android version metadata is bumped to versionCode 25 and versionName 2.22
- [x] #2 Full release validation passes, including domain/application/infrastructure/app tests, Android compile, debug build, and signed release build
- [x] #3 Signed release APK metadata and SHA-256 are recorded
- [x] #4 GitHub release v2.22 is published with release notes and app-release.apk asset
- [x] #5 README links to V2.22 release notes and impact notes are recorded
- [x] #6 Backlog task records validation evidence and Settings crash hotfix scope
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirmed fixed Settings crash commit `af34a23` is present and current release metadata was V2.21/versionCode 24.
2. Bumped Android metadata to versionCode 25 and versionName 2.22.
3. Added releases/v2.22.md with hotfix scope, APK metadata, signing/install guidance, validation commands, and verified checksum.
4. Added README link to V2.22 release notes above V2.21.
5. Verified release keystore, ran clean full release validation, inspected APK signing/badging, and recorded SHA-256.
6. Committed release metadata as `09fecc6`, pushed the branch, created/pushed tag v2.22, published GitHub release with signed APK, and verified uploaded asset digest.
7. Closed task with checked acceptance criteria and canonical README/business/diagram/ADR impact notes.

Deviation: local tag creation required escalated filesystem access because .git tag writes are restricted by the sandbox. Architecture impact: not architecture-significant; no product behavior, persistence, sync, dependency, schema, signing strategy, or module boundary changes beyond version/release metadata. Approval and ADR: not required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published the official V2.22 signed Android hotfix release for the Settings crash fixed in task-146. V2.21 was published before the fix, so V2.22 is the installable APK users should take.

Release URL: https://github.com/DinoDeWaen/wacken/releases/tag/v2.22

GitHub release verification: tag `v2.22`, title `Wacken Planner 2026 V2.22`, not draft, not prerelease, asset `app-release.apk` uploaded with GitHub digest `sha256:3c2eacd2aa4f5fe6f711f9ee689c07bd825418351c596c52805e108858f35662`.

## Acceptance criteria validation

- AC1: Android version metadata is `versionCode 25` and `versionName 2.22`.
- AC2: Full release validation passed, including domain, application, infrastructure, app unit tests, Android compile, debug build, and signed release build.
- AC3: APK metadata and SHA-256 are recorded below.
- AC4: GitHub release v2.22 is published with release notes and uploaded `app-release.apk`.
- AC5: README impact: updated README with the V2.22 release-notes link.
- AC6: This task records the Settings crash hotfix scope and validation evidence.

## How to test

### Automated tests

- `scripts/ensure-release-keystore.sh`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `shasum -a 256 app/build/outputs/apk/debug/app-debug.apk app/build/outputs/apk/release/app-release.apk`
- `git diff --check`
- `gh release view v2.22 --json tagName,name,url,assets,createdAt,publishedAt,isDraft,isPrerelease`

Debug APK SHA-256: `912a223aebdecf7ed59dd72d7fd8aef072a95b656418569df0887a9f5cac5565`
Release APK SHA-256: `3c2eacd2aa4f5fe6f711f9ee689c07bd825418351c596c52805e108858f35662`

APK signature verification result: verifies with v1 and v2 schemes; apksigner reported Android packaging META-INF warnings only.

APK badging metadata:

- package: `be.wacken.planner`
- versionCode: `25`
- versionName: `2.22`
- minSdkVersion: `23`
- targetSdkVersion: `36`

### Manual validation

Physical-device Settings smoke testing was not run in this environment. Manual check for V2.22: install the APK, sign in, open Settings from the overview cog, and confirm Group, Rating allocation, Sync, and Admin sections render, with `Export ratings CSV` visible.

## TDD / BDD / approval-test evidence

No new product behavior was implemented in this release task. The hotfix packages task-146, which added `SettingsActivityRegressionTest` for the initialization-order crash. Full automated release validation passed from the fixed state.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required; release packaging did not change architecture, module boundaries, dependencies, persistence, sync/storage boundaries, auth design, or signing strategy.

## README impact

README impact: updated README with the V2.22 release-notes link.

## Business requirements impact

Business requirements impact: none, because the hotfix only packages a defect fix that restores existing Settings behavior described by BR-062 and BR-077.

## Diagram impact

Diagram impact: none, because release packaging did not change architecture, module structure, dependencies, or runtime data flow.

## ADR impact

ADR impact: none, because release packaging did not introduce or change an architecture decision.

## Commits / logical change list

- `af34a23` Fix settings screen crash.
- `09fecc6` Prepare V2.22 hotfix release.
- Tag pushed: `v2.22`.
- GitHub release published: https://github.com/DinoDeWaen/wacken/releases/tag/v2.22

## Risks and follow-up

- Physical Settings smoke testing was not run from this workspace.
- Devices still using an APK signed before the stable V2.9+ release key must uninstall once, install V2.22, and sync from Supabase. Future releases signed with the stable key update normally.
<!-- SECTION:NOTES:END -->
