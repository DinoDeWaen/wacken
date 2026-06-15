---
id: task-107
title: 'US: Hide schedule acts at selected star threshold or below'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-15 07:41'
updated_date: '2026-06-15 07:58'
labels:
  - ui
  - schedule
  - filter
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user viewing the group schedule, I want a star-threshold filter so I can hide all acts at or below the selected rating level and quickly focus on higher-rated acts.

Business rule from the request:
- If the user selects 2 stars in the filter, all 2-star and lower acts are hidden.
- The selected threshold is inclusive: acts with stars less than or equal to the selected value are hidden.

In scope:
- Add a schedule-screen filter control for choosing the inclusive hide threshold.
- Apply the selected threshold to schedule blocks and visible schedule detail candidates consistently.
- Keep filtering local to the current schedule view.

Out of scope:
- Changing conflict resolution rules.
- Persisting or syncing the threshold setting unless a later story asks for it.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the group schedule is open, when no star threshold is selected, then schedule acts are shown according to the normal generated and manual-selection rules.
- [x] #2 Given the user selects a 2-star threshold, when the schedule is shown, then all acts rated 2 stars or lower are hidden and acts rated 3 stars or higher remain visible.
- [x] #3 Given the user selects another threshold, when the schedule is shown, then acts with rating less than or equal to that selected threshold are hidden and higher-rated acts remain visible.
- [x] #4 Given the threshold filter is active, when schedule decision details are opened, then visible candidates rated at or below the threshold are hidden from the detail list.
- [x] #5 Given the threshold is cleared or changed, then the visible schedule updates without changing ratings, selected acts, generated decisions, manual choices, or synced data.
- [x] #6 Automated tests or UI-level tests cover at least the 2-star threshold example and one higher threshold.
- [x] #7 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Extended ScheduleRatingFilter tests for inclusive selected thresholds, including 2-star and 3-star thresholds.
2. Added a local threshold button row to ScheduleActivity while preserving the Hide <=2★ toggle from task-106.
3. Applied the effective inclusive threshold to schedule blocks, walking markers, scratch comparison candidates, and decision detail candidates without mutating generated data/manual choices.
4. Updated README and business requirements for the threshold schedule filter.
5. Ran validation, deleted the previous release APK, rebuilt a signed release APK, and verified signature/digest.

Deviation: retained the fixed Hide <=2★ toggle and added threshold buttons; when both are active, the effective filter uses the highest active inclusive threshold. Architecture impact: not architecture-significant; no approval or ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added a local inclusive star-threshold filter to the group schedule. The filter row now includes threshold buttons Off, 1★, 2★, 3★, and 4★. Selecting a threshold hides visible schedule blocks and decision-detail candidates rated at or below that threshold. The existing Hide <=2★ toggle remains available; if both controls are active, the schedule uses the highest active threshold.

The filter remains local to the schedule view and does not change ratings, generated decisions, manual choices, persistence, or synced data.

Fresh signed release APK: app/build/outputs/apk/release/app-release.apk
SHA-256: c5beb8a8421611dce83b0ee1ff4fa7b8b6772460954088b9b97b6ce0c1fe26d5

## Acceptance criteria validation

- AC1: Off threshold maps to ScheduleRatingFilter.none(), so the normal generated/manual-selection rules are shown.
- AC2: Selecting 2★ uses an inclusive threshold and hides ratings 0, 1, and 2 while keeping ratings 3 and above visible.
- AC3: Selecting another threshold, covered by 3★, hides that rating and lower while keeping higher ratings visible.
- AC4: Decision detail candidates are filtered through the same active threshold.
- AC5: Changing or clearing the threshold only changes local visibility; tests assert the input candidates are not mutated, and production code does not write ratings, manual choices, persistence, or sync state.
- AC6: App unit tests cover the 2-star threshold and a higher 3-star threshold.
- AC7: README and business requirements were updated and impacts are recorded below.

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

- Added threshold unit tests before wiring the Android threshold control.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR impact: none, because this remains Android presentation filtering and package-local helper behavior with no architecture, persistence, API, dependency, or module-boundary change.

## README impact

README impact: updated current functionality to mention hiding schedule acts at or below a selected star threshold.

## Business requirements impact

Business requirements impact: updated with BR-076 for the local inclusive selected-threshold schedule filter.

## Diagram impact

Diagram impact: none, because no architecture or flow diagram changed.

## Commits / logical change list

- Extend ScheduleRatingFilter tests for inclusive thresholds.
- Add threshold buttons to ScheduleActivity.
- Compute effective threshold from the fixed toggle and selected threshold.
- Update README and business requirements.
- Rebuild signed release APK.

## Risks and follow-up

- The filter controls are local and reset with the Activity lifecycle; persistence was explicitly out of scope.
<!-- SECTION:NOTES:END -->
