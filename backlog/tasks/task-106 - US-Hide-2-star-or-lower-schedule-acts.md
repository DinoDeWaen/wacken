---
id: task-106
title: 'US: Hide 2-star or lower schedule acts'
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
As a Wacken Planner user viewing the group schedule, I want a simple toggle to hide all acts rated 2 stars or less, so that the schedule can focus on stronger choices without deleting or changing any ratings.

In scope:
- Add a schedule-screen filter toggle for hiding visible acts rated 2 stars or lower.
- Apply the filter to schedule blocks and visible schedule detail candidates consistently.
- Preserve the underlying generated schedule, ratings, manual choices, and sync data.

Out of scope:
- Changing conflict resolution rules.
- Persisting or syncing the filter setting unless a later story asks for it.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the group schedule is open, when the hide 2-star-or-lower toggle is off, then schedule acts are shown according to the normal generated and manual-selection rules.
- [ ] #2 Given the group schedule is open, when the hide 2-star-or-lower toggle is on, then visible schedule blocks rated 2 stars or lower are hidden from the schedule view.
- [ ] #3 Given a hidden act has alternatives or decision details, when the filter is active, then the visible detail content does not show acts rated 2 stars or lower.
- [ ] #4 Given the filter is changed, when the user turns it off again, then the hidden acts reappear without changing ratings, selected acts, or synced data.
- [ ] #5 Automated tests or UI-level tests cover the toggle behavior and prove the underlying schedule data is not mutated.
- [ ] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
