---
id: task-107
title: 'US: Hide schedule acts at selected star threshold or below'
status: To Do
assignee: []
created_date: '2026-06-15 07:41'
labels:
  - ui
  - schedule
  - filter
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user viewing the group schedule, I want a star-threshold filter so I can hide all acts at or below the selected rating level and quickly focus on higher-rated acts.

Business rule from the request:
- If the user selects 2 stars in the filter, all 2-star and lower acts are hidden.
- The selected threshold is inclusive: acts with stars less than or equal to the selected value are hidden.

In scope:
- Add a schedule-screen filter control for choosing the inclusive hide threshold.
- Apply the selected threshold to schedule blocks and visible schedule detail candidates consistently.
- Keep filtering local to the current schedule view.

Out of scope:
- Changing conflict resolution rules.
- Persisting or syncing the threshold setting unless a later story asks for it.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the group schedule is open, when no star threshold is selected, then schedule acts are shown according to the normal generated and manual-selection rules.
- [ ] #2 Given the user selects a 2-star threshold, when the schedule is shown, then all acts rated 2 stars or lower are hidden and acts rated 3 stars or higher remain visible.
- [ ] #3 Given the user selects another threshold, when the schedule is shown, then acts with rating less than or equal to that selected threshold are hidden and higher-rated acts remain visible.
- [ ] #4 Given the threshold filter is active, when schedule decision details are opened, then visible candidates rated at or below the threshold are hidden from the detail list.
- [ ] #5 Given the threshold is cleared or changed, then the visible schedule updates without changing ratings, selected acts, generated decisions, manual choices, or synced data.
- [ ] #6 Automated tests or UI-level tests cover at least the 2-star threshold example and one higher threshold.
- [ ] #7 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
