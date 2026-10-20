---
id: TASK-226
title: 'DOC: Preserve and version the reviewed Groma architecture baseline'
status: To Do
assignee: []
created_date: '2026-10-20 06:24'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies: []
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
As a maintainer, I want the existing architecture knowledge available in every checkout so it can be reviewed and cannot silently disappear.

## Scope
Review the existing untracked groma directory, preserve authored knowledge, commit the intended baseline and document pinned tooling, Java-only coverage and clean-checkout verification. Exclude unrelated user changes and generated exports. No structural curation or application refactoring in this task.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F1. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a clean checkout of the completed commit, the approved Groma project, scanner configuration, IDs, ownership, groups, descriptions, authored relationships and flows are available without copying local files.
- [ ] #2 Given two consecutive scans, owned files do not get duplicate components and reviewed authored meaning, groups and ownership survive; any tool-created difference is explained.
- [ ] #3 The baseline has an automated semantic inventory or equivalent reproducible verification and recorded fresh-worktree evidence; a negative control detects missing architecture files.
- [ ] #4 README documents architecture location, exact tool/scanner versions, scan/lint commands and the distinction between scanning and curation.
- [ ] #5 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #6 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->
