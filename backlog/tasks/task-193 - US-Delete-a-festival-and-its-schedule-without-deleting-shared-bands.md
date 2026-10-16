---
id: TASK-193
title: 'US: Delete a festival and its schedule without deleting shared bands'
status: To Do
assignee: []
created_date: '2026-10-16 05:57'
labels: []
milestone: m-1
dependencies: []
priority: high
type: feature
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: a group can remove an unwanted festival and its festival-owned planning schedule while keeping reusable bands in the central catalog. Scope: visible delete action with explicit confirmation, remote-first group-wide deletion, cleanup of festival-owned lineup/performances/schedule locks/planning ratings and local cache, and prevention of deleted-festival reseeding. Preserve the shared bands catalog. The current schema does not scope performances or schedule locks by festival; Supabase permits only platform-admin festival deletion and personal rating history loses its festival reference on hard delete. Resolve these data and authorization choices through explicit architecture approval before coding. Out of scope: silently deleting a production festival during this task.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given an authorized user and a selected festival, when deletion is confirmed, then that festival disappears for all group members after sync and its own schedule no longer appears.
- [ ] #2 Deleting a festival never deletes a band from the central bands catalog or another festival lineup, schedule, or rating.
- [ ] #3 The user sees the affected data and receives clear success or failure feedback; a remote denial leaves the local festival intact.
- [ ] #4 The app does not recreate an intentionally deleted default Wacken festival on restart, and tests cover deletion plus cross-festival preservation.
- [ ] #5 README, business requirements, migration/authorization decision, ADR, and diagram impacts are recorded.
<!-- AC:END -->
