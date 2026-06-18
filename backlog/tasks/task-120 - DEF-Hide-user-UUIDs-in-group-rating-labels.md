---
id: task-120
title: 'DEF: Hide user UUIDs in group rating labels'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-18 19:02'
updated_date: '2026-06-18 19:13'
labels:
  - defect
  - ui
  - ratings
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The band overview and detail screens show raw Supabase UUIDs next to group rating stars. Users should see recognizable short names instead, using Dino/Sofie when known and compact initials for unknown identifiers.

Business value: group ratings remain readable on small screens and no internal user identifiers leak into the UI.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a group rating belongs to Dino or Sofie, when the band overview or detail renders per-person ratings, then the label shows Dino/Sofie or compact D/S instead of a UUID.
- [x] #2 Given a group rating belongs to an unknown UUID, when the UI renders per-person ratings, then it does not display the raw UUID and instead shows a compact fallback label.
- [x] #3 Stored rating identities remain unchanged so sync and ownership logic still use stable IDs.
- [x] #4 A focused automated regression test covers UUID display-name formatting.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added application regression tests for compact rating labels, including known Dino/Sofie UUIDs and unknown UUID fallback.
2. Implemented display-only label formatting in PersonRatingStars while leaving SavedRating.userName identities unchanged for sync/repository behavior.
3. Updated existing compact summary expectations to the D/S label format.
4. Bumped the local release to V2.15, added release notes, deleted the previous release APK, rebuilt a fresh signed APK, and verified APK metadata/signing.

Deviation: the focused test run initially failed because an existing summary assertion expected full names; that expectation was updated to the new compact UI behavior. Architecture impact remained not architecture-significant. No ADR was required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed the group-rating label defect by changing only the application presentation model: `PersonRatingStars.displayText()` now renders compact labels instead of raw UUID-like identities. Known Dino and Sofie IDs from the synced data render as `D` and `S`; unknown UUID-like identifiers render as `U`. Stored `SavedRating.userName()` values are unchanged, so sync and ownership logic still use stable IDs.

Created V2.15 release metadata and rebuilt the signed local release APK at `app/build/outputs/apk/release/app-release.apk`.

## Acceptance criteria validation

- AC1: Covered by `PersonRatingStarsTest.rendersKnownSofieAndDinoIdsAsCompactLabels`; known synced IDs render as `D` and `S`.
- AC2: Covered by `PersonRatingStarsTest.hidesUnknownUuidValuesBehindCompactFallbackLabel`; unknown UUIDs render as `U` and the raw UUID is absent.
- AC3: Met by keeping the change inside `PersonRatingStars.displayText()` only; repository identities and `SavedRating.userName()` are not transformed.
- AC4: Covered by the new `PersonRatingStarsTest` regression test class.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `git diff --check`

### Manual validation

Not run on a physical Android device in this environment. Install V2.15 and open the band overview/detail group ratings; UUIDs should be replaced by compact `D`, `S`, or `U` labels.

## TDD / BDD / approval-test evidence

Used a defect regression test first: `PersonRatingStarsTest` captures known UUID and unknown UUID display behavior before relying on the formatter change. Existing application summary tests were updated for the intended compact display.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none, because this is a presentation-label formatting fix and does not change persistence, ports, adapters, or domain rules.

## README impact

README impact: updated release-note links to include V2.15.

## Business requirements impact

Business requirements impact: none, because BR-060 already requires compact per-person group ratings and this fix only removes leaked technical identifiers from that existing UI.

## Diagram impact

Diagram impact: none, because module boundaries and runtime relationships did not change.

## Commits / logical change list

- Added compact display-name formatting for group rating labels.
- Added regression tests for known and unknown UUID-like rating identities.
- Updated V2.15 release metadata and rebuilt the local signed APK.

## Risks and follow-up

Unknown UUID-like members show as `U` until a future task adds a profile-display-name sync path with appropriate Supabase RLS support.

Release APK: `app/build/outputs/apk/release/app-release.apk`
Version metadata: `versionCode 18`, `versionName 2.15`
SHA-256: `1f40426df15fcd9314230cef29985a89e3a7fe50b0031f1a4a17a17631f9ac05`
<!-- SECTION:NOTES:END -->
