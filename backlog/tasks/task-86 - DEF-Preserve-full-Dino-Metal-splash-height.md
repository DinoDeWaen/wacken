---
id: task-86
title: 'DEF: Preserve full Dino Metal splash height'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 18:49'
updated_date: '2026-06-10 18:50'
labels:
  - android
  - ui
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Adjust the Dino Metal sync splash image scaling so the artwork is never vertically cropped on wide or differently shaped screens.

As a festival attendee, I want the splash image height to be fully respected on every screen so that the full Dino Metal artwork remains visible, with black side gutters when the screen is wider than the artwork.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the sync splash appears on a wide screen, when the image is rendered, then the full artwork height is visible and extra side space is black.
- [x] #2 Given the sync splash appears on a normal or narrow screen, when the image is rendered, then the artwork is scaled without vertical cropping.
- [x] #3 Given the Android app is compiled, then the splash scaling change packages without resource or compile errors.
- [x] #4 README, business requirements, ADR, and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Changed the splash ImageView background to black and scale type to FIT_CENTER.
2. Compiled the Android app and ran app unit tests to verify resource packaging.
3. Closed the task with validation and impact notes.
4. Create and publish the next signed release after the fix.

Deviation: none. Architecture impact: not architecture-significant; Android presentation-only scaling fix.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Changed the Dino Metal sync splash image from `CENTER_CROP` to `FIT_CENTER` and gave the `ImageView` a black background. This keeps the full artwork visible without vertical cropping and leaves black side gutters on wider screens.

## Acceptance criteria validation

- AC1: Wide screens now render the full image inside a black `ImageView`, so extra horizontal space is black.
- AC2: The splash uses `FIT_CENTER`, so the bitmap is scaled without vertical cropping.
- AC3: Android debug compile and app unit tests passed.
- AC4: README, business requirements, ADR, and diagram impact are recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac :app:testDebugUnitTest
- git diff --check

### Manual validation

- Not run on device in this environment; installed-device visual validation remains recommended after installing the release APK.

## TDD / BDD / approval-test evidence

- This is a visual scaling correction. Validation is compile/resource packaging plus existing app unit tests; no new business-rule test was required.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: none, because this task changes an existing Android splash visual scaling behavior and does not change setup, commands, architecture, or documented capability.

## Business requirements impact

Business requirements impact: none, because this implements the requested splash presentation refinement without changing product rules or scope.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Changed the sync splash image scale type to FIT_CENTER.
- Added a black ImageView background for side gutters.

## Risks and follow-up

- Installed-device visual validation remains recommended because local Android UI screenshot automation is not configured for this native Activity.
<!-- SECTION:NOTES:END -->
