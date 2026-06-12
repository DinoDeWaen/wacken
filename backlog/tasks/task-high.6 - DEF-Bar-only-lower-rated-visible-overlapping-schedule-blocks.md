---
id: task-high.6
title: 'DEF: Bar only lower-rated visible overlapping schedule blocks'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-12 15:13'
updated_date: '2026-06-12 15:21'
labels:
  - defect
  - ui
  - schedule
dependencies: []
parent_task_id: task-high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The schedule overview currently draws scratch bands on visible selected blocks when they have a lost alternative. This is wrong: winners such as Hämatom can be scratched, and non-overlapping acts such as Thundermother can be scratched even though they should remain clear.

As a festival attendee, I want scratch bands to mark only visible selected acts that should be skipped because they overlap a higher-rated visible selected act by more than 15 minutes, so the schedule makes the actual winner/skip decision clear.

Business rules for validation:
- A block is barred only when it overlaps another visible selected block by more than 15 minutes.
- The lower-rated overlapping visible block is barred.
- The higher-rated overlapping visible block is not barred.
- Equal ratings do not bar each other automatically.
- Blocks with no visible selected-act overlap over 15 minutes are not barred.
- Border color remains independent: 5-star gold, 2-star light grey, otherwise red.
- Lost-alternative text alone must not make the visible winning block barred.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a visible block has no overlap over 15 minutes with another visible selected block, when the schedule is rendered, then the block is not barred.
- [x] #2 Given two visible selected blocks overlap by more than 15 minutes and one has a higher rating, when the schedule is rendered, then only the lower-rated block is barred.
- [x] #3 Given two visible selected blocks overlap by more than 15 minutes and have equal ratings, when the schedule is rendered, then neither block is barred by this visual rule.
- [x] #4 Given a visible winning block has a lost alternative, when no higher-rated visible selected block overlaps it by more than 15 minutes, then the winning block is not barred.
- [x] #5 Border colors continue to follow rating: 5-star gold, 2-star light grey, otherwise red.
- [x] #6 Automated tests cover the corrected barred-block rules and border colors.
- [x] #7 Business requirements, README, diagram, and ADR impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Update ScheduleBlockStyle tests to reproduce the defect: winners with lost alternatives are not barred, non-overlapping acts are not barred, and lower-rated visible overlaps are barred.
2. Change ScheduleBlockStyle to classify barred/scratched blocks by comparing the visible candidate against all visible candidates for that day.
3. Pass the day visible-candidate list from ScheduleActivity into the style classifier.
4. Update BR-064b to replace lost-alternative-based scratch wording with lower-rated visible overlap wording.
5. Run focused app tests, Android compile, full validation, and build/verify a fresh signed local APK.
6. Close the defect with validation evidence and commit.
Architecture impact: not architecture-significant; this is Android presentation logic only. No ADR expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed the barred/scratch logic so it no longer uses lost-alternative data. Scratch bands now mark only visible selected blocks that overlap a higher-rated visible selected block by more than 15 minutes. Winners such as Hämatom remain clear when they beat lower-rated overlapping blocks, non-overlapping acts such as Thundermother remain clear, and equal-rated visible overlaps do not bar each other automatically.

## Business rules applied

- A block is barred only when it overlaps another visible selected block by more than 15 minutes.
- The lower-rated overlapping visible block is barred.
- The higher-rated overlapping visible block is not barred.
- Equal ratings do not bar each other automatically.
- Blocks with no visible selected-act overlap over 15 minutes are not barred.
- Border color remains independent: 5-star gold, 2-star light grey, otherwise red.
- Lost-alternative text alone does not make the visible winning block barred.

## Acceptance criteria validation

- AC1: ScheduleBlockStyleTest covers Thundermother-style no-overlap blocks as not scratched.
- AC2: ScheduleBlockStyleTest covers the Hämatom/Kadavar-style higher-rated winner and lower-rated overlap; only the lower-rated block is scratched.
- AC3: ScheduleBlockStyleTest covers equal 5-star visible overlaps as not scratched.
- AC4: ScheduleBlockStyleTest covers a winning block with only lost-alternative context as not scratched.
- AC5: Existing border tests still cover 5-star gold, 2-star light grey, and default red.
- AC6: Automated tests cover corrected barred-block rules and border colors.
- AC7: Impact notes recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleBlockStyleTest --tests be.wacken.planner.ScheduleBlockContentTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac

### Manual validation

- Built signed local release APK at app/build/outputs/apk/release/app-release.apk.
- Verified package be.wacken.planner versionName 2.10 versionCode 13 using aapt.
- Verified APK signature with apksigner: v1=true, v2=true.
- SHA-256: db967b537cd86832941c59facfe2255d95eef1274a8d23d651888ae28ed42941.
- git diff --check passed.

## TDD / BDD / approval-test evidence

Updated ScheduleBlockStyleTest to reproduce the incorrect lost-alternative-based logic and encode the corrected visible-overlap winner/loser rules. The focused tests failed against the old API/logic, then passed after implementation.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because setup, commands, architecture, and high-level capability descriptions did not change.

## Business requirements impact

Business requirements impact: updated BR-064b so scratch bands are driven by lower-rated visible overlaps, not lost alternatives.

## Diagram impact

Diagram impact: none, because this changes Android presentation styling without changing architecture or workflows.

## Commits / logical change list

- Change ScheduleBlockStyle to compare a block against all visible candidates on the day.
- Remove lost-alternative dependency from scratch classification.
- Pass visible day candidates from ScheduleActivity into the style classifier.
- Update tests for no-overlap, winner/loser overlap, equal-rating overlap, and lost-alternative-only cases.
- Update BR-064b.

## Risks and follow-up

- This fixes the displayed bars for visible selected blocks. The app still does not render rejected alternatives as separate schedule blocks; if that is desired, it should be a separate story because it changes the overview model and layout.
<!-- SECTION:NOTES:END -->
