---
id: task-14
title: 'US-014: Band detail rating screen with music links'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-15 06:33'
updated_date: '2026-05-15 11:56'
labels:
  - mvp1
  - rating
  - ui
dependencies:
  - task-7
  - task-8
  - task-12
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## US-014: Band detail rating screen with music links

**As an** attendee
**I want** a band detail screen inspired by the official Wacken band detail page with rating stars and optional music links
**So that** I can review a band and rate it without leaving the rating flow

### Notes
- Source: `backlog/docs/business-requirements.md` BR-036.
- The screen should be inspired by the Wacken page, not a direct copy.
- YouTube and Spotify links are optional band metadata and should render only when available.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a band exists When I open its detail screen Then I see the band information needed for rating
- [x] #2 Given a band has a stored rating When I open its detail screen Then the selected star rating is displayed
- [x] #3 Given I select a 0-4 star rating on the detail screen When I save or leave the screen Then the rating is stored through the application use case
- [x] #4 Given a band has YouTube or Spotify links When I open its detail screen Then those links are available from the screen
- [x] #5 Given a band has no YouTube or Spotify links When I open its detail screen Then the screen does not show broken or empty link controls
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added application tests for a band detail use case: found band detail, stored/default rating display, optional YouTube/Spotify links, blank-link removal, and missing-band behavior.
2. Implemented `BandDetailItem`, `MusicLinks`, and `ShowBandDetailUseCase` using existing `BandRepository`, `RatingRepository`, and `EffectiveRatingResolver`; optional music links remain application input until metadata import is refined.
3. Added a minimal Android `BandDetailActivity` that displays band info, selectable 0-4 star buttons, default-rating text, and only non-empty music link buttons.
4. Wired star selection to the existing `RateBandUseCase`; current Activity storage is in-memory because durable storage is not part of MVP 1 yet.
5. Validated with application tests, full Gradle tests, and debug APK build.
6. README update not needed because setup, commands, architecture, and documented run behavior did not change.

Architecture impact: not architecture-significant; avoided domain metadata/schema changes and reused existing repositories/use cases. No ADR needed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added the minimal band detail rating flow. The application layer can now return band detail state with current/default rating and optional YouTube/Spotify links. Android has a new detail Activity that renders the band name, selected/default rating, 0-4 star buttons, and only real music link buttons. Selecting a star saves through `RateBandUseCase`.

## Acceptance criteria validation

- AC1: Covered by `ShowBandDetailUseCaseTest.returnsBandInformationNeededForRating` and APK build validation of `BandDetailActivity`.
- AC2: Covered by `ShowBandDetailUseCaseTest.displaysStoredRatingAsSelectedStarRating`.
- AC3: Covered by existing `RateBandUseCaseTest.storesValidRatingForBand` plus `BandDetailActivity` wiring to `RateBandUseCase` on star selection.
- AC4: Covered by `ShowBandDetailUseCaseTest.includesAvailableMusicLinks`.
- AC5: Covered by `ShowBandDetailUseCaseTest.removesBlankMusicLinksFromDetail`; Android renders buttons only for present links.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :application:jacocoTestCoverageVerification`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug`

### Manual validation

Not run on device. The Activity is registered and build-validated, but no emulator/manual click-through was performed in this environment.

## TDD / BDD / approval-test evidence

Added failing application tests for detail behavior first, then implemented the use case and view model until tests passed. No approval tests were needed because this was new behavior.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: not needed; existing Clean Architecture boundaries and repository ports were reused. Permanent band metadata schema remains deferred to CSV/scraping refinement tasks.

## README impact

README not updated because setup, commands, architecture, troubleshooting, and documented public behavior did not change.

## Diagram impact

No diagram update needed because module boundaries and dependencies did not change.

## Commits / logical change list

- Added band detail application state/use case and tests.
- Added optional music link normalization.
- Added minimal Android detail Activity and manifest entry.

## Risks and follow-up

The detail Activity currently uses in-memory data, and list-to-detail navigation is still skeletal because durable storage/navigation is not defined yet. CSV/scraping metadata refinement should decide where YouTube/Spotify links are imported and stored.
<!-- SECTION:NOTES:END -->
