---
id: task-158
title: >-
  Defect: Archived festival detail hides rated bands when lineup entries are
  missing
status: Done
assignee:
  - '@codex'
created_date: '2026-08-14 09:17'
updated_date: '2026-08-14 09:20'
labels:
  - defect
  - archive
  - ratings
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
When opening an archived festival, the screen can show no lineup entries even though planning ratings exist. The archive detail must still show bands and planning/personal ratings from available cached rating history.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given an archived festival has planning ratings but no lineup entries, when the user opens the archived festival, then the screen shows the rated bands instead of an empty lineup.
- [x] #2 Given planning ratings exist for an archived festival, then the archive screen shows the band names and rating values instead of only a count.
- [x] #3 Given personal rating events exist for an archived festival, then the archive screen shows them even when lineup entries are missing.
- [x] #4 Automated tests cover archive fallback and rating display behavior.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirmed the archive detail used only festival_lineup_entries for lineup and personal-history lookup.
2. Added archive fallback bands from planning ratings and personal events when lineup entries are missing.
3. Added explicit planning-rating display items and personal-event lookup by archived festival.
4. Wired Room and syncing repository support for findByUserAndFestival.
5. Added regression test for archive fallback and rating display behavior.
6. Ran validation, rebuilt signed release APK, and verified APK signature/metadata.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed the archived festival detail screen so it no longer shows an empty lineup when legacy/local data has planning ratings but no festival_lineup_entries rows. The archive now derives band names from lineup entries, planning ratings, and personal rating events, displays actual planning ratings with band/user/value, and can show personal post-show history for the archived festival even when lineup rows are missing.

## Acceptance criteria validation

- AC1: ArchivedFestivalHistoryUseCaseTest covers an archived festival with planning ratings and no lineup rows, and verifies rated bands are shown.
- AC2: Archive history now exposes planning rating display items; Android archive screen renders band/user/star values instead of only a count.
- AC3: PersonalBandRatingHistoryRepository now supports archived festival lookup; Room and syncing adapters delegate it, and the regression test covers personal events without lineup rows.
- AC4: Automated regression coverage added in ArchivedFestivalHistoryUseCaseTest.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac qaTest assembleDebug
- WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleRelease -x lintVitalAnalyzeRelease

### Manual validation

- Install app/build/outputs/apk/release/app-release.apk, open the archived festival, and verify rated bands and planning rating values are visible.

## TDD / BDD / approval-test evidence

- Added a failing archive regression test for missing lineup rows with existing ratings, then implemented the fallback and display behavior.

## Architecture impact

- Architecture-significant change: no, this is a scoped defect fix within the approved archive/rating repository model.
- Approval received: not required.
- ADR: ADR impact: none, because the approved ADR 0011 model is unchanged.

## README impact

README impact: none, because the README already states archived festival history is available; this fixes the implementation gap.

## Business requirements impact

Business requirements impact: none, because BR-102 already requires archived rating visibility with context.

## Diagram impact

Diagram impact: none, because module/container boundaries are unchanged.

## Commits / logical change list

- Derive archive bands from lineup entries, planning ratings, and personal events.
- Display planning rating rows instead of only a count.
- Query personal rating events by archived festival.
- Add regression test and signed APK build.

## Risks and follow-up

- If no real post-show ratings were ever recorded, the personal-history section will correctly stay empty; planning ratings are now shown separately.
<!-- SECTION:NOTES:END -->
