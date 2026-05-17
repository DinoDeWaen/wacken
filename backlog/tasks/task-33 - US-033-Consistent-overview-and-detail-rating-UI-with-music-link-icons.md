---
id: task-33
title: 'US-033: Consistent overview and detail rating UI with music link icons'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-16 20:44'
updated_date: '2026-05-16 20:57'
labels:
  - ui
  - rating
  - music-links
  - mvp1
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee, I want the overview table and band detail screen to share the same dark visual style, rating interaction, and music-link actions, so that I can quickly preview and rate bands without learning two different UI patterns.

In scope:
- Fix overview star hover preview so stars fill left-to-right while hovering over valid 0-4 rating positions.
- Keep the overview as a compact dark table with Band, Rating, Stage, Date, and Time as the data columns.
- Add YouTube and Spotify icon buttons on the same row as each band, positioned next to the Rating control.
- Update the band detail screen to use the same color scheme and rating interaction as the overview.
- Show band name, short band explanation/biography from the band CSV data, rating, stage, day/date, time, and band links on the detail screen.
- Provide Home, YouTube, and Spotify icon buttons on the detail screen; Home returns to the overview.

Out of scope:
- Changing the existing 0-4 rating scale.
- Changing group scheduling rules.
- Adding music services beyond YouTube and Spotify.
- Changing CSV schemas unless implementation proves the current imported band data cannot support the requested content.
- Adding a new UI framework or design system.

Notes:
- Overview visual reference: /Users/dino/Desktop/Screenshot 2026-05-16 at 21.54.40.png.
- Detail visual reference: /Users/dino/Desktop/Screenshot 2026-05-16 at 22.39.59.png.
- Detail should follow the table overview color scheme, not copy the screenshot colors blindly.
- The requested row buttons belong in the same table row as the band, directly next to the Rating control.
- The rating domain remains 0-4; do not introduce a rating above 4.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the band overview is displayed, when bands have imported data, then each row uses the data columns Band, Rating, Stage, Date, and Time and keeps the YouTube and Spotify actions on the same row next to the Rating control.
- [x] #2 Given a band has a YouTube URL, when the overview row is displayed, then a recognizable YouTube icon button is shown next to the rating and opens the YouTube link without also opening the detail screen.
- [x] #3 Given a band has Spotify data, when the overview row is displayed, then a recognizable Spotify icon button is shown next to the rating and opens Spotify without also opening the detail screen.
- [x] #4 Given a band has no YouTube or Spotify data, when the overview row is displayed, then missing music-link buttons are not shown as broken or empty actions.
- [x] #5 Given the user hovers over rating stars in the overview, when the pointer moves across valid 0-4 rating positions, then stars fill left-to-right up to the hovered in-scale rating.
- [x] #6 Given the user selects a rating in the overview, when the rating is saved, then the selected 0-4 rating remains visible after hover ends.
- [x] #7 Given the user opens a band detail screen, when band CSV metadata is available, then the detail screen shows the band name and a short explanation or biography from that data.
- [x] #8 Given the user opens a band detail screen, when schedule data is available or missing, then the screen shows Stage, Day/Date, and Time using imported values or TBA where unavailable.
- [x] #9 Given the band detail screen is displayed, then it uses the same dark color scheme and compact visual language as the overview table.
- [x] #10 Given the band detail screen is displayed, when the user interacts with rating stars, then the rating behavior matches the overview: no explicit rating is quiet at rest, hover/focus reveals the control, hover fills left-to-right, and selection saves a 0-4 rating.
- [x] #11 Given the band detail screen is displayed, then Home, YouTube, and Spotify actions use recognizable icons; Home returns to the overview, and missing YouTube or Spotify data does not produce broken buttons.
- [x] #12 Automated tests cover the fixed rating display/selection behavior and band-detail data mapping where the current test setup supports it; BDD coverage is updated where practical for the externally visible workflow.
- [x] #13 README is updated if public behavior or usage guidance changes, or implementation notes explain why no README update was needed.
- [x] #14 Architecture impact is assessed before implementation; the 0-4 domain rating scale is preserved and no architecture-significant change is made without explicit approval.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Extended the existing Band model with optional biography/explanation from bands.csv while preserving the 0-4 rating scale.
2. Updated CSV import to strip biography_html into plain text and file-backed band persistence to retain biography with backward-compatible reads for existing rows.
3. Updated application detail data mapping and tests so detail shows stored biography and stored/fallback music links.
4. Added a shared Android RatingStarsView used by overview and detail; hover/touch previews fill left-to-right and saved ratings remain visible.
5. Added YouTube and Spotify icon buttons next to the overview rating control, and rebuilt the detail screen with matching dark layout, Home/YouTube/Spotify icons, schedule info, biography, and rating control.
6. Regenerated data/wacken-2026 CSV artifacts from the official Wacken JSON feed and rebuilt the debug APK.
7. Updated README, ran targeted/full validation, and prepared merge-ready task notes.

