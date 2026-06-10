---
id: task-67
title: 'US: Show group schedule as calendar days'
status: To Do
assignee: []
created_date: '2026-06-10 07:05'
labels:
  - mvp2
  - ui
  - schedule
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want the group schedule shown as calendar days with hour lines and performance blocks, so I can quickly understand where the group is going during the festival.

Business rules: BR-063, BR-064.

In scope:
- Replace the current group schedule presentation with a day-based calendar layout.
- Show hour lines for each festival day.
- Position performance blocks by their scheduled time range.
- Show band, stage, and rating stars in each block.

Out of scope:
- Editing the selected act from the calendar block.
- Adding PDF export or travel/lunch MVP3 behavior.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a generated schedule has performances on multiple festival days, when I open the group schedule, then each day is shown in calendar form with hour lines.
- [ ] #2 Given a scheduled performance has a start and end time, then its block is positioned in the correct day and approximate time range.
- [ ] #3 Given a scheduled performance is shown as a block, then the block shows the band name, stage, and rating stars.
- [ ] #4 Given there is no generated schedule or a day has no selected performances, then the screen shows a clear empty state without crashing.
- [ ] #5 The calendar layout remains usable on common Android phone widths without overlapping unreadable text.
- [ ] #6 Domain/application schedule behavior remains covered by tests, and Android compile or unit validation is run.
<!-- AC:END -->
