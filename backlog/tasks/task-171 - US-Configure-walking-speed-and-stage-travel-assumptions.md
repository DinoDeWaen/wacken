---
id: task-171
title: 'US: Configure walking speed and stage travel assumptions'
status: To Do
assignee: []
created_date: '2026-08-19 11:02'
labels:
  - user-story
  - future
  - schedule
  - settings
dependencies: []
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: The schedule can match the real walking pace and stage layout of the group instead of relying only on fixed defaults.

As a festival planner, I want to adjust walking-time assumptions so that schedule warnings reflect how fast we actually move.

Scope: settings for default walking minutes and stage-pair or stage-group overrides, recalculation of schedule walking labels, and reset to defaults.

Out of scope: GPS routing, maps, and live crowd-density estimates.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given walking-time settings are changed, when the schedule is regenerated, then walking labels and impractical-overlap markings use the configured values.
- [ ] #2 Given a user resets walking-time settings, then the app restores the agreed MVP defaults for nearby and distant stage groups.
- [ ] #3 Given the app is offline, when configured walking settings already exist locally, then the schedule still uses them.
- [ ] #4 Relevant tests cover default values, configured overrides, reset behavior, and schedule recalculation.
- [ ] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->