Deviation: Spotify icons are rendered with a compact music-note glyph in Spotify green because the project has no icon library or bundled brand icon assets. YouTube is rendered as a play glyph in the accent color.
Architecture approval: user approved option 2, adding optional biography/explanation to existing Band and repository persistence.
Architecture impact: limited extension through existing domain/application/infrastructure boundaries; no new port, module, framework, or rating-scale change.
ADR outcome: no ADR required because boundaries and persistence strategy did not change.
README impact: updated for overview/detail rating, music links, and biography display.
Diagram impact: not needed because module boundaries and dependencies did not change.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented task-33. The app now preserves imported band biography from bands.csv, shows it on the detail screen, and uses a shared star rating control for both overview and detail. The overview keeps Band, Rating, Stage, Date, and Time as the data columns and adds same-row YouTube/Spotify icon actions directly next to Rating.

The detail screen now uses the same dark color scheme as the table overview. It shows band name, imported biography when available, rating, Stage/Day/Time with TBA fallback, and Home/YouTube/Spotify icon actions. Home returns to the overview.

The 0-4 rating scale was preserved.

## Acceptance criteria validation

- AC1: Overview row keeps the requested data columns and places music actions next to Rating.
- AC2: YouTube icon button opens the stored YouTube URL and is separate from row navigation.
- AC3: Spotify icon button opens the stored Spotify artist URL and is separate from row navigation.
- AC4: Missing YouTube/Spotify values produce no broken icon button.
- AC5: Shared RatingStarsView previews hover/touch ratings left-to-right for in-scale values.
- AC6: Overview selection saves through RateBandUseCase and keeps the saved rating visible.
- AC7: ImportFestivalCsvUseCase stores plain-text biography from biography_html; BandDetailItem exposes it.
- AC8: Detail receives Stage/Day/Time from the selected overview row and falls back to TBA.
- AC9: Detail uses the same dark overview color scheme.
- AC10: Detail uses the same shared rating control and saves through RateBandUseCase.
- AC11: Detail has Home, YouTube, and Spotify icon actions; missing music links are omitted.
- AC12: Added/updated application and infrastructure tests for biography import, detail mapping, compact display helpers, and persistence; existing QA listing/rating scenario remains passing.
- AC13: README updated for public UI/import behavior.
- AC14: Architecture impact assessed; approved Band biography extension implemented without rating-scale or boundary changes.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:compileDebugJavaWithJavac
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug

### Manual validation

Not run on device/emulator in this environment. The Android UI compiles and the debug APK was regenerated at app/build/outputs/apk/debug/app-debug.apk.

## TDD / BDD / approval-test evidence

Added focused tests for biography import/detail mapping and persistence, then implemented the model/import/repository changes. Existing QA scenario coverage for listing/rating still passes. No approval tests were needed because this is an approved feature extension, not a legacy refactor.

## Architecture impact

- Architecture-significant change: approved limited domain/entity extension.
- Approval received: yes, user approved option 2.
- ADR: not needed because no architecture boundary, module, framework, port, or persistence strategy changed; the existing Band repository and file-backed persistence were extended in place.

## README impact

README updated to describe overview/detail rating, YouTube/Spotify links, and imported biography display.

## Diagram impact

No diagram update needed because module boundaries and dependencies did not change.

## Regenerated artifacts

- app/build/outputs/apk/debug/app-debug.apk
- data/wacken-2026/bands.csv
- data/wacken-2026/stages.csv
- data/wacken-2026/performances.csv
- data/wacken-2026/distances.csv
- data/wacken-2026/food.csv
- data/wacken-2026/SOURCE.md

CSV validation: 164 band rows, 17 columns, no duplicate ids or blank ids/names, 158 YouTube values, 155 Spotify artist ids, 163 biography values.

## Commits / logical change list

- Extended Band with optional biography.
- Imported biography_html as plain text and persisted it in file-backed band storage.
- Updated detail application model/use case/tests.
- Added shared Android star rating control.
- Added overview music icon actions next to Rating.
- Rebuilt detail screen with dark table-style layout and Home/YouTube/Spotify icons.
- Regenerated Wacken CSV artifacts and debug APK.

## Risks and follow-up

The project still has no Android instrumentation test harness, so hover visuals were not exercised on a real device/emulator. Spotify is represented by a compact music-note glyph in Spotify green rather than an official brand asset because no icon asset pipeline exists yet.
<!-- SECTION:NOTES:END -->
