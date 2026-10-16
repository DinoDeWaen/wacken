---
id: TASK-198
title: 'REL: Publish V2.33.4 atomic festival creation hotfix'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-16 20:32'
updated_date: '2026-10-16 20:56'
labels:
  - release
  - android
dependencies:
  - TASK-197
modified_files:
  - app/build.gradle
  - README.md
  - releases/v2.33.4.md
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Publish the completed TASK-197 festival creation repair as an official signed Android update. Include complete CSV/API and Android regression validation, upgrade preservation evidence, release metadata and GitHub APK publication. No additional product behavior or key rotation.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given V2.33.3, the new APK is versionName 2.33.4 and versionCode 40, uses the stable signing certificate, and can update the previous release while retaining app data.
- [x] #2 Full unit/QA/coverage, real festival creation API gate, Android instrumentation, release lint-vital and fresh signed release build pass on the release source.
- [x] #3 APK signature, package, SDK metadata and SHA-256 are verified and recorded.
- [x] #4 The version tag and GitHub release are published with the verified signed app-release.apk and complete release notes.
- [x] #5 README links the release notes; business requirements, ADR and diagram impact use the canonical wording and the task contains the validation package.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed minimal release packaging from TASK-197 in codex/release-2-33-4.
1. Bumped app/build.gradle to 2.33.4/40, added release notes and README link (c3adaa6).
2. Verified the stable key. The API gate passed ten scenarios with zero skips plus SQL fault/permission assertions. Clean unit/QA/coverage, release lint-vital and signed build passed. Recovered the stalled single-CPU emulator with four CPUs; all ten local Android tests passed.
3. Verified certificate, signatures, metadata and digest. Updated the published 2.33.3 APK in place, retained install timestamp, data directory and external data marker, and launched successfully.
4. Tagged validated source c3adaa6 as v2.33.4, published the signed APK, and verified the downloaded asset byte for byte.

Deviation: the first combined build failed when the emulator went offline; successful separate build and Android runs replaced it. CI Android passed; two CI API attempts were blocked before tests by public ECR Data limit exceeded. The required API command passed locally on the release source.
Architecture impact: no new architecture, dependencies, product behavior, key rotation, ADR or diagram change. TASK-197 already documents the released behavior.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published the signed V2.33.4 (40) Android hotfix containing completed TASK-197. Add Festival now uses atomic authorized creation, independent band identities, safe retry and transactional Room publication. The existing 63-band Summer breeze repair and V013 deployment were completed in TASK-197.

## Acceptance criteria validation

1. APK package/version/SDK metadata and stable certificate verified; adb install -r succeeded from downloaded published V2.33.3 (digest 63db42b9d65ab5c9a03d8100b606b10cc5058509209fc15e891b81db03b28da9). Version39/2.33.3 became40/2.33.4; firstInstallTime remained2026-10-16 22:54:31 Europe/Brussels and dataDir remained /data/user/0/be.wacken.planner. The external app data marker task198-preserve-data survived. The updated MainActivity launched successfully to LoginActivity, as expected for this test device without a session. Internal pending-rating preservation is covered by the Android Room suite; the upgrade smoke did not use a real signed-in account.
2. Clean unit/QA/coverage, release lint-vital and fresh signed build passed. Ten real API scenarios passed with zero skips plus SQL failure/permission checks. All ten local Android tests passed, zero failures/errors/skips; clean retry build passed94 tasks (93 executed), Android passed69 tasks (45 executed). CI Android passed on c3adaa6 in https://github.com/DinoDeWaen/wacken/actions/runs/38964436027 . Both CI attempts at the API job were blocked before executing tests by public ECR image-download Data limit exceeded (exit125). The exact-source mandatory API gate passed locally, so no passing overall CI run is claimed.
3. Signed APK verifies v1/v2, certificate SHA-256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. Artifact SHA-256 d5fd009fc394a0b5aa73139dfd794c11f557d9632e135ec5ad6be4c32078deb4.
4. Published https://github.com/DinoDeWaen/wacken/releases/tag/v2.33.4 from c3adaa6bb2e43ebe84913d13cdc2437b433bb115 with app-release.apk. Downloaded published asset matches the verified digest.
5. README links releases/v2.33.4.md; release notes document scope, install/retry steps, build, signing, gates, non-goals and risks.

## How to test

### Automated tests

- JDK21/SDK36: scripts/verify-festival-creation.sh — ten HTTP scenarios, zero skips, disposable real PostgreSQL/PostgREST with migrations/RLS and transactional SQL assertions.
- Existing Keychain signing values, JDK21/SDK36: ./gradlew clean test qaTest :app:lintVitalRelease assembleRelease --no-daemon — passed.
- JDK21/SDK36: ./gradlew :app:connectedDebugAndroidTest --no-daemon — passed ten tests, zero skips/failures.
- apksigner verify --verbose --print-certs, aapt dump badging, shasum -a 256 and git diff --check passed.
- Official APK: /Users/dino/.codex/worktrees/fix-festival-creation/wacken/app/build/outputs/apk/release/app-release.apk. VersionName 2.33.4, versionCode 40, package be.wacken.planner, minSdk23, targetSdk36.
- The first combined local command stopped when the existing emulator went offline during package-manager recovery. This was recorded as a failed attempt, then the successful build and Android results above were obtained.

### Manual validation

Install the published APK over V2.33.3 and use Sync from Supabase to see recovered Summer breeze. Preserve pending edits; do not clear storage. The reported physical phone was not attached. Emulator upgrade/launch observations are described above.

## TDD / BDD / approval-test evidence

This task changes release metadata and documentation only. It reuses TASK-197's red/green behavior regressions: actual 63-band import, exact-name collisions, rollback, duplicate CSV names, permissions, replay, concurrency and Android UI/Room failure handling. No implementation-mirroring metadata unit test was added.

## Architecture impact

- Architecture-significant change: none in release packaging; delivers TASK-197's previously approved design.
- Approval received: user requested the new release; no new architecture approval needed.
- ADR impact: none, because ADR0014 already documents the shipped creation design and packaging adds no decision.

## README impact

README impact: updated the release-note index with V2.33.4.

## Business requirements impact

Business requirements impact: none, because this release packages completed TASK-197 behavior already documented in Workflow11 and BR087a/087b.

## Diagram impact

Diagram impact: none, because runtime boundaries and flows are unchanged from TASK-197's approved diagram.

## Commits / logical change list

- 7fd83c1: release task plan.
- c3adaa6: version40/2.33.4, release notes and README.
- Publication evidence committed separately after tagging.

## Risks and follow-up

No physical-phone file-picker walkthrough; emulator activity/worker/Room and real HTTP boundaries are separately tested. Full lintDebug retains the previously recorded NewApi errors; release lint-vital passed. Remote and local transactions are independent: identical retry confirms a complete remote result after a lost response or cache error. Exact-name variants intentionally remain distinct. No Play Store distribution, automatic fuzzy merge or key rotation. Current stable-key builds upgrade in place; installations signed by the lost older key require protecting local-only data before a one-time reinstall.

## Modified files

app/build.gradle, README.md, releases/v2.33.4.md
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Published V2.33.4/40: https://github.com/DinoDeWaen/wacken/releases/tag/v2.33.4 . Signed APK and downloaded asset match SHA-256 d5fd009fc394a0b5aa73139dfd794c11f557d9632e135ec5ad6be4c32078deb4. Clean build, ten API and ten Android tests passed; in-place upgrade preserved data marker and install identity. CI Android passed; API CI download was externally rate-limited twice, with local exact-source gate passed.
<!-- SECTION:FINAL_SUMMARY:END -->
