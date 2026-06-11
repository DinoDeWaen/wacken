---
id: task-89
title: 'US: Use full splash only on startup sync'
status: To Do
assignee: []
created_date: '2026-06-11 15:18'
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
- [ ] #1 Given the app starts and initial sync is running, when the sync visual is shown, then the full Dino Metal splash image is used.
- [ ] #2 Given I trigger sync after startup from Settings or another in-app action, when the sync visual is shown, then the current screen remains visible and only a moving sync animation/status overlay appears.
- [ ] #3 Given the app is reactivated after startup and sync runs, when the sync visual is shown, then it uses the over-current-view animation rather than the full Dino Metal splash.
- [ ] #4 Given I use sync-and-exit after startup, when the sync visual is shown, then it uses the lighter over-current-view sync animation and still exits after sync completes.
- [ ] #5 Automated tests or focused characterization coverage prove the startup-vs-later sync visual selection behavior where practical, with manual visual validation steps documented.
- [ ] #6 README, business requirements, ADR, and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
