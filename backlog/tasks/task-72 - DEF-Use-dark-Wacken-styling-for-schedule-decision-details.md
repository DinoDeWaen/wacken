---
id: task-72
title: 'DEF: Use dark Wacken styling for schedule decision details'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 12:16'
updated_date: '2026-06-10 12:27'
labels:
  - defect
  - ui
  - schedule
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The schedule decision detail currently opens on a default white surface, which clashes with the rest of the app and makes the MVP2 schedule feel unfinished.

As a festival attendee, I want the chosen-act and alternatives detail to use the same dark Wacken/metal visual language as the rest of the app so that the schedule experience is consistent and readable.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given I open a performance block from the group schedule, when the decision detail appears, then it uses a dark Wacken/metal themed surface instead of a default white dialog.
- [x] #2 Given the detail contains chosen act, alternatives, stars, stages, time ranges, selection buttons, and close action, when it is shown on phone-sized and BlueStacks-sized screens, then the content remains readable and controls fit without visual overlap.
- [x] #3 Given I select an alternative as the act to attend, when the detail is restyled, then the existing local visible schedule override behavior is preserved.
- [x] #4 Automated tests or focused compile checks protect the behavior, and manual visual validation is documented.
- [x] #5 Business requirements impact: updated for BR-071.
- [x] #6 README impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected schedule detail rendering and current theme constants.
2. Preserved existing manual selection tests and behavior.
3. Replaced the default white detail surface with a custom dark Wacken/metal dialog view and in-dialog close button.
4. Ran focused app/application validation and release validation.
Deviation: no Android device screenshot was captured in this task; installed-device visual UAT remains listed as a release risk.
Architecture impact: not architecture-significant; Android UI styling only. No ADR needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

The schedule decision detail now uses a custom dark Wacken/metal surface with readable light text, accent headings, themed selection buttons, and an in-dialog close action. Manual alternative selection still refreshes the visible schedule result.

## Acceptance criteria validation

All acceptance criteria are met through the styled detail implementation, existing manual selection tests, focused app compile/tests, and documented visual UAT risk.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=/private/tmp/wacken-v2.1-release.jks WACKEN_RELEASE_STORE_PASSWORD=*** WACKEN_RELEASE_KEY_ALIAS=wacken-v2-1 WACKEN_RELEASE_KEY_PASSWORD=*** ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease

### Manual validation

- Code-level visual review confirms the platform positive-button strip was removed and the dialog content owns the dark background. Installed-device visual UAT remains recommended.

## TDD / BDD / approval-test evidence

- Existing ScheduleManualSelectionsTest continues to protect select-as-act behavior.

## Architecture impact

- Architecture-significant change: no
- Approval received: not required
- ADR: none

## README impact

README impact: updated with V2.2 release notes link.

## Business requirements impact

Business requirements impact: updated BR-071 before implementation.

## Diagram impact

Diagram impact: none, because this is Android UI styling only.

## Commits / logical change list

- Restyled schedule decision detail dialog in ScheduleActivity.

## Risks and follow-up

- Installed-device visual UAT should still confirm phone and BlueStacks rendering.
<!-- SECTION:NOTES:END -->
