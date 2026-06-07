---
id: task-56
title: 'US-058: Generate shared day timeline from resolved performances'
status: To Do
assignee: []
created_date: '2026-06-07 15:37'
labels:
  - mvp2
  - scheduling
  - application
dependencies:
  - task-55
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner group member, I want the app to generate a day-based shared timeline from the group decisions, so the group has one clear MVP2 plan to review.

In scope:
- Generate one timeline per festival day from non-overlapping selected performances.
- Include selected band, stage, date, start/end time, decision strength, and lost alternative where available.
- Keep optional selected performances visibly marked as optional.
- Preserve chronological ordering within each day.
- Provide an application use case that Android UI can call.

Out of scope:
- Travel feasibility re-runs, travel time annotations, lunch blocks, food suggestions, PDF export, and manual override editing.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given resolved performances for multiple festival days, when a shared schedule is generated, then timeline slots are grouped by day and sorted by start time.
- [ ] #2 Given a selected performance has a lost alternative, when the timeline is generated, then the slot includes that lost alternative for review.
- [ ] #3 Given a resolved conflict returns OPTIONAL, when the timeline is generated, then the slot is visibly marked optional in the timeline model.
- [ ] #4 Given a conflict returns no selected performance because all options are vetoed, when the timeline is generated, then no slot is created for that conflict.
- [ ] #5 Given band-only data without performance times, when schedule generation runs, then those bands are ignored with a clear no-scheduled-performance outcome rather than crashing.
- [ ] #6 Automated tests cover day grouping, sorting, optional slots, lost alternatives, and no-selection conflicts.
- [ ] #7 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [ ] #8 Architecture impact is assessed; ADR impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
