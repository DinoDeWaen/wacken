---
id: task-84
title: 'US: Use Dino Metal image as sync splash screen'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 18:39'
updated_date: '2026-06-10 18:42'
labels:
  - android
  - ui
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Replace the current generated heavy-metal sync splash backdrop with the provided Dino Metal image asset.

As a festival attendee, I want the app sync splash to show the Dino Metal artwork so that startup and sync feel more visually heavy metal.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the app starts or sync overlay is shown, when the sync splash appears, then the provided Dino Metal image is used as the full-screen splash backdrop instead of the current generated backdrop.
- [x] #2 Given the sync overlay is visible, when sync status and animation are displayed, then they remain readable without hiding the Dino Metal artwork unnecessarily.
- [x] #3 Given the Android app is compiled, then the new drawable resource is packaged without resource or compile errors.
- [x] #4 README, business requirements, ADR, and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added the provided PNG as app/src/main/res/drawable-nodpi/splash_dino_metal.png.
2. Replaced the generated HeavyMetalBackdropView usage in the sync overlay with a full-screen ImageView using the new resource.
3. Kept sync animation/status readable in a compact translucent top strip so the artwork remains visible.
4. Ran app compile, app unit tests, and git diff checks.
5. Closed the task with validation and impact notes before creating the next release.

Deviation: none. Architecture impact: not architecture-significant; this is Android UI resource/presentation work only.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Replaced the custom-drawn sync splash backdrop with the provided Dino Metal PNG as a full-screen Android drawable resource. The existing sync spinner and status text remain visible in a compact translucent top strip so the artwork remains the main splash visual.

## Acceptance criteria validation

- AC1: The sync overlay now uses `R.drawable.splash_dino_metal` in a full-screen `ImageView` instead of `HeavyMetalBackdropView`.
- AC2: Sync status and animation are retained in a small translucent top overlay with two-line ellipsized status text.
- AC3: Android debug compile and app unit tests passed.
- AC4: README, business requirements, ADR, and diagram impact are recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac :app:testDebugUnitTest
- git diff --check

### Manual validation

- Not run on device in this environment; installed-device visual validation is included in the release/UAT risk.

## TDD / BDD / approval-test evidence

- This is a visual asset swap. Validation is compile/resource packaging plus existing app unit tests; no new business-rule test was required.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: none, because this task changes an existing Android splash visual and does not change setup, commands, architecture, or documented capability.

## Business requirements impact

Business requirements impact: none, because this implements the already requested heavy-metal splash presentation without changing product rules or scope.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Added `splash_dino_metal.png` as a drawable-nodpi resource.
- Replaced the custom sync backdrop with a full-screen image splash and compact readable sync strip.

## Risks and follow-up

- Installed-device visual validation remains recommended because local Android UI screenshot automation is not configured for this native Activity.
<!-- SECTION:NOTES:END -->
