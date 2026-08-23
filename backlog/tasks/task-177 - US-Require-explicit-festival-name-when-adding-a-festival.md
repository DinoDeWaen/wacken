---
id: task-177
title: 'US: Require explicit festival name when adding a festival'
status: To Do
assignee: []
created_date: '2026-08-23 09:33'
labels:
  - user-story
  - festival
  - import
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: The app should not accidentally create a future festival with a placeholder name, because festival names become visible context for planning ratings, archives, and future history.

As a festival admin, I want the add-festival name field to start empty and be mandatory, so that the active festival is created with the real festival name I intended.

Scope: add-festival mode only. The festival name field starts blank, validates before adding the festival, keeps the selected bands CSV intact after validation errors, and shows clear feedback when the name is missing.

Out of scope: renaming an already active festival, fuzzy band linking, metadata enrichment, and multiple upcoming festivals.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given no festival is active, when the add-festival screen opens, then the festival name field is empty and no placeholder value is submitted.
- [ ] #2 Given the festival name is blank or whitespace, when the user tries to add the festival from bands.csv, then the app blocks creation and shows a clear required-name message.
- [ ] #3 Given a valid festival name and valid bands.csv are selected, when the user adds the festival, then the festival is created using the entered trimmed name.
- [ ] #4 Automated tests cover blank-name validation and successful add-festival creation with an explicit name.
- [ ] #5 Business requirements and README impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->
