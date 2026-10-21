---
id: TASK-244
title: 'REL: Publish signed Android 2.35.1 with ArchUnit checks'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-21 15:14'
updated_date: '2026-10-21 16:25'
labels:
  - release
dependencies: []
modified_files:
  - app/build.gradle
  - releases/v2.35.1.md
  - README.md
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Publish the requested patch release following completed TASK-243. Retain signed GitHub APK distribution and runtime behavior; include ArchUnit development checks in the release source. Latest stable is2.35/code43, so use2.35.1/code44 and the same stable certificate. Build from a clean checkout preserving unrelated user changes. Business requirements impact: none planned because this patch packages a development-time architecture gate without product behavior changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Fresh signed APK uses versionName2.35.1/code44, package be.wacken.planner and the same stable certificate as published2.35.
- [x] #2 Required JVM/ArchUnit/QA/coverage/lint, Groma, real database/API and native API23/API36 release gates pass on final source.
- [x] #3 Exact published2.35 to final2.35.1 signed install-r retains fixture login/cache, pending operations, own/other ratings, planning/after values, history and corrections; negative control rejects the old version.
- [x] #4 Tag and GitHub release publish APK, checksum, notes, installation guidance and exact source/validation evidence; downloaded assets and remote tag are verified.
- [x] #5 README release links and notes describe actual scope and limits; final task evidence uses canonical README/business requirements/diagram/ADR impact wording and is complete before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirmed stable2.35/code43 and completed TASK243. Prepared2.35.1/code44 and release notes/README. 2. Built exact7aab9fa fresh in clean checkout using verified stable key, passed local full tests/QA and all4 final-source CI jobs. 3. Created a new disposable API36 AVD after setup failures; exact published43-to-final44 signed upgrade and old-version negative control passed with all retained-state checks. 4. Tagged exact source, published five assets, downloaded and verified all checksums/signature/version/tag. 5. Finalized validation and task closure before PR merge. No new architecture/business/diagram change. Deviation: publication precedes documentation-only closure so publication acceptance can be verified before Done; unrelated user work remains excluded.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published https://github.com/DinoDeWaen/wacken/releases/tag/v2.35.1 with official signed APK, SHA256SUMS, release-validation.json, upgrade-evidence.zip and Groma report. Exact tagged/build source7aab9fac90e639c7461e1a3564041f583e470e38; version2.35.1/code44. APK SHA256 26ae82deb30bd0f91aa4c0a7956db699e1755f26e5e3c27234cb986c6df561d7. Stable signer ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. This packages completed TASK243; no production dependency or behavior change.

## Acceptance criteria validation

AC1: Clean disposable checkout; existing keystore verified, no replacement key. Fresh ./gradlew test qaTest assembleRelease --rerun-tasks passed. Signed release and separate opt-in probe verified.
AC2: CI https://github.com/DinoDeWaen/wacken/actions/runs/39624372331 all4 jobs green, head7aab9fa, merge6b19c56688275cf8121f1c7a78b00b865da49074 overa71c1c5. JVM/ArchUnit/coverage/QA/lint, Groma27 fixtures and two scans, real disposable database/API creation and lifecycle/correction/replay/isolation gates, API23/API36 each71 native tests passed. Local domain63/application163/QA3/infrastructure29 including5 ArchUnit tests have zero failures/skips. Debug/release Android unit variants each264 with16 existing environment-gated integration skips; those are not counted as passes. Dedicated real integration gates passed without skipped mandatory cases.
AC3: Exact downloaded published2.35/code43 digest b8a7230ac6831945b7ee8d81b07084c6b914267ff3cc0c6ea6848d0daffdff34 installed on fresh disposable release244 API36 AVD. Seed PASS; before-update negative control rejected old code; install-r44 succeeded; verify-current PASS with retained synthetic auth/private cache, canonical metadata/festival identity, pending planning/history/schedule work, own/other established and after ratings, planning/after values, dated history, immutable correction fields/dates and normal offline MainActivity launch. Evidence tied to exact candidate digest. Earlier emulator setup attempts refused existing data or timed out before boot; these are infrastructure failures, not test passes. New AVD copied no application data and no user device was wiped.
AC4: All five published assets downloaded independently. Every SHA256SUMS entry passes. Downloaded APK signature v1/v2 verifies with stable certificate; aapt confirms package,2.35.1/code44/min23/target36. Remote tag points exactly7aab9fa. Notes include installation/update/rollback risks and exact evidence assets.
AC5: README and release notes reflect actual patch scope and test-tool limits; impacts and validation finalized here before merge.

## How to test

### Automated tests

Use commands in README and releases/v2.35.1.md; exact CI and asset/source binding are published release-validation.json. Run existing signed current-baseline probe only on a fresh isolated emulator; phase outputs/signatures/digests are in upgrade-evidence.zip.

### Manual validation

Reviewed source/build isolation, signed upgrade phases and offline launch, public release assets, downloaded checksums/signature/version and remote tag. Unrelated shared-worktree changes were preserved and excluded.

## TDD / BDD / approval-test evidence

Packaging adds no product behavior. TASK243 red/green ArchUnit negative/import/positive controls remain green; existing BDD/characterization/native suites passed. Signed upgrade uses an explicit failing old-version control and passing exact-candidate verification.

## Architecture impact

Architecture-significant change: none in release packaging; explicit user release request and existing signed GitHub distribution authorization apply. Existing ADR0023 test-tool adoption remains unchanged. ADR impact: none, because version metadata/publication introduce no new architecture decision.

## README impact

README impact: updated2.35.1 release-note link with scope, commands, validation and install guidance in notes.

## Business requirements impact

Business requirements impact: none, because the patch packages development-only architecture checks and changes no runtime capability, workflow, schema or business rule.

## Diagram impact

Diagram impact: none, because runtime collaborations and source ownership are unchanged; Groma gate passed95 components/294 sources/13 flows/88 reviewed duplicate groups with no unexplained islands.

## Commits / logical change list

7aab9fa:2.35.1/code44, release notes/README and task plan. Final closure commit contains task evidence only. PR https://github.com/DinoDeWaen/wacken/pull/7. Tag remains exact built/tested source before documentation-only closure.

## Risks and follow-up

ArchUnit does not import Android adapter bytecode or prove unused-code safety/within-layer cycles; no production dependency added. Two accepted stale Groma importer arrows and known scanner limits remain explicit. Android downgrade and lost historical key limits remain documented. No new database deployment or3.0 UI included. Publication preceded final task-evidence closure so all publication AC could be verified before Done/merge; release tag never moves to a different APK source.
<!-- SECTION:NOTES:END -->
