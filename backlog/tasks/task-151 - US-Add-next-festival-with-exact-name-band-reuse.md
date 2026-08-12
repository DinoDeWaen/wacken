---
id: task-151
title: 'US: Add next festival with exact-name band reuse'
status: To Do
assignee: []
created_date: '2026-08-12 07:53'
labels:
  - user-story
  - post-mvp3
  - festivals
  - bands
  - import
dependencies:
  - task-149
  - task-150
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want to add the next festival after archiving the current one so that planning can restart without losing known bands.

Business value: The app can move from Wacken to a later festival such as Rock am Ring or Rock im Park while reusing band knowledge over time.

Scope: add a festival only when no active festival exists, upload/import that festival lineup, reuse existing band records by exact name, create new band records for unmatched names, and make the new festival active.

Out of scope: adding a second upcoming festival while one is active, fuzzy matching, alias confirmation, editing archived festival data.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given no active festival exists, when a user adds a new festival with lineup data, then the festival becomes the active festival.
- [ ] #2 Given an active festival already exists, when a user tries to add another upcoming festival, then the first post-Wacken version prevents it.
- [ ] #3 Given an imported lineup contains a band name that exactly matches an existing band, then the lineup entry links to the existing band record.
- [ ] #4 Given an imported lineup contains a band name with no exact existing match, then a new band record is created.
- [ ] #5 Fuzzy matching and alias storage are not implemented in this story and remain future scope.
- [ ] #6 Domain/application tests cover band reuse and the one-active-festival constraint; BDD covers adding a next festival after archive.
- [ ] #7 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->
