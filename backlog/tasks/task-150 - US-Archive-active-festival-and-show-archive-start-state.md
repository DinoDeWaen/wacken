---
id: task-150
title: 'US: Archive active festival and show archive start state'
status: To Do
assignee: []
created_date: '2026-08-12 07:52'
labels:
  - user-story
  - post-mvp3
  - festivals
  - archive
dependencies:
  - task-149
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want to archive the completed active festival so that Wacken becomes historical and the app can move on to the next festival.

Business value: The group can preserve Wacken for later review while making it clear that no current festival is being planned.

Scope: manual archive action on the active festival start screen, one-active-festival invariant, read-only archived festival state, start screen behavior when no active festival exists.

Out of scope: editing archived festivals, archive confirmation, multiple active or upcoming festivals.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given an active festival exists, when a user opens the start screen, then the active festival band list is shown with an Archive action at the top.
- [ ] #2 Given the active festival is archived, when the archive completes, then the festival is marked archived and no longer active.
- [ ] #3 Given no active festival exists, when the app starts, then archived festivals and an add-festival path are shown instead of the active band list.
- [ ] #4 Given a festival is archived, when a user opens its data, then festival data and ratings are read-only in the first version.
- [ ] #5 The one-active-festival invariant is covered by domain/application tests and externally visible behavior is covered by BDD scenarios.
- [ ] #6 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->
