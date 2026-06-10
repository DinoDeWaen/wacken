---
id: task-73
title: 'US: Show weekday names and 02:00 festival day cutoff in schedule'
status: To Do
assignee: []
created_date: '2026-06-10 12:17'
labels:
  - ui
  - schedule
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The calendar schedule should match how festival days are experienced on site: a day can continue after midnight and only ends at 02:00, and the day header should be recognizable as Monday, Tuesday, and so on.

As a festival attendee, I want each schedule day to show the weekday and late-night hours through 02:00 so that after-midnight performances stay visible in the correct festival day.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a calendar schedule day is shown, when the header is rendered, then it includes the weekday name and the festival date.
- [ ] #2 Given a festival day contains late performances, when the calendar renders the day, then the visible hour range continues through 02:00 instead of stopping at 00:00.
- [ ] #3 Given a performance starts after midnight and ends by 02:00, when it belongs to the previous festival day, then it appears in that previous day's calendar view.
- [ ] #4 Given the hour lines include late-night slots, when performance blocks are positioned, then they remain aligned to the correct time labels.
- [ ] #5 Automated tests cover the festival-day boundary behavior and focused UI validation is documented.
- [ ] #6 Business requirements impact: updated for BR-069 and BR-070.
- [ ] #7 README impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
