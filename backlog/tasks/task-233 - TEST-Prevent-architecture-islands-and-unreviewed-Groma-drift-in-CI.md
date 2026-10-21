---
id: TASK-233
title: 'TEST: Prevent architecture islands and unreviewed Groma drift in CI'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-20 06:25'
updated_date: '2026-10-21 12:23'
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
  - backlog/docs/task-233-architecture-gate-proposal.md
modified_files:
  - backlog/docs/task-233-architecture-gate-proposal.md
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

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect current CI, exact Groma/scanner pin, curated map, duplicate dispositions and accepted TASK228 exception; prepare concrete minimal/standard/maximum architecture gate options. 2. Obtain explicit architecture/test-strategy approval required by AC6/7 before implementing executable gate or CI changes. Proposed standard: standalone pinned semantic validator, reviewed stable duplicate identities and exact scoped exceptions, positive/negative fixture tests, clean-checkout/two-scan repeatability and diagnostics. 3. After approval, implement through red/green tests; preserve human/browser semantic review, document local/CI usage and ADR, validate fresh signed APK and finalize evidence. No application runtime or production dependency change planned.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Investigation started after user-authorized TASK228 closure. TASK228 accepts exactly two stale authored deleted-importer rows; this is a proposed narrow gate exception, not blanket permission for dangling links. AC6/7 explicitly require architecture/test-strategy approval before implementation. Preparing a reviewable proposal; no validator or CI code changed.

Concrete standard proposal saved at backlog/docs/task-233-architecture-gate-proposal.md. It specifies a development-only semantic CI gate using existing exact pins, raw and resolved endpoint validation, stable duplicate identities, source/ownership/containment/flow/task-reference checks, disposable clean-checkout/two-scan repeatability and actionable diagnostics. Exactly the two user-accepted TASK228 stale rows are proposed scoped exceptions, with no deleted-file ownership or flow usage allowed. Positive/negative fixtures and human/source/browser review remain required. ADR0022 and implementation await explicit approval under AC6/7; no gate code, tests or CI mutations yet.
<!-- SECTION:NOTES:END -->
