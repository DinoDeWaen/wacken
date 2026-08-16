---
id: task-166
title: 'DEF: Parse Supabase personal rating timestamps with offsets'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-08-16 16:14'
updated_date: '2026-08-16 16:14'
labels: []
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a signed-in Wacken Planner user syncing cached festival data, I want Supabase personal rating history timestamps with timezone offsets to parse correctly, so sync does not fail and cached data can become up to date.

Scope: fix Supabase personal rating event timestamp parsing for values such as 1970-01-01T00:00:00+00:00 while preserving existing Z-form timestamp support.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given Supabase returns personal_band_rating_events.created_at as 1970-01-01T00:00:00+00:00, when personal rating sync pulls events, then parsing succeeds.
- [ ] #2 Given Supabase returns created_at in existing Z instant format, when personal rating sync pulls events, then parsing still succeeds.
- [ ] #3 Automated regression tests cover both timestamp formats.
- [ ] #4 A signed hotfix APK is built and published after validation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect the Supabase personal rating sync parser and tests for created_at handling.
2. Add regression coverage for Supabase offset timestamps like 1970-01-01T00:00:00+00:00 and existing Z timestamps.
3. Implement an offset-aware timestamp parser that preserves existing Instant.parse behavior.
4. Run targeted tests and full signed release validation.
5. Publish a signed hotfix APK after validation.
Architecture impact: not architecture-significant; this is an adapter parsing fix with no schema, API, or domain model change.
<!-- SECTION:PLAN:END -->
