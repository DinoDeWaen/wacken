---
id: task-117
title: 'DEF: Apply and verify group schedule locks backend schema'
status: To Do
assignee: []
created_date: '2026-06-17 20:23'
updated_date: '2026-06-17 20:23'
labels:
  - defect
  - backend
  - schedule
  - supabase
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The Group Schedule screen reports that Supabase cannot find `public.group_schedule_locks` in the schema cache. The repository contains `backend/flyway/sql/V007__group_schedule_locks.sql`, so the app expects the table, but the active Supabase backend has not applied or exposed that migration.

Business value: group-wide locked schedule choices cannot sync until the backend table exists and PostgREST can see it. The generated schedule can remain usable offline, but manual locks must persist for the group when online.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given V007 has already been applied but PostgREST schema cache is stale, when verification runs, then the schema cache is refreshed or the operational step is documented and executed.
- [ ] #2 Given the table is unavailable, when the app opens the schedule, then generated schedule remains visible and the warning does not block normal offline use.
- [ ] #3 Migration status and lock-table verification commands are recorded in implementation notes.
- [ ] #4 README/business requirements impact is recorded using the canonical wording from delivery-governance.md.
- [ ] #5 Given the production Supabase project is migrated, when the app loads group schedule locks, then `public.group_schedule_locks` exists and PostgREST no longer returns schema-cache missing-table errors.
<!-- AC:END -->
