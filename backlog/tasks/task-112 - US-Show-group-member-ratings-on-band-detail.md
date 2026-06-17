---
id: task-112
title: 'US: Show group member ratings on band detail'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-16 20:01'
updated_date: '2026-06-17 06:33'
labels:
  - band-detail
  - ratings
  - group
  - ui
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want to see the ratings other people gave on the band detail screen, so that I understand the group preference before deciding what to rate or attend.

Business value: band detail should expose the same shared group context as planning and overview, making disagreements and must-see ratings visible without leaving the detail page.

In scope:
- Show read-only per-person group ratings on the band detail screen when shared group rating data is available.
- Clearly distinguish my editable rating from other people's read-only ratings.
- Handle unrated group members and missing/offline group-rating data without broken UI.
- Keep existing biography, image, schedule, and music-link behavior intact.

Out of scope:
- Editing another user's rating.
- Adding multiple-group support.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given group ratings are available for a band, when I open that band detail, then I see each available group member's rating for that band.
- [x] #2 Given I am on the band detail screen, when my own rating is shown, then it remains the editable rating control and other people's ratings are read-only.
- [x] #3 Given a group member has not rated the band, when group ratings are displayed, then the UI shows an unrated/empty state instead of treating it as a veto.
- [x] #4 Given group ratings are not yet synced or are unavailable offline, when I open band detail, then the screen still works and shows a clear no-group-ratings state.
- [x] #5 Automated tests or focused app coverage prove detail rating presentation and separation between editable own rating and read-only group ratings; Android compile validation passes.
- [x] #6 Business requirements and README impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected band detail use case, Android detail rendering, and existing per-person rating summary behavior.
2. Added application tests for group ratings on detail, own editable rating separation, unrated group-member omission, and unavailable ratings.
3. Extended `BandDetailItem` and `ShowBandDetailUseCase` to include read-only per-person rating stars for the selected band.
4. Rendered group ratings on `BandDetailActivity` below the editable rating control without changing rating save behavior.
5. Updated README and business requirements.
6. Ran application/app tests, Android compile, diff checks, signed release APK rebuild, and APK verification.

Architecture impact: not architecture-significant; reused existing RatingRepository group rating data with no schema, sync, dependency, or boundary change.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Band detail now shows compact read-only group member ratings below the current user's editable rating stars. The editable rating control is unchanged. If group ratings are unavailable or not synced yet, the detail screen shows `Group ratings: not synced yet` instead of treating missing ratings as vetoes.

## Acceptance criteria validation

- AC1: `ShowBandDetailUseCase` includes available group ratings for the selected band in `BandDetailItem.personRatings()`.
- AC2: `BandDetailActivity` keeps the current user rating as the editable `RatingStarsView`; group ratings are rendered as read-only text.
- AC3: Ratings with value `0` are omitted from read-only group stars, so unrated users do not appear as vetoes.
- AC4: Empty or unavailable `RatingRepository.findAll()` produces an empty person-rating list and the Android screen shows `Group ratings: not synced yet`.
- AC5: Application tests cover detail rating presentation; Android compile validation passed.
- AC6: README and business requirements impact are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `git diff --check`
- Deleted previous `app/build/outputs/apk/release/app-release.apk` and rebuilt signed release APK.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `shasum -a 256 app/build/outputs/apk/release/app-release.apk`

APK metadata: package `be.wacken.planner`, `versionCode=15`, `versionName=2.12`, minSdk `23`, targetSdk `36`.

APK SHA-256: `142369447d367690d0821c416e1cde9f3c2e5ef56ca0dc240542f09b7de35559`.

### Manual validation

Not run on a physical device. Manual UAT should open a band detail after sync and verify the editable stars still change only the current user rating while the group summary is read-only.

## TDD / BDD / approval-test evidence

Added focused application tests for the new visible behavior before implementing the use-case and Android rendering changes. No approval baseline was needed because this is additive behavior.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: updated basic functionality to document per-person stars on band detail screens.

## Business requirements impact

Business requirements impact: updated current capabilities, band detail capability wording, and BR-060 for detail-screen group ratings.

## Diagram impact

Diagram impact: none, because system structure and data flow did not change.

## Commits / logical change list

- Extend band detail model with read-only person ratings.
- Populate group ratings from `RatingRepository.findAll()`.
- Render group ratings/empty state in Android band detail.
- Update README and business requirements.

## Risks and follow-up

- The app currently shows only users with explicit ratings available in local synced data; showing named unrated group members would require a group-member listing source, which is outside this story.
<!-- SECTION:NOTES:END -->
