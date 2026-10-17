---
id: TASK-196
title: 'REL: Publish V2.33.3 festival CSV hotfix'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-16 15:00'
updated_date: '2026-10-16 18:23'
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
- [x] #4 The v2.33.3 tag and GitHub release publish the verified signed app-release.apk; the remote asset digest and release URL are recorded.
- [x] #5 README, business requirements, diagram, and ADR impact use canonical delivery-governance wording; unrelated workspace changes are excluded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Treatment: standard signed release packaging; no release-only architecture change. TASK-195 already had approval and updated ADR 0011.
1. Based a clean codex/release-2-33-3 branch on v2.33.2, cherry-picked TASK-195, and resolved the README overlap while retaining V011 festival deletion behavior.
2. Set versionCode 39/versionName 2.33.3, wrote release notes with APK digest, and added the README link.
3. Verified the stable keystore; ran clean full unit/QA/debug compilation/release lint-vital/signed build; checked APK signature, metadata, SHA-256, and an in-place emulator upgrade from the published V2.33.2 APK.
4. Pushed branch and tag, published the signed APK, and verified GitHub asset digest equals the local digest.
Tests: JDK 21 Gradle validation, apksigner, aapt, SHA-256, emulator upgrade and launch, git diff --check. No physical phone was attached for a CSV walkthrough.
Deviation: cherry-pick needed a README conflict resolution. Backlog CLI generated a config rewrite, excluded from release commits.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Published signed V2.33.3 from the V2.33.2 tag plus the completed TASK-195 festival CSV fix.
- Android metadata is versionCode 39 and versionName 2.33.3. The release includes the V012 policy source; that policy was already deployed and tested during TASK-195.
- Release: https://github.com/DinoDeWaen/wacken/releases/tag/v2.33.3
- Verified asset: https://github.com/DinoDeWaen/wacken/releases/download/v2.33.3/app-release.apk

## Acceptance criteria validation

- AC1: The emulator accepted the published V2.33.2 APK and then V2.33.3 through adb install -r. firstInstallTime stayed 2026-10-15 22:23:46 and dataDir stayed /data/user/0/be.wacken.planner; V2.33.3 launched.
- AC2: apksigner verified v1=true, v2=true and one signer. Certificate SHA-256: ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. aapt confirmed be.wacken.planner, versionCode 39, versionName 2.33.3, minSdk 23, targetSdk 36. APK SHA-256: 63db42b9d65ab5c9a03d8100b606b10cc5058509209fc15e891b81db03b28da9.
- AC3: clean full validation and release build passed; release notes cover scope, installation, tests, and risks.
- AC4: v2.33.3 tag was pushed at da0dc9731b1e319b4e7a5791557377b6bbcbdb1e. GitHub release is public, non-draft, non-prerelease, and has uploaded app-release.apk with the same SHA-256 digest as the local APK.
- AC5: clean release worktree excluded unrelated original-checkout changes and Backlog CLI generated config rewrite; impact notes are below.

## How to test

### Automated tests

- scripts/ensure-release-keystore.sh verified the existing stable keystore.
- With JDK 21, ANDROID_HOME and Keychain signing values: ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest qaTest :app:compileDebugJavaWithJavac :app:lintVitalRelease assembleRelease --no-daemon — passed.
- apksigner verify --verbose --print-certs, aapt dump badging, shasum -a 256, and git diff --check — passed.
- The published V2.33.2 APK download digest matched 2d846f269d5d209c1fb37515424e8804dbbae72bbf36dc79545555dcd8bb8175.
- gh release view verified the published asset name, uploaded state, URL, and matching server digest.

### Manual validation

- Emulator upgrade from published V2.33.2 retained the app data directory and original install time, and V2.33.3 launched.
- No physical phone was attached for an authenticated group-wide CSV walkthrough.

## TDD / BDD / approval-test evidence

- Release metadata and packaging add no new runtime behavior. TASK-195 contains red/green Android and RLS regression evidence and the user's architecture approval.

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
- da0dc97 prepared the signed release metadata, notes and task validation; tag v2.33.3 points to this commit. Final task closure is committed afterward.

## Risks and follow-up

- Festival creation uses several remote requests. Failure after the initial insert can leave a partial festival requiring support cleanup.
- No physical-device CSV walkthrough was available. Emulator upgrade verifies installation and launch, not the authenticated CSV flow.
- Installations signed with the historical lost key cannot update in place. The stable certificate matches V2.33.2.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Published Wacken Planner V2.33.3 at https://github.com/DinoDeWaen/wacken/releases/tag/v2.33.3 with verified signed app-release.apk (SHA-256 63db42b9d65ab5c9a03d8100b606b10cc5058509209fc15e891b81db03b28da9).
<!-- SECTION:FINAL_SUMMARY:END -->
