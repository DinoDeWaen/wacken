---
id: TASK-240
title: 'REF: Consolidate justified small presentation and adapter helpers'
status: To Do
assignee: []
created_date: '2026-10-20 06:26'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-239
references:
  - wacken
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
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
- [ ] #1 Characterization tests or stable output baselines protect each selected nontrivial helper before extraction; trivial constructor/validation boilerplate is retained with a documented reason.
- [ ] #2 Selected helpers preserve exact output, optional/missing handling, current table action restrictions and provider-specific throttling/trust semantics.
- [ ] #3 Domain remains independent of application/Android utilities; typed planning/after repositories and TSV schemas are not merged to reduce lint counts.
- [ ] #4 The remaining audit candidates are explicitly classified as consolidated, intentionally retained or covered by existing 3.0 tasks; no all-zero-duplication claim is made.
- [ ] #5 Relevant tests and Groma source ownership checks pass, with canonical documentation and architecture impact notes.
- [ ] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->
