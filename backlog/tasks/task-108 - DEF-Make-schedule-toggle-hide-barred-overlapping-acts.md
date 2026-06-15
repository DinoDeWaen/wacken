---
id: task-108
title: 'DEF: Make schedule toggle hide barred overlapping acts'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-15 08:17'
updated_date: '2026-06-15 08:22'
labels:
  - defect
  - schedule
  - filter
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The schedule filter toggle was implemented as Hide <=2★, but the intended toggle is to hide barred/scratched lost-overlap acts. Correct the toggle so it hides acts marked as lost due to overlapping with a higher-rated selected act, while keeping the separate selected-star-threshold filter behavior from task-107.

The threshold filter remains valid as a separate feature.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the group schedule is open, when the hide-barred toggle is off, then barred/scratched overlapping acts remain visible with their scratch styling.
- [x] #2 Given the hide-barred toggle is on, when the schedule is shown, then barred/scratched overlapping acts are hidden from the schedule blocks.
- [x] #3 Given the hide-barred toggle is on, when decision details are opened, then barred/scratched overlapping acts are hidden from the detail candidates.
- [x] #4 Given the selected star-threshold filter is used, then it still hides acts at or below the selected threshold independently of the hide-barred toggle.
- [x] #5 Automated tests cover barred-act filtering and threshold filtering together.
- [x] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Extended ScheduleRatingFilter to combine rating threshold filtering with hide-barred filtering based on ScheduleBlockStyle.scratched().
2. Replaced the schedule checkbox label/state from Hide <=2★ to Hide barred.
3. Changed calendar filtering to evaluate barred status against the full visible schedule before removing filtered blocks.
4. Added tests for barred acts visible when the toggle is off, hidden when on, and combined with the selected star threshold.
5. Corrected README and BR-075 to describe barred overlapping acts instead of 2-star-or-lower acts.
6. Ran validation, deleted the previous release APK, rebuilt a signed release APK, and verified signature/digest.

Deviation: task-107 threshold behavior remains unchanged and independent. Architecture impact: not architecture-significant; no approval or ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Corrected the schedule checkbox to hide barred/scratched overlapping acts instead of hiding 2-star-or-lower acts. The checkbox is now labeled Hide barred and uses the same ScheduleBlockStyle.scratched() rule that draws the diagonal bars, so visible behavior and filtering are aligned.

The separate selected star-threshold filter remains intact. If Hide barred and a threshold are both active, both filters apply.

Fresh signed release APK: app/build/outputs/apk/release/app-release.apk
SHA-256: a1304ac42e6db5bf082d8033983e1cb50adebeb1adf22ea78b28ce9c3fb04919

## Acceptance criteria validation

- AC1: With Hide barred off, barred/scratched acts remain visible and keep scratch styling.
- AC2: With Hide barred on, ScheduleRatingFilter hides candidates whose ScheduleBlockStyle is scratched.
- AC3: Decision detail candidates use the same filter and hide scratched/barred candidates when active.
- AC4: The selected star-threshold filter still works independently and combines with Hide barred.
- AC5: Added app unit tests for barred filtering, off-state visibility, and combined threshold behavior.
- AC6: README and business requirements were corrected and impacts are recorded below.

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

- Added/updated unit tests for the clarified barred-filter behavior before closing the defect.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR impact: none, because this remains Android presentation filtering and package-local helper behavior without changing architecture, persistence, APIs, dependencies, or module boundaries.

## README impact

README impact: updated current functionality to say the schedule can hide barred overlapping acts.

## Business requirements impact

Business requirements impact: updated BR-075 to define the hide-barred schedule filter correctly.

## Diagram impact

Diagram impact: none, because no architecture or flow diagram changed.

## Commits / logical change list

- Rename/repoint schedule checkbox behavior to Hide barred.
- Filter scratched/barred candidates using ScheduleBlockStyle.
- Preserve selected star-threshold filtering.
- Correct README and business requirements.
- Rebuild signed release APK.

## Risks and follow-up

- Barred status depends on the current visible candidates and manual local choices, matching the scratch rendering rule.
<!-- SECTION:NOTES:END -->
