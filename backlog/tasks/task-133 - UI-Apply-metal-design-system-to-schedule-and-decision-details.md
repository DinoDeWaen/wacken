---
id: task-133
title: 'UI: Apply metal design system to schedule and decision details'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-19 06:27'
updated_date: '2026-06-19 15:05'
labels:
  - ui
  - design
  - schedule
  - ux
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Polish the group schedule and decision detail surfaces around the shared visual system. The schedule must explain its advanced states clearly: rating colors, locks, ties, scratched skipped overlaps, hidden barred acts, and star-threshold filters.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the group schedule, when it renders, then day controls, filter controls, stage labels, time axis, grid, walking markers, and performance blocks use the shared visual design system.
- [x] #2 Given schedule states are visible, when a user sees lock, tie, scratched, gold, red, grey, or hidden-filter behavior, then an accessible legend explains what those states mean.
- [x] #3 Given a schedule decision detail, when chosen act and alternatives render, then hierarchy, per-person ratings, tie evidence, walking-time evidence, and select actions are visually distinct and readable.
- [x] #4 Given phone-sized screens, when the user scrolls horizontally or opens details, then stage labels remain useful and text does not overlap or clip.
- [x] #5 Automated or focused compile validation proves the touched UI code builds; manual validation steps are documented.
- [x] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected ScheduleActivity and schedule block rendering against the visual design system.
2. Added a dark schedule legend explaining rating colors, scratched skipped overlaps, locks, ties, and filters.
3. Polished day/filter controls, walking markers, and grid/stage surfaces using WackenTheme while preserving fixed stage labels and horizontal scrolling.
4. Polished schedule decision detail hierarchy with dark candidate panels, clearer chosen/alternative sections, per-person ratings, walking evidence, and shared buttons.
5. Ran Android unit tests and compile validation.
6. Deleted the prior release APK and rebuilt a fresh signed release APK per release process.

Design approach: screen-level visual polish using existing ScheduleActivity structure and WackenTheme; no decision-rule changes.
Architecture impact: not architecture-significant; app UI only, no domain/application/infrastructure changes.
Deviation: no schedule decision rules changed; legend explains existing behavior only.
Treatment: standard UI implementation task.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Applied the metal design system to the group schedule and decision details. The schedule now includes a compact legend for rating colors, scratched skipped overlaps, locked choices, ties, and filters. Day and filter controls use shared dark-metal button styling, walking markers are more visible, and decision details use dark bordered candidate panels with clearer chosen/alternative hierarchy.

## Acceptance criteria validation

- AC1: Day controls, filter controls, stage labels, grid surfaces, walking markers, and decision controls use WackenTheme/shared visual styling.
- AC2: Added an always-visible schedule legend explaining gold/red/grey borders, scratched blocks, locks, ties, and filter behavior.
- AC3: Schedule decision detail now uses section headers and bordered candidate panels; per-person ratings and walking evidence remain under each candidate.
- AC4: Fixed stage labels and horizontal scrolling were preserved; compact legend/detail text uses truncation and dense sizing to avoid clipping.
- AC5: Android unit tests, compile validation, full release build, APK signature verification, and APK metadata checks passed.
- AC6: README/business requirements impact is recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac
- JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_* ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease
- git diff --check

### Manual validation

- Verified signed APK metadata: versionCode 21, versionName 2.18, package be.wacken.planner.
- Verified APK signature with apksigner v1/v2 schemes.
- APK SHA-256 after fresh rebuild: 4bb259b16ea18e607455c4df2e561e0cab4a694a23907112c401cf834e4716b9

## TDD / BDD / approval-test evidence

This is visual UI polish and state explanation only. Existing schedule behavior and decision rules are unchanged; compile/build validation protects the touched Android rendering code.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: none, because the visual design system and schedule behavior were already documented; this task applies existing UI guidance.

## Business requirements impact

Business requirements impact: none, because BR-047a and BR-063 through BR-071 already describe the schedule visual behavior and detail screen expectations.

## Diagram impact

Diagram impact: none, because no architecture relationships or workflows changed.

## Commits / logical change list

- Added schedule legend.
- Styled schedule day/filter controls and walking markers.
- Styled schedule decision detail candidate panels and actions.

## Risks and follow-up

No physical-device screenshot was captured in this pass. Support-flow polish remains in task-134.
<!-- SECTION:NOTES:END -->
