---
id: task-high.8
title: 'US: Show archived festivals with the active festival browsing layout'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-14 09:41'
updated_date: '2026-08-14 18:28'
labels:
  - user-story
  - archive
  - ui
  - ratings
dependencies: []
parent_task_id: task-high
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want an archived festival to use the same browsing layout as the active festival so that looking back at Wacken feels like opening the original festival, not a reduced summary screen.

Business value: Archived festivals remain useful and familiar, with the same band list and band detail experience users already trust.

Scope: archived festival opens to the same style band list as the active festival; archived band rows show band, planning rating, stage, date, time, and music-link actions where available; tapping a band opens a band detail screen with the same visual sections as the active band detail screen, including image, Your Rating, Real Rating, Running Order, Band Links, and biography where available. Archived data remains read-only unless a later requirement explicitly allows editing.

Out of scope: changing active festival behavior, editing archived ratings, adding analytics dashboards, fuzzy band matching, or multiple active/upcoming festivals.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a user opens an archived festival, when archived band data exists, then the screen uses the same table layout and visual style as the active festival band list.
- [x] #2 Given an archived band has stage/date/time/music-link metadata, when the archived list is shown, then those values appear in the same columns/actions as the active festival list.
- [x] #3 Given a user taps an archived band, then the detail screen uses the same visual sections as the active band detail screen: image, Your Rating, Real Rating, Running Order, Band Links, and biography where available.
- [x] #4 Given the archived festival is read-only, then rating controls and reset actions do not modify archived data; if controls are visible for layout parity, they must be disabled or otherwise clearly non-editing.
- [x] #5 Automated tests or characterization coverage protect active festival layout behavior while adding archived layout parity.
- [x] #6 README impact, business requirements impact, diagram impact, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Characterized the active band list and active band detail layout in MainActivity and BandDetailActivity.
2. Updated ArchivedFestivalActivity to render the same table shape: Band, Rating, action links, Stage, Date, and Time.
3. Updated archived rows to use read-only RatingStarsView plus YouTube/Spotify action buttons where band metadata exists.
4. Replaced the reduced ArchivedBandDetailActivity summary with active-style sections: image, Your Rating, Real Rating, Personal History, Running Order, Band Links, and biography.
5. Added source-level regression coverage for archive list/detail layout parity and read-only controls.
6. Ran focused and full validation including debug APK assembly.

Deviation: the archive list still uses archive-specific text in the subtitle so the user can see which historical festival is open. No architecture approval or ADR was needed because the change reused existing UI/application patterns without changing boundaries.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Archived festivals now open as a read-only version of the active band browsing flow instead of the reduced summary screen. The archived list uses the active table shape with Band, Rating, action links, Stage, Date, and Time. Tapping a row opens an active-style detail screen with image, Your Rating, Real Rating, Personal History, Running Order, Band Links, and biography where available.

## Acceptance criteria validation

- AC1: ArchivedFestivalActivity now uses the same weighted table columns and alternating Wacken row styling as the active band list.
- AC2: Archived rows show planning stars, stage/date/time, and YouTube/Spotify actions when metadata exists.
- AC3: ArchivedBandDetailActivity renders the active-style detail sections including image, Your Rating, Real Rating, Running Order, Band Links, and biography.
- AC4: Archived rating stars and reset buttons are disabled; link and back buttons remain usable.
- AC5: ArchivedFestivalLayoutRegressionTest protects the archive table/detail layout and read-only controls.
- AC6: Impact notes are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug`
- `git diff --check`

### Manual validation

Install the built APK, open the archived Wacken festival, confirm the band list has the active table layout, open Airbourne, and confirm the active-style detail sections are visible and read-only.

## TDD / BDD / approval-test evidence

Added ArchivedFestivalLayoutRegressionTest as characterization coverage for the requested UI parity. Existing application tests continue to cover archive data retrieval.

## Architecture impact

- Architecture-significant change: no; this reuses existing activity, use-case, repository, and theme patterns.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because this fixes intended archived festival behavior already described by the project requirements.

## Business requirements impact

Business requirements impact: none, because BR-086 already requires archived festival inspection through read-only band list and band detail screens.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- ArchivedFestivalActivity: active-style read-only band table with action links.
- ArchivedBandDetailActivity: active-style read-only detail screen.
- ArchivedFestivalLayoutRegressionTest: UI parity regression checks.

## Risks and follow-up

The archive view remains read-only by design. Full visual validation still needs to be performed on the target phone after installing the release APK.
<!-- SECTION:NOTES:END -->
