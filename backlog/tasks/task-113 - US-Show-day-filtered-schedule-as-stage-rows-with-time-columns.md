---
id: task-113
title: 'US: Show day-filtered schedule as stage rows with time columns'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-16 20:02'
updated_date: '2026-06-17 19:23'
labels:
  - schedule
  - ui
  - calendar
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want to filter the group schedule by day and view it with stages on the left and time across the top, so that the schedule matches the Wacken app pattern and overlapping acts are easier to scan on a phone.

Business value: the schedule is the festival-day working view. Matching the familiar Wacken orientation makes it easier to compare stages at the same time and use the app during the festival.

In scope:
- Add an explicit day selector/filter for the schedule view.
- Rotate the current schedule grid so stage lanes are horizontal rows on the left and time runs left-to-right across the top.
- Position performance blocks by start/end time within their stage row.
- Keep existing schedule filters, locked/manual choices, barred/skipped markings, ratings, lost alternatives, and block-detail navigation working in the rotated view.
- Keep 02:00 festival-day cutoff and weekday/day labels.
- Use the provided Wacken app screenshots as visual reference while staying within the app's existing dark metal style.

Out of scope:
- Copying official Wacken app artwork or copyrighted images.
- Adding band photos unless a separate asset/import story provides image data for schedule blocks.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given multiple festival days exist, when I open the group schedule, then I can choose which day to view and the schedule only shows acts for that selected festival day.
- [x] #2 Given a selected day has performances, when the schedule is shown, then stages are listed as horizontal rows on the left and time is shown as columns across the top.
- [x] #3 Given two acts overlap on different stages, when the rotated schedule is shown, then each act appears in its own stage row at the correct time position instead of covering the other act.
- [x] #4 Given an act spans a time range, when it is rendered in the rotated schedule, then the block width reflects its start and end time and remains readable at common phone widths through horizontal scrolling or scaling.
- [x] #5 Given existing schedule controls are active, when I use hide-barred or star-threshold filters, manual/locked winner choices, barred scratch markings, lost alternatives, or block details, then those behaviors still work in the day-filtered rotated view.
- [x] #6 Given the festival day includes late-night acts, when the selected day is shown, then the view still includes performances through 02:00 before the next festival day starts.
- [x] #7 Automated layout/presentation tests cover day filtering, stage-row ordering, time-position calculations, and preservation of existing block interactions; Android compile validation passes.
- [x] #8 Business requirements and README impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected the existing schedule activity, layout helper, filters, manual lock handling, and schedule presentation tests.
2. Added focused Android unit tests for selected-day fallback/selection, stage-row exposure, rotated time positioning, existing late-night cutoff behavior, and stage-lane ordering.
3. Added `ScheduleDaySelection` and extended `ScheduleCalendarLayout` with stage-row and horizontal time-position helpers while preserving existing vertical helper methods used by tests.
4. Updated `ScheduleActivity` to show a day selector, render one selected day, rotate the grid to stage rows with time columns across the top, and keep existing filters, scratch markings, locked choices, detail navigation, and alternative selection wiring.
5. Updated README and business requirements from stage-column wording to day-filtered stage-row schedule behavior.
6. Ran relevant automated checks, deleted and rebuilt the signed release APK, verified signatures, package metadata, and SHA-256.

Architecture impact: not architecture-significant; this was an Android presentation refactor using existing application/domain schedule results, filters, locks, and schedule decision data. No ADR required.
Deviation: no persistence or domain-rule changes were needed. The old signed APK was rebuilt twice because a final cleanup happened after the first release build.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

The group schedule now renders one selected festival day at a time. A compact day selector chooses the day, stages are listed as horizontal rows on the left, and time runs left-to-right across the top with full-hour vertical lines and dotted half-hour vertical lines. Performance blocks are positioned by start/end time within their stage row, with horizontal scrolling for phone readability.

Existing hide-barred and star-threshold filters, manual/group locked choices, lock icon display, scratch markings, lost alternatives, decision details, and band-detail navigation remain wired through the same schedule candidate pipeline.

## Acceptance criteria validation

- AC1: `ScheduleDaySelection` selects a requested day or falls back to the first available day, and `ScheduleActivity` renders only that selected day.
- AC2: `ScheduleActivity` renders stage labels as rows and the time axis across the top.
- AC3: `ScheduleCalendarLayout.stageRowIndex` places overlapping acts on different stage rows by stage.
- AC4: `ScheduleCalendarLayout.leftOffsetMinutes` and `durationMinutes` drive horizontal block position/width; the schedule remains inside a `HorizontalScrollView`.
- AC5: Existing filter/manual selection/detail code paths remain in use; app tests for filters, locks, scratch styling, and details still pass.
- AC6: Existing late-night layout test still covers the 02:00 festival-day cutoff.
- AC7: Added/updated Android unit tests for day filtering and rotated layout helpers; Android compile validation passed.
- AC8: README and business requirements impact are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `git diff --check`
- Deleted previous `app/build/outputs/apk/release/app-release.apk` and rebuilt signed release APK.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `shasum -a 256 app/build/outputs/apk/release/app-release.apk`

APK metadata: package `be.wacken.planner`, `versionCode=15`, `versionName=2.12`, minSdk `23`, targetSdk `36`.

APK SHA-256: `ad37de13a6b5d044fbf6e9cb8edc59b2dae5ab1b45b2f9a164469824c2ba1861`.

### Manual validation

Not run on a physical Android device. Manual UAT should open the schedule, switch days, horizontally scroll time, and tap blocks to verify details and locked alternative selection.

## TDD / BDD / approval-test evidence

Added focused Android unit tests around the new day-selection and rotated layout helper behavior before completing the UI refactor. Existing tests continue to protect filters, lock behavior, scratch/barred styling, late-night cutoff, and schedule content formatting.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: updated MVP2 schedule wording to document the day-filtered stage-row schedule with time columns.

## Business requirements impact

Business requirements impact: updated calendar schedule capability, BR-063, BR-063a, BR-064, BR-064a, and glossary wording from stage columns to day-filtered stage rows with a horizontal time axis.

## Diagram impact

Diagram impact: none, because system structure and data flow did not change.

## Commits / logical change list

- Add selected-day schedule helper and tests.
- Extend layout helper with stage-row and horizontal time-position accessors.
- Rotate Android schedule rendering to selected day, stage rows, and top time axis.
- Preserve existing filters, locked choices, scratches, alternatives, and detail navigation.
- Update README and business requirements.

## Risks and follow-up

Manual device UAT is still needed for fine-grained spacing and phone ergonomics. The release APK was rebuilt and verified locally, but this task did not create an official new version tag or GitHub release.
<!-- SECTION:NOTES:END -->
