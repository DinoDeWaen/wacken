---
id: TASK-233
title: 'TEST: Prevent architecture islands and unreviewed Groma drift in CI'
status: To Do
assignee: []
created_date: '2026-10-20 06:25'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-228
  - TASK-229
  - TASK-230
  - TASK-231
references:
  - wacken
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
priority: high
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want CI to reject unexplained architecture drift so incomplete documentation cannot repeatedly pass unnoticed.

## Scope
Add a reviewed, pinned architecture validation gate after map curation. Verify ownership, containment, meaningful documentation, referenced IDs, authored relationships, flows and repeatability. Preserve explicit justified exceptions for implementation values/ports and scanner limitations. Baseline the audit-reviewed duplicate groups by stable identities; do not demand zero duplication or blanket-ignore lint exit 1.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F5; missing map regression checks. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the curated baseline, two scans and clean-checkout validation pass without duplicate ownership, missing sources, dangling IDs/endpoints/flows or unexplained containment changes.
- [ ] #2 Given deliberately disconnected responsibility, removed owned source, lost authored relationship, broken task reference or recreated component fixtures, the gate fails with actionable diagnostics.
- [ ] #3 Known intentional duplicates and explicitly justified isolated implementation details are reviewable exceptions; new findings or unexplained islands fail without requiring fake arrows or merged domain responsibilities.
- [ ] #4 Scanner/tool versions are reproducible and diagnostic degradation is surfaced; scanner output warnings are not represented as application compiler errors.
- [ ] #5 Positive and negative tests cover semantic identity, legitimate file moves/combines and fixture restoration; manual/browser review remains required for meaning.
- [ ] #6 README and delivery guidance document local/CI commands, exception approval and PR evidence. Architecture/test-strategy approval is obtained before implementing this gate.
- [ ] #7 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #8 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->
