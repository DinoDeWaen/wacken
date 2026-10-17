---
id: task-173
title: 'US: Add richer group schedule lock administration'
status: To Do
assignee: []
created_date: '2026-08-19 11:02'
labels:
  - user-story
  - future
  - schedule
  - locks
  - groups
dependencies: []
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Group-wide manual choices can be trusted and corrected without confusion when several people use the same schedule.

As a group member, I want to see and manage group schedule locks clearly so that manual overrides do not become invisible or stale.

Scope: lock owner/last-changed visibility, clear/reset flow, optional admin or owner rule, and audit notes for changed locks.

Out of scope: changing rating rules and multi-group permissions unless the multi-group story is also implemented.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a conflict has a locked winner, then the schedule and detail screen show that the choice is locked and who last changed it when that information is available.
- [ ] #2 Given a permitted user clears a lock, when the schedule is regenerated, then the generated decision rules choose the winner again.
- [ ] #3 Given a user is not permitted to change a lock under the chosen permission model, then the app prevents the change and explains why.
- [ ] #4 Relevant tests cover lock visibility, clear/reset behavior, sync impact, and permission boundaries.
- [ ] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->
