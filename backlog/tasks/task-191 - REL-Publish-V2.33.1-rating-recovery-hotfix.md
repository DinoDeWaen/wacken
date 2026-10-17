---
id: TASK-191
title: 'REL: Publish V2.33.1 rating recovery hotfix'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-15 19:58'
updated_date: '2026-10-15 20:27'
labels: []
dependencies:
  - TASK-190
modified_files:
  - app/build.gradle
  - releases/v2.33.1.md
  - README.md
priority: high
type: chore
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: deliver TASK-190 as an installable update that preserves the affected device database and lets the user recover the pending Wacken rating. Scope: version bump, signed APK validation, release notes, and GitHub release. Out of scope: changing the rating on the user device without review or clearing app storage.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given V2.33 is installed with a pending rating, when V2.33.1 is installed with the same signing key, then Android accepts the update without clearing app data.
- [x] #2 The official release APK is signed, has versionCode 37 and versionName 2.33.1, and passes the full validation suite.
- [x] #3 Release notes and README document recovery steps, installation, and known risks.
- [x] #4 The V2.33.1 tag and GitHub release contain the verified signed APK and validation evidence.
- [x] #5 Business requirements, ADR, and diagram impacts are recorded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Started from completed TASK-190 in a clean managed worktree and created a dedicated codex/release-2-33-1 branch.
2. Bumped Android versionCode 36 to 37 and versionName 2.33 to 2.33.1; added release notes and README link.
3. Verified the existing stable release key, ran the full JDK 21 test/QA/release build, verified v1/v2 signatures, package/version/minSdk, and SHA-256. Installed V2.33 then V2.33.1 with adb install -r on an emulator; original install time was retained.
4. Committed metadata, pushed branch and v2.33.1 tag, published the signed APK on GitHub, and verified the remote asset digest. No architecture boundary or deployment mechanism changed.

Deviation: used a clean worktree because the primary checkout contains unrelated user changes. The user phone remains unavailable for direct rating recovery verification.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published the official signed V2.33.1 hotfix at https://github.com/DinoDeWaen/wacken/releases/tag/v2.33.1 with `app-release.apk`. It includes TASK-189 archive repair and TASK-190 user-confirmed missing-festival rating recovery. The branch is `codex/release-2-33-1`; tag `v2.33.1` points to release commit `2208f8c`, which follows recovery commit `ddbe5d5`.

## Acceptance criteria validation

- Emulator accepted a V2.33 to V2.33.1 `adb install -r` update with the same signing certificate; firstInstallTime preceded lastUpdateTime.
- APK package `be.wacken.planner`, versionCode 37, versionName 2.33.1, minSdk 23. v1/v2 signing verified.
- Release notes and README link published. GitHub asset digest matches the local APK.

## How to test

### Automated tests

- PASS: `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest qaTest :app:compileDebugJavaWithJavac assembleRelease` with existing keychain signing values.
- PASS: `apksigner verify --verbose --print-certs`, `aapt dump badging`, and `git diff --check`.
- Local and GitHub SHA-256: `a2be471a439f440eb28eb1f2d9a1909768d2a32a2cf940c1230c3ad28cd2b6b8`.
- Signing certificate SHA-256: `ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f`, matching V2.33.
- Full Android `lintDebug` remains blocked by 14 existing `NewApi` errors; release lint-vital passed.

### Manual validation

- On local API 36 emulator, installed the V2.33 signed APK then installed V2.33.1 over it using `adb install -r`; both succeeded. Package version became 2.33.1, and original install time was retained.
- Affected phone is not attached; the actual pending rating has not been moved or synced. User must review band/stars/festival ID in the updated app and confirm the Wacken move there.

## TDD / BDD / approval-test evidence

- TASK-190 contains the red/green regression and recovery tests. Release criteria were validated by signed build inspection and emulator update.

## Architecture impact

- Architecture-significant change: none in this release packaging task. TASK-190 records the approved recovery architecture.
- Approval received: TASK-190 user approval on 2026-10-15.
- ADR: no new ADR; existing ADR 0011 was updated by TASK-190.

## README impact

- README impact: updated with V2.33.1 release-note link.

## Business requirements impact

- Business requirements impact: none, because BR-095a was added in TASK-190 and packaging adds no behavior.

## Diagram impact

- Diagram impact: none, because application and deployment boundaries did not change.

## Commits / logical change list

- `ddbe5d5` rating recovery; `2208f8c` version metadata and release notes. Branch and tag pushed; GitHub release asset verified.

## Risks and follow-up

- User must install over the current app without uninstalling or clearing app storage, then inspect and confirm the pending Wacken rating before archiving. The actual phone recovery remains unverified. Devices signed with the old lost certificate cannot update in place using the stable V2.33 signing key.
- The release branch is not merged to `main`. Existing 14 `NewApi` lint errors are outside this hotfix.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Published signed V2.33.1 rating recovery hotfix with verified APK digest and emulator upgrade validation.
<!-- SECTION:FINAL_SUMMARY:END -->
