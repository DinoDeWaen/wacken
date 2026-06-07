---
id: task-57
title: 'US-059: Show generated group schedule in Android'
status: To Do
assignee: []
created_date: '2026-06-07 15:37'
labels:
  - mvp2
  - scheduling
  - android
dependencies:
  - task-56
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner group member, I want to generate and view the shared group schedule in the Android app, so the MVP2 decision engine is usable without developer tools.

In scope:
- Add a schedule action from the band overview or an equivalent reachable Android entry point.
- Show the generated timeline grouped by festival day.
- Show selected band, stage, time range, decision strength, optional marker, and lost alternative where available.
- Show clear empty/error states when no scheduled performances or no ratings are available.
- Keep existing sync, rating, overview, and detail workflows reachable.

Out of scope:
- Timeline editing, manual overrides, travel/lunch/food annotations, PDF export, and Play Store distribution.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given synced festival data and ratings exist, when the user generates the schedule, then the Android app shows a day-based timeline.
- [ ] #2 Given a timeline slot has a lost alternative, when the user views the schedule, then the lost alternative is visible enough to compare with the selected band.
- [ ] #3 Given optional performances are selected, when the schedule is shown, then optional status is clearly marked.
- [ ] #4 Given no scheduled performances are available, when the user opens the schedule, then a clear empty state is shown instead of a blank screen.
- [ ] #5 Given schedule generation fails, when the user opens the schedule, then a clear error message is shown and the band overview/rating workflow remains usable.
- [ ] #6 Focused automated or manual validation covers the Android schedule screen and navigation.
- [ ] #7 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [ ] #8 Architecture impact is assessed; ADR impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
