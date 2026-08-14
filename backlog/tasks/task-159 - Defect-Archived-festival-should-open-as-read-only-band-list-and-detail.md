---
id: task-159
title: 'Defect: Archived festival should open as read-only band list and detail'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-14 09:24'
updated_date: '2026-08-14 09:31'
labels:
  - defect
  - archive
  - ratings
  - ui
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Archived festivals currently open as a summary panel. The archived festival should behave like the active festival browsing flow: show a band list first, allow opening a band detail screen, and show planning and personal ratings read-only. Personal ratings recorded at Wacken must be visible in the archive.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a user opens an archived festival, then the first screen is a read-only band list for that festival instead of a summary-only view.
- [x] #2 Given the user taps a band in an archived festival, then a read-only band detail screen opens for that band.
- [x] #3 Given planning ratings exist for the archived festival, then the archive list/detail shows those planning ratings without edit controls.
- [x] #4 Given real ratings were recorded before personal history sync existed, then archived Wacken shows them as personal rating history after migration or fallback.
- [x] #5 Automated tests cover archived band list/detail data and legacy real-rating fallback.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Replaced the archived festival summary panel with a read-only archived band list.
2. Added a read-only archived band detail activity opened from archived band rows.
3. Displayed archived planning ratings on list/detail without edit controls.
4. Added Wacken-only fallback from legacy real_ratings so old Wacken personal ratings appear even when no personal event rows exist.
5. Added regression tests for archived list data and legacy real-rating fallback.
6. Updated README/business requirements, ran validation, rebuilt signed release APK, and verified APK signature/metadata.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed the archived festival UX to match the active festival browsing pattern: opening an archived festival now shows a read-only band list, and tapping a band opens a read-only band detail screen. The archive uses lineup entries, planning ratings, personal rating events, and Wacken legacy real ratings as available data sources. Legacy real ratings are only treated as Wacken archive history and display with date unknown when no event timestamp exists.

## Acceptance criteria validation

- AC1: ArchivedFestivalActivity now renders a read-only band list instead of the summary-only archive panel.
- AC2: ArchivedBandDetailActivity opens from archived rows and shows read-only band detail sections.
- AC3: ListArchivedFestivalBandsUseCase and archived detail show planning stars/rows without save/reset controls.
- AC4: ViewArchivedFestivalHistoryUseCase falls back to legacy real_ratings for wacken-2026 when personal events are missing.
- AC5: ArchivedFestivalHistoryUseCaseTest and ListArchivedFestivalBandsUseCaseTest cover archive fallback, list data, planning rating display, and legacy real-rating fallback.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac qaTest assembleDebug
- WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleRelease -x lintVitalAnalyzeRelease

### Manual validation

- Install app/build/outputs/apk/release/app-release.apk, archive Wacken, open Wacken from the archive state, verify a read-only band list appears, tap a band, and verify planning and personal rating history sections are shown read-only.

## TDD / BDD / approval-test evidence

- Added regression tests for missing lineup rows and legacy real-rating fallback before implementing the archive list/detail behavior.

## Architecture impact

- Architecture-significant change: no, this is a scoped UX/defect fix within the existing approved archive and rating model.
- Approval received: not required.
- ADR: ADR impact: none, because ADR 0011 remains accurate.

## README impact

README impact: updated archived festival behavior to state archived festivals open as read-only band lists with band detail screens.

## Business requirements impact

Business requirements impact: updated BR-086/current capability wording to reflect read-only archive band lists and details.

## Diagram impact

Diagram impact: none, because module/container boundaries are unchanged.

## Commits / logical change list

- Add read-only archived band list and band detail screen.
- Add archive list use case and legacy Wacken real-rating fallback.
- Add regression tests and documentation updates.

## Risks and follow-up

- Legacy real ratings have no original created date, so they display as date unknown. New personal rating events continue to show exact created dates.
<!-- SECTION:NOTES:END -->
