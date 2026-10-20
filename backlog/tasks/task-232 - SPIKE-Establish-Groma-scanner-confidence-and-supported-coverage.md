---
id: TASK-232
title: 'SPIKE: Establish Groma scanner confidence and supported coverage'
status: To Do
assignee: []
created_date: '2026-10-20 06:25'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-226
references:
  - wacken
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
priority: medium
type: spike
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want analysis limitations visible so an incomplete call graph is not mistaken for proof that code is disconnected or unused.

## Scope
Investigate unresolved cross-module/generated/Android symbols, deferred callbacks and Java-only coverage using the installed scanner and representative fixtures. Evaluate supported configuration/tool options, including whether classpath-aware analysis is available. Recommend a minimal approach; no dependency upgrade, new scanner or runtime architecture change without approval.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: Scanner limitations; F2/F3/F5. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Reproduce representative ordinary-call, callback, Room/generated and cross-module cases; distinguish scanner diagnostics from actual Gradle compiler results.
- [ ] #2 Document which supported settings or tools improve analysis and which interactions still require authored evidence; do not promise a complete graph or zero unresolved references without proof.
- [ ] #3 Document SQL/RPC/RLS, manifest/resource and verification-script coverage gaps and the evidence required for curated relationships across them.
- [ ] #4 Compare minimal, standard and maximum options with repeatable commands, compatibility and maintenance costs; obtain explicit approval before any significant implementation.
- [ ] #5 README records the supported scan interpretation/troubleshooting, or uses canonical no-impact wording if this remains a decision report.
- [ ] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->
