---
id: TASK-228
title: >-
  DOC: Connect festival creation, CSV review and canonical band identity in
  Groma
status: To Do
assignee: []
created_date: '2026-10-20 06:25'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-227
references:
  - wacken
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
priority: high
type: docs
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want the festival import and creation paths connected so I can trace how a CSV becomes a shared festival without losing reusable band metadata.

## Scope
Curate the remaining responsibilities, ownership, descriptions, groups and relationships for add/create gateways and caches, canonical bands and aliases, band/CSV review, local TSV/Room import transaction, festival-scoped performance access, deletion and cache markers. Cover these Appendix A records; coordinate shared ownership with the rating-map task. Describe existing behaviour only.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F3 and F10; Appendix A creation/import/alias/deletion records. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Every disconnected Appendix A record in this scope is explicitly assigned to a truthful component or combined through the CLI with a responsibility-based explanation; no arbitrary arrow is added to hide an island.
- [ ] #2 Given reviewed group-wide creation, a flow traces screen, identity review, creation use case/gateway, backend result and cache update, including one-time per-member planning seed and empty after-ratings.
- [ ] #3 Given local five-file review or festival deletion, the map distinguishes local review/transaction behaviour from group-wide creation/deletion and documents preservation/failure outcomes.
- [ ] #4 Authored links cite actual source interactions; important port/adapter and callback wiring is reviewed from code rather than inferred solely from imports.
- [ ] #5 Ownership/source links and rendered scenarios are verified after two scans; README architecture links and diagram impacts are updated.
- [ ] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->
