---
id: task-41
title: 'US-041: Replace TSV backend source with Supabase source adapter'
status: To Do
assignee: []
created_date: '2026-05-17 16:52'
labels:
  - backend
  - supabase
  - sync
  - room
dependencies:
  - task-39
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee, I want the app to synchronize festival master data from Supabase into the local Room cache so that centrally managed bands and schedule data appear in the app without manual TSV import.

Business value:
- Replaces the MVP TSV backend-like source with the real central backend while preserving the Room cache and clean domain boundaries.
- Lets the app owner manage bands centrally in Supabase.

In scope:
- Add Supabase source adapters for bands, stages, performances, stage distances, and food options.
- Keep Room as the local cache used by app reads.
- Keep TSV source available only as a fallback/import tool if needed, not as the primary source.
- Pull backend master data into Room using explicit sync behavior.
- Keep domain/application modules independent of Supabase client details.

Out of scope:
- User rating push/pull sync.
- Admin data editing UI.
- Realtime subscriptions.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given central band data exists in Supabase, when the app syncs, then Room contains the latest bands and the overview can list them.
- [ ] #2 Given central performance/stage data exists in Supabase, when the app syncs, then schedule metadata is available from Room.
- [ ] #3 Given the app lists bands, then it reads from Room and does not query Supabase directly from UI code.
- [ ] #4 Given Supabase replaces TSV as the primary source, then domain/application APIs remain unchanged or changes are explicitly justified.
- [ ] #5 Given sync fails, then existing Room data remains available and the user sees a clear error or stale-data state where applicable.
- [ ] #6 Automated tests cover adapter mapping and sync behavior; existing listing/import/rating tests remain green.
- [ ] #7 README documents the Supabase sync setup and remaining TSV fallback/import role.
<!-- AC:END -->
