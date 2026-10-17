---
id: task-157
title: 'US: Manage multiple upcoming festivals in a future version'
status: To Do
assignee: []
created_date: '2026-08-12 07:54'
labels:
  - user-story
  - future
  - festivals
dependencies:
  - task-149
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want to manage more than one upcoming festival in a future version so that planning can overlap across festival seasons when that becomes relevant.

Business value: The app can later grow beyond a single active festival while preserving the simpler first post-Wacken flow now.

Scope: future support for multiple upcoming/planning festivals, choosing the current working festival, and avoiding rating/schedule confusion across festivals.

Out of scope: first post-Wacken implementation, multiple independent friend groups, invite flows.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given multiple upcoming festivals are supported in a future version, then the user can choose which festival is the current planning context.
- [ ] #2 Given planning ratings exist for more than one upcoming festival, then schedule generation and band screens use only the selected festival context.
- [ ] #3 Given a festival is archived, then its historical data remains read-only unless a future archive-editing story changes that rule.
- [ ] #4 The story is marked future scope and must not block the first post-Wacken one-active-festival implementation.
- [ ] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->
