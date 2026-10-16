---
id: TASK-196
title: 'REL: Publish V2.33.3 festival CSV hotfix'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-16 15:00'
updated_date: '2026-10-16 18:22'
labels:
  - release
  - android
milestone: m-1
dependencies:
  - TASK-195
modified_files:
  - app/build.gradle
  - releases/v2.33.3.md
  - README.md
priority: high
type: chore
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Publish a signed direct-install V2.33.3 APK on the V2.33.2 line containing the completed group-wide festival CSV fix (TASK-195). Preserve upgrade compatibility and record full provenance. Out of scope: Play Store distribution, key rotation, unrelated features.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the published V2.33.2 build, when V2.33.3 is installed with the same certificate, then Android accepts an in-place update without clearing app data.
- [x] #2 The signed release APK has versionCode 39, versionName 2.33.3, package be.wacken.planner, minSdk 23, verified v1/v2 signatures, and a recorded SHA-256.
- [x] #3 Full relevant tests and the release build pass; release notes explain the festival CSV fix, install guidance, validation, and known risks.
- [ ] #4 The v2.33.3 tag and GitHub release publish the verified signed app-release.apk; the remote asset digest and release URL are recorded.
- [x] #5 README, business requirements, diagram, and ADR impact use canonical delivery-governance wording; unrelated workspace changes are excluded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Treatment: standard release packaging. No release-only architecture change or dependency; TASK-195 already has approval and ADR evidence.
1. Based the clean codex/release-2-33-3 branch on v2.33.2 and cherry-picked only TASK-195. Resolved the README overlap while retaining V011 deletion behavior.
2. Set versionCode 39 and versionName 2.33.3, wrote release notes, and added the README link.
3. Verified the existing stable keystore, ran clean full unit/QA/debug compilation/release lint-vital/signed release build, verified signature and APK metadata, and tested in-place emulator upgrade from the published V2.33.2 APK.
4. Commit release metadata and evidence, push the branch and tag, publish the signed APK, and verify the GitHub asset digest.
Tests: JDK 21 Gradle clean release validation, apksigner, aapt, SHA-256, published-baseline emulator upgrade, git diff --check. No phone was attached for a CSV walkthrough.
README impact: updated release link. Business requirements impact: none, release packaging only. Diagram impact: none, no boundary change. ADR impact: none, TASK-195 updated ADR 0011.
Deviation: cherry-pick needed a README conflict resolution to retain both festival deletion and group-member creation documentation. Backlog CLI generated a config rewrite; it was excluded.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Prepared signed V2.33.3 from the published V2.33.2 tag plus the completed TASK-195 festival CSV fix.
- Version metadata is versionCode 39 and versionName 2.33.3. The release notes and README link are ready.
- The V012 Supabase policy was already applied and verified in TASK-195.

## Acceptance criteria validation

- AC1: The emulator accepted the published V2.33.2 APK and then V2.33.3 through adb install -r. firstInstallTime stayed 2026-10-15 22:23:46 and dataDir stayed /data/user/0/be.wacken.planner; V2.33.3 launched.
- AC2: apksigner verified v1=true, v2=true, one signer, certificate SHA-256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. aapt confirmed be.wacken.planner, versionCode 39, versionName 2.33.3, minSdk 23, targetSdk 36. APK SHA-256: 63db42b9d65ab5c9a03d8100b606b10cc5058509209fc15e891b81db03b28da9.
- AC3: Clean full release validation passed; notes cover scope, installation, validation and risks.
- AC4: publication pending.
- AC5: README, business requirements, diagram and ADR impacts below. Clean release worktree excluded other checkout changes and Backlog generated config rewrite.

## How to test

### Automated tests

- scripts/ensure-release-keystore.sh verified the existing stable keystore.
- With JDK 21, ANDROID_HOME and Keychain signing values: ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest qaTest :app:compileDebugJavaWithJavac :app:lintVitalRelease assembleRelease --no-daemon — passed.
- apksigner verify --verbose --print-certs, aapt dump badging, shasum -a 256, and git diff --check — passed.
- Published V2.33.2 download SHA-256 matched 2d846f269d5d209c1fb37515424e8804dbbae72bbf36dc79545555dcd8bb8175 before the emulator test.

### Manual validation

- Emulator upgrade from the published V2.33.2 APK completed with data directory and install time retained; app launched.
- No physical phone was attached for an authenticated group-wide CSV walkthrough.

## TDD / BDD / approval-test evidence

- Release metadata and packaging add no runtime behavior. TASK-195 contains red/green Android and RLS test evidence and the user's architecture approval.

## Architecture impact

- Architecture-significant release-only change: no.
- Approval received: TASK-195 approval covers the included RLS change.
- ADR impact: none, because TASK-195 already updated ADR 0011.

## README impact

README impact: updated the V2.33.3 release-notes link.

## Business requirements impact

Business requirements impact: none, because release packaging adds no business behavior; TASK-195 restored the already specified group-wide festival creation.

## Diagram impact

Diagram impact: none, because packaging changes no runtime or module boundary.

## Commits / logical change list

- 2631560 integrated TASK-195 onto v2.33.2, retaining V011 festival deletion.
- Release metadata and publication evidence to follow.

## Risks and follow-up

- Festival creation is a multi-request remote workflow. Failure after the initial insert can leave a partial festival requiring support cleanup.
- No physical-device CSV walkthrough was available. Emulator upgrade verifies installation and launch, not the authenticated CSV flow.
- Installations signed with the historical lost key cannot update in place. The stable certificate matches V2.33.2.
<!-- SECTION:NOTES:END -->
