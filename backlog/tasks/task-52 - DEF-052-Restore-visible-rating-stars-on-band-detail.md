---
id: task-52
title: 'DEF-052: Restore visible rating stars on band detail'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-04 19:54'
updated_date: '2026-06-04 19:57'
labels:
  - mvp1
  - release
  - defect
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## Business value

MVP1 users must be able to see and change a band rating from the detail screen. UAT showed the detail rating control is clickable but visually empty, so users cannot tell what rating is selected from the detail view.

## User story

As a festival attendee, I want visible rating stars on the band detail screen, so that I can review and change my rating without returning to the band overview.

## Scope

In scope:
- Make the band detail rating control visibly render the current 1-5 star rating.
- Preserve the existing rating save behavior from detail to overview and local persistence.
- Add or update focused tests where feasible.
- Re-run the affected MVP1 UAT steps after the fix.

Out of scope:
- Redesigning the full detail screen.
- Changing rating business rules or the 1-5/unrated scale.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a band detail screen is opened, when the rating section is visible, then the current rating stars are visible and understandable.
- [x] #2 Given a user changes the rating from the detail screen, when they return to the overview, then the overview shows the updated rating.
- [x] #3 Given the app is restarted after a detail rating change, when the overview is reopened, then the changed rating remains visible.
- [x] #4 Automated or focused validation covers the detail rating rendering/save behavior where feasible.
- [x] #5 README impact, business requirements impact, diagram impact, and ADR impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Reproduced the MVP1 UAT defect on the emulator: the detail rating control was clickable but visually empty for default/unrated detail ratings.
2. Updated BandDetailActivity to call RatingStarsView.showAvailableRating() for the detail rating control so stars render immediately on the detail screen.
3. Ran focused validation: :app:compileDebugJavaWithJavac, :app:testDebugUnitTest, and assembleDebug.
4. Reinstalled the debug APK on the emulator and verified detail stars are visible, detail rating changes update the overview, and the changed rating persists after app restart.

Deviation: no separate local JVM view assertion was added because the Android view rendering defect is best proven through emulator UAT screenshots; existing app unit tests and compile checks passed.
Architecture impact: not architecture-significant. No approval or ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Restored visible rating stars on the band detail screen by forcing the detail rating control into its visible state after binding the current rating. The overview behavior remains unchanged.

## Acceptance criteria validation

- AC1: Passed. Emulator UAT shows visible stars under the Rating heading on the 5th Avenue detail screen.
- AC2: Passed. Changing the detail rating updates the overview rating stars.
- AC3: Passed. After force-stopping and reopening the app, the changed overview rating remains visible.
- AC4: Passed through focused validation: app compile/unit tests plus emulator rendering and save/persistence UAT.
- AC5: Impact notes recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug

### Manual validation

- Installed app/build/outputs/apk/debug/app-debug.apk on the Medium_Phone_API_36.0 emulator.
- Opened 5th Avenue detail and confirmed rating stars are visible.
- Changed the rating from detail and confirmed the overview updates.
- Force-stopped and reopened the app and confirmed the changed rating persisted.

## TDD / BDD / approval-test evidence

Bug was discovered through MVP1 UAT. The fix was validated with focused emulator regression steps and existing app unit tests; no approval baseline was needed because this was not a refactor.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because this is a narrow visual defect fix and does not change setup, public commands, architecture, or documented behavior.

## Business requirements impact

Business requirements impact: none, because the existing rating-detail behavior remains the intended behavior; the task restores the UI to match it.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Updated app/src/main/java/be/wacken/planner/BandDetailActivity.java to show the detail rating stars immediately after constructing the rating control.

## Risks and follow-up

No release-blocking follow-up remains for this defect after emulator validation.
<!-- SECTION:NOTES:END -->
