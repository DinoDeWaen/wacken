---
id: task-45
title: 'US-045: Keep ratings consistent between list detail and datastore'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-03 06:07'
updated_date: '2026-06-03 06:10'
labels:
  - ratings
  - defect
  - ui
  - persistence
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee, I want rating changes made in either the band overview or band detail screen to be reflected everywhere immediately so that the list, detail view, and datastore always show the same explicit rating.

Defect observed:
- When a user sets a rating such as 4 in the band list and opens the band detail, the detail can still show the previous rating such as 1.
- When a user changes the rating in the band detail, for example to 3, and returns to the list, the list can still show the previous rating such as 4.
- This means UI state and persisted rating state are not consistently refreshed after rating changes.

Business value:
- Prevents users from losing trust in the rating workflow.
- Ensures the rating shown in overview, detail, local datastore, and sync pipeline is one consistent source of truth.

In scope:
- Ensure overview rating changes are persisted and detail reads the current persisted value.
- Ensure detail rating changes are persisted and overview refreshes from the current persisted value when returning.
- Prevent stale intent extras, row models, or in-memory UI state from overriding the datastore.
- Add automated regression coverage where feasible for application behavior and Android navigation/repository wiring.

Out of scope:
- Redesigning the rating UI.
- Changing the 1-5/default-0 business scale.
- Changing Supabase schema beyond using the already migrated rating scale.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a user changes a band rating in the overview, when the band detail screen opens, then the detail shows the newly saved rating.
- [x] #2 Given a user changes a band rating in the detail screen, when the user returns to the overview, then the overview shows the newly saved rating.
- [x] #3 Given a rating is changed in either screen, when the datastore is queried, then it contains the same explicit rating shown by the UI.
- [x] #4 Given stale rating values are present in navigation arguments or row models, when the destination screen is rendered, then the datastore value wins.
- [x] #5 Automated regression tests cover the rating refresh/save behavior where feasible, and any remaining manual UI validation is documented.
- [x] #6 README/business requirement impact is documented using canonical wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected overview/detail rating flow and found stale rating navigation extras being written into the detail datastore on open.
2. Added application regression tests proving ratings saved through the shared datastore are read consistently by detail and overview use cases.
3. Removed rating/default-rating intent extras from overview-to-detail navigation and removed detail-side rating seeding from intent data.
4. Marked the overview as needing reload before opening detail so returning to the list re-queries the datastore and reflects detail changes.
5. Ran focused Android/application validation and full local Gradle validation successfully.
6. Documented no README/business-rule behavior impact beyond fixing the defect; no ADR needed.

Architecture impact: not architecture-significant; this fixes Activity wiring and preserves existing repository/use-case boundaries.
Deviation: direct Android UI automation is not available in this repo, so UI behavior requires manual emulator/device validation in addition to use-case regression tests.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Recorded and fixed the rating consistency defect. Band detail no longer accepts rating/default-rating navigation extras and no longer seeds the datastore from those extras. Detail now reads the persisted rating through `ShowBandDetailUseCase`, so stale row or intent values cannot overwrite the datastore.

The overview now sets `reloadNeeded = true` before opening band detail. When the user returns, `onResume()` reloads the band list from the repositories, so a rating changed on detail is reflected in the list.

## Acceptance criteria validation

- AC1: Detail reads the saved rating from the shared rating repository; regression test covers overview-save then detail-read through use cases.
- AC2: Overview reloads on return from detail, so detail-save then overview-read uses current datastore values; regression test covers detail-save then overview-read through use cases.
- AC3: Both screens save through `RateBandUseCase`/`RatingRepository`; regression tests assert the same shared datastore value is read by the opposite view model.
- AC4: Rating navigation extras were removed and detail-side seeding from intent was removed, so stale navigation values cannot override persisted ratings.
- AC5: Added `RatingConsistencyUseCaseTest`; manual UI validation is still needed because this repo has no Android UI automation framework.
- AC6: README impact: none, because public setup, commands, architecture, and intended behavior did not change; this fixes an implementation defect. Business requirements impact: none, because the existing rule already requires consistent rating workflow and datastore behavior.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:compileDebugJavaWithJavac :app:testDebugUnitTest` - passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test qaTest assembleDebug` - passed.

### Manual validation

1. Open the band list and set 5th Avenue to rating 4.
2. Open 5th Avenue detail and verify it shows rating 4.
3. Change 5th Avenue detail rating to 3.
4. Return to the band list and verify 5th Avenue shows rating 3.
5. Restart or navigate away/back and verify the rating remains 3 from the datastore.

## TDD / BDD / approval-test evidence

Added application-level regression tests before closing the defect: one for list-save/detail-read and one for detail-save/list-read. The Activity change is covered by Android compilation and JVM test execution; direct UI automation is not configured.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none, because this changes UI wiring inside existing Activity/repository boundaries.

## README impact

README impact: none, because setup, commands, architecture, and public intended behavior did not change.

## Business requirements impact

Business requirements impact: none, because the existing business behavior already requires one consistent rating state across screens and datastore.

## Diagram impact

Diagram impact: none, because module/container relationships did not change.

## Commits / logical change list

- Removed stale rating/default-rating navigation extras and detail datastore seeding.
- Reloaded overview from datastore after returning from band detail.
- Added rating consistency regression tests.

## Risks and follow-up

Manual emulator/device validation is still required for the exact UI flow shown in the screenshots because the project does not currently include Android UI automation.
<!-- SECTION:NOTES:END -->
