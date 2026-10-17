---
id: task-111
title: 'US: Hide Metal Battle placeholder acts from band list'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-16 20:01'
updated_date: '2026-06-17 05:41'
labels:
  - bands
  - ratings
  - import
  - ui
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want Metal Battle placeholder acts removed from the band list, so that the rating overview focuses on real bands I can meaningfully rate.

Business value: placeholder or generic Metal Battle entries clutter the band overview and make rating allocation less useful.

In scope:
- Hide or exclude all imported band entries that represent Metal Battle placeholders from the band overview and band search/filter results.
- Ensure hidden Metal Battle placeholders do not affect visible band counts or rating allocation counts.
- Preserve imported master data unless implementation planning confirms deletion is safe and approved.

Out of scope:
- Removing real named bands that happen to participate in a Metal Battle unless they are imported as generic Metal Battle placeholders.
- Changing Wacken source import contracts beyond the minimum needed to classify these placeholders.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the imported band list contains generic Metal Battle placeholder entries, when I open the band overview, then those entries are not shown in the band list.
- [x] #2 Given I search or filter the band list, when generic Metal Battle placeholder entries match the search term, then they still remain hidden.
- [x] #3 Given rating allocation counts are shown in Settings, when Metal Battle placeholder entries are hidden from the band list, then they do not inflate visible-band or rating-allocation counts.
- [x] #4 Given a real named band exists, when its metadata mentions Metal Battle but the band name is not a generic placeholder, then it is not hidden by this rule.
- [x] #5 Automated tests cover placeholder classification and visible-list filtering; Android compile validation passes.
- [x] #6 Business requirements and README impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected band list, search/filter behavior, and rating allocation count paths.
2. Added `BandVisibilityPolicy` in the domain layer and tests for generic Metal Battle placeholders versus real named bands.
3. Filtered generic Metal Battle placeholders from scheduled and unscheduled band list rows in `ListBandsUseCase`.
4. Excluded hidden placeholders from Settings rating allocation counts.
5. Updated README and business requirements.
6. Ran full domain/application/app validation, Android compile, diff checks, and rebuilt/verified the signed release APK after task-114 restored repeatable signing.

Deviation: no master-data deletion was done; imported placeholder data is preserved and only hidden from rating UX. Architecture impact: not architecture-significant.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Generic Metal Battle placeholder acts are now hidden from the rating-facing band list and Settings rating allocation counts. The imported master data is preserved; only rating UX filters out generic placeholder names such as `Metal Battle`, `Metal Battle tba.`, or `Metal Battle Germany`. Real named bands that merely contain similar words are still visible.

## Acceptance criteria validation

- AC1: `ListBandsUseCase` filters generic Metal Battle placeholders from performance-backed band rows.
- AC2: The source band list no longer includes placeholders, so downstream search/filter cannot reveal them.
- AC3: `RatingAllocationSummary` excludes hidden placeholders from settings counts.
- AC4: `BandVisibilityPolicyTest` and list tests preserve real named bands such as `Battle Beast` and `The Metal Battle Alumni`.
- AC5: Automated tests cover placeholder classification, visible-list filtering, and rating count exclusion; Android compile validation passed.
- AC6: README and business requirements impact are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `git diff --check`
- Signed release rebuild after signing recovery: `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk`
- `shasum -a 256 app/build/outputs/apk/release/app-release.apk`

APK metadata: package `be.wacken.planner`, `versionCode=15`, `versionName=2.12`, minSdk `23`, targetSdk `36`.

APK SHA-256: `237836320da995a70d132070ab99d3e2ef8e4504e1139b98e7a6f570308f608f`.

### Manual validation

Not run on a physical device. Install the rebuilt APK, open the band overview, and verify generic Metal Battle placeholder acts are absent while real named bands remain visible.

## TDD / BDD / approval-test evidence

Added focused tests for placeholder classification, scheduled and unscheduled list filtering, and settings count exclusion before completing the implementation. No approval baseline was needed because this is intentional new behavior.

## Architecture impact

- Architecture-significant change: no. This is a small domain/application/app filtering rule with no schema, sync contract, dependency, or module-boundary change.
- Approval received: not required.
- ADR: none.

## README impact

README impact: updated basic functionality to document hiding generic Metal Battle placeholders from rating lists and counts.

## Business requirements impact

Business requirements impact: updated current capabilities and added BR-050a for hiding generic Metal Battle placeholders from rating lists while preserving master data.

## Diagram impact

Diagram impact: none, because system structure and data flow did not change.

## Commits / logical change list

- Add `BandVisibilityPolicy` and tests.
- Filter placeholder performances and unscheduled bands from `ListBandsUseCase`.
- Exclude hidden placeholders from Settings rating allocation counts.
- Update README and business requirements.

## Risks and follow-up

- The classifier intentionally hides names beginning with generic `Metal Battle`; if Wacken later publishes a real band whose official name begins that way, the classifier should be adjusted.
<!-- SECTION:NOTES:END -->
