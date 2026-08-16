---
id: task-1.1
title: 'DEF: Fix v2.24 startup crash and protect real-rating recovery'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-08-16 15:45'
updated_date: '2026-08-16 15:45'
labels: []
dependencies: []
parent_task_id: task-1
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user installing v2.24, I want the app to start reliably while preserving recovered real ratings, so that a hotfix cannot block access to my festival data.

Scope: fix the startup crash risk in the v2.24 real-rating recovery path and verify that Supabase personal rating history is not deleted by the app sync path.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given legacy real ratings exist in local storage, when the app repository initializes, then recovery does not throw on Android runtime APIs or duplicate legacy rows.
- [ ] #2 Given the user is signed in, when recovered real ratings are backfilled, then rows are created locally for the signed-in user without deleting remote personal rating history.
- [ ] #3 Given the user is signed out or the session is unavailable, when the app starts, then the app reaches the existing auth/offline handling instead of crashing in repository construction.
- [ ] #4 Automated tests cover the startup recovery regression and confirm Supabase personal rating sync has no delete path for recovered events.
- [ ] #5 A signed hotfix APK is built and released after validation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Correct the defect task metadata and acceptance criteria through Backlog.md CLI.
2. Inspect the v2.24 startup recovery path and Supabase migration/sync paths for destructive behavior.
3. Add focused regression coverage for Android-runtime-safe legacy recovery and no remote personal-rating delete path.
4. Replace startup recovery code that can throw on Android/runtime/session edge cases.
5. Run targeted tests, compile, build debug/release APK, then publish a hotfix release if validation passes.
Architecture impact: not architecture-significant; this stays inside existing Room/Supabase adapters and app composition.
<!-- SECTION:PLAN:END -->
