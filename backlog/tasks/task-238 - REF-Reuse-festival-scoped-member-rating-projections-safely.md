---
id: TASK-238
title: 'REF: Reuse festival-scoped member-rating projections safely'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:26'
updated_date: '2026-10-21 06:15'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies: []
references:
  - wacken
  - memberratingprojection
  - listactivefestivalbandsusecase
  - generatesharedscheduleusecase
  - exportratingscsvusecase
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - >-
    application/src/test/java/be/wacken/planner/application/MemberRatingProjectionCharacterizationTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/MemberRatingProjection.java
  - >-
    application/src/main/java/be/wacken/planner/application/ListBandsUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ListArchivedFestivalBandsUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ExportRatingsCsvUseCase.java
  - groma/systems/wacken/components/memberratingprojection.md
  - groma/systems/wacken/components/listactivefestivalbandsusecase.md
  - groma/systems/wacken/components/generatesharedscheduleusecase.md
  - groma/systems/wacken/components/exportratingscsvusecase.md
priority: medium
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want rating projections to share only their common rules so list, detail, schedule and export cannot drift across festivals or members.

## Scope
Characterize canonical-name matching, positive-rating filtering and member ordering, then factor a small application-owned scoped projection where justified. Keep archive query selection explicit, CSV formatting separate and planning/after/established roles distinct. Do not change the domain or ports solely to reduce duplication.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F9; lint group 85. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Characterization tests precede refactoring for list, detail, schedule and export, covering zero/veto/positive values, stable member ordering and canonical-name semantics.
- [x] #2 Given conflicting values for the same band across two festivals and members, each consumer returns only its intended scope and never falls back to another festival or rating role.
- [x] #3 Legacy nine-column CSV, lifecycle v2 and correction-audit export contracts remain unchanged; the shared projection does not own CSV formatting or archive-source promotion.
- [x] #4 Regression and BDD scenarios demonstrate preserved output and correct read-only/archive behaviour; inward dependency direction and approval requirements are verified.
- [x] #5 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #6 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added three characterization scenarios and verified them before extraction: positive/zero/veto values, exact names, stable equal-case member ordering and conflicting values across two festivals in list/detail/schedule/CSV/archive. Full pre-change application/domain/BDD run passed coverage gates.
2. Extracted package-local read-only MemberRatingProjection; five consumers retain repository query selection, explicit archive festival scope, decision rules, rating roles and CSV formatting. No domain or port changes.
3. Full verification passed: application168/domain63/BDD3; Android JVM259 with 16 pre-existing disposable API skips; coverage and lint gates passed. Existing CSV9/lifecycle v2/correction exports and archive guards remain green.
4. Described/grouped the shared projection in Groma and authored five exact source collaborations via CLI; rescan preserved ownership. Built fresh signed APK after deleting prior artifact, verified signature/digest, recorded impacts and committed.
Minimal application implementation detail reuse; no architecture-significant boundary/module/domain-policy/port/schema/dependency or testing-strategy change; approval/ADR not required. Deviation: focused characterization initially triggered global coverage minimum because only three tests ran; full unfiltered suites passed before and after refactor, with no coverage waiver.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Five consumers reuse one read-only application projection for exact canonical band matching, positive values (including veto1) and stable case-insensitive member ordering. Archive callers keep explicit findByFestival selection; CSV formatting and established/after-rating roles remain separate.

## Acceptance criteria validation
1. Three new characterization scenarios passed before refactoring, covering list/detail/CSV zero/veto/positive values and stable Alice/alice ordering, schedule positive/zero/exact names, and all five consumers across conflicting festival/member values.
2. Two-festival fixtures prove first/next projections stay scoped without global findAll fallback or writes. Existing festival detail/read-only and independent after/established-role tests passed.
3. Legacy CSV9, lifecycle v2 and correction-audit export tests pass unchanged. Shared helper returns PersonRatingStars only, never formats CSV or promotes archives.
4. Full application/domain/BDD suites and archive scope/read-only checks passed. Helper remains package-local application code with inward domain imports; no new ports/policies.
5–6. Architecture assessment, finalized plan, validation and canonical impacts recorded.

## How to test
### Automated tests
JDK21 ./gradlew :application:test :domain:test qaTest :app:testDebugUnitTest :app:lintDebug passed. XML counts: application168/domain63/BDD3, no failures/errors/skips; Android JVM259, 243 passed and 16 skipped disposable AddFestivalPersistenceRegressionTest cases. Coverage gates passed (application70%, domain80% minimum). The focused three-test characterization run passed its assertions but failed aggregate coverage; full unfiltered suites subsequently passed before/after refactor. No coverage gate disabled. git diff --check passed. Groma rescan after source ownership/relationship updates preserved component IDs; known scanner limitations remain.
Required fresh release APK: previous artifact deleted, JDK21 assembleRelease --rerun-tasks succeeded (53 executed); apksigner v1/v2 verified. Digest recorded below. No version bump/publication.
### Manual validation
Reviewed all five production diffs: each retains its existing repository query, archive festival selection, independent CSV string join and rating/decision logic; helper only projects scoped planning stars. New tests verify read paths never call save. No native emulator or live database validation performed; release gates remain TASK-241.

## TDD / BDD / approval-test evidence
Three public-consumer characterization scenarios and full suites passed before extraction. After extraction identical outputs and BDD scenarios pass. Test matrix explicitly distinguishes case-sensitive canonical names from case-insensitive stable member ordering. This is behavior-preserving refactoring, so no intentional business change or new red feature scenario was needed.

## Architecture impact
- Architecture-significant change: no; internal application projection reuse, no changed domain/port/boundary/schema/dependency/test strategy.
- Approval received: not required.
- ADR: ADR impact: none, because existing architecture decisions and rule ownership are preserved.

## README impact
README impact: none, because public behavior, setup, commands and module/runtime boundaries are unchanged.

## Business requirements impact
Business requirements impact: none, because festival/member scope, rating roles and export behavior are preserved exactly.

## Diagram impact
Diagram impact: updated Groma with a described shared member projection and five source-supported consumer collaborations; repository query/CSV/domain responsibilities stay with existing components.

## Commits / logical change list
Pre-refactor characterization; package-local projection; five consumer delegations; Groma description/group/relationships; finalized validation record.

## Risks and follow-up
The helper receives the existing scoped repository result; it does not infer festival/group or fallback to other rating roles. Raw ID normalization and roster policy are unchanged. Sixteen disposable API tests and native supported-device release gates remain for TASK-241. Known Groma containment blocker remains TASK-227. No merge/tag/publication.

Fresh signed APK SHA-256 bb8a407d4e5842a313b291798fc1a097154ab2226df76791fbb34770193415fc at app/build/outputs/apk/release/app-release.apk. Existing stable key, v1/v2 verified; version2.34/code42, not published.
<!-- SECTION:NOTES:END -->
