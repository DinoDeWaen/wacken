---
id: task-128
title: 'UX: Add schedule legend and filter clarity'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-19 06:04'
updated_date: '2026-06-19 18:50'
labels:
  - ux
  - schedule
  - ui
dependencies: []
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The schedule uses colors, scratched blocks, locked choices, and rating filters, but the meaning is not obvious enough during field use. Add a compact legend and clearer filter labels.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the schedule is open, when filters or special block styles are visible, then the meaning of gold/red/grey borders, scratches, locks, and hidden filters is available without leaving the screen.
- [x] #2 Filter labels clearly distinguish hide barred acts from star-threshold filtering.
- [x] #3 The legend does not consume excessive schedule space on phone screens.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added BDD-style acceptance tests for schedule-key coverage and distinct filter labels through TDD.
2. Added a compact in-place Schedule key disclosure that exposes colors, scratches, locks, ties, and filter meaning only when requested.
3. Renamed the controls to Hide barred overlaps and Hide ratings at/below, preserving existing filtering behavior.
4. Ran focused and full Android validation, deleted the prior release APK, then built and verified a fresh signed APK.
5. Recorded validation evidence and closed the task.
Architecture impact: not architecture-significant; this is Android presentation text and disclosure state over existing schedule behavior with no rule, persistence, API, module, or dependency changes. No ADR required.
Documentation impact: README, business requirements, and diagrams unchanged because this does not change setup, business rules, user workflow, or architecture.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Added a compact `Schedule key` show/hide control directly below the filters. It provides the meaning of all border colors, scratches, locks, ties, and filters without leaving the schedule.
- Renamed the controls to `Hide barred overlaps` and `Hide ratings at/below` so their independent effects are clear.

## Acceptance criteria validation

- AC1: The in-place Schedule key documents gold/red/grey borders, scratches, locks, ties, and filters.
- AC2: The barred-overlap and rating-threshold controls now use distinct explicit labels.
- AC3: The detailed key is collapsed by default and only consumes schedule space after the user taps Show.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.ScheduleLegendContentTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease`
- APK verification: `apksigner verify --verbose app/build/outputs/apk/release/app-release.apk` verified v1 and v2 signatures.

### Manual validation

- Open Group Schedule, confirm the Schedule key is collapsed, tap Show, read each state description, then tap Hide and confirm the calendar regains the vertical space.

## TDD / BDD / approval-test evidence

- Added `ScheduleLegendContentTest` first for the acceptance-level schedule state coverage and distinct filter wording; then wired the tested content into the Android controls.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR impact: none, because no architectural boundary, persistence schema, external API, or dependency changed.

## README impact

- README impact: none, because setup, public workflow, architecture, commands, and troubleshooting instructions did not change.

## Business requirements impact

- Business requirements impact: none, because this clarifies the existing schedule presentation without changing business rules.

## Diagram impact

- Diagram impact: none, because the existing system structure is unchanged.

## Commits / logical change list

- Added tested legend/filter wording content.
- Added compact on-demand schedule-key UI and clarified filter labels.
- Rebuilt signed release APK: `app/build/outputs/apk/release/app-release.apk`, SHA-256 `e4e10ddca2d7ea2dcb5585535c2ab092b40b7bec33931344f8ccc3e844aa4580`.

## Risks and follow-up

- The key disclosure is intentionally ephemeral during a single schedule-screen session; its state is not persisted because it is a view preference only.
<!-- SECTION:NOTES:END -->
