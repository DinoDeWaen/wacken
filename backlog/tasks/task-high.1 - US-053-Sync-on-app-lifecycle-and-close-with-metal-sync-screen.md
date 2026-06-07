---
id: task-high.1
title: 'US-053: Sync on app lifecycle and close with metal sync screen'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-07 15:14'
updated_date: '2026-06-07 15:19'
labels:
  - ux
  - android
dependencies: []
parent_task_id: task-high
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a signed-in Wacken Planner user using the same account on multiple Android devices, I want ratings and festival data to sync when the app starts, reactivates, or closes, so scores stay visible across devices without relying on force-closing the app.

In scope:
- Add a visible close action that runs Supabase sync before closing the app.
- Sync automatically on app start and whenever the band overview is reactivated.
- Keep the manual Sync from Supabase action working.
- Show a Wacken/metal-themed splash or sync overlay with animated sync feedback while sync is running.
- Preserve local-first rating saves and cached data on sync failure.

Out of scope:
- Background services, scheduled sync, push notifications, conflict-rule changes, backend schema changes, and multi-group behavior.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a signed-in user starts the app, when the band overview opens, then Supabase master data and group ratings sync before cached data is presented as current.
- [x] #2 Given the app returns from background or a detail/import screen, when the band overview is reactivated, then Supabase sync runs and the visible ratings refresh after completion.
- [x] #3 Given the user taps the close action, when sync succeeds, then the app closes only after master data and ratings have synced with Supabase.
- [x] #4 Given sync fails during startup, reactivation, manual sync, or close, then local cached ratings remain available, a clear failure message is shown, and the user can retry manually.
- [x] #5 Given sync is running, then a Wacken/metal-themed splash or sync animation is visible and conflicting sync/close actions are disabled.
- [x] #6 Automated or focused validation covers the lifecycle and close sync behavior where feasible.
- [x] #7 README impact and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [x] #8 Architecture impact is assessed; ADR impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected the existing MainActivity sync flow and kept the change scoped to Android UI/lifecycle orchestration.
2. Repaired automatic sync so MainActivity starts Supabase master-data and ratings sync on every overview onResume when no sync is already running.
3. Added a full-width Sync & close action that runs the same Supabase sync before finishAndRemoveTask.
4. Added a full-screen Wacken/metal sync overlay with a custom animated steel ring during startup, reactivation, manual sync, and close sync; sync/close actions are disabled while sync is running.
5. Updated README and business requirements for lifecycle sync, close sync, and sync feedback.
6. Ran app compile, app unit tests, debug APK assembly, and diff whitespace validation.
7. Closed acceptance criteria with implementation notes.

Deviation: the existing uncommitted MainActivity changes already contained part of the requested UI; I repaired and completed that work instead of replacing it. Architecture impact: not architecture-significant; existing Supabase sync repositories and Activity lifecycle/UI code are reused. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Added automatic Supabase master-data and ratings sync from MainActivity on every overview activation when no sync is already running. This covers app start, returning from another app, and returning from detail/import screens.
- Added a full-width **Sync & close** action that runs the same sync path before calling `finishAndRemoveTask()`. On sync failure the app remains open, local cached/pending ratings are preserved, and the stale-data message remains visible.
- Added a full-screen Wacken/metal sync overlay with an animated steel ring for startup, reactivation, manual sync, and close sync. Sync and close buttons are disabled during sync.
- Updated README and business requirements to make lifecycle sync, close sync, and sync feedback part of the documented MVP1 behavior.

## Acceptance criteria validation

- AC1: MainActivity `onResume()` starts Supabase master-data and rating sync before loading the band list as current.
- AC2: Every subsequent overview reactivation runs the same sync path unless a sync is already in progress, then reloads visible ratings.
- AC3: **Sync & close** runs sync first and closes only on success.
- AC4: Failure handling preserves cached data/pending ratings and leaves the manual retry button enabled with a clear stale-data message.
- AC5: The sync overlay and animation show while sync runs; sync/close actions are disabled.
- AC6: Focused Android compile/unit/package validation completed.
- AC7: README impact and business requirements impact are recorded below.
- AC8: Architecture impact assessed and ADR impact recorded below.

## How to test

### Automated tests

- `/bin/zsh -lc 'JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest'`
- `/bin/zsh -lc 'JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac'`
- `/bin/zsh -lc 'JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug'`
- `git diff --check`

### Manual validation

- Install `app/build/outputs/apk/debug/app-debug.apk`, sign in, confirm the WACKEN SYNC overlay appears at overview startup and the list loads after sync.
- Change a rating, return to the overview, and confirm sync runs and the visible rating refreshes.
- Tap **Sync & close** and confirm the sync overlay appears before the app closes.
- Force a network/auth failure and confirm cached data remains visible and **Sync from Supabase** can be retried manually.

## TDD / BDD / approval-test evidence

- Acceptance criteria were written in BDD form in Backlog.md before completing the implementation.
- This is Android lifecycle/presentation orchestration around existing sync use cases; no new domain policy was introduced for a focused unit test. Existing app unit tests were run as regression coverage, and compile/package checks validate the touched Activity code.
- No approval baseline was needed because this is not a legacy refactoring task.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: updated MVP1 and Supabase sync sections for lifecycle sync, close sync, manual retry, and metal sync overlay behavior.

## Business requirements impact

Business requirements impact: updated current product scope/capabilities and added BR-057, BR-058, and BR-059 for lifecycle sync, close sync, and sync feedback.

## Diagram impact

Diagram impact: none, because this change does not alter module/container relationships or data-flow architecture.

## Commits / logical change list

- `MainActivity.java`: automatic sync on every overview activation, Sync & close action, metal sync overlay/animation, and disabled sync actions while running.
- `README.md`: lifecycle/close sync behavior documented.
- `backlog/docs/business-requirements.md`: sync behavior added as business rules.

## Risks and follow-up

- No emulator/manual device pass was run in this turn; the APK builds and the manual validation steps above are ready for device verification.
- The app syncs every overview activation by request. If Supabase usage becomes too chatty, a later task can add throttling or background sync policy.
<!-- SECTION:NOTES:END -->
