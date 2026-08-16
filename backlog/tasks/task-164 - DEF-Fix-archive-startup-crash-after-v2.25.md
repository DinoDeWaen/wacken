---
id: task-164
title: 'DEF: Fix archive startup crash after v2.25'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-08-16 15:56'
updated_date: '2026-08-16 15:57'
labels: []
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user with archived festival data, I want the app to remain open after the main band list renders and archive state is checked, so that I can continue using active and archived festival views.

Scope: fix the remaining crash after v2.25, focused on archive/start-state rendering and archived festival list/detail data loading.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the active Wacken band list is shown, when the app checks festival archive state or background sync reloads the start state, then the app does not crash.
- [ ] #2 Given archived festival data is rendered, when archived band list ratings are collected, then Android-runtime-compatible collection APIs are used.
- [ ] #3 Automated regression tests cover archive/start-state crash risk and Android-incompatible Stream.toList usage in app/application production code paths touched by archive startup.
- [ ] #4 A signed hotfix APK is built and published after validation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Try to capture a constrained AndroidRuntime crash log from the connected emulator.
2. Inspect MainActivity start-state and archived festival list/detail use cases for Android runtime incompatibilities.
3. Add regression coverage that archive/start-state production code does not use Stream.toList in runtime paths.
4. Replace incompatible collection calls with Collectors.toList and run targeted tests plus release validation.
5. Publish a signed hotfix release if validation passes.
Architecture impact: not architecture-significant; this is an Android/runtime compatibility defect in existing application/app code.
<!-- SECTION:PLAN:END -->
