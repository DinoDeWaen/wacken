---
id: TASK-241
title: 'TEST: Validate 2.35 architecture repairs and reliability regressions'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-20 06:26'
updated_date: '2026-10-21 13:41'
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
- [ ] #1 A finding-to-task-to-test matrix covers F1-F10, scanner limitations and all 99 lint dispositions, explicitly distinguishing fixed, intentionally retained and any human-approved deferral.
- [ ] #2 A clean checkout reproduces the curated architecture with truthful containers, resolvable sources/flows/references, documented exceptions and passing positive/negative architecture gates.
- [ ] #3 Controlled stalled-network, error precedence, image resource/lifecycle, identity and rating-scope tests pass and retain pending edits, member ownership and offline cache.
- [ ] #4 Existing festival CSV creation, archive promotion, source-aware corrections, canonical unknown-link handling and export contracts pass relevant BDD/native/SQL validation on supported Android versions.
- [ ] #5 README/release documentation and impact notes are accurate; unresolved risks or failures prevent a passing validation package rather than being silently waived.
- [ ] #6 Evidence records exact source commit, commands, results and test coverage limits; any architecture-significant change has linked approval and ADR evidence.
- [x] #7 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #8 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Integrate at source commit935ca2f after TASK233 and inspect audit F1-F10 plus all99 helper dispositions and scanner limitations. Minimal validation-only work introduces no runtime boundary, dependency, schema or test-strategy change; no additional architecture approval required. 2. Reproduce clean-checkout semantic gate/27 negative-positive tests and exact tool/scanner pins; retain precisely the user-approved TASK228 stale-row exception. 3. Run existing targeted reliability/regression/coverage/lint and disposable PostgreSQL/PostgREST creation/archive/correction/alias/deletion/isolation gates, existing Android smoke suite on supported API23/36 via local emulator or branch CI, and upgrade contracts where applicable. 4. Produce finding-to-task-to-test matrix with exact commits, commands, results and limits; update README validation link, build fresh stable-key APK and finalize validation package only when required gates pass. Use a draft PR for branch CI; no merge/publication before package complete.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Integrated validation in progress at935ca2f, draft PR5 attached. Local disposable creation and rating-state SQL/PostgREST gates pass with no skipped creation cases; Linux CI architecture and build/coverage/lint/database jobs pass. Native API23/36 jobs remain required. Older persisted local API36 image failed smoke preflight with missing phone service before application tests; no runner weakening. Fresh isolated emulator data is starting for signed upgrade regression. Report maps all F1-F10 and historical99/current88 duplicate dispositions with explicit TASK228/scanner/3.0 limits.

All four PR CI jobs passed at935ca2f/merge70dbece: Linux build/coverage/lint/database, architecture27 fixtures/two scans, API23 native71 and API36 native71 (zero skipped/failed each). Local disposable creation and rating/creation/archive/correction/deletion/alias/lifecycle SQL/API pass. Fresh isolated API36 signed Room9 regression passes published2.33.5/code41 → currentcode42 with old-schema negative control, preserved fixtures and offline startup. Initial persisted-emulator phone-service preflight failure is recorded explicitly, superseded by required successful equivalent CI native coverage and fresh-emulator signed probe; no runner weakened. Final TASK241 APK build/notes and TASK242 latest-stable final higher-code upgrade remain.
<!-- SECTION:NOTES:END -->
