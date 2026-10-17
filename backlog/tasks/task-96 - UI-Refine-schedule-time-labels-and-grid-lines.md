---
id: task-96
title: 'UI: Refine schedule time labels and grid lines'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-12 12:23'
updated_date: '2026-06-12 12:26'
labels:
  - ui
  - schedule
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The stage-column schedule needs clearer time presentation. The left time axis should be less noisy, while each block should carry its own full time range.

As a festival attendee, I want each block to show `HH:mm-HH:mm` as its first line, with the left time scale showing hourly labels, small half-hour notches, full hour lines, and dotted half-hour lines, so that overlapping stage columns remain readable.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a schedule block is rendered, when it is visible, then its first line shows the compact time range in the format HH:mm-HH:mm.
- [x] #2 Given the schedule time axis is shown, when hour markers are rendered, then only hourly labels are shown on the left.
- [x] #3 Given half-hour positions are rendered, when the schedule is shown, then half-hours use a small notch and dotted line rather than full time labels.
- [x] #4 Relevant automated tests and Android compile checks pass.
- [x] #5 A fresh local signed APK is built and verified.
- [x] #6 README, business requirements, ADR, and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add/update ScheduleBlockContent tests so the first block line is HH:mm-HH:mm instead of separate start/end lines.
2. Update ScheduleBlockContent and ScheduleActivity to render a single compact time-range first line in each block and remove the separate final end-time line.
3. Refine ScheduleActivity grid rendering: hourly labels on the left with full hour lines, half-hour rows with a small notch and dotted line but no time label.
4. Update business requirements for the refined time-axis and in-block time range behavior.
5. Run focused app tests, Android compile, full relevant validation, and git diff checks.
6. Build and verify a fresh local signed APK.
7. Close task with validation evidence and impact notes.
Architecture impact: not architecture-significant; this is Android presentation behavior only. No ADR expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Refined schedule time display for the stage-column calendar. Each performance block now starts with a compact `HH:mm-HH:mm` time range. The left time axis keeps hourly labels only, with full hour lines and unlabeled dotted half-hour markers.

A fresh local signed APK was built and verified after the change.

## Acceptance criteria validation

- AC1: Covered by ScheduleBlockContentTest asserting `13:30-14:15` as the first block line.
- AC2: Implemented by keeping labels only in `hourLine`; half-hour markers use an unlabeled helper.
- AC3: Implemented by `halfHourLine`/`halfHourLineLayout`, using a small dotted marker between full hour rows.
- AC4: Automated validation passed.
- AC5: Fresh local signed APK built and verified.
- AC6: Business requirements impact is recorded; README/ADR/diagram impact are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleBlockContentTest` passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac` passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac` passed.
- `git diff --check` passed.

### Local APK validation

- STORE_FILE=$(security find-generic-password -w -s WACKEN_RELEASE_STORE_FILE 2>/dev/null) STORE_PASSWORD=$(security find-generic-password -w -s WACKEN_RELEASE_STORE_PASSWORD 2>/dev/null) KEY_ALIAS=$(security find-generic-password -w -s WACKEN_RELEASE_KEY_ALIAS 2>/dev/null) KEY_PASSWORD=$(security find-generic-password -w -s WACKEN_RELEASE_KEY_PASSWORD 2>/dev/null) JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=*** WACKEN_RELEASE_STORE_PASSWORD=*** WACKEN_RELEASE_KEY_ALIAS=*** WACKEN_RELEASE_KEY_PASSWORD=*** ./gradlew assembleRelease passed.
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk` passed with v1=true and v2=true.
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/aapt dump badging app/build/outputs/apk/release/app-release.apk` reports versionCode=12, versionName=2.9, minSdk=23.
- SHA-256: f80657843edcc566f2b2d9d83b10eaa39970509001ba941877c9371a584b637d.

### Manual validation

- Not run on an installed device in this environment. Installed-device visual validation remains recommended.

## TDD / BDD / approval-test evidence

- Updated focused app test coverage for the new compact time-range block content.
- No approval baseline was needed because this is an intentional schedule presentation improvement.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: none, because this is a small schedule presentation refinement and does not change setup, architecture, commands, or high-level product scope.

## Business requirements impact

Business requirements impact: updated BR-064 and added BR-064a for compact in-block time ranges and the hour/half-hour axis behavior.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- Changed schedule block content to expose one `HH:mm-HH:mm` time range.
- Rendered the time range as the first block line.
- Added unlabeled dotted half-hour grid markers.
- Updated business requirements and tests.
- Built and verified a fresh local signed APK.

## Risks and follow-up

- Installed-device visual UAT should confirm the dotted half-hour marker is subtle enough and visible on target device densities.
<!-- SECTION:NOTES:END -->
