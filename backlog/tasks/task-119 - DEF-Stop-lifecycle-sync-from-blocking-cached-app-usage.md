---
id: task-119
title: 'DEF: Stop lifecycle sync from blocking cached app usage'
status: To Do
assignee: []
created_date: '2026-06-17 20:26'
updated_date: '2026-06-17 20:33'
labels:
  - android
  - sync
  - offline
  - defect
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The current lifecycle sync path can make the Room cache feel like a bottleneck because app start/reactivation tries to talk to Supabase. Cached screens should render from Room first, while Supabase sync runs in the background and reports a concise offline/sync status without blocking normal use.

Business value: the app remains useful at Wacken when mobile data is poor, while still refreshing from Supabase when the network is available.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given cached data exists on app start or reactivation, when Supabase is slow or unavailable, then the band list and group schedule render from cache before or independently of Supabase sync.
- [ ] #2 Given Supabase sync fails, when the user is viewing cached data, then the UI remains usable and shows a concise cached/offline status instead of a blocking error.
- [ ] #3 Given Supabase sync succeeds, when cached views are visible, then they refresh without losing the user's current context.
- [ ] #4 Automated or focused Android coverage verifies the cached-first lifecycle behavior.
- [ ] #5 The release APK is rebuilt and verified after the change.
- [ ] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
