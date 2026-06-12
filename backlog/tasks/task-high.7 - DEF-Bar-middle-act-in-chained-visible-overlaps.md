---
id: task-high.7
title: 'DEF: Bar middle act in chained visible overlaps'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-12 15:28'
updated_date: '2026-06-12 15:30'
labels:
  - defect
  - ui
  - schedule
dependencies: []
parent_task_id: task-high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The schedule block scratch logic must handle chained visible overlaps. In the screenshot, Pig Destroyer overlaps Saxon and loses, while Saxon also overlaps Hatebreed and should lose to Hatebreed. The current UI shows Pig Destroyer barred but leaves Saxon unbarred.

As a festival attendee, I want every visible block to be compared against every other visible block, so a middle act can both beat one lower-rated overlap and lose to another higher-rated overlap.

Business rules for validation:
- Each visible block is compared independently against all other visible blocks on the same schedule day.
- If a visible block overlaps any higher-rated visible block by more than 15 minutes, it is barred.
- A visible block can be barred even if it also beats another lower-rated visible block.
- Example: Pig Destroyer 2 stars 18:00-18:45 loses to Saxon 3 stars 18:15-19:30; Saxon also loses to Hatebreed 4 stars 19:00-20:00.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a middle act overlaps a lower-rated act and a higher-rated act by more than 15 minutes, when the schedule is rendered, then the middle act is barred because it loses to the higher-rated act.
- [x] #2 Given the lower-rated first act in the same chain overlaps the middle act by more than 15 minutes, when the schedule is rendered, then the lower-rated first act is also barred.
- [x] #3 Given the highest-rated act in the same chain has no higher-rated visible overlap over 15 minutes, when the schedule is rendered, then it is not barred.
- [x] #4 Automated tests cover the Pig Destroyer / Saxon / Hatebreed chained-overlap case.
- [x] #5 Business requirements, README, diagram, and ADR impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add a focused regression test for the Pig Destroyer / Saxon / Hatebreed chained visible-overlap case.
2. Run the focused style tests to confirm whether the current code already satisfies the rule or needs a code change.
3. If needed, adjust ScheduleBlockStyle or the visible-candidate wiring so middle acts lose to higher-rated overlapping visible blocks.
4. Run focused app tests, Android compile, full validation, and build/verify a fresh signed local APK.
5. Close the defect with validation evidence and commit.
Architecture impact: not architecture-significant; this is Android presentation logic only. No ADR expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added a regression test for the Pig Destroyer / Saxon / Hatebreed chain-overlap case. The current corrected ScheduleBlockStyle implementation already compares each visible block independently against all other visible blocks on the day, so the regression passes without additional production-code changes.

Business rules validated:
- Each visible block is compared independently against all other visible blocks on the same schedule day.
- If a visible block overlaps any higher-rated visible block by more than 15 minutes, it is barred.
- A visible block can be barred even if it also beats another lower-rated overlap.
- Pig Destroyer 2 stars 18:00-18:45 is barred by Saxon 3 stars 18:15-19:30.
- Saxon 3 stars 18:15-19:30 is barred by Hatebreed 4 stars 19:00-20:00.
- Hatebreed is not barred in that chain because no higher-rated visible block overlaps it by more than 15 minutes.

## Acceptance criteria validation

- AC1: ScheduleBlockStyleTest covers Saxon as the middle act and asserts it is scratched because it loses to Hatebreed.
- AC2: The same test asserts Pig Destroyer is scratched because it loses to Saxon.
- AC3: The same test asserts Hatebreed is not scratched.
- AC4: Automated regression test covers the exact Pig Destroyer / Saxon / Hatebreed chain.
- AC5: Impact notes recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleBlockStyleTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac

### Manual validation

- Built signed local release APK at app/build/outputs/apk/release/app-release.apk.
- Verified package be.wacken.planner versionName 2.10 versionCode 13 using aapt.
- Verified APK signature with apksigner: v1=true, v2=true.
- SHA-256: d7ff43430c996df15939f998e7b9c0cb0476f414407d924fb853899b85e3b6f2.
- git diff --check passed.

## TDD / BDD / approval-test evidence

Added a focused regression test for the screenshot case. It passed against the current corrected implementation, which means the behavior is already fixed in code and the screenshot likely came from an older installed APK.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because setup, commands, architecture, and high-level capability descriptions did not change.

## Business requirements impact

Business requirements impact: none, because BR-064b already states lower-rated visible overlaps are barred.

## Diagram impact

Diagram impact: none, because this is a regression test for presentation behavior and does not change architecture or workflows.

## Commits / logical change list

- Add Pig Destroyer / Saxon / Hatebreed chained-overlap regression test.
- Create and close defect task with validation evidence.

## Risks and follow-up

Install the fresh local APK before rechecking this scenario; older local builds can still show Saxon unbarred.
<!-- SECTION:NOTES:END -->
