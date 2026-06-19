---
id: task-126
title: 'UX: Add persistent offline and pending-sync status'
status: To Do
assignee: []
created_date: '2026-06-19 06:04'
labels:
  - ux
  - sync
  - offline
dependencies: []
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The app now supports offline-first behavior, but users need clearer confidence while at the festival. Add a compact persistent status area that shows whether data is cached, whether sync is running, and whether local changes are pending.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the app is offline or sync fails, when the user views main screens, then cached/offline state is visible without blocking use.
- [ ] #2 Given pending ratings or schedule locks exist, when the user views settings or main screens, then pending sync count/status is visible.
- [ ] #3 Given sync succeeds, when pending changes are cleared, then the status updates without requiring app restart.
<!-- AC:END -->
