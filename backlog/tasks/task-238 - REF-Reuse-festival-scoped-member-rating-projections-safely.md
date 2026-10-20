---
id: TASK-238
title: 'REF: Reuse festival-scoped member-rating projections safely'
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
- [ ] #1 Characterization tests precede refactoring for list, detail, schedule and export, covering zero/veto/positive values, stable member ordering and canonical-name semantics.
- [ ] #2 Given conflicting values for the same band across two festivals and members, each consumer returns only its intended scope and never falls back to another festival or rating role.
- [ ] #3 Legacy nine-column CSV, lifecycle v2 and correction-audit export contracts remain unchanged; the shared projection does not own CSV formatting or archive-source promotion.
- [ ] #4 Regression and BDD scenarios demonstrate preserved output and correct read-only/archive behaviour; inward dependency direction and approval requirements are verified.
- [ ] #5 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #6 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->
