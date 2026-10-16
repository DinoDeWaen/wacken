---
id: TASK-194
title: 'REL: Publish V2.33.2 festival cleanup hotfix'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-16 05:57'
updated_date: '2026-10-16 08:35'
labels: []
milestone: m-1
dependencies:
  - TASK-192
  - TASK-193
modified_files:
  - app/build.gradle
  - README.md
  - releases/v2.33.2.md
priority: high
type: chore
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Deliver the approved pending-rating discard and festival deletion work as a signed Android update over V2.33.1. Scope: versionCode 38, versionName 2.33.2, release notes, full validation, signed APK, tag and GitHub release. Do not publish until TASK-192 and TASK-193 are complete or explicitly accepted for release.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a V2.33.1 installation, when V2.33.2 is installed with the stable signing key, then Android accepts the update without clearing app data.
- [x] #2 The official release APK is signed, has versionCode 38 and versionName 2.33.2, and passes the required validation.
- [x] #3 Release notes and README document both changes, deletion consequences, installation, and known risks.
- [x] #4 The V2.33.2 tag and GitHub release contain the verified signed APK and validation evidence.
- [x] #5 Business requirements, ADR, and diagram impacts are recorded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed versionCode 38/versionName 2.33.2, release notes and README link. Built with the stable Keychain-backed key and JDK 21; verified signatures, metadata, hash, release lint-vital, and an in-place V2.33.1 upgrade. Pushed codex/release-2-33-2 and tag v2.33.2, published the signed APK, and verified GitHub asset digest. No deviations from release scope.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
- Published [Wacken Planner 2026 V2.33.2](https://github.com/DinoDeWaen/wacken/releases/tag/v2.33.2) with the signed app-release.apk.
- TASK-192 and TASK-193 are Done in Release 2.33. Release tag v2.33.2 points to 095583a on codex/release-2-33-2.
- APK: app/build/outputs/apk/release/app-release.apk, 4,647,030 bytes.
- SHA-256: 2d846f269d5d209c1fb37515424e8804dbbae72bbf36dc79545555dcd8bb8175. GitHub asset digest matches exactly.
- Package be.wacken.planner; versionCode 38; versionName 2.33.2; minSdk 23; targetSdk 36.
- Stable certificate SHA-256: ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f.

## Validation
- JDK 21 signed build passed: ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest qaTest :app:compileDebugJavaWithJavac assembleRelease.
- Full unit/QA suites and four connected Android database tests passed in TASK-193. Release lintVitalRelease passed.
- apksigner verified v1 and v2 signatures; aapt metadata and SHA-256 matched the release task.
- Installed the published V2.33.1 APK, then adb install -r accepted V2.33.2. firstInstallTime remained unchanged and the updated signed app launched successfully.
- Flyway V011 and rolled-back member/non-member cascade tests passed; production Wacken remains intact. Details are in TASK-193.
- GitHub API confirms a public non-draft, non-prerelease release with only the signed app-release.apk; uploaded digest matches local artifact.
- git diff --check passed. Android debug lint has 14 existing NewApi errors; release lint-vital passes.
- The affected physical phone was not attached. No production festival was deleted.

## Architecture impact
- No additional architecture decision in packaging; TASK-193 records user approval and ADR 0013.
- README impact: updated the newest release-note link; TASK-193 documents feature behavior and architecture.
- Business requirements impact: none, because TASK-192/TASK-193 already updated the affected rules and this task packages those completed changes.
- Diagram impact: none, because TASK-193 already updated the README C4 labels.
- ADR impact: none, because TASK-193 created ADR 0013 for the approved design.

## Risks
- Discard and festival deletion are irreversible after confirmation.
- Group devices should update to V2.33.2 because schedule locks now carry festival identity.
- The stable certificate supports in-place upgrades from stable-key V2.33/V2.33.1; historical missing-key installations still require the documented one-time reinstall.
<!-- SECTION:NOTES:END -->
