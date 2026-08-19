---
id: task-167
title: 'US: Support multiple independent friend groups'
status: To Do
assignee: []
created_date: '2026-08-19 11:01'
labels:
  - user-story
  - future
  - groups
dependencies: []
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Friends can plan separately without mixing ratings, schedule locks, exports, or member data across groups.

As a festival planner, I want to create, join, and switch between independent friend groups so that each group has its own planning context.

Scope: group creation, joining, switching, selected-group context, and group-scoped ratings, schedule locks, schedules, and exports.

Out of scope: multiple upcoming festivals, fuzzy aliases, Play Store distribution, and invite deep-link implementation details.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a user belongs to multiple groups, when they select a current group, then band screens, schedule generation, locks, settings, and exports use only that group context.
- [ ] #2 Given a user creates or joins a group, when data syncs, then membership is stored without leaking data into the existing Sofie and Dino group.
- [ ] #3 Given the existing one-group setup, when this feature is introduced, then existing users remain in their current group without data loss.
- [ ] #4 Relevant domain, application, and adapter tests cover group scoping and migration behavior.
- [ ] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->
