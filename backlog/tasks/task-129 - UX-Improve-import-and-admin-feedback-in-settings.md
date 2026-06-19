---
id: task-129
title: 'UX: Improve import and admin feedback in settings'
status: To Do
assignee: []
created_date: '2026-06-19 06:04'
labels:
  - ux
  - settings
  - import
dependencies: []
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Settings contains group, import, and sync/admin actions. Make import/sync outcomes easier to understand with explicit success/failure summaries, last sync time, and next recommended action.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a sync or import completes, when the user remains on settings, then a concise result summary is shown.
- [ ] #2 Given a sync or import fails, when the user sees the error, then the message includes what can still be used offline and what action to try next.
- [ ] #3 Settings actions remain visually grouped by normal user actions versus admin/import actions.
<!-- AC:END -->
