---
id: task-68
title: 'US: Open schedule block decision details'
status: To Do
assignee: []
created_date: '2026-06-10 07:05'
labels:
  - mvp2
  - ui
  - schedule
dependencies:
  - task-67
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want to tap a calendar performance block and see the chosen act plus all alternatives, so I can understand why the group schedule picked that act.

Business rule: BR-065.

In scope:
- Open a detail view or panel from a calendar performance block.
- Show the chosen act clearly.
- Show all alternatives for the same conflict or schedule decision.
- For every shown band, include band name, stage, rating stars, and decision status.

Out of scope:
- Changing the chosen act.
- Persisting manual schedule choices.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a calendar performance block has alternatives, when I tap the block, then a detail view opens for that schedule decision.
- [ ] #2 Given the detail view opens, then the chosen act is visibly distinguished from alternatives.
- [ ] #3 Given alternatives exist for the decision, then all alternatives are listed with band name, stage, rating stars, and status.
- [ ] #4 Given a selected act has no alternatives, then the detail view still opens and clearly shows that no alternatives are available.
- [ ] #5 Given I close the detail view, then I return to the same calendar day and scroll position where practical.
- [ ] #6 The behavior is covered by focused tests or explicit Android UI validation, and compile validation is run.
<!-- AC:END -->
