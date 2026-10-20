---
id: TASK-235
title: 'BUG: Make band image loading bounded and lifecycle safe'
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
type: bug
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As an attendee, I want band images to load safely when I navigate or rotate so stale requests do not affect a different screen or retain resources.

## Scope
Characterize active/archive image success and failure behaviour; give duplicated loading a justified Android-side owner with stream closure, bounded waits and lifecycle/stale-target protection. Preserve existing visual design and missing-image behaviour. No new image framework without approval.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F7; lint groups 7/8. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given image success, decode failure or network failure, streams and owned request resources are released and the existing visible fallback remains correct.
- [ ] #2 Given stalled image retrieval, navigation, recreation or a rebound target, requests are bounded/cancelled or ignored safely and stale results cannot update the wrong/destroyed view.
- [ ] #3 Characterization tests precede refactoring; controlled stream/network and lifecycle tests cover success, error, timeout and stale completion without public image hosts.
- [ ] #4 Active and archived details retain their band/festival context and edit restrictions; no rating state or metadata is changed by image failures.
- [ ] #5 README troubleshooting and architecture impacts are documented, with approval/ADR handling for any significant new boundary or dependency.
- [ ] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->
