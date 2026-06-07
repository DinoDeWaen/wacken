---
id: task-54
title: 'US-056: Group overlapping performances into conflict sets'
status: To Do
assignee: []
created_date: '2026-06-07 15:36'
labels:
  - mvp2
  - scheduling
  - domain
dependencies:
  - task-53
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner group member, I want overlapping performances to be grouped into conflicts, so the app can reason about which acts cannot all be attended.

In scope:
- Detect overlaps between performances on the same festival day.
- Keep non-overlapping performances separate.
- Return conflict sets that include all mutually relevant overlapping candidates for scheduling.
- Preserve band, stage, date, and time information in each conflict candidate.

Out of scope:
- Deciding the winner of a conflict.
- Travel feasibility, lunch planning, food suggestions, PDF output, and Android UI.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given two performances on the same day overlap in time, when conflicts are detected, then they appear in the same conflict set.
- [ ] #2 Given performances on different days have the same time range, when conflicts are detected, then they do not conflict.
- [ ] #3 Given performances touch at end/start time without overlap, when conflicts are detected, then they are not grouped as overlapping.
- [ ] #4 Given a chain of overlapping performances, when conflicts are detected, then all connected overlapping candidates are available for conflict resolution.
- [ ] #5 Automated tests cover overlap, non-overlap, cross-day, and boundary-time cases.
- [ ] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [ ] #7 Architecture impact is assessed; ADR impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
