---
id: task-131
title: 'UI: Centralize Android visual tokens and component helpers'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-19 06:26'
updated_date: '2026-06-19 06:44'
labels:
  - ui
  - design
  - technical-debt
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Create a shared Android UI styling foundation so colors, typography, spacing, buttons, panels, icon buttons, and status messages follow the visual design system instead of being redefined per Activity. This reduces visual drift and makes later screen polish consistent.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the app UI code, when common colors and control styles are needed, then Activities use shared visual tokens/helpers instead of duplicating screen-local constants where practical.
- [x] #2 Given an icon or action button, when it is rendered, then dimensions, text color, background, and content description follow the design system.
- [x] #3 Existing observable behavior is preserved with focused tests or compile validation.
- [x] #4 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added WackenTheme in the Android app module with shared visual design tokens for colors plus reusable button, icon-button, panel, and dp helpers.
2. Added WackenThemeTest for token values and button-style semantics that can be tested without Android runtime rendering.
3. Replaced duplicated common color constants and simple action/icon button styling across MainActivity, BandDetailActivity, SettingsActivity, LoginActivity, ImportCsvActivity, and ScheduleActivity where practical without changing workflows.
4. Ran app unit tests and Android compile validation.
5. Deleted the prior release APK and rebuilt a fresh signed release APK per release process.

Design approach: app-edge presentation helper only; Android widget construction remains Activity-owned.
Architecture impact: not architecture-significant; no domain/application/infrastructure dependency changes, persistence changes, APIs, or module boundaries changed.
Deviation: deeper layout polish is deferred to task-132, task-133, and task-134.
Treatment: standard UI foundation/refactoring task with compile validation.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added a shared Android visual foundation through WackenTheme and migrated repeated colors and simple button/icon styling across the main Activities. This creates a single source for the current metal palette and reusable action/icon/panel helpers while preserving existing workflows.

## Acceptance criteria validation

- AC1: Activities now consume shared WackenTheme tokens/helpers for common colors, panel backgrounds, action buttons, and icon buttons where practical.
- AC2: WackenTheme provides reusable action and icon-button helpers with stable sizing, text color, background, border, and content-description handling.
- AC3: Added WackenThemeTest and ran focused Android unit/compile checks plus full release validation.
- AC4: README/business requirements impact is recorded below.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac
- JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_* ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleRelease
- git diff --check

### Manual validation

- Verified signed APK metadata: versionCode 21, versionName 2.18, package be.wacken.planner.
- Verified APK signature with apksigner v1/v2 schemes.
- APK SHA-256 after fresh rebuild: 740a5a04ce421efd3004a2c2bdf172824ddc04d2010b6d3b1727decbeab9191a

## TDD / BDD / approval-test evidence

Added WackenThemeTest before closing the refactor so the shared visual token semantics are protected. Existing app unit tests and compile validation protect behavior preservation for the touched Android code.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: none, because task-130 already linked the visual design system and this task only implements an internal Android UI foundation without changing setup, commands, or public behavior.

## Business requirements impact

Business requirements impact: none, because BR-047a already captures the design-system requirement and this task implements the internal UI foundation for it.

## Diagram impact

Diagram impact: none, because no architecture relationships or workflows changed.

## Commits / logical change list

- Added WackenTheme and WackenThemeTest.
- Migrated common colors and simple action/icon button styling across overview, detail, schedule, settings, login, and import/admin Activities.

## Risks and follow-up

The foundation is intentionally small. Screen-specific hierarchy, spacing, legends, and support-flow layout polish remain in task-132, task-133, and task-134.
<!-- SECTION:NOTES:END -->
