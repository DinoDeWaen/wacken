---
id: TASK-240
title: 'REF: Consolidate justified small presentation and adapter helpers'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:26'
updated_date: '2026-10-21 08:04'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-239
references:
  - wacken
  - listactivefestivalbandsusecase
  - generatesharedscheduleusecase
  - banddetailactivity
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - >-
    application/src/test/java/be/wacken/planner/application/MemberRatingSummaryCharacterizationTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/PersonRatingStars.java
  - application/src/main/java/be/wacken/planner/application/BandDetailItem.java
  - application/src/main/java/be/wacken/planner/application/BandListItem.java
  - >-
    application/src/main/java/be/wacken/planner/application/ScheduleDecisionCandidate.java
  - app/src/main/java/be/wacken/planner/BandDetailActivity.java
  - app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java
  - app/src/main/java/be/wacken/planner/BandDetailPresentation.java
  - backlog/docs/groma-helper-dispositions-2026-10-21.md
  - README.md
priority: low
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want simple shared mechanics where they reduce maintenance so future UI and provider work does not repeat incidental fixes.

## Scope
After detail presentation is stable, review remaining low-risk candidates such as density/stars/table cells, Intent fallback, optional JSON strings, UTF-8 reading and private proposal expansion. Pick only helpers with a coherent existing-layer owner; record intentional residual duplication. Do not add frameworks, a global utility layer, DTO/use-case inheritance, cross-layer dependencies or 3.0 styles.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: Nice to have; Appendix B P3 groups. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Characterization tests or stable output baselines protect each selected nontrivial helper before extraction; trivial constructor/validation boilerplate is retained with a documented reason.
- [x] #2 Selected helpers preserve exact output, optional/missing handling, current table action restrictions and provider-specific throttling/trust semantics.
- [x] #3 Domain remains independent of application/Android utilities; typed planning/after repositories and TSV schemas are not merged to reduce lint counts.
- [x] #4 The remaining audit candidates are explicitly classified as consolidated, intentionally retained or covered by existing 3.0 tasks; no all-zero-duplication claim is made.
- [x] #5 Relevant tests and Groma source ownership checks pass, with canonical documentation and architecture impact notes.
- [x] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added two passing pre-extraction member-summary characterizations across three public DTOs; retained the 71-test native presentation baseline.
2. Reused PersonRatingStars summary formatting and existing WackenTheme.dp; removed unused Activity predicates and replaced empty-link method with MusicLinks.none.
3. Classified all original 99 groups with mixed exceptions, intentional repetition and existing 3.0 ownership; updated README lint interpretation.
4. Passed application/domain/BDD/coverage, Android unit/lint, full 71 native tests, source ownership, diff checks and fresh stable-key signed release build.
Minimal existing-owner refactor; no significant architecture change or ADR/approval required. Deviation: README needed correction because historical 99 groups differ from current 88; no product behavior change.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Shared member-star summary formatting in PersonRatingStars across three projections. Detail density conversion delegates existing WackenTheme.dp; removed unused Activity breakpoint predicates and misleading empty musicLinksFromIntent. Recorded dispositions for all 99 historical audit groups, explicitly preserving mixed-cluster exceptions, typed repositories/TSV and provider trust/throttle semantics.

## Acceptance criteria validation
1. Two public-consumer characterization tests passed before extraction (all three DTOs, empty/order/spacing/positive stars/privacy labels); prior 71-test native baseline protects detail presentation.
2–3. Exact formatting and density truncation preserved. No domain dependency, schema, table restriction, repository or provider-policy change.
4. Exhaustive original group IDs 1–99 verified once each in groma-helper-dispositions-2026-10-21.md; retained and existing 3.0 work explicitly identified.
5–7. Checks and source ownership pass; architecture assessed before coding, finalized plan and impacts below.

## How to test
### Automated tests
JDK 21: ./gradlew :application:test :domain:test qaTest :app:testDebugUnitTest :app:lintDebug — PASS. Application 170, domain 63, BDD 3; Android unit 264 (248 passed, 16 existing disposable-API skips). Coverage gates pass.
sh scripts/verify-android-smoke.sh emulator-5560 all — PASS, 71 native tests, zero failures/errors/skips, disposable API 36 AVD.
groma view for all seven changed production sources — existing owners retained: listactivefestivalbandsusecase, generatesharedscheduleusecase, banddetailactivity.
groma lint --count — 88 candidates, expected exit 1; no zero-duplication claim.
git diff --check — PASS.
Deleted prior local APK and rebuilt ./gradlew assembleRelease --rerun-tasks — PASS. apksigner verifies v1/v2 and established certificate SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f.
APK SHA256: 40412fc6d6008d98dfd80a7b4b2bf973968841ff485de107d6ac5b23a746b3c0. Intermediate artifact remains version 2.34/code 42; this is not release publication.

### Manual validation
Reviewed exact pre/post formatting/density expressions and all historical audit dispositions. Verified source ownership through Groma CLI. Native automation validates active/archive action availability, missing metadata/links, lifecycle/image and existing workflows; no claim of new manual device testing.

## TDD / BDD / approval-test evidence
Characterization first, extraction second, full suites green afterward. Existing three acceptance scenarios and 71 native tests remain green. Trivial constructor/validation boilerplate deliberately retained with reasons.

## Architecture impact
Architecture-significant change: no. Existing application value owner and existing Android theme helper only; no new abstraction/layer/framework or runtime boundary.
Approval received: not required for this minimal internal refactor.
ADR impact: none, because existing responsibilities and dependency boundaries remain intact.

## README impact
README impact: updated lint interpretation and link to all original helper dispositions; group ordering/counts are not stable identities.

## Business requirements impact
Business requirements impact: none, because output, workflow, identity, persistence and provider policy remain unchanged.

## Diagram impact
Diagram impact: none, because existing components retain source ownership and collaborations; CLI ownership checks pass.

## Commits / logical change list
One coherent TASK-240 commit: public-output characterization, small helper reuse and exhaustive disposition report.

## Risks and follow-up
16 API-dependent JVM tests remain skipped without disposable API credentials; real backend/API23/full signed-upgrade milestone evidence remains TASK-241. Existing 3.0 tasks own future visual changes. Groma container-curation limitation remains TASK-227. No merge or publication performed.
<!-- SECTION:NOTES:END -->
