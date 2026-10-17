---
id: task-98
title: 'DEF: Include walking time when barring overlapping schedule blocks'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-12 15:32'
updated_date: '2026-06-12 15:53'
labels:
  - defect
  - ui
  - schedule
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The schedule scratch logic must treat walking time as part of a visible conflict. A lower-rated visible act should be barred when its raw overlap with a higher-rated visible act plus the walking time between stages exceeds 15 minutes. A raw 15-minute overlap with a 15-minute walk is therefore a conflict, while a raw 15-minute overlap with only a 5-minute nearby-stage walk remains acceptable.

As a festival attendee, I want the schedule to mark the act I realistically have to skip, so the visual conflict state reflects both overlap and the time needed to move between stages.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given two visible acts overlap for 15 minutes and the lower-rated act requires a 15-minute walk to or from the higher-rated act, when the schedule is rendered, then the lower-rated act is barred.
- [x] #2 Given two visible acts overlap for 15 minutes and the walking time between their stages is 5 minutes, when the schedule is rendered, then the lower-rated act is not barred.
- [x] #3 Given a lower-rated visible act overlaps a higher-rated visible act by more than 15 effective minutes after adding walking time, when the schedule is rendered, then only the lower-rated act is barred.
- [x] #4 Automated tests cover 15-minute raw overlap with 15-minute walking time and 5-minute walking time.
- [x] #5 Business requirements, README, diagram, and ADR impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added ScheduleBlockStyle regression tests for 15-minute raw overlap plus 15-minute cross-group walk and plus 5-minute nearby-stage walk.
2. Confirmed the 15-minute overlap plus 15-minute walk case failed against the raw-overlap-only implementation.
3. Updated ScheduleBlockStyle to compute effective conflict minutes as raw overlap plus walking time beyond the nearby-stage 5-minute allowance.
4. Updated BR-064b to document the effective conflict rule.
5. Ran focused tests, full relevant validation, built and verified a signed local release APK.
Architecture impact: not architecture-significant; this changed Android presentation styling using the existing StageWalkingTimePolicy. No ADR required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Updated schedule block scratch logic so walking time is included when deciding whether a lower-rated visible act should be barred. The effective conflict is raw overlap plus walking time beyond the nearby-stage 5-minute allowance. This means a 15-minute overlap plus a 15-minute cross-group walk is barred, while a 15-minute overlap plus a 5-minute nearby-stage walk remains acceptable.

Also kept the previous chained-overlap regression: Pig Destroyer loses to Saxon, Saxon loses to Hatebreed, and Hatebreed remains unbarred.

## Acceptance criteria validation

- AC1: Covered by ScheduleBlockStyleTest.scratchesLowerRatedBlockWhenFifteenMinuteOverlapAlsoNeedsFifteenMinuteWalk.
- AC2: Covered by ScheduleBlockStyleTest.doesNotScratchWhenFifteenMinuteOverlapOnlyNeedsNearbyFiveMinuteWalk.
- AC3: Existing and new ScheduleBlockStyle tests verify only lower-rated blocks are scratched.
- AC4: Automated tests cover both 15-minute raw overlap plus 15-minute walking time and 5-minute walking time.
- AC5: Impact notes recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleBlockStyleTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac

### Manual validation

- Built signed local release APK at app/build/outputs/apk/release/app-release.apk.
- Verified package be.wacken.planner versionName 2.10 versionCode 13 using aapt.
- Verified APK signature with apksigner: v1=true, v2=true.
- SHA-256: ca3329eb7cc3f2238a88ef31b3455cac53c943c127c8a75bc8799f2f2c9c0bbb.
- git diff --check passed.

## TDD / BDD / approval-test evidence

Used a bug-fix TDD loop: added the failing regression for 15-minute overlap plus 15-minute walking time, then updated the implementation to pass it.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because setup, commands, architecture, and high-level capability descriptions did not change.

## Business requirements impact

Business requirements impact: updated BR-064b to include walking time beyond the nearby-stage 5-minute allowance in barred-block decisions.

## Diagram impact

Diagram impact: none, because this changes presentation behavior and does not alter architecture or workflows.

## Commits / logical change list

- Add chained visible-overlap regression coverage.
- Add walking-time effective-conflict regression coverage.
- Include walking pressure in schedule block scratch decisions.
- Update BR-064b.

## Risks and follow-up

Install the fresh local signed APK before rechecking screenshots; older installed builds will still show the previous scratch behavior.
<!-- SECTION:NOTES:END -->
