---
id: TASK-229
title: 'DOC: Connect festival rating, archive, correction and sync lifecycles in Groma'
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
As a maintainer, I want rating lifecycles connected so I can verify which values belong to a band, a member and a festival.

## Scope
Curate the remaining rating/context models and ports, Room entities/DAOs/adapters, rating state sync, after-rating recording, archive gateway/cache, correction/review/audit, pending recovery and rating exports. Account for shared composition and cache wiring with the festival-map task. Do not change ADR-0016.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F3 and F10; Appendix A rating/context/archive/sync/export records. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Every Appendix A rating/archive/correction/sync/export island receives a responsibility, meaningful ownership and supported relationships or is combined with a justified owner; source-free placeholders are handled by the separate cleanup task.
- [ ] #2 Flows trace festival-bound planning and after-rating edits, durable reset and offline/pending preservation, with established rating updates only through the existing archive/correction rules.
- [ ] #3 Archive and source-aware correction flows distinguish missing versus reset after-ratings, newer-source protection, stale review and member ownership; original event dates and correction times remain distinct.
- [ ] #4 Context and legacy/lifecycle/audit exports are described as separate contracts; map prose does not suggest cross-festival fallback or latest-event promotion.
- [ ] #5 Two scans preserve IDs, groups, ownership and flows; rendered source links and affected README architecture documentation are verified.
- [ ] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->
