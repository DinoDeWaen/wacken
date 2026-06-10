---
id: task-76
title: 'US: Show walking time in the group schedule'
status: To Do
assignee: []
created_date: '2026-06-10 12:17'
updated_date: '2026-06-10 12:17'
labels:
  - ui
  - schedule
  - travel
  - mvp3
dependencies:
  - task-75
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Once walking-time defaults are known, the calendar schedule should make movement time visible between consecutive selected acts so users can judge whether the plan is practical on site.

As a festival attendee, I want the group schedule to show walking time between selected acts so that I can see travel impact directly in the schedule.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given two consecutive selected acts are on Heavy and Louder, when the schedule is shown, then the movement between them displays 5 minutes walking time.
- [ ] #2 Given two consecutive selected acts move between Heavy or Louder and another stage, when the schedule is shown, then the movement between them displays 15 minutes walking time.
- [ ] #3 Given walking time is unknown for a stage pair, when the schedule is shown, then the UI handles the unknown value clearly without hiding the selected acts.
- [ ] #4 Given walking-time information is displayed, when performance blocks and hour lines are rendered, then travel information is visible without overlapping band, stage, star, or time labels.
- [ ] #5 Automated tests or focused UI validation cover walking-time display, and manual schedule validation is documented.
- [ ] #6 Business requirements impact: updated for BR-074.
- [ ] #7 README impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
