---
id: TASK-215
title: 'ARCH: Adopt canonical band catalog and archive-promoted personal ratings'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 11:53'
updated_date: '2026-10-17 13:25'
labels:
  - architecture
  - release
  - approval
milestone: m-0
dependencies: []
modified_files:
  - backlog/docs/release-2.34-remaining-architecture-proposal.md
  - README.md
  - backlog/decisions/0016-band-catalog-and-archive-promoted-personal-ratings.md
  - backlog/decisions/0011-post-wacken-festival-rating-model.md
  - backlog/docs/business-requirements.md
  - backlog/decisions/0014-atomic-festival-creation.md
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the release owner, I need a concrete assessment of the remaining TASK-156, TASK-169 and TASK-172 boundary changes so that implementation can proceed with explicit architecture approval and verifiable preservation of ratings/history. Inspect current import/relink/archive persistence; document minimal, standard and maximum options, exact schema/API/transaction impacts, rollback/authorization/concurrency and test gates. No production implementation before explicit approval. Existing signed GitHub distribution and 3.0 design scope remain unchanged.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the remaining 2.34 stories, then current implementation gaps and the smallest complete persistence/API proposal are documented with alternatives and risks.
- [x] #2 Given architecture-significant changes are proposed, then explicit human approval is recorded before their implementation and accepted ADR handling is identified.
- [x] #3 The task records source inspection, validation plans and canonical README/business/diagram/ADR impact notes.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed: recorded explicit user approval; accepted ADR-0016; marked old rating/prefill semantics superseded; revised standard proposal, business requirements and README target labeling; created216–220vertical implementation/migration/validation stories; aligned remaining2.34and all3.0stories with the contract; verified dependency closure and documentation consistency; passed existing completion build/test/coverage/release-lint and fresh stable-signed APK checks. No runtime/schema implementation or deployment in this increment; implementation begins with216.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Recorded the user's explicit2026-10-17 approval of the clarified canonical band and archive-promotion architecture in accepted ADR-0016. Revised the prior standard proposal; marked conflicting ADR-0011 latest-event and ADR-0014 creator/history-prefill rules as superseded for the new target. No repeated approval is required for this accepted scope.

Created five high-priority2.34 stories:
- TASK-216: personal current-band/festival-after isolation, persistence/sync and safe legacy migration.
- TASK-217: canonical linking and each member's one-time planning carry-over.
- TASK-218: atomic group-wide archive plus final nonempty after-rating promotion.
- TASK-219: correctly scoped detail/history/export.
- TASK-220: integrated lifecycle, correction, pending-data and signed-upgrade validation.

Aligned TASK-156/169/172/214 acceptance criteria and dependencies; added the governing contract to all ten3.0 stories, with explicit safeguards in204/208/211/212. Updated business rules and README target/implementation distinction. Canonical metadata is shared; ratings remain personal. Missing after-ratings retain the prior band rating; older corrections cannot overwrite a newer source or existing planning snapshots.

## Acceptance criteria validation

1: Current gaps, alternatives, bounded persistence/API direction and lifecycle are documented in the revised proposal and ADR-0016.
2: User explicitly instructed "Indeed please use this architecture for all stories and add tasks if needed for this" after the lifecycle explanation; approval is accepted and recorded. Implementation stories remain To Do.
3: Source assessment, test/migration/rollout gates, dependencies and canonical impacts recorded. Superseded requirements are clearly distinguished from current runtime.

## How to test

### Automated tests

Read-only dependency-closure validation passed for30related2.34/3.0 task records: no missing dependency targets or cycles. New216–220tasks are in milestone m-0 and have no premature implementation plans.
git diff --check passed.
Repository completion gate: existing Keychain/JDK21 ./gradlew :app:clean test qaTest :app:lintVitalRelease :app:assembleRelease passed, deleting previous APK output and creating a fresh signed task-validation artifact. Coverage/unit/QA and release lint-vital passed. The ten existing API-fixture scenarios skip in the ordinary app unit suite; final exact-source API/native validation remains required by TASK-220/214.
apksigner verified the stable signer certificate SHA-256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f.
APK SHA-256:654e1511ed31aa2ec2e6d50acb03f0dd556fdb8b69f1af4b2388cf2494a82cfd. This remains baseline2.33.5/code41; no2.34release was published.
No runtime/schema code changed. The new architecture's behavior is not claimed tested or implemented by this planning build.

### Manual validation

Checked the canonical4 → planning4 → planning5/after3 → archive band3 → next planning3 example against ADR, normative business rules, story ACs and release gate. Reviewed missing/reset outcomes, multiple users, old-source corrections, history, old-client compatibility and pending-data requirements.

## TDD / BDD / approval-test evidence

Planning/architecture increment only. Added business-readable Given/When/Then criteria and test expectations to new and affected stories; implementation must use BDD/TDD, characterization before legacy refactoring, real database/RLS and native/migration/upgrade tests. No redundant documentation unit tests added.

## Architecture impact

Architecture-significant change: approved canonical rating roles, explicit provenance and archive-promotion transaction, followed by additive persistence/API/sync work in implementation stories.
Approval received: direct user approval2026-10-17 after explicit lifecycle explanation.
ADR impact: created0016-band-catalog-and-archive-promoted-personal-ratings.md; updated0011and0014with supersession notices. Existing deployed runtime has not changed.

## README impact

README impact: updated approved2.34target and ADR link while labeling it not yet implemented.

## Business requirements impact

Business requirements impact: updated workflows10/11, affected existing rating/archive/prefill rules, new BR-113–117 and terminology to the user-approved lifecycle. Implemented-capability notes remain explicitly earlier-runtime context.

## Diagram impact

Diagram impact: none, because system/container deployment boundaries are unchanged and no existing diagram was modified; concept ownership and transitions are explicit in the ADR table.

## Commits / logical change list

Accepted ADR/supersession; revised proposal and requirements; five vertical stories; existing2.34/3.0 acceptance/dependency alignment; validation package.

## Risks and follow-up

TASK-216 must inventory legacy provenance and preserve ambiguous records without inventing dates or promoting active outcomes. New archive/correction authorization must handle all affected members without exposing private history and must prevent old clients bypassing the invariant. No physical-device lifecycle/migration test is claimed before implementation. TASK-216is the first implementation step; final2.34delivery remains TASK-214. Signed GitHub distribution stays selected and3.0themes remain separate.
<!-- SECTION:NOTES:END -->
