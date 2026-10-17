---
id: task-64
title: 'US-065: Show each person''s rating stars on band overview'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-09 16:34'
updated_date: '2026-06-09 16:52'
labels:
  - ui
  - rating
  - mvp2
dependencies: []
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner group member, I want to see the stars given by each person as a small detail on the band overview, so that I can quickly understand the group opinion without opening the band detail screen.

In scope:
- Add a subtle read-only per-person star summary to each band row/card on the band overview.
- Keep the existing overview layout, sorting, navigation, sync, and rating workflow unchanged.
- Show only available group member ratings; do not invent placeholder ratings.

Out of scope:
- Editing another person's rating from the overview.
- Reworking the band overview layout or schedule screen.
- Changing rating storage, sync rules, or group decision logic.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the band overview is displayed and group ratings are available, then each band shows a compact per-person star detail for the people who rated that band.
- [x] #2 Given a person has not rated a band, then the overview either omits that person from the small detail or shows a subtle unrated state without drawing attention away from the band row.
- [x] #3 Given the user taps or uses the existing rating controls, then the current overview rating and navigation behavior remains unchanged.
- [x] #4 Given the per-person stars are shown, then they are read-only on the overview and do not allow editing another person's rating.
- [x] #5 Given the band row is shown on a narrow Android screen, then the per-person star detail fits without horizontal scrolling or clipping the main band, rating, stage, date, and time information.
- [x] #6 Focused validation covers the display formatting for rated and unrated people where the current test setup supports it.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected MainActivity band overview rendering, ListBandsUseCase, RatingRepository.findAll(), RoomRatingRepository, and sync behavior.
2. Added application-level coverage for compact per-person rating summaries, including rated users, unrated zero ratings, and ratings for other bands.
3. Extended BandListItem with immutable per-person rating stars and display helpers, derived in ListBandsUseCase from existing rating repository data.
4. Rendered per-person stars as muted read-only secondary text under the band name in the Android overview row, preserving existing rating controls, actions, sort, and navigation.
5. Updated README and business requirements for the public overview behavior.
6. Validated with focused application tests, full unit tests, QA scenarios, Android compile, debug APK assembly, and git diff whitespace check.

Deviation: no persistence, sync, or Supabase contract change was made; the implementation uses the existing SavedRating user identity. Friendly display-name mapping remains a possible follow-up if required.
Architecture impact: not architecture-significant; existing Clean Architecture boundaries and repository ports were reused. No approval or ADR required.
README impact: updated. Business requirements impact: updated. Diagram impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented compact per-person rating stars on the Android band overview. Each band list item now carries read-only per-person rating stars derived from existing group ratings, excluding unrated/cleared `0` values and ratings for other bands. The overview renders the detail as muted secondary text under the band name, keeping the existing rating control, row actions, sorting, and detail navigation unchanged.

README and business requirements were updated because this is user-visible overview behavior.

## Acceptance criteria validation

- AC1: `ListBandsUseCase` now includes compact per-person rating details for available group ratings; `MainActivity` renders them under the band name.
- AC2: Ratings with value `0` are omitted from the compact detail, so missing/unrated people do not add visual noise.
- AC3: The existing overview rating control and row navigation remain in place; saving a rating still uses `RateBandUseCase`.
- AC4: Per-person stars are rendered as `TextView` display text only; editing another person's rating is not introduced.
- AC5: The detail is a single-line, ellipsized, muted secondary line inside the band cell; row height was increased slightly without adding columns or horizontal scrolling.
- AC6: Application tests cover rated users, unrated zero values, and other-band filtering; Android compile validates presentation wiring.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test --tests be.wacken.planner.application.ListBandsUseCaseTest -x :application:jacocoTestCoverageVerification`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`
- `git diff --check`

### Manual validation

Not run on a device or emulator in this environment. The Android screen is compile-validated and the application-level formatting behavior is covered by tests.

## TDD / BDD / approval-test evidence

Added focused application tests for the new overview display model before wiring the Android row. This is new behavior, so no approval baseline was needed. Existing QA scenarios continue to cover the externally visible listing/rating workflow.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

Existing Clean Architecture boundaries were reused: group rating detail is derived in the application use case from the existing `RatingRepository.findAll()` port, and Android only renders the display model. No persistence schema, sync contract, domain decision rule, or dependency changed.

## README impact

README impact: updated Basic Functionality to mention compact read-only per-person star details on overview rows.

## Business requirements impact

Business requirements impact: updated current implemented capabilities and added BR-060 for compact read-only per-person group ratings on the band overview.

## Diagram impact

Diagram impact: none, because module boundaries, dependencies, and containers did not change.

## Commits / logical change list

- Added `PersonRatingStars` and extended `BandListItem` with immutable per-person rating display data.
- Updated `ListBandsUseCase` to derive per-person non-zero ratings for each listed band from existing synced rating data.
- Added application tests for per-person overview rating summaries.
- Updated `MainActivity` band rows to show muted read-only per-person stars under the band name and refresh that detail after current-user rating saves.
- Updated README and business requirements.

## Risks and follow-up

The implementation uses the existing saved rating user identity. In synced Supabase data that identity is currently `user_id`; a later story can add friendly profile/display-name mapping if the overview should show names such as Dino/Sofie instead of technical identifiers.
<!-- SECTION:NOTES:END -->
