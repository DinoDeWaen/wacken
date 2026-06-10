---
id: task-72
title: 'DEF: Use dark Wacken styling for schedule decision details'
status: To Do
assignee: []
created_date: '2026-06-10 12:16'
labels:
  - defect
  - ui
  - schedule
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The schedule decision detail currently opens on a default white surface, which clashes with the rest of the app and makes the MVP2 schedule feel unfinished.

As a festival attendee, I want the chosen-act and alternatives detail to use the same dark Wacken/metal visual language as the rest of the app so that the schedule experience is consistent and readable.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given I open a performance block from the group schedule, when the decision detail appears, then it uses a dark Wacken/metal themed surface instead of a default white dialog.
- [ ] #2 Given the detail contains chosen act, alternatives, stars, stages, time ranges, selection buttons, and close action, when it is shown on phone-sized and BlueStacks-sized screens, then the content remains readable and controls fit without visual overlap.
- [ ] #3 Given I select an alternative as the act to attend, when the detail is restyled, then the existing local visible schedule override behavior is preserved.
- [ ] #4 Automated tests or focused compile checks protect the behavior, and manual visual validation is documented.
- [ ] #5 Business requirements impact: updated for BR-071.
- [ ] #6 README impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
