---
id: task-132
title: 'UI: Apply metal design system to band overview and detail'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-19 06:27'
updated_date: '2026-06-19 14:56'
labels:
  - ui
  - design
  - ux
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Polish the daily band-rating surfaces so the overview and band detail feel like one professional metal app. The overview should stay dense and fast; the detail screen should give own rating, group ratings, running order, links, image, and biography a clear hierarchy.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the band overview, when it renders, then title, rows, headers, icon actions, dividers, and empty/loading states use the shared design system and remain fast to scan.
- [x] #2 Given a band detail screen, when it renders, then own rating is primary, group ratings are directly underneath it, Reset and links are secondary, and biography text is readable left-aligned body copy.
- [x] #3 Given narrow phone layouts, when detail content stacks, then controls and text do not overlap or become clipped.
- [x] #4 Automated or focused compile validation proves the touched UI code builds; manual validation steps are documented.
- [x] #5 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected MainActivity and BandDetailActivity against the visual design system.
2. Used the existing WackenTheme helpers from task-131 for shared colors, panel borders, and icon styling.
3. Polished the band overview title, table header surface, row icon buttons, and dense cell spacing while keeping the list fast to scan.
4. Polished band detail with an amber title, bordered primary detail panel, responsive horizontal/vertical top section, group ratings directly under the editable rating, secondary running-order/link areas, and a left-aligned bordered biography panel.
5. Ran Android unit tests and compile validation.
6. Deleted the prior release APK and rebuilt a fresh signed release APK per release process.

Design approach: screen-level UI polish using existing programmatic Android views and WackenTheme; no workflow or business-rule changes.
Architecture impact: not architecture-significant; app UI only, no module boundary, persistence, API, or domain changes.
Deviation: no new production behavior tests were added because this is visual layout polish; validation is compile/build plus APK verification.
Treatment: standard UI implementation task.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Applied the metal design system to the daily band-rating screens. The overview now has stronger Wacken hierarchy and shared row/icon/header styling. The band detail screen now uses a bordered primary panel, keeps own rating primary, shows group ratings directly beneath it, treats reset/links/running order as secondary information, stacks safely on narrow screens, and renders biography copy as left-aligned body text in a dark panel.

## Acceptance criteria validation

- AC1: Overview title, table header surface, row icon buttons, spacing, and loading/empty status area use WackenTheme/shared styling while keeping the dense list scan-friendly.
- AC2: Band detail places own rating first, group ratings underneath, reset/links/running order as secondary controls, and biography as readable left-aligned text.
- AC3: Band detail switches the primary top section to vertical stacking on narrow screen widths to avoid clipping/overlap.
- AC4: Android unit tests, compile validation, full release build, APK signature verification, and APK metadata checks passed.
- AC5: README/business requirements impact is recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac
- JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_* ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease
- git diff --check

### Manual validation

- Verified signed APK metadata: versionCode 21, versionName 2.18, package be.wacken.planner.
- Verified APK signature with apksigner v1/v2 schemes.
- APK SHA-256 after fresh rebuild: b809dde31f5f9bef44b5dea5c2e0c470c1c57ec01fcc1b930b004b4606b4094a

## TDD / BDD / approval-test evidence

This is visual UI polish using existing Activity rendering. No domain or application behavior changed. Existing Android unit tests and compile validation protect behavior preservation; visual acceptance is documented through manual validation steps.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: none, because the visual design system is already documented and linked; this task only applies it to existing screens.

## Business requirements impact

Business requirements impact: none, because BR-047a, BR-053, and BR-060 already describe the intended visual/detail behavior.

## Diagram impact

Diagram impact: none, because no architecture relationships or workflows changed.

## Commits / logical change list

- Polished MainActivity overview title/header/icon styling.
- Polished BandDetailActivity primary panel, responsive stacking, group rating placement, and biography panel.

## Risks and follow-up

No device screenshot was captured in this pass. Schedule and support-flow visual polish remain in task-133 and task-134.
<!-- SECTION:NOTES:END -->
