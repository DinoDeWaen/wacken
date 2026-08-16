---
id: task-165
title: 'DEF: Fix crash while loading bands after v2.26'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-08-16 16:04'
updated_date: '2026-08-16 16:04'
labels: []
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user opening the app, I want the band list to finish loading after the Loading bands message, so that startup does not crash before the list is usable.

Scope: diagnose and fix the loadBandList startup path after V2.26, including repository construction, start-state evaluation, cached band loading, and sync-status rendering.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the app shows Loading bands, when loadBandList runs, then repository construction and start-state evaluation do not crash.
- [ ] #2 Given cached active or archived festival data exists, when the band list is loaded, then unsupported Android runtime APIs are not used in that path.
- [ ] #3 Automated regression tests cover the loadBandList startup crash risk.
- [ ] #4 A signed hotfix APK is built and published after validation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect the exact MainActivity loadBandList path reached after Loading bands.
2. Audit AppRepositories and Room migrations/queries for runtime exceptions during repository construction, start-state evaluation, and cached band loading.
3. Add focused regression coverage for the discovered startup crash class.
4. Implement the smallest fix and run targeted plus full release validation.
5. Publish a signed hotfix APK after validation.
Architecture impact: not architecture-significant unless a schema or repository boundary change is required; stop if that becomes necessary.
<!-- SECTION:PLAN:END -->
