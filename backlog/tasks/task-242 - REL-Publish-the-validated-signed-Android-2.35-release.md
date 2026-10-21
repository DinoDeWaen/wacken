---
id: TASK-242
title: 'REL: Publish the validated signed Android 2.35 release'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-20 06:26'
updated_date: '2026-10-21 14:02'
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
- [ ] #1 The final APK has versionName 2.35 and a versionCode greater than the last published version, is built fresh from the release commit and verifies with the stable release certificate.
- [ ] #2 Installing over the latest supported published stable APK retains login/cache, pending ratings, member-specific established ratings, festival planning/after values, history and corrections; upgrade evidence is tied to the exact candidate.
- [ ] #3 Required unit/QA/Android compatibility/architecture and applicable database gates pass for the final source; no failed or skipped mandatory gate is represented as a pass.
- [ ] #4 GitHub tag/release contain the verified signed APK, checksum, release notes, installation instructions, audit remediation summary and exact validation evidence.
- [ ] #5 Published asset digest/signature/version and tag source are verified after upload; Backlog acceptance checks, notes and Done status are completed after publication.
- [ ] #6 README and release documentation describe implemented changes, retained limits and rollback/upgrade risks without claiming all lint similarities are defects.
- [x] #7 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #8 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirm latest published stable v2.34/code42, stable certificate and completed TASK241 package. Use a clean disposable release checkout so unrelated user working-tree changes are preserved and excluded. 2. Bump2.35/code43; update notes/README/current implemented release context. Extend the existing opt-in signed upgrade fixture/runner to seed Room12 latest-stable state, including own/other established and after ratings plus immutable corrections, with strict higher-code and before-update negative control. This is existing validation-contract coverage, not a new test strategy, runtime boundary, schema or dependency; no additional architecture approval required. 3. Run runner safety red/green, normal native/API23/API36/architecture/BDD/coverage/lint and disposable SQL gates on final source. Build fresh signed release and signed opt-in test probe in clean checkout using existing stable key. Verify exact latest-stable to candidate install-r preservation and normal offline startup on a separate fresh emulator. 4. Finalize evidence and draft PR, merge only after complete required validation, tag exact release source, publish APK/checksum/notes and download to verify digest/signature/version/tag. Complete Backlog only after publication. Existing approved GitHub APK distribution and ADR0016/0020/0021/0022 remain unchanged.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Latest stable confirmed v2.34/code42 with downloaded APK digest e03c6e956af13fd6155537d84e74e6368ae902bc2da90263f28652448b112c4a matching published SHA256SUMS. Prepared2.35/code43 and notes. Extended existing opt-in signed upgrade fixture for Room12 own/other established/after state and correction audit; current-baseline runner requires strict higher code and rejects old package version before install-r. New runner cases failed before implementation, then8 safety/integration fixture tests pass; Android test source compilation passes. Using a clean disposable checkout for final release build to preserve/exclude unrelated shared-worktree changes. No new runtime schema/boundary/dependency or validation platform.
<!-- SECTION:NOTES:END -->
