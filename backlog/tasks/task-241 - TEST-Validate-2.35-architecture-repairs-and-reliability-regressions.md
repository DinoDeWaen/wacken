---
id: TASK-241
title: 'TEST: Validate 2.35 architecture repairs and reliability regressions'
status: To Do
assignee: []
created_date: '2026-10-20 06:26'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-233
  - TASK-232
  - TASK-234
  - TASK-235
  - TASK-236
  - TASK-237
  - TASK-238
  - TASK-239
  - TASK-240
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
As a maintainer, I want an evidence package proving 2.35 repairs the audit findings without regressing the released festival and rating workflows.

## Scope
Integrate the completed 2.35 changes and verify a clean-checkout map, two rescans, architecture/duplicate gates, targeted reliability tests and existing critical Android/database gates. Compare against every finding and recommendation in TASK-225. This task verifies fixes, not product redesign.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: Cross-cutting validation for F1-F10. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 A finding-to-task-to-test matrix covers F1-F10, scanner limitations and all 99 lint dispositions, explicitly distinguishing fixed, intentionally retained and any human-approved deferral.
- [ ] #2 A clean checkout reproduces the curated architecture with truthful containers, resolvable sources/flows/references, documented exceptions and passing positive/negative architecture gates.
- [ ] #3 Controlled stalled-network, error precedence, image resource/lifecycle, identity and rating-scope tests pass and retain pending edits, member ownership and offline cache.
- [ ] #4 Existing festival CSV creation, archive promotion, source-aware corrections, canonical unknown-link handling and export contracts pass relevant BDD/native/SQL validation on supported Android versions.
- [ ] #5 README/release documentation and impact notes are accurate; unresolved risks or failures prevent a passing validation package rather than being silently waived.
- [ ] #6 Evidence records exact source commit, commands, results and test coverage limits; any architecture-significant change has linked approval and ADR evidence.
- [ ] #7 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #8 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->
