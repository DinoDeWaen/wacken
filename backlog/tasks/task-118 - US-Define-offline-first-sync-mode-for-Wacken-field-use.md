---
id: task-118
title: 'US: Define offline-first sync mode for Wacken field use'
status: To Do
assignee: []
created_date: '2026-06-17 20:23'
labels:
  - offline
  - sync
  - architecture
  - mvp
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user at the festival, I want the app to remain useful with poor or no network, so that I can view the lineup, ratings, and schedule without waiting on Supabase.

Recommendation: keep Room, but make the architecture explicitly offline-first. UI reads should always use local Room data first. Supabase should sync in the background and never block schedule/list/detail viewing. User edits should be queued locally as pending changes and synced when a connection is available, with clear pending/synced status.

This story is a design/decision story because it affects sync policy, user expectations, and potentially architecture/ADR documentation.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the app starts without network, when cached festival data exists, then the band list, band details, and schedule are usable without waiting for Supabase.
- [ ] #2 Given the user changes ratings or schedule locks while offline, when offline edits are allowed, then the changes are stored locally as pending and synced later when network returns.
- [ ] #3 Given offline edits could conflict with remote group changes, when sync resumes, then a conflict policy is documented and implemented or split into follow-up stories.
- [ ] #4 Given this changes sync policy, when implementation starts, then architecture impact is assessed and an ADR is created or updated if required.
- [ ] #5 Business requirements and README impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
