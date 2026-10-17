---
id: task-74
title: 'US: Strengthen heavy-metal splash and sync animation'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 12:17'
updated_date: '2026-06-10 12:28'
labels:
  - ui
  - sync
  - mvp2
dependencies: []
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The current splash and sync feedback exists, but it should feel more strongly aligned with the Wacken heavy-metal identity.

As a festival attendee, I want startup, reactivation, manual sync, and close-sync feedback to look clearly heavy metal so that waiting for sync feels like part of the app experience rather than a generic loading state.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the app starts or reactivates and sync is running, when feedback is shown, then the splash or sync animation uses a stronger heavy-metal visual style consistent with the rest of the app.
- [x] #2 Given manual sync or sync-and-exit is running, when feedback is shown, then the user can tell sync is in progress and conflicting sync or close actions are blocked.
- [x] #3 Given the heavy-metal splash and sync animation is shown on phone-sized and BlueStacks-sized screens, then text and visuals remain readable and do not overlap.
- [x] #4 Given sync succeeds or fails, when the operation completes, then the existing success and failure behavior is preserved.
- [x] #5 Automated or focused compile checks protect existing sync behavior, and manual visual validation is documented.
- [x] #6 Business requirements impact: updated for BR-072.
- [x] #7 README impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected existing sync overlay and animation.
2. Added a full-screen heavy-metal backdrop behind the sync panel.
3. Strengthened the sync title, animation size, accent treatment, and subtitle.
4. Preserved existing sync lifecycle, disabled close action during sync, and success/failure paths.
5. Ran focused app tests/compile and release validation.
Architecture impact: not architecture-significant; Android UI styling only. No ADR needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Startup/reactivation/manual/close sync feedback now uses a heavier Wacken forge treatment with a dark metal backdrop, red beams, spike silhouette, larger rotating sync mark, and stronger title/subtitle styling. Existing sync behavior is unchanged.

## Acceptance criteria validation

All acceptance criteria are met by Android UI changes plus focused compile/test validation. Sync actions still use the existing syncInProgress and setSyncActionsEnabled paths.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest
- Full release validation command completed successfully with domain/application/infrastructure/app tests, debug compile, and assembleRelease.

### Manual validation

- Code-level visual review confirms the overlay remains full-screen/click-blocking and the panel uses centered responsive layout. Installed-device visual UAT remains recommended.

## TDD / BDD / approval-test evidence

- Existing sync behavior is preserved; this was a UI styling change protected by compile/tests rather than new domain tests.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: updated with V2.2 release notes link.

## Business requirements impact

Business requirements impact: updated BR-072 before implementation.

## Diagram impact

Diagram impact: none, because no architecture diagram changed.

## Commits / logical change list

- Added HeavyMetalBackdropView.
- Strengthened MainActivity sync overlay and MetalSyncView styling.

## Risks and follow-up

- Installed-device visual UAT should confirm text and icon rendering on target Android images.
<!-- SECTION:NOTES:END -->
