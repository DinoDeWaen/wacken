---
id: TASK-230
title: 'DOC: Connect verified music discovery and metadata evidence review in Groma'
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
As a maintainer, I want verified music discovery connected so I can distinguish trusted evidence from ranked metadata suggestions.

## Scope
Curate MusicLinksReviewActivity, lookup use case/port, evidence values, verified provider and official-page client with existing providers and external services. Show Settings festival and band-detail entry points. Keep optional credentials and verification trust boundaries explicit; do not implement a new provider.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F3 and F10; Appendix A music-link/evidence/provider records. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Every Appendix A music-link island is described and assigned to a justified owner, with source-supported incoming/outgoing interactions where it has an independent responsibility.
- [ ] #2 Given festival Settings or one band, a flow traces lookup, identity/source evidence review, explicit confirmation and canonical link save; unknown or unverified links stay empty.
- [ ] #3 General metadata ranking and verified link evidence remain distinct; optional provider unavailability, cancellation and account changes are documented without claiming ranking alone establishes identity.
- [ ] #4 Existing provider/external relationships remain truthful and no secret or credential value appears in map documentation.
- [ ] #5 Two scans and rendered source/flow inspection preserve curated knowledge; README architecture and diagram impacts are recorded.
- [ ] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->
