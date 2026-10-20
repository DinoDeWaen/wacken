---
id: TASK-227
title: 'DOC: Restore truthful runtime and data boundaries in the architecture map'
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
priority: high
type: docs
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a developer, I want runtime and storage boundaries visible so I can understand how the installed app and shared backend fit together.

## Scope
After the baseline is tracked, inspect actual runtime/storage ownership and curate the missing C4 container level using Groma. Explain Android process, local Room storage and owned data versus the external Supabase platform as supported by evidence and tool capabilities. Gradle modules and visual groups must not be treated as independent runtimes. No deployed architecture change.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F2. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the rendered map, a reader can identify the internal software system, Android runtime boundary and local/shared storage ownership without interpreting groups as containers.
- [ ] #2 Every existing component has truthful containment or an explicitly documented tool limitation; IDs, file ownership and useful authored meaning are preserved and no fake source file or boundary is invented.
- [ ] #3 Groma CLI constraints on described-component moves are investigated safely; blocked operations are documented with a supported remedy or explicit unresolved decision, without stripping meaning or directly editing Groma Markdown.
- [ ] #4 Two rescans and a fresh-checkout comparison preserve the approved structure, source links and existing flows; README and the affected architecture diagrams explain the boundaries.
- [ ] #5 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #6 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->
