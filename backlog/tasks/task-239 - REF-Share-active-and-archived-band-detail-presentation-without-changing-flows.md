---
id: TASK-239
title: 'REF: Share active and archived band-detail presentation without changing flows'
status: To Do
assignee: []
created_date: '2026-10-20 06:26'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies:
  - TASK-235
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
As an attendee, I want consistent band details while active editing and archived correction remain clearly distinct.

## Scope
Characterize current detail screens and share justified read-only presentation for context, history, links, biography and sections. Reuse the lifecycle-safe image owner from the image task. Preserve current styling/navigation; the Ironline/Field Notes redesign remains in TASK-208 and related 3.0 tasks.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F8; lint groups 2-12. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Approved characterization baselines capture active/archive rendered content and action availability before extraction, including missing metadata and unknown music links.
- [ ] #2 Active planning/after edits stay bound to the opened festival; archived controls remain read-only until explicit own-rating correction and never reopen normal editing.
- [ ] #3 Canonical link source, per-member ratings, selected-festival history/context, rotation/back navigation and pending state retain behaviour through shared presentation.
- [ ] #4 Native regression tests cover both contexts and unsupported actions; no 3.0 theme, navigation or style contract is implemented in this task.
- [ ] #5 New presentation ownership stays within the Android adapter; README and diagram impacts are recorded and significant changes require approval.
- [ ] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [ ] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->
