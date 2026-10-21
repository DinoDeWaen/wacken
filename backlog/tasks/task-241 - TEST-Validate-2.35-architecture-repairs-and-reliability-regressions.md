---
id: TASK-241
title: 'TEST: Validate 2.35 architecture repairs and reliability regressions'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:26'
updated_date: '2026-10-21 13:44'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-233
  - TASK-232
  - TASK-234
  - TASK-235
  - TASK-236
  - TASK-237
  - TASK-238
  - TASK-239
  - TASK-240
references:
  - wacken
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
  - backlog/docs/release-2.35-validation.md
modified_files:
  - backlog/docs/release-2.35-validation.md
  - README.md
priority: high
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want an evidence package proving 2.35 repairs the audit findings without regressing the released festival and rating workflows.

## Scope
Integrate the completed 2.35 changes and verify a clean-checkout map, two rescans, architecture/duplicate gates, targeted reliability tests and existing critical Android/database gates. Compare against every finding and recommendation in TASK-225. This task verifies fixes, not product redesign.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: Cross-cutting validation for F1-F10. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 A finding-to-task-to-test matrix covers F1-F10, scanner limitations and all 99 lint dispositions, explicitly distinguishing fixed, intentionally retained and any human-approved deferral.
- [x] #2 A clean checkout reproduces the curated architecture with truthful containers, resolvable sources/flows/references, documented exceptions and passing positive/negative architecture gates.
- [x] #3 Controlled stalled-network, error precedence, image resource/lifecycle, identity and rating-scope tests pass and retain pending edits, member ownership and offline cache.
- [x] #4 Existing festival CSV creation, archive promotion, source-aware corrections, canonical unknown-link handling and export contracts pass relevant BDD/native/SQL validation on supported Android versions.
- [x] #5 README/release documentation and impact notes are accurate; unresolved risks or failures prevent a passing validation package rather than being silently waived.
- [x] #6 Evidence records exact source commit, commands, results and test coverage limits; any architecture-significant change has linked approval and ADR evidence.
- [x] #7 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #8 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Integrated source935ca2f and linked all audit F1-F10 to completed repair tasks/tests, all99 historical/current88 duplicate dispositions, known scanner limitations, exact TASK228 accepted stale rows and approved3.0 visual deferrals. Validation-only task changed no app boundary/schema/dependency/test strategy. 2. Reproduced27-fixture architecture gate and clean-checkout two scans; Linux CI confirms95 components,294 sources,13 flows and88 reviewed identities. 3. Ran existing local disposable PostgreSQL/PostgREST creation and rating/archive/correction/deletion/alias/integrated lifecycle gates; all pass. GitHub PR5 CI run39608937492 passes all four jobs at head935ca2f/merge70dbece, including71 native tests each on API23/API36 with zero skips/failures. Existing JVM/BDD/coverage/lint and runner-safety checks pass. 4. Recorded initial persisted local-emulator phone preflight failure without weakening tests; used required successful CI native equivalents and a fresh isolated API36 emulator for signed Room9 published2.33.5/code41 to currentcode42 migration, with old-schema red control and retained-data/offline startup verification. 5. Added final evidence matrix/report and README link. Fresh53-task stable-key release rebuild from doc checkpointe9015a1 verifies, SHA8aac5e28a2de510a671427ce0bc4ab583a49925266c6868a279c247537d33174. Prior migration candidate exact digest/source are separately recorded. TASK242 retains final version, latest2.34-to-exact2.35 preservation and publication. No new ADR or additional architecture approval required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Completed integrated2.35 audit/remediation validation at source935ca2f, CI merge70dbece over base84cafb6. Report backlog/docs/release-2.35-validation.md maps every F1-F10 finding to repair and test evidence; all99 historical helper groups and88 current reviewed identities are accounted for. Draft PR https://github.com/DinoDeWaen/wacken/pull/5 is attached.

## Acceptance criteria validation

All eight criteria satisfied for the integrated package. Zero unexplained map islands, valid three-container hierarchy,294 unique existing sources and13 flows survive clean-checkout/two scans. Controlled transport/error/image/identity/scope contracts and existing creation/archive/correction/unknown-link/export/lifecycle behavior pass. Exactly two TASK228 stale rows and scanner limitations are explicit accepted limits, not silently waived findings. Larger approved3.0 visual work remains separately owned.

## How to test

### Automated tests

GitHub CI run39608937492: all four jobs success at935ca2f, including Linux build/unit/QA/coverage/lint/runner safety/database gates, pinned architecture gate and API23/API36 native71 tests each with0 failures/0 skipped. Local architecture27 tests pass; gate95 components/294 sources/13 flows/88 reviewed identities and two create0/refreshed95 scans. Existing local domain63/application163/infrastructure24/BDD3 pass; Android264 has248 passes and16 pre-existing integration-environment skips, while the dedicated real disposable database/API gate executes creation cases without skips. Local verify-festival-creation.sh and verify-rating-state.sh pass, including full migration/isolation/reset/replay/RLS/rollback/archive/source-aware correction/audit/deletion/alias/integrated lifecycle. Runner safety:3 smoke,6 signed-upgrade,6 test-image tests pass.

### Manual validation

Existing source/browser/user map review preserved by semantic baseline. Downloaded successful CI logs confirm exact native counts and no mandatory native skips. Signed isolated API36 regression: real published2.33.5/code41/Room9 baseline, seeded fixture, before-update red control, install-r candidatecode42, full preservation/offline-startup assertions pass. Probe source unchanged fromv2.34. Exact migration candidate is TASK233 buildd7215cb SHA24fef4103c6d64e2eaff5a859c78277b540684520abacd0777e032525c691875. Final task artifact separately rebuilt from doc checkpointe9015a1 with53 rerun tasks, stable v1/v2 signature and SHA8aac5e28a2de510a671427ce0bc4ab583a49925266c6868a279c247537d33174. Application/map code is identical across those documentation-only commits; no claim that the prior migration probe tested a different final binary. No production data/account touched.

## TDD / BDD / approval-test evidence

No new application behavior. Reused completed task characterization/TDD and BDD regressions plus27 positive/negative architecture fixtures. Signed migration retains its real old-schema negative control before passing on update. No mandatory failed/skipped application gate represented as a pass.

## Architecture impact

Architecture-significant change: none in validation. Prior approved changes link ADR0020/0021/0022; ADR0016 behavior remains unchanged. Approval received: milestone validation requested; exact TASK228 limitation and curated map accepted by user. ADR impact: none, because no new design or test strategy was introduced here.

## README impact

README impact: updated integrated validation link and exact coverage limits.

## Business requirements impact

Business requirements impact: none, because validation confirms existing product scope and rating lifecycle without changing behavior.

## Diagram impact

Diagram impact: none, because current reviewed CLI-authored map and Mermaid boundaries are unchanged and validated.

## Commits / logical change list

e9015a1 records finding/test matrix, integrated results and README link; final notes/report add artifact/source identities and close validation.

## Risks and follow-up

Older persisted local API36 image initially lacked phone service and smoke runner stopped before app tests. This is recorded, not passed; required equivalent API23/API36 CI tests pass and fresh local signed migration passes without runner weakening. Scanner Java-only/known diagnostics and two accepted stale arrows remain explicit limits. No fresh production permission audit claimed; TASK224 is separate. TASK242 must bump version and validate latest published2.34 to exact higher-code2.35 candidate, including correction audit, before publishing verified assets. PR remains draft/unmerged; no publication.
<!-- SECTION:NOTES:END -->
