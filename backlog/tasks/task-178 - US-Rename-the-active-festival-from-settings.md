---
id: task-178
title: 'US: Rename the active festival from settings'
status: To Do
assignee: []
created_date: '2026-08-23 09:33'
labels:
  - user-story
  - festival
  - settings
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: The active festival name can be corrected after creation without losing ratings, lineup links, archive continuity, or sync context.

As a festival admin, I want to rename the currently active festival from Settings, so that typos or placeholder names can be fixed while preserving the same festival identity.

Scope: Settings/Admin workflow for the single active festival. Users can open a rename action, enter a non-blank new display name, save it, and see the updated name on active festival screens. The festival id and all existing lineup, planning rating, personal rating, and schedule references remain unchanged.

Out of scope: editing archived festival names, changing festival ids, managing multiple upcoming festivals, and renaming remote Supabase rows unless the active festival persistence/sync contract already supports it.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a festival is active, when the user opens Settings/Admin, then a rename-active-festival action is available.
- [ ] #2 Given the user saves a non-blank new festival name, then the active festival display name is updated without changing the festival id or linked lineup and ratings.
- [ ] #3 Given the new festival name is blank or whitespace, when the user saves, then the app blocks the rename and shows a clear required-name message.
- [ ] #4 Given no festival is active, then the rename-active-festival action is not shown or is disabled with clear feedback.
- [ ] #5 Automated tests cover successful rename, blank-name validation, and identity preservation.
- [ ] #6 Architecture impact is assessed before implementation; if persistence or sync contracts must change, explicit approval is requested before coding and ADR impact is recorded.
- [ ] #7 Business requirements and README impact are recorded using delivery-governance wording when implemented.
<!-- AC:END -->
