---
id: task-106
title: 'US: Hide 2-star or lower schedule acts'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-15 07:41'
updated_date: '2026-06-15 07:49'
labels:
  - ui
  - schedule
  - filter
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user viewing the group schedule, I want a simple toggle to hide all acts rated 2 stars or less, so that the schedule can focus on stronger choices without deleting or changing any ratings.

In scope:
- Add a schedule-screen filter toggle for hiding visible acts rated 2 stars or lower.
- Apply the filter to schedule blocks and visible schedule detail candidates consistently.
- Preserve the underlying generated schedule, ratings, manual choices, and sync data.

Out of scope:
- Changing conflict resolution rules.
- Persisting or syncing the filter setting unless a later story asks for it.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the group schedule is open, when the hide 2-star-or-lower toggle is off, then schedule acts are shown according to the normal generated and manual-selection rules.
- [x] #2 Given the group schedule is open, when the hide 2-star-or-lower toggle is on, then visible schedule blocks rated 2 stars or lower are hidden from the schedule view.
- [x] #3 Given a hidden act has alternatives or decision details, when the filter is active, then the visible detail content does not show acts rated 2 stars or lower.
- [x] #4 Given the filter is changed, when the user turns it off again, then the hidden acts reappear without changing ratings, selected acts, or synced data.
- [x] #5 Automated tests or UI-level tests cover the toggle behavior and prove the underlying schedule data is not mutated.
- [x] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added ScheduleRatingFilter and unit tests for no-filter and hide <=2-star behavior.
2. Wired ScheduleActivity with a Hide <=2★ checkbox.
3. Filtered rendered schedule blocks, walking markers, scratch comparison candidates, and decision detail candidates without changing generated schedule data or manual choices.
4. Updated README and business requirements for the new local schedule filter.
5. Ran validation, deleted the previous release APK, rebuilt a signed release APK, and verified the signature/digest.

Deviation: none. Architecture impact: not architecture-significant; no approval or ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added a local schedule filter toggle labeled Hide <=2★. When enabled, visible schedule blocks rated 2 stars or lower are hidden, and decision details only show candidates above that rating. The filter is local to the schedule view and does not mutate generated schedule data, ratings, manual choices, persistence, or sync state.

Updated README and business requirements with the new public behavior.

Fresh signed release APK: app/build/outputs/apk/release/app-release.apk
SHA-256: 9895c7ff051ce3c2c40376269fa1ced136d774939b544eaab435b332efe576cd

## Acceptance criteria validation

- AC1: With the toggle off, ScheduleRatingFilter.none() shows all candidates and the schedule uses normal visible candidates.
- AC2: With the toggle on, ScheduleRatingFilter.hideAtOrBelow(2) hides ratings 0, 1, and 2 from rendered schedule blocks.
- AC3: Decision detail candidates are filtered through the same rating filter.
- AC4: Turning the filter off uses ScheduleRatingFilter.none() again and does not mutate the candidate list or underlying schedule state.
- AC5: Added app unit tests for no-filter, hide <=2-star behavior, and turning the filter off.
- AC6: README and business requirements were updated and impacts are recorded below.

## How to test

### Automated tests

- /bin/zsh -lc "JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac"
- /bin/zsh -lc "JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac"

### Release APK rebuild

- rm app/build/outputs/apk/release/app-release.apk
- /bin/zsh -lc "JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=*** WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=*** ./gradlew assembleRelease"
- /Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
- shasum -a 256 app/build/outputs/apk/release/app-release.apk

### Manual validation

- Not run on a device in this task.

## TDD / BDD / approval-test evidence

- Added focused unit tests for the filter before wiring the Android screen.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR impact: none, because this is Android presentation filtering plus a package-local helper and does not change architecture, persistence, APIs, dependencies, or module boundaries.

## README impact

README impact: updated current functionality to mention the local hide-2-star-or-lower schedule filter.

## Business requirements impact

Business requirements impact: updated with BR-075 for the local hide-2-star-or-lower schedule filter.

## Diagram impact

Diagram impact: none, because no architecture or flow diagram changed.

## Commits / logical change list

- Add ScheduleRatingFilter and tests.
- Add Hide <=2★ checkbox to ScheduleActivity.
- Filter schedule blocks and detail candidates locally.
- Update README and business requirements.
- Rebuild signed release APK.

## Risks and follow-up

- task-107 will extend this into a selectable inclusive star-threshold filter.
<!-- SECTION:NOTES:END -->
