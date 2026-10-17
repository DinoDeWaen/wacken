---
id: task-32
title: 'US-032: Spreadsheet-style band overview with hover rating stars'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-16 19:58'
updated_date: '2026-05-16 20:23'
labels:
  - ui
  - rating
  - mvp1
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee, I want the band overview to look like a compact music-library table with the festival columns I need, so that I can quickly scan bands, schedule information, and ratings while rating unrated bands without opening detail screens.

In scope:
- Adapt the band overview visual style toward the provided compact dark table reference.
- Show the columns Band, Rating, Stage, Date, and Time.
- Let users set a band rating from the overview with hover-driven star feedback.

Out of scope:
- Changing import formats or festival data schemas.
- Changing group schedule decision rules.
- Reworking the band detail screen except where shared rating behavior requires consistency.

Notes:
- Visual reference screenshot: /Users/dino/Desktop/Screenshot 2026-05-16 at 21.54.40.png.
- The screenshot is a visual reference for density, dark table styling, row striping, typography, separators, and star interaction placement only; the app must use the Wacken-specific columns named in this story.
- Existing rating scale and storage rules must be checked during implementation so the UI representation stays consistent with current domain behavior.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given imported bands are available, when the user opens the band overview, then the list is displayed as a compact table with the columns Band, Rating, Stage, Date, and Time.
- [x] #2 Given a band has no stored rating, when the row is not hovered or focused, then the Rating column shows no stars for that band.
- [x] #3 Given a band has no stored rating, when the user hovers over or focuses the band row, then the Rating column shows five empty star positions as the available 0-4 rating control.
- [x] #4 Given the rating control is visible, when the pointer hovers over a valid 0-4 rating position, then the preview fills stars left-to-right up to the selected in-scale rating and never offers a rating above 4.
- [x] #5 Given the user selects an in-scale star rating in the overview, when the rating is saved, then the number of filled stars represents the 0-4 band rating and remains visible after the hover ends.
- [x] #6 Given a band already has a stored rating, when the band overview is displayed, then the Rating column shows the saved 0-4 rating with the correct number of filled stars.
- [x] #7 Given schedule data is incomplete for a band, when the overview is displayed, then missing Stage, Date, or Time values are handled without breaking table alignment or rating interaction.
- [x] #8 Automated tests cover the rating display and selection behavior where the current test setup supports it, with BDD coverage for the externally visible overview workflow where practical.
- [x] #9 README is updated if public behavior or usage guidance changes, or implementation notes explain why no README update was needed.
- [x] #10 Architecture impact is assessed before implementation; the 0-4 domain rating scale is preserved and no architecture-significant change is made without explicit approval.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected the existing Android band overview, application list item, rating use case, and historical rating stories.
2. Confirmed the requested five-star behavior conflicted with the existing 0-4 domain rating scale, stopped for user decision, and preserved the 0-4 scale after clarification.
3. Updated task acceptance criteria to reflect the approved 0-4 behavior.
4. Added compact overview display helpers on BandListItem for Date, Time, and explicit/default rating state, with focused application test coverage including the TBA edge case.
5. Replaced MainActivity card rendering with a compact dark table using Band, Rating, Stage, Date, and Time columns.
6. Added overview rating stars at the Android UI edge: default/unrated rows are blank at rest, row hover/focus shows five empty positions, star hover previews valid 0-4 ratings, and selection saves through RateBandUseCase.
7. Updated README Basic Functionality to describe the compact table overview.
8. Validated with application tests, Android compile, full Gradle test, QA scenarios, and debug APK assembly.

Deviation: no domain rating-scale change was made because the user explicitly chose to keep 0-4.
Architecture impact: not architecture-significant; existing domain/application/infrastructure boundaries and rating repository/use case were reused.
Approval and ADR outcome: no architecture approval or ADR required after preserving the existing scale.
README impact: updated for visible overview behavior.
Diagram impact: not needed because module boundaries and dependencies did not change.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented the compact dark band overview for task-32. The Android band list now renders a table with Band, Rating, Stage, Date, and Time columns, alternating dark rows, compact typography, and a rating column styled after the supplied screenshot.

The existing 0-4 rating scale is preserved. Unrated/default rows show no stars at rest, reveal five empty positions on row hover/focus, preview only valid 0-4 values, and save selected ratings through RateBandUseCase. Stored ratings remain visible after hover ends.

## Acceptance criteria validation

- AC1: MainActivity renders the table header and row columns Band, Rating, Stage, Date, and Time.
- AC2: Default/unrated rows render the rating control invisible at rest.
- AC3: Row hover/focus reveals five empty star positions.
- AC4: Star hover previews only in-scale 0-4 ratings.
- AC5: Star selection saves through RateBandUseCase and updates the visible saved rating.
- AC6: Explicit stored ratings are visible when rows render.
- AC7: BandListItem display helpers keep TBA schedule values intact, covered by test.
- AC8: Added focused application test coverage for compact overview display state; Android UI behavior is compile-validated.
- AC9: README updated for the public overview behavior.
- AC10: Architecture impact assessed; 0-4 scale preserved and no architecture-significant change made.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:compileDebugJavaWithJavac
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug

### Manual validation

Not run on a device or emulator in this environment. The Android screen is build-validated, and the UI logic is scoped to MainActivity.

## TDD / BDD / approval-test evidence

Added a focused failing application test for compact overview Date/Time and explicit/default rating state, fixed the TBA parsing edge case, then implemented the UI wiring. Existing QA scenarios continue to cover the externally visible listing/rating workflow at the application level. No approval tests were needed because this was a UI behavior change, not legacy refactoring.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required after preserving the existing 0-4 rating scale.
- ADR: not needed because no architecture boundary, module, persistence, framework, or domain model decision changed.

## README impact

README updated to document the compact table overview columns.

## Diagram impact

No diagram update needed because module boundaries and dependencies did not change.

## Commits / logical change list

- Updated README Basic Functionality for the table overview.
- Added BandListItem display helpers and tests.
- Reworked MainActivity from Wacken cards to compact table rows.
- Added overview star hover/selection behavior saving through RateBandUseCase.

## Risks and follow-up

The app has no Android UI test harness yet, so hover visuals were not exercised by instrumentation. The fifth visible star position acts as the 0-4 control affordance and does not introduce a rating above 4.
<!-- SECTION:NOTES:END -->
