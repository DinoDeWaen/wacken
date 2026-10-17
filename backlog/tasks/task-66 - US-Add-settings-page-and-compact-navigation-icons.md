---
id: task-66
title: 'US: Add settings page and compact navigation icons'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-10 07:05'
updated_date: '2026-06-10 07:38'
labels:
  - mvp2
  - ui
  - navigation
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want the main overview to use compact icon actions and move secondary actions into settings, so the overview stays focused while group, import, sync, schedule, and exit actions remain reachable.

Business rules: BR-061, BR-062, BR-058, BR-059.

In scope:
- Add a cog icon action that opens settings.
- Move group/invite, import, and manual sync actions into settings.
- Add a calendar icon action for the group schedule.
- Add a direct exit action that performs sync-and-exit.

Out of scope:
- Redesigning the schedule calendar layout.
- Changing sync conflict rules or Supabase data contracts.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given I am on the band overview, when the screen is shown, then I can open settings through a cog icon action.
- [x] #2 Given I am on settings, then group/invite, import, and manual sync actions are available there instead of the main overview action area.
- [x] #3 Given I am on the band overview, when I tap the calendar icon, then the group schedule opens.
- [x] #4 Given I am on the band overview, when I tap the exit action, then the app attempts Supabase sync before exiting and stays open with a clear error if sync fails.
- [x] #5 Existing group, import, manual sync, startup sync, and close-sync behavior remains covered by tests or explicit validation.
- [x] #6 README, business requirements, diagram, and ADR impact are assessed using the delivery-governance validation package.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected the current Android overview action row, sync/close behavior, import screen, schedule screen, and invite sharing.
2. Implemented a compact overview action row with settings cog, calendar schedule action, and sync-exit action.
3. Added SettingsActivity and moved group invite, lineup import, and manual Supabase sync there.
4. Preserved existing startup/reactivation sync, manual sync, import, group invite, and sync-exit behavior.
5. Updated README basic functionality for the new navigation structure.
6. Ran app unit tests, Android Java compile, and diff checks.

Deviation: no focused Activity unit tests were added because the project has no Android UI test harness; coverage is compile validation plus existing sync/invite tests. Architecture impact: not architecture-significant; Android presentation/navigation only. ADR impact: none.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added a compact top-level overview action row: cog opens Settings, calendar opens the group schedule, and the exit icon triggers the existing sync-and-exit flow. Added `SettingsActivity` for secondary actions: group invite, lineup CSV import, manual Supabase sync, and back to bands.

Existing startup/reactivation sync, manual sync, import, invite sharing, and sync-and-exit behavior were preserved.

## Acceptance criteria validation

- AC1: The band overview now exposes a cog icon action with content description `Settings` that opens `SettingsActivity`.
- AC2: Settings contains group invite, import, and manual Supabase sync actions. These actions were removed from the overview action area.
- AC3: The overview calendar icon opens `ScheduleActivity`.
- AC4: The overview exit icon calls the existing sync-and-exit path, which syncs before `finishAndRemoveTask()` and stays open with an error message on sync failure.
- AC5: Existing invite text and sync repository tests remain part of app unit validation; Android compile validates the new Activity wiring.
- AC6: README, business requirements, diagram, and ADR impact are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `git diff --check`

### Manual validation

- Not run on a physical Android device in this task. Install the APK and verify the overview shows settings, calendar, and sync-exit icons; settings shows group invite, import, and sync actions.

## TDD / BDD / approval-test evidence

This is Android presentation/navigation work in a project without an Activity UI test harness. Existing focused tests cover invite text and sync repository behavior; Android Java compilation covers `SettingsActivity`, manifest registration, and MainActivity wiring.

## Architecture impact

- Architecture-significant change: no. This is Android presentation/navigation only. No schema, API, dependency, domain, persistence, or module-boundary change was introduced.
- Approval received: not required.
- ADR impact: none, because no architecture-significant decision was made.

## README impact

README impact: updated basic functionality to document compact overview navigation and settings actions.

## Business requirements impact

Business requirements impact: none, because BR-061 and BR-062 already documented this behavior before implementation.

## Diagram impact

Diagram impact: none, because the system structure and data flow did not change.

## Commits / logical change list

- Add `SettingsActivity` for group invite, import, and manual sync.
- Replace overview secondary buttons with settings, calendar, and sync-exit icon actions.
- Register settings activity in the Android manifest.
- Update README basic functionality.

## Risks and follow-up

- Device visual validation remains useful because the UI is programmatic and compact icon rendering can vary by Android font/device.
<!-- SECTION:NOTES:END -->
