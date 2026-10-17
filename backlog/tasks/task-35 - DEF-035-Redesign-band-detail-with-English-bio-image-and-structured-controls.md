---
id: task-35
title: 'DEF-035: Redesign band detail with English bio image and structured controls'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-17 06:02'
updated_date: '2026-05-17 06:25'
labels:
  - defect
  - ui
  - detail
  - mvp1
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee, I want the detail screen to show the band image, English explanation, rating, schedule info, and music actions in a clear layout, so that I can quickly understand and rate a band.

Business value:
- Band details should support fast evaluation, not just rating. English text and visual context help the group decide which bands to inspect further.

In scope:
- Use the English biography from bands.csv, not German, when available.
- Show the band image on the left side of the main detail area.
- Show rating, stage, time, and action buttons on the right side of the main detail area.
- Show the explanation below the image/info area.
- Keep the same dark color scheme as the overview table.
- Keep the existing 0-4 rating scale.
- Use YouTube and Spotify icon buttons where available.

Out of scope:
- Changing the 0-4 rating scale.
- Changing group scheduling rules.
- Adding music services beyond YouTube and Spotify.
- Adding a new design system or UI framework.

Notes:
- This likely needs another approved model extension: store English biography and image URL/thumbnail URL from CSV.
- Detail layout should be responsive enough for phone screens; if side-by-side does not fit, the content should stack cleanly without overlap.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a band has English biography data in bands.csv, when the detail screen opens, then the English explanation is shown.
- [x] #2 Given both English and German biography fields exist, then English is preferred.
- [x] #3 Given no English biography exists, then the app falls back gracefully without showing broken text.
- [x] #4 Given a band has image metadata, when the detail screen opens, then the band image is shown on the left side of the main detail area.
- [x] #5 Given the detail screen opens, then rating, stage, time, Home, YouTube, and Spotify actions are grouped on the right side of the main detail area.
- [x] #6 Given the detail screen opens, then the explanation appears below the image/info area.
- [x] #7 Given schedule data is missing, then stage and time show TBA.
- [x] #8 Given a user hovers or focuses the rating control, then stars preview left-to-right using the 0-4 scale.
- [x] #9 Given a user selects a rating, then the rating is saved and remains visible.
- [x] #10 Missing YouTube, Spotify, or image data does not show broken buttons or broken images.
- [x] #11 Automated tests cover English biography mapping and image/link metadata where practical.
- [x] #12 README is updated if public behavior or usage guidance changes, or implementation notes explain why no README update was needed.
- [x] #13 Architecture impact is assessed before implementation; if storing English biography or images is architecture-significant, explicit approval is requested before coding.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Extend existing Band metadata with optional image URL and prefer English biography during import while preserving existing constructors and rating behavior.
2. Update file-backed persistence and application detail mapping/tests for English biography and image metadata.
3. Regenerate Wacken CSV so biography_html carries English source text for the current schema.
4. Redesign BandDetailActivity with image left, rating/stage/time/actions right, explanation below, and safe fallbacks for missing metadata.
5. Validate with targeted tests/compile and update README/task notes before closing.

Architecture impact: user approved executing this task after validating the business rules; implement as a limited extension of existing Band metadata through the existing repository, no new ports/modules/frameworks.
Complexity: standard because it touches domain/application/infrastructure/UI.
README/ADR/diagram impact: README likely updated if behavior wording changes; ADR and diagrams not expected because boundaries stay unchanged.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented the band detail redesign defect. The import path now prefers English biography text, stores image metadata on Band through the existing repository, and exposes image URL plus biography in BandDetailItem. The Wacken CSV artifacts were regenerated so biography_html now carries the English source text from the official feed.

BandDetailActivity now presents the band image on the left when available, with rating, stage/day/time, and Home/YouTube/Spotify actions grouped on the right. The explanation appears below that main image/info area. Missing image or link metadata is omitted instead of showing broken controls.

## Acceptance criteria validation

- AC1: Import now prefers English biography and regenerated CSV contains English biography text.
- AC2: Import chooses `biography` before `biography_html` when both are present, covered by ImportFestivalCsvUseCaseTest.
- AC3: Optional biography remains absent when missing; detail omits the paragraph.
- AC4: Band image URL is imported, persisted, exposed, and rendered on the left when available.
- AC5: Detail groups rating, stage/day/time, Home, YouTube, and Spotify actions in the right-side facts area.
- AC6: Biography paragraph is rendered below the main image/facts area.
- AC7: Existing TBA fallback remains for missing stage/day/time.
- AC8: Detail continues using shared RatingStarsView with 0-4 hover/touch preview.
- AC9: Rating selection still saves through RateBandUseCase.
- AC10: Image and link controls are only rendered when metadata is present.
- AC11: Tests cover English biography preference, image metadata import/detail mapping, and persistence.
- AC12: README updated for English biography and image metadata display.
- AC13: Architecture impact assessed; user approved executing the validated task and the implementation used the existing Band/repository boundary without new architecture.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:compileDebugJavaWithJavac

CSV validation:
- 164 band rows
- 17 columns
- 163 English biography values
- 164 image values
- no malformed rows detected

### Manual validation

Not run on device/emulator in this environment. Manual validation should import the regenerated data/wacken-2026 CSV set, open a band detail screen, and confirm image-left/facts-right/explanation-below layout.

## TDD / BDD / approval-test evidence

Updated application and infrastructure tests for English biography preference, image URL mapping, and persistence before validating the UI compile. No approval tests were needed because this was a feature defect correction, not a legacy refactor.

## Architecture impact

- Architecture-significant change: limited metadata extension to existing Band.
- Approval received: user validated and asked to execute the defect task containing this model-extension note.
- ADR: not needed because no new port, module, persistence strategy, or architecture boundary was introduced.

## README impact

README updated to mention English biography/explanation and image metadata on detail.

## Diagram impact

No diagram update needed because module boundaries and dependencies did not change.

## Regenerated artifacts

- data/wacken-2026/bands.csv
- data/wacken-2026/stages.csv
- data/wacken-2026/performances.csv
- data/wacken-2026/distances.csv
- data/wacken-2026/food.csv
- data/wacken-2026/SOURCE.md

## Commits / logical change list

- Extended Band and persistence with optional image URL.
- Preferred English biography during import and regenerated CSV data with English biography.
- Exposed image URL in BandDetailItem.
- Reworked detail layout to image-left, controls-right, explanation-below.
- Updated README.

## Risks and follow-up

Remote ImageView loading is compile-validated but not device-validated here. If Android does not render remote image URIs reliably on target devices, a follow-up should add an explicit image loading adapter or local image caching strategy.
<!-- SECTION:NOTES:END -->
