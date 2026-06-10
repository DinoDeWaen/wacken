---
id: task-69
title: 'US: Select schedule alternative as chosen act'
status: To Do
assignee: []
created_date: '2026-06-10 07:06'
labels:
  - mvp2
  - ui
  - schedule
  - decision
dependencies:
  - task-68
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want to select an alternative act as the one the group is going to, so the schedule can reflect the group's real decision when we disagree with the generated result.

Business rules: BR-066, BR-067.

In scope:
- Add a select-as-act action for alternatives in the schedule decision detail.
- Update the visible schedule result so the selected alternative becomes the chosen act.
- Keep original ratings and generated decision evidence visible; selection must not rewrite ratings.
- Mark manually selected choices clearly.

Out of scope until clarified:
- Supabase schema changes, cross-device sync of manual choices, permissions, and reset behavior unless explicitly decided during implementation planning.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a schedule decision detail shows alternatives, when I select an alternative as the act we are going to, then that act becomes the chosen act in the visible schedule result.
- [ ] #2 Given an alternative is selected, then the previously chosen act remains visible as an alternative or prior generated choice.
- [ ] #3 Given a manual choice has changed the visible result, then the UI clearly marks it as manually selected and still shows the original rating evidence.
- [ ] #4 Before implementation starts, the task records whether manual choices are local-only or synced to Supabase, who may change them, how they are cleared, and whether regeneration preserves them.
- [ ] #5 If persistence, Supabase schema, sync contracts, or permissions change, explicit architecture approval is obtained before coding and an ADR is created or updated when required.
- [ ] #6 The selected behavior is covered by BDD or focused domain/application tests plus Android compile validation.
<!-- AC:END -->
