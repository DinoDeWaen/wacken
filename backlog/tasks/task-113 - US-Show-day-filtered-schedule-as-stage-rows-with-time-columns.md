---
id: task-113
title: 'US: Show day-filtered schedule as stage rows with time columns'
status: To Do
assignee: []
created_date: '2026-06-16 20:02'
labels:
  - schedule
  - ui
  - calendar
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want to filter the group schedule by day and view it with stages on the left and time across the top, so that the schedule matches the Wacken app pattern and overlapping acts are easier to scan on a phone.

Business value: the schedule is the festival-day working view. Matching the familiar Wacken orientation makes it easier to compare stages at the same time and use the app during the festival.

In scope:
- Add an explicit day selector/filter for the schedule view.
- Rotate the current schedule grid so stage lanes are horizontal rows on the left and time runs left-to-right across the top.
- Position performance blocks by start/end time within their stage row.
- Keep existing schedule filters, locked/manual choices, barred/skipped markings, ratings, lost alternatives, and block-detail navigation working in the rotated view.
- Keep 02:00 festival-day cutoff and weekday/day labels.
- Use the provided Wacken app screenshots as visual reference while staying within the app's existing dark metal style.

Out of scope:
- Copying official Wacken app artwork or copyrighted images.
- Adding band photos unless a separate asset/import story provides image data for schedule blocks.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given multiple festival days exist, when I open the group schedule, then I can choose which day to view and the schedule only shows acts for that selected festival day.
- [ ] #2 Given a selected day has performances, when the schedule is shown, then stages are listed as horizontal rows on the left and time is shown as columns across the top.
- [ ] #3 Given two acts overlap on different stages, when the rotated schedule is shown, then each act appears in its own stage row at the correct time position instead of covering the other act.
- [ ] #4 Given an act spans a time range, when it is rendered in the rotated schedule, then the block width reflects its start and end time and remains readable at common phone widths through horizontal scrolling or scaling.
- [ ] #5 Given existing schedule controls are active, when I use hide-barred or star-threshold filters, manual/locked winner choices, barred scratch markings, lost alternatives, or block details, then those behaviors still work in the day-filtered rotated view.
- [ ] #6 Given the festival day includes late-night acts, when the selected day is shown, then the view still includes performances through 02:00 before the next festival day starts.
- [ ] #7 Automated layout/presentation tests cover day filtering, stage-row ordering, time-position calculations, and preservation of existing block interactions; Android compile validation passes.
- [ ] #8 Business requirements and README impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
