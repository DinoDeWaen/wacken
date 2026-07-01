---
id: task-137
title: 'US: Validate MVP2 group schedule locks end to end'
status: To Do
assignee: []
created_date: '2026-06-30 15:45'
updated_date: '2026-07-01 02:46'
labels:
  - mvp2
  - schedule
  - supabase
  - uat
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member of the Sofie and Dino group, I want manually selected schedule winners to sync and reload across devices, so that MVP2 group planning is trustworthy after backend availability is confirmed by current app behavior and UAT.

Scope: run an end-to-end MVP2 validation path covering one user locking a schedule choice, another device/user syncing it, offline fallback, and clear user-facing warnings when lock sync is unavailable. Task-117 was cancelled because the Supabase schema-cache error no longer reproduces.

Out of scope: changing schedule decision rules, adding new MVP3/MVP4 capabilities, or implementing multiple groups.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given user A manually selects an alternative winner, when sync completes and user B opens or refreshes the schedule, then user B sees the same locked group winner with a lock indication
- [ ] #2 Given the app is offline or Supabase is unavailable, when a manual lock is changed locally, then the generated schedule remains usable and pending sync state is visible
- [ ] #3 Given the lock backend is unavailable, when the schedule is opened, then the warning is user-friendly and does not expose table names, schema names, UUID internals, or stack traces
- [ ] #4 Automated tests cover warning/fallback behavior or implementation notes explain why manual validation is required
- [ ] #5 Manual validation steps and device/backend evidence are recorded in implementation notes
- [ ] #6 README impact is recorded using the canonical wording from delivery-governance.md
- [ ] #7 Business requirements impact is recorded using the canonical wording from delivery-governance.md
- [ ] #8 Given current backend availability is confirmed, when the group schedule is opened online, then no schema-cache missing-table warning is shown for schedule locks
<!-- AC:END -->
