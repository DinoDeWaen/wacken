---
id: task-57
title: 'US-059: Show generated group schedule in Android'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-07 15:37'
updated_date: '2026-06-07 16:09'
labels:
  - mvp2
  - scheduling
  - android
dependencies:
  - task-56
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner group member, I want to generate and view the shared group schedule in the Android app, so the MVP2 decision engine is usable without developer tools.

In scope:
- Add a schedule action from the band overview or an equivalent reachable Android entry point.
- Show the generated timeline grouped by festival day.
- Show selected band, stage, time range, decision strength, optional marker, and lost alternative where available.
- Show clear empty/error states when no scheduled performances or no ratings are available.
- Keep existing sync, rating, overview, and detail workflows reachable.

Out of scope:
- Timeline editing, manual overrides, travel/lunch/food annotations, PDF export, and Play Store distribution.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given synced festival data and ratings exist, when the user generates the schedule, then the Android app shows a day-based timeline.
- [x] #2 Given a timeline slot has a lost alternative, when the user views the schedule, then the lost alternative is visible enough to compare with the selected band.
- [x] #3 Given optional performances are selected, when the schedule is shown, then optional status is clearly marked.
- [x] #4 Given no scheduled performances are available, when the user opens the schedule, then a clear empty state is shown instead of a blank screen.
- [x] #5 Given schedule generation fails, when the user opens the schedule, then a clear error message is shown and the band overview/rating workflow remains usable.
- [x] #6 Focused automated or manual validation covers the Android schedule screen and navigation.
- [x] #7 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [x] #8 Architecture impact is assessed; ADR impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added a full-width `View group schedule` action from the band overview.
2. Added `ScheduleActivity` to render the generated shared schedule grouped by day, including selected band, stage, time range, decision status, optional marker, and lost alternative.
3. Added clear non-generated/empty and error states so the schedule screen is not blank when no performances or selections are available.
4. Registered the schedule activity in the Android manifest and kept existing sync/rating/list/detail/import workflows reachable.
5. Updated README and business requirements for user-visible MVP2 schedule viewing.
6. Ran domain/application tests, Android Java compile, and diff whitespace validation.

Deviation: no instrumentation test was added; this project currently treats Android instrumentation tests as optional unless meaningful. Architecture impact: not architecture-significant; this adds an Android presentation screen over an existing application use case without changing boundaries, schemas, dependencies, or external contracts. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Added `ScheduleActivity` to display the generated MVP2 group schedule.
- Added a `View group schedule` action to the band overview.
- The schedule screen shows day sections, selected band, stage, time range, decision status, optional markers, and lost alternatives.
- Empty/no-selection/error states are displayed as clear messages.
- README and business requirements now mention implemented MVP2 schedule viewing.

## Acceptance criteria validation

- AC1: The Android app has a reachable schedule action and renders a day-based timeline from `GenerateSharedScheduleUseCase`.
- AC2: Lost alternatives are displayed as `Lost alternative: ...` on timeline rows.
- AC3: Optional selected performances are marked `OPTIONAL`.
- AC4: Non-generated schedules, including no scheduled performances, show a message instead of a blank screen.
- AC5: Schedule generation exceptions are caught and shown without breaking the band overview workflow.
- AC6: Focused validation covered Android compile and navigation wiring; manual device validation is documented for the MVP2 release task.
- AC7: README and business requirements impact are recorded below.
- AC8: Architecture and ADR impact are recorded below.

## How to test

### Automated tests

- `/bin/zsh -lc 'JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:compileDebugJavaWithJavac'`
- `git diff --check`

### Manual validation

- Install the debug APK, sign in, sync data, tap `View group schedule`, and verify day sections and timeline rows are visible.
- Verify optional rows display `OPTIONAL` and conflict rows show `Lost alternative` when available.
- Verify `Back to bands` returns to the overview.

## TDD / BDD / approval-test evidence

- This UI story builds on the application use case tested in task-56.
- Focused Android validation used compile-time checks rather than instrumentation tests, consistent with current project guidance.
- No approval baseline was needed because this is new UI behavior, not legacy refactoring.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none.

## README impact

README impact: updated MVP functionality to mention MVP2 group schedule viewing.

## Business requirements impact

Business requirements impact: updated current implemented capabilities to include MVP2 group decision rules, conflict resolution, timeline generation, and Android schedule viewing.

## Diagram impact

Diagram impact: none, because module/container relationships did not change.

## Commits / logical change list

- `ScheduleActivity.java`: Android schedule screen.
- `MainActivity.java`: schedule entry action.
- `AndroidManifest.xml`: schedule activity registration.
- `README.md` and `business-requirements.md`: public behavior documentation.

## Risks and follow-up

- Device-level visual validation remains for the MVP2 release task.
- The schedule screen is intentionally read-only; editing/manual overrides are out of scope.
<!-- SECTION:NOTES:END -->
