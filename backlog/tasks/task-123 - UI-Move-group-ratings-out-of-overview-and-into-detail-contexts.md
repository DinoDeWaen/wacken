---
id: task-123
title: 'UI: Move group ratings out of overview and into detail contexts'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-19 05:57'
updated_date: '2026-06-19 06:07'
labels:
  - ui
  - ratings
  - schedule
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The band overview currently shows compact other-member ratings under the band name. The overview should stay clean. Group member ratings should be visible only in band detail and schedule decision detail, directly under the current/main rating line.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the band overview is shown, when a band has group ratings, then no group rating text is shown under the band name.
- [x] #2 Given the band detail screen is shown, when group ratings exist, then other-member ratings are shown under the user's own rating controls.
- [x] #3 Given a schedule decision detail is shown, when candidate group ratings exist, then the candidate displays the per-person ratings under the candidate's main rating line.
- [x] #4 Given no group ratings are synced, when a detail context is shown, then the UI uses a clear not-synced/empty state without affecting the overview.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Removed overview-only group-rating rendering from MainActivity; band overview rows now show band name only in the name cell.
2. Kept band detail group ratings directly under the user rating controls.
3. Extended ScheduleDecisionCandidate with per-person rating summaries generated from group ratings.
4. Rendered candidate group ratings under each candidate main rating line in schedule decision detail.
5. Added focused application coverage for schedule candidate per-person ratings and ignored unrated zero values.
6. Updated README/business requirements wording, bumped local release metadata to V2.17, deleted the old release APK, rebuilt a fresh signed APK, and verified signature/metadata/checksum.

Deviation: overview behavior was validated through code review and Android compile because Activity-level UI rendering is not currently instantiated in unit tests. Architecture impact remained not architecture-significant.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Moved per-person group ratings out of the band overview and into detail contexts. The band overview now keeps the name cell clean. Band detail still shows group ratings directly under the user rating controls. Schedule decision detail candidates now carry and render compact D/S-style per-person rating summaries under the candidate rating line.

Created the requested follow-up tickets for HTML cleanup and UX work, and recorded the UX review in task-125.

Built a fresh signed local V2.17 APK at `app/build/outputs/apk/release/app-release.apk`.

## Acceptance criteria validation

- AC1: Met by removing `personRatings` TextView creation/binding from `MainActivity` overview rows.
- AC2: Met by keeping `BandDetailActivity.groupRatingsView()` directly after the user rating stars and Reset action.
- AC3: Met by extending `ScheduleDecisionCandidate` with `personRatings`, populating it in `GenerateSharedScheduleUseCase`, and rendering it in `ScheduleActivity.candidateView()`.
- AC4: Met by leaving the band detail not-synced state intact; overview no longer renders group ratings or any not-synced group-rating text.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `shasum -a 256 app/build/outputs/apk/release/app-release.apk`
- `git diff --check`

### Manual validation

Installed-device validation was not run here. Install V2.17, verify the overview has no group-rating line under band names, band detail shows group ratings under your rating controls, and schedule decision detail shows group ratings below each candidate rating line.

## TDD / BDD / approval-test evidence

Added/updated application-level regression coverage in `GenerateSharedScheduleUseCaseTest` to prove schedule decision candidates include per-person rating summaries and ignore unrated zero values.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none, because this changes application presentation data and Android rendering only; no persistence, API, schema, dependency, or domain-rule ownership changed.

## README impact

README impact: updated basic functionality and V2.17 release-note link to reflect that per-person group ratings are shown in detail contexts rather than overview rows.

## Business requirements impact

Business requirements impact: updated BR-060 and implemented-capability wording so the business source of truth matches the requested rating placement.

## Diagram impact

Diagram impact: none, because module boundaries and runtime relationships did not change.

## Commits / logical change list

- Removed overview group-rating text rendering.
- Added per-person rating summaries to schedule decision candidates.
- Rendered candidate group ratings in schedule decision detail.
- Updated README/business requirements and V2.17 release notes.

## Risks and follow-up

- HTML biography cleanup is tracked separately in task-124.
- Broader UX cleanup is tracked in tasks 126-129.
- Manual installed-device validation was not run here.

Release APK: `app/build/outputs/apk/release/app-release.apk`
Version metadata: `versionCode 20`, `versionName 2.17`
SHA-256: `1f7620b8f97740c72f8218af2cc2953abd3504b27cfdd83b1b80b78efaddcbaf`
<!-- SECTION:NOTES:END -->
