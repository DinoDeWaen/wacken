---
id: task-89
title: 'US: Use full splash only on startup sync'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-11 15:18'
updated_date: '2026-06-11 18:30'
labels:
  - android
  - ui
  - sync
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Split sync visuals between startup and later app activity.

As a festival attendee, I want the big Dino Metal artwork only during startup sync and a lighter moving sync animation over the current screen for later syncs so that normal app use is not interrupted by a full-screen splash after startup.

Scope: keep the full Dino Metal image for first startup/start-of-session sync and use an over-current-view moving sync animation for manual sync, lifecycle reactivation sync, and sync-and-exit. Out of scope: changing sync timing, sync data rules, authentication, or release signing.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the app starts and initial sync is running, when the sync visual is shown, then the full Dino Metal splash image is used.
- [x] #2 Given I trigger sync after startup from Settings or another in-app action, when the sync visual is shown, then the current screen remains visible and only a moving sync animation/status overlay appears.
- [x] #3 Given the app is reactivated after startup and sync runs, when the sync visual is shown, then it uses the over-current-view animation rather than the full Dino Metal splash.
- [x] #4 Given I use sync-and-exit after startup, when the sync visual is shown, then it uses the lighter over-current-view sync animation and still exits after sync completes.
- [x] #5 Automated tests or focused characterization coverage prove the startup-vs-later sync visual selection behavior where practical, with manual visual validation steps documented.
- [x] #6 README, business requirements, ADR, and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added SyncVisualPolicy and SyncVisualMode to select full startup splash only for initial startup sync and compact overlay for later syncs.
2. Covered the policy with focused app unit tests.
3. Updated MainActivity sync overlay rendering to support full-image and compact-over-current-view modes.
4. Routed initial startup sync through the full splash mode and later lifecycle/sync-exit paths through compact mode.
5. Added a lightweight rotating sync indicator in Settings for manual sync over the current Settings view.
6. Ran focused app tests, Android compile, and git diff checks.
Architecture impact: not architecture-significant; this is Android presentation state around existing sync behavior.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Sync visuals now distinguish startup from later syncs. The first non-exit sync uses the full Dino Metal splash. Later MainActivity syncs hide the image/scrim and show only the moving sync ring/status over the current screen. Settings manual sync keeps the Settings screen visible and shows a rotating lightning sync indicator while the sync runs.

## Acceptance criteria validation

- AC1: `SyncVisualPolicy` returns `FULL_STARTUP_SPLASH` for the initial non-exit sync; MainActivity shows the Dino image layers for that mode.
- AC2: Settings manual sync now uses a rotating indicator over the current Settings screen, and MainActivity later syncs use compact overlay mode.
- AC3: MainActivity reactivation sync after `syncAttempted` uses compact overlay mode.
- AC4: Sync-and-exit uses compact overlay mode and still calls `finishAndRemoveTask()` after sync completes.
- AC5: `SyncVisualPolicyTest` covers startup, after-startup, and sync-and-exit visual selection; manual visual validation steps are documented below.
- AC6: README, business requirements, ADR, and diagram impact are recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.SyncVisualPolicyTest :app:compileDebugJavaWithJavac
- git diff --check

### Manual validation

- Launch the app and verify the first startup sync shows the full Dino Metal splash.
- Trigger sync after startup and verify the current screen remains visible with only the compact moving sync/status overlay.
- Open Settings, tap Sync from Supabase, and verify Settings remains visible while the lightning indicator rotates.
- Tap Sync and exit after startup and verify compact sync feedback appears and the app exits after sync completes.

## TDD / BDD / approval-test evidence

- Added focused unit coverage for sync visual selection before wiring MainActivity modes.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: none, because this task refines an existing sync visual behavior and does not change setup, commands, architecture, or troubleshooting.

## Business requirements impact

Business requirements impact: none, because this implements the requested sync presentation refinement without changing sync timing, data rules, authentication, or scheduling behavior.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Added sync visual selection policy and unit tests.
- Added full-startup/compact overlay modes to MainActivity.
- Added lightweight Settings sync animation over the current view.

## Risks and follow-up

- Installed-device visual validation remains recommended because local Android UI screenshot automation is not configured for these native Activities.
<!-- SECTION:NOTES:END -->
