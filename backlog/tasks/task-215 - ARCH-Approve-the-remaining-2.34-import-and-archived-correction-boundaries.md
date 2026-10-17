---
id: TASK-215
title: 'ARCH: Approve the remaining 2.34 import and archived-correction boundaries'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 11:53'
updated_date: '2026-10-17 12:31'
labels:
  - architecture
  - release
  - approval
milestone: m-0
dependencies: []
modified_files:
  - backlog/docs/release-2.34-remaining-architecture-proposal.md
  - README.md
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the release owner, I need a concrete assessment of the remaining TASK-156, TASK-169 and TASK-172 boundary changes so that implementation can proceed with explicit architecture approval and verifiable preservation of ratings/history. Inspect current import/relink/archive persistence; document minimal, standard and maximum options, exact schema/API/transaction impacts, rollback/authorization/concurrency and test gates. No production implementation before explicit approval. Existing signed GitHub distribution and 3.0 design scope remain unchanged.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the remaining 2.34 stories, then current implementation gaps and the smallest complete persistence/API proposal are documented with alternatives and risks.
- [ ] #2 Given architecture-significant changes are proposed, then explicit human approval is recorded before their implementation and accepted ADR handling is identified.
- [x] #3 The task records source inspection, validation plans and canonical README/business/diagram/ADR impact notes.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Inspect the current import/relink/cache/server and archived rating/export contracts. Identify gaps vs156/169/172; compare scoped alternatives and specify additive schema, ports, transaction/auth/concurrency invariants and tests. Write a reviewable approval proposal; request explicit user approval before coding affected stories. Keep completed features and direct GitHub distribution intact.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Prepared the concrete standard/minimal/maximum proposal in backlog/docs/release-2.34-remaining-architecture-proposal.md after inspecting application, Android, Room, existing Flyway/RLS/RPC and ADR contracts. Identified that TASK-179 does not complete156, current CSV import has no review169, and172 lacks correction audit. Proposed additive aliases/reviewed atomic creation, local import diff review and own archived-rating atomic audit. No implementation or production DB mutation. Await explicit human approval; AC2 remains unchecked. README impact: none, because this is an unimplemented proposal. Business requirements impact: none, because scope/rules are only proposed; affected rules are listed for approved implementation. Diagram impact: none, because the proposal does not change the current architecture. ADR impact: none, because acceptance is pending; planned decisions are identified in the proposal. Validation: source inspection and current official Supabase RLS/functions/changelog review; no runtime tests warranted for the proposal itself. Completed features already pass unit/QA/coverage,26native checks and fresh signed build.

README impact: updated the stale design-review banner to record the user's actual validation and the separate3.0 milestone; proposal remains unimplemented. Independent release preflight: existing scripts/verify-festival-creation.sh passed all10real PostgreSQL/PostgREST scenarios with zero failures/skips plus SQL rollback/authorization checks on current source. This is not final2.34 validation; rerun after remaining feature implementation.
<!-- SECTION:NOTES:END -->
