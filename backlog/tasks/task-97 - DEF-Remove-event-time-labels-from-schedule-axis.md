---
id: task-97
title: 'DEF: Remove event time labels from schedule axis'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-06-12 12:35'
updated_date: '2026-06-12 12:41'
labels:
  - defect
  - ui
  - schedule
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The stage-column schedule still shows per-event start/end labels on the left axis, such as 15:15 and 16:45. This conflicts with the desired axis behavior where only full hours are labeled and half-hours are shown as subtle dotted markers. The horizontal grid lines also stop near the time column instead of spanning all stage columns.

As a festival attendee, I want the schedule axis to show only full-hour labels with full-width hour lines and unlabeled half-hour dotted lines, so the stage-column schedule is clear and not cluttered by individual event times.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given event blocks start or end at non-hour times, when the schedule is shown, then those event start/end times are not rendered on the left axis.
- [x] #2 Given full hours are shown, when the schedule is rendered, then only full-hour labels such as 15:00 appear on the left axis.
- [x] #3 Given half-hour markers are shown, when the schedule is rendered, then they are unlabeled dotted lines across all stage columns.
- [x] #4 Given hour markers are shown, when the schedule is rendered, then full hour lines continue across all stage columns.
- [x] #5 Relevant automated tests and Android compile checks pass.
- [x] #6 A fresh local signed APK is built and verified.
- [x] #7 README, business requirements, ADR, and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Remove the old per-event start/end time labels from ScheduleActivity so non-hour event times no longer appear on the left axis.
2. Replace text-based grid lines with separate full-width grid line views: labeled full-hour axis rows and unlabeled dotted half-hour rows across the stage columns.
3. Keep in-block HH:mm-HH:mm time ranges from task-96 as the source for exact event times.
4. Run focused app tests, Android compile, full relevant validation, and git diff checks.
5. Build and verify a fresh local signed APK.
6. Close task with validation and impact notes, then commit.
Architecture impact: not architecture-significant; this is Android presentation rendering only. No ADR expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Removed the event-derived start/end labels from the schedule time axis and replaced the text-based grid with real hour and half-hour line views sized across the full stage grid. Performance blocks still show their own HH:mm-HH:mm range as the first line.

## Acceptance criteria validation

- AC1: Non-hour event start/end labels such as 15:15 and 16:45 are no longer added to the left axis.
- AC2: The axis label source is now only ScheduleCalendarLayout.hourLabel for full-hour rows.
- AC3: Half-hour rows are rendered as unlabeled dotted line views across the stage grid, with only a small notch in the time lane.
- AC4: Hour rows are rendered as full line views across every stage column.
- AC5: Automated tests and Android compile checks passed.
- AC6: Fresh signed release APK built and verified.
- AC7: Impact notes recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleCalendarLayoutTest --tests be.wacken.planner.ScheduleBlockContentTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:compileDebugJavaWithJavac
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac

### Manual validation

- Built signed release APK at app/build/outputs/apk/release/app-release.apk.
- Verified package be.wacken.planner versionName 2.9 versionCode 12 using aapt.
- Verified APK signature with apksigner: v1=true, v2=true.
- SHA-256: 3d7c05c7ca30ca80ebffca8b4f5f478e3a75bfd39d2fc88e663569561bd0515a.

## TDD / BDD / approval-test evidence

Existing ScheduleCalendarLayoutTest and ScheduleBlockContentTest cover the schedule hour labels, stage-column layout, and in-block time range formatting affected by this rendering change. This defect fix is Android presentation rendering only; no domain acceptance behavior changed.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because setup, commands, architecture, and public capability descriptions did not change.

## Business requirements impact

Business requirements impact: updated BR-064a to clarify that hour and half-hour guide lines span all stage columns and that half-hour rows are unlabeled.

## Diagram impact

Diagram impact: none, because this is a screen rendering refinement and does not change architecture or workflows.

## Commits / logical change list

- Remove per-event axis time labels.
- Draw full-width hour grid lines and full-width dotted half-hour grid lines.
- Keep exact event time range inside each schedule block.
- Clarify BR-064a.

## Risks and follow-up

Low risk. The remaining visual risk is device-density alignment of the horizontal guide lines; compile and unit tests pass, and the view code uses density-scaled dimensions.
<!-- SECTION:NOTES:END -->
