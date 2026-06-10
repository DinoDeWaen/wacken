---
id: task-66
title: 'US: Add settings page and compact navigation icons'
status: To Do
assignee: []
created_date: '2026-06-10 07:05'
labels:
  - mvp2
  - ui
  - navigation
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want the main overview to use compact icon actions and move secondary actions into settings, so the overview stays focused while group, import, sync, schedule, and exit actions remain reachable.

Business rules: BR-061, BR-062, BR-058, BR-059.

In scope:
- Add a cog icon action that opens settings.
- Move group/invite, import, and manual sync actions into settings.
- Add a calendar icon action for the group schedule.
- Add a direct exit action that performs sync-and-exit.

Out of scope:
- Redesigning the schedule calendar layout.
- Changing sync conflict rules or Supabase data contracts.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given I am on the band overview, when the screen is shown, then I can open settings through a cog icon action.
- [ ] #2 Given I am on settings, then group/invite, import, and manual sync actions are available there instead of the main overview action area.
- [ ] #3 Given I am on the band overview, when I tap the calendar icon, then the group schedule opens.
- [ ] #4 Given I am on the band overview, when I tap the exit action, then the app attempts Supabase sync before exiting and stays open with a clear error if sync fails.
- [ ] #5 Existing group, import, manual sync, startup sync, and close-sync behavior remains covered by tests or explicit validation.
- [ ] #6 README, business requirements, diagram, and ADR impact are assessed using the delivery-governance validation package.
<!-- AC:END -->
