---
id: task-high.4
title: 'REL: Create V2.10 official release APK'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-12 14:24'
updated_date: '2026-06-12 14:34'
labels:
  - release
  - android
dependencies: []
parent_task_id: task-high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Release V2.10 as the next official non-debug Android APK after the completed schedule UI and walking-time refinements.

Scope:
- Bump Android versionCode to 13 and versionName to 2.10.
- Create V2.10 release notes and link them from README.
- Run full validation and build the signed release APK.
- Verify APK signature, metadata, and SHA-256.
- Commit release metadata, tag v2.10, push branch/tag, and publish the GitHub release with the signed APK asset.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Full release validation passes for domain, application, infrastructure, app unit tests, Android compile, and assembleRelease.
- [x] #2 The signed release APK is verified with apksigner and reports package be.wacken.planner, versionCode 13, and versionName 2.10.
- [x] #3 README links to the V2.10 release notes and releases/v2.10.md records scope, APK path, build command, metadata, signing, install guidance, validation, non-goals, and risks.
- [x] #4 Git tag v2.10 is created and pushed.
- [x] #5 GitHub release v2.10 is published with app-release.apk as the asset.
- [x] #6 Business requirements, diagram, and ADR impact are recorded using the canonical wording from delivery-governance.md.
- [x] #7 The release task records the release URL, APK SHA-256, validation commands, signing evidence, and residual risks before being marked Done.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Update app/build.gradle to versionCode 13 and versionName 2.10.
2. Create releases/v2.10.md using the standard release-note sections for the schedule axis, stage-column block, and walking-time refinements.
3. Add the V2.10 release-note link to README above V2.9.
4. Run full release validation with signing variables from the macOS keychain and build app/build/outputs/apk/release/app-release.apk.
5. Verify APK signature, package metadata, min/target SDK, versionCode/versionName, and SHA-256.
6. Commit release metadata, push the branch, tag v2.10, push the tag, and publish the GitHub release asset.
7. Close the release task with validation evidence, URL, checksum, and impact notes.
Architecture impact: not architecture-significant; release packaging only. No ADR expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published Wacken Planner 2026 V2.10 as the next official non-debug Android APK release. The release includes the post-V2.9 schedule readability changes: full-width schedule grid lines, full-hour-only axis labels, simplified stage-column blocks, smaller lost-alternative text, and corrected nearby-stage walking groups.

## Acceptance criteria validation

- AC1: Full release validation passed with domain, application, infrastructure, app unit tests, Android compile, and assembleRelease.
- AC2: The signed APK was verified with apksigner and aapt; package be.wacken.planner, versionCode 13, versionName 2.10, minSdk 23, targetSdk 36.
- AC3: README links to releases/v2.10.md; V2.10 release notes include scope, APK path, build command, metadata, signing, install guidance, validation, non-goals, and risks.
- AC4: Git tag v2.10 was created and pushed.
- AC5: GitHub release v2.10 was published with app-release.apk as the asset.
- AC6: Impact notes recorded below.
- AC7: Release URL, APK SHA-256, validation commands, signing evidence, and risks are recorded here.

## How to test

### Automated tests

- STORE_FILE=$(security find-generic-password -w -s WACKEN_RELEASE_STORE_FILE 2>/dev/null) && STORE_PASSWORD=$(security find-generic-password -w -s WACKEN_RELEASE_STORE_PASSWORD 2>/dev/null) && KEY_ALIAS=$(security find-generic-password -w -s WACKEN_RELEASE_KEY_ALIAS 2>/dev/null) && KEY_PASSWORD=$(security find-generic-password -w -s WACKEN_RELEASE_KEY_PASSWORD 2>/dev/null) && JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE="$STORE_FILE" WACKEN_RELEASE_STORE_PASSWORD="$STORE_PASSWORD" WACKEN_RELEASE_KEY_ALIAS="$KEY_ALIAS" WACKEN_RELEASE_KEY_PASSWORD="$KEY_PASSWORD" ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease
- git diff --check

### Manual validation

- APK path: app/build/outputs/apk/release/app-release.apk.
- APK SHA-256: 06020d5be0002f2aa9abd3825333a9c3ac889253128833e3f73d1269715139d8.
- apksigner verified: v1=true, v2=true, signer certificate SHA-256 e0f384db6cf96c4c5e4924dc03384f8c7a8130ad2af3772a0ecf36c5a4af50f9.
- aapt metadata: package be.wacken.planner, versionCode 13, versionName 2.10, minSdk 23, targetSdk 36.
- GitHub release verified: https://github.com/DinoDeWaen/wacken/releases/tag/v2.10.
- Release asset verified: app-release.apk.
- Tag v2.10 points to commit 32c7b53177ee60c3c5a33cc65a6b95da93deeeff.

## TDD / BDD / approval-test evidence

Release task only. The underlying user-visible changes were already covered by task-97 and task-high.3 tests before release packaging. This release reran the full validation suite and signed release build.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: updated the release-notes list with V2.10.

## Business requirements impact

Business requirements impact: none, because the released schedule behavior was already recorded in BR-064, BR-064a, BR-073, and BR-074 before this release task.

## Diagram impact

Diagram impact: none, because release packaging does not change architecture or workflows.

## Commits / logical change list

- Bumped Android versionCode to 13 and versionName to 2.10.
- Added releases/v2.10.md.
- Linked V2.10 release notes from README.
- Built and verified signed release APK.
- Pushed branch codex-task-2-testing-coverage.
- Created and pushed tag v2.10.
- Published GitHub release v2.10 with app-release.apk.

## Risks and follow-up

- Installed-device UAT still needs to be run on a connected Android device or emulator using the MVP2 UAT checklist.
- V2.10 uses the same local self-signed V2.9 release-key line, so it can install over V2.9 but not necessarily over V2.8/older/debug builds. Uninstall older builds first if Android rejects the update.
- This is direct-install APK distribution, not Play Store distribution.
<!-- SECTION:NOTES:END -->
