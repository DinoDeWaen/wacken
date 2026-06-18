---
id: task-121
title: 'UI: Rename clear rating button to Reset'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-18 19:21'
updated_date: '2026-06-18 19:24'
labels:
  - ui
  - ratings
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The band detail rating section currently shows a large `0` button for clearing a rating. This is technically correct because 0 means unrated, but it is confusing to users. Replace the visible label with `Reset` while preserving the existing clear-to-unrated behavior.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a user opens a band detail screen, when the clear rating action is shown, then the button label is Reset instead of 0.
- [x] #2 Given a user taps Reset, when the action succeeds, then their rating is still cleared to unrated value 0.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Changed the band detail clear-rating button label from `0` to `Reset`.
2. Preserved the existing click handler that saves rating value `0` for unrated/reset behavior.
3. Bumped the local release metadata to V2.16 and added release notes.
4. Deleted the previous local release APK, rebuilt a fresh signed APK, verified signing/metadata/checksum, and closed the task.

Deviation: no automated UI text assertion was added because the change is a direct Android widget label in Activity code and the existing app unit-test setup does not instantiate Activities; compile plus release build validates the touched code path. Architecture impact remained not architecture-significant.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Renamed the band detail clear-rating button from `0` to `Reset`. The underlying reset behavior is unchanged: tapping the button still calls the existing rating use case with value `0`, which means unrated/cleared.

Created V2.16 release metadata and rebuilt the signed local release APK at `app/build/outputs/apk/release/app-release.apk`.

## Acceptance criteria validation

- AC1: Met by changing `BandDetailActivity` so the clear rating button visible label is `Reset` instead of `0`.
- AC2: Met by preserving the existing click handler that calls `rateBand(currentUser(), selectedBand, 0)` and applies saved rating `0`.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `git diff --check`

### Manual validation

Not run on a physical Android device in this environment. Install V2.16, open a band detail screen, and verify the clear action under the stars shows `Reset`; tapping it should clear the rating.

## TDD / BDD / approval-test evidence

No new automated UI test was added for this text-only Activity label change. The behavior was preserved by keeping the existing reset call to value `0`; compile and release validation covered the changed Android code.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none, because this is an Android UI label-only change and does not affect domain rules, persistence, ports, adapters, or sync.

## README impact

README impact: updated release-note links to include V2.16.

## Business requirements impact

Business requirements impact: none, because BR-001/BR-001a still define `0` as unrated/reset and the stored behavior did not change.

## Diagram impact

Diagram impact: none, because module boundaries and runtime relationships did not change.

## Commits / logical change list

- Renamed the visible clear-rating button label to `Reset`.
- Bumped Android release metadata to V2.16.
- Added V2.16 release notes and rebuilt the local signed release APK.

## Risks and follow-up

Manual installed-device verification was not run here.

Release APK: `app/build/outputs/apk/release/app-release.apk`
Version metadata: `versionCode 19`, `versionName 2.16`
SHA-256: `ee655593fece35e94bbbb8d3f38411f20bb3f19a01a1c083e10e55ad2ef53394`
<!-- SECTION:NOTES:END -->
