---
id: task-62
title: 'US-064: Return from detail to the selected overview row'
status: To Do
assignee: []
created_date: '2026-06-07 16:02'
labels:
  - mvp2
  - android
  - ux
  - rating
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user browsing bands, I want returning from a band detail screen to bring me back to the same band row in the overview, so I can continue rating without losing my place.

In scope:
- Remember the band row opened from the overview.
- When returning from detail, restore the overview scroll position so that band row is visible.
- Refresh the row rating/state after any detail changes without jumping to the top.
- Keep normal app reactivation sync behavior working.

Out of scope:
- New sorting/filtering controls, schedule timeline navigation, and manual row pinning.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given I open a band detail from the overview, when I go back, then the overview returns to the row for that band instead of the top of the list.
- [ ] #2 Given I change a rating in detail, when I go back, then the same row is visible and shows the updated rating.
- [ ] #3 Given the app syncs on reactivation while returning from detail, when the overview refreshes, then it still scrolls to the selected band row after data loads.
- [ ] #4 Given the selected band is no longer present after refresh, when I go back, then the overview falls back gracefully without crashing.
- [ ] #5 Focused Android validation covers returning from detail after no change, after rating change, and after refresh.
- [ ] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [ ] #7 Architecture impact is assessed; ADR impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
