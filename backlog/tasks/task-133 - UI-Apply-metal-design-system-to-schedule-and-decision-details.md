---
id: task-133
title: 'UI: Apply metal design system to schedule and decision details'
status: To Do
assignee: []
created_date: '2026-06-19 06:27'
labels:
  - ui
  - design
  - schedule
  - ux
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Polish the group schedule and decision detail surfaces around the shared visual system. The schedule must explain its advanced states clearly: rating colors, locks, ties, scratched skipped overlaps, hidden barred acts, and star-threshold filters.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the group schedule, when it renders, then day controls, filter controls, stage labels, time axis, grid, walking markers, and performance blocks use the shared visual design system.
- [ ] #2 Given schedule states are visible, when a user sees lock, tie, scratched, gold, red, grey, or hidden-filter behavior, then an accessible legend explains what those states mean.
- [ ] #3 Given a schedule decision detail, when chosen act and alternatives render, then hierarchy, per-person ratings, tie evidence, walking-time evidence, and select actions are visually distinct and readable.
- [ ] #4 Given phone-sized screens, when the user scrolls horizontally or opens details, then stage labels remain useful and text does not overlap or clip.
- [ ] #5 Automated or focused compile validation proves the touched UI code builds; manual validation steps are documented.
- [ ] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
