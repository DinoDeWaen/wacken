---
id: TASK-237
title: 'REF: Keep canonical band lookup consistent across remote rating adapters'
status: To Do
assignee: []
created_date: '2026-10-20 06:26'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies: []
references:
  - wacken
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
priority: medium
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want canonical identity lookups consistent so rating adapters cannot drift in how they address the same band.

## Scope
Characterize exact-name-to-ID and ID/name mapping in rating/history clients, then consolidate only the justified adapter-owned lookup responsibility. Retain distinct schedule-lock parsing and festival/member-scoped queries. No database schema, identity normalization or canonical-band merge change.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F9; lint groups 63/64. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Characterization tests capture existing exact-name/case/escaping behaviour, missing and malformed IDs, unknown bands and response mappings before refactoring.
- [ ] #2 Given the same canonical band, affected clients resolve the same backend identity while preserving user/group/festival predicates and pending-write behaviour.
- [ ] #3 Failure, account change and response errors do not create bands, guess IDs, merge aliases or apply a rating to a different identity.
- [ ] #4 Domain/application depend on existing inward-facing contracts; significant new ports or shared transport ownership require approval before coding.
- [ ] #5 Tests prove equivalence across affected clients; README/business requirements/diagram/ADR impacts are recorded.
- [ ] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->
