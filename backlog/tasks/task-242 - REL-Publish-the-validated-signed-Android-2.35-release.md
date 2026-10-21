---
id: TASK-242
title: 'REL: Publish the validated signed Android 2.35 release'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:26'
updated_date: '2026-10-21 14:40'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-241
references:
  - wacken
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - scripts/tests/test-signed-upgrade-runner.py
  - scripts/verify-signed-upgrade.sh
  - app/src/androidTest/java/be/wacken/planner/SignedUpgradeInstrumentation.java
  - app/build.gradle
  - releases/v2.35.md
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/docs/release-process.md
priority: high
type: chore
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As an attendee, I want a verified signed 2.35 update so the reliability improvements reach my existing installation without losing data.

## Scope
Prepare final version metadata, release notes, fresh stable-key signed APK, checksum, supported in-place upgrade and GitHub publication after the 2.35 validation package is complete. Continue the approved signed GitHub APK distribution. No new testing-distribution service or 3.0 design scope.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: Delivery of approved audit remediation. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 The final APK has versionName 2.35 and a versionCode greater than the last published version, is built fresh from the release commit and verifies with the stable release certificate.
- [x] #2 Installing over the latest supported published stable APK retains login/cache, pending ratings, member-specific established ratings, festival planning/after values, history and corrections; upgrade evidence is tied to the exact candidate.
- [x] #3 Required unit/QA/Android compatibility/architecture and applicable database gates pass for the final source; no failed or skipped mandatory gate is represented as a pass.
- [x] #4 GitHub tag/release contain the verified signed APK, checksum, release notes, installation instructions, audit remediation summary and exact validation evidence.
- [x] #5 Published asset digest/signature/version and tag source are verified after upload; Backlog acceptance checks, notes and Done status are completed after publication.
- [x] #6 README and release documentation describe implemented changes, retained limits and rollback/upgrade risks without claiming all lint similarities are defects.
- [x] #7 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #8 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirmed published2.34/code42 and stable certificate. 2. Extended existing opt-in current-schema upgrade fixture with red/green runner tests; prepared2.35/code43 and release docs. 3. Built exact974ccd2 in clean checkout, passed all4 CI jobs and exact signed latest-stable install-r retained-data check. 4. Tagged exact source, published five assets, downloaded and verified all digests/signature/version/tag. 5. Finalized evidence and task closure before PR merge. Deviation: publication preceded final documentation-only closure so acceptance checks are verifiable before merge; no new architecture decision.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Published signed Android2.35/code43 at https://github.com/DinoDeWaen/wacken/releases/tag/v2.35 with APK, SHA256SUMS, release-validation.json, upgrade-evidence.zip and Groma report. Exact tagged/build source974ccd2eeaecd315d626e0a92880f0a09a5f48ea; clean checkout excludes unrelated user changes. APK SHA256 b8a7230ac6831945b7ee8d81b07084c6b914267ff3cc0c6ea6848d0daffdff34. Stable certificate ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f.

## Acceptance criteria validation

AC1-2: Fresh assembleRelease --rerun-tasks and opt-in signed probe. Actual published2.34/code42 digest e03c6e956af13fd6155537d84e74e6368ae902bc2da90263f28652448b112c4a installed on isolated API36 emulator. seed-current PASS, pre-update negative control rejected old version, install-r43 succeeded, verify-current PASS. Retained synthetic auth/cache, festival metadata/identity, own/other established and after ratings, pending planning/schedule work, dated history, immutable correction fields/dates and normal offline startup.

AC3: https://github.com/DinoDeWaen/wacken/actions/runs/39613436121 all4 jobs green on head974ccd2, tested merge b0574933d20fbaa5833b1282c8c617a263d7bdd0 over84cafb6. JVM/coverage/BDD/lint, API23 and API36 each71 native tests, real disposable PostgreSQL/PostgREST creation/rating/archive/correction/replay gates,27 architecture fixtures and semantic repeatability,8 upgrade-runner safety tests,3 smoke-runner and6 image-acquisition tests passed. Existing16 environment-gated unit integration cases are not counted as passes; dedicated real gates provide applicable evidence.

AC4-6: Published and independently downloaded all assets. SHA256SUMS verifies every asset. Downloaded APK v1/v2 signature and stable certificate verified; aapt confirms package/version/min23/target36; remote tag points exactly974ccd2. Notes include maintenance scope, install/update/rollback and retained limits.

## How to test

### Automated tests

Use CI run above, README commands and releases/v2.35.md. Exact latest-stable retained-data phases and signatures are in published upgrade-evidence.zip; release-validation.json binds artifacts and source. Earlier integrated legacy Room9 upgrade and matrix: backlog/docs/release-2.35-validation.md.

### Manual validation

Reviewed published release and downloaded assets, checksums/signature/version/tag, opt-in fixture phases and offline MainActivity launch. Emulator setup attempts stopped before app testing are infrastructure failures, not passing tests; final successful run used a newly created separate data directory. No production user installation was wiped.

## TDD / BDD / approval-test evidence

Two new current-baseline runner tests failed before implementation then all8 passed. Existing legacy safeguards and characterization/native/BDD suites remain green. Fixture checks dated immutable corrections and separate members. No product behavior changed in this release-packaging task.

## Architecture impact

- Architecture-significant change: no new runtime boundary, persistence schema, dependency or test strategy; extended existing opt-in signed-upgrade protocol for latest supported Room12 baseline.
- Approval received: existing milestone/release authorization and signed GitHub distribution approval; ADR0016/0020/0021/0022 retained.
- ADR impact: none, because packaging and existing validation-contract coverage introduce no new significant decision.

## README impact

README impact: updated current2.35 release link and validation context.

## Business requirements impact

Business requirements impact: updated Read Me First implemented maintenance-release context; rating lifecycle/product scope unchanged.

## Diagram impact

Diagram impact: none, because final packaging changes no runtime collaboration; existing curated map and gate passed with95 components,294 owned sources,13 flows and0 unexplained islands.

## Commits / logical change list

974ccd2: code43/2.35 notes/docs and current-baseline retained-data fixture/runner; final closure commit is task evidence only. PR https://github.com/DinoDeWaen/wacken/pull/5. Tag remains exact pre-closure built source.

## Risks and follow-up

Two user-accepted stale Groma importer arrows and scanner limitations remain explicit.88 reviewed duplicate groups are not88 confirmed defects. ArchUnit assessment recommends complementary Java dependency rules, no new library installed. HTTP timeouts bound connect/inactive read, not total wall time. Lost historical signing key compatibility and Android downgrade limits retained. No3.0 UI or fresh production security audit claimed. Deviation: publish exact validated tag before task-evidence closure/PR merge so publication AC can be verified before Done and merge; tag never moves to a different APK source.
<!-- SECTION:NOTES:END -->
