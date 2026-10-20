---
id: TASK-231
title: 'DOC: Resolve empty and obsolete Groma component records'
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
type: docs
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want source-free placeholders resolved so the architecture does not imply nonexistent implementation.

## Scope
Investigate pendingplanningratingrecovery and androidjavacompatibility, including task references and flows. Retain/rehome meaningful knowledge if supported; otherwise remove obsolete records through Groma. Do not merge the old recovery ID with the current use case by name alone.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F4. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Each empty record has a recorded evidence-based disposition: retained with a justified supported responsibility, or removed through the CLI when obsolete.
- [ ] #2 Removal or replacement preserves useful knowledge and updates all affected Backlog architecture references through the CLI; unresolved historical origin is recorded honestly.
- [ ] #3 Rescans do not recreate obsolete components and all task references, relationship endpoints and flow steps resolve after the cleanup.
- [ ] #4 README impact: none, because cleanup does not change runtime behaviour, unless the implemented map explanation requires a documented update.
- [ ] #5 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #6 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->
