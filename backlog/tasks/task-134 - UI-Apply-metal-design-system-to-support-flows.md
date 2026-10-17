---
id: task-134
title: 'UI: Apply metal design system to support flows'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-19 06:28'
updated_date: '2026-06-19 15:10'
labels:
  - ui
  - design
  - settings
  - sync
  - import
dependencies: []
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Polish settings, login, import/admin, and sync/offline feedback so support flows feel as professional as the daily schedule and rating screens. These flows should use consistent sections, inputs, buttons, status panels, and short actionable messages.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given settings renders, when group, rating allocation, sync, and admin actions appear, then they are grouped into clear sections using shared panels and buttons.
- [x] #2 Given login renders or shows an error/progress state, then inputs, primary action, and status messaging use the shared visual system.
- [x] #3 Given import/admin renders file selection and validation results, then selected files, success, warnings, and errors use shared panels and readable status formatting.
- [x] #4 Given sync/offline/pending states are shown, then they use the global status language and remain useful under weak connectivity.
- [x] #5 Automated or focused compile validation proves the touched UI code builds; manual validation steps are documented.
- [x] #6 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected SettingsActivity, LoginActivity, ImportCsvActivity, and sync status text against the visual design system.
2. Grouped settings into group, rating allocation, sync, and admin sections using shared panels and buttons.
3. Polished login with a dark input/action panel and bordered status message area.
4. Polished import/admin file selection rows and result messages with shared panels and readable success/error states.
5. Ran Android unit tests and compile validation.
6. Deleted the prior release APK and rebuilt a fresh signed release APK per release process.

Design approach: screen-level support-flow polish using WackenTheme and existing Activity flows; no auth, import, or sync behavior changes.
Architecture impact: not architecture-significant; app UI only, no persistence/API/domain changes.
Deviation: persistent offline queue indicators remain task-126 scope; this task improved existing status-state presentation only.
Treatment: standard UI implementation task.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Applied the metal design system to support flows. Settings now uses clear Group, Rating allocation, Sync, and Admin panels. Login uses a dark input/action panel and consistent status surface. Import/admin file rows and result messages use dark bordered panels with readable success/error styling. Existing auth, sync, and import behavior is unchanged.

## Acceptance criteria validation

- AC1: Settings groups group identity/invite, rating allocation, sync, and admin import actions into visual sections with shared panels/buttons.
- AC2: Login inputs, sign-in action, progress, and error/status messaging use shared dark-panel styling.
- AC3: Import/admin file pickers and result messages use shared panels with clear success/error colors.
- AC4: Existing sync states now use clearer status text, including cached-data fallback language for failed sync.
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
- APK SHA-256 after fresh rebuild: f882de9b665c5778e4dc4aa526cce496d46c27832a150c300f33d0df476ac918

## TDD / BDD / approval-test evidence

This is support-flow visual polish. No auth, sync, import, domain, or persistence behavior changed; compile/build validation protects the touched Android rendering code.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required.

## README impact

README impact: none, because the visual design system is already documented and linked; this task applies it to existing support flows.

## Business requirements impact

Business requirements impact: none, because BR-047a, BR-057, BR-059, and BR-062 already describe the visual/support-flow behavior.

## Diagram impact

Diagram impact: none, because no architecture relationships or workflows changed.

## Commits / logical change list

- Grouped settings into visual sections.
- Styled login input/action/status surfaces.
- Styled import file rows and result messages.

## Risks and follow-up

Persistent offline and pending-sync indicators remain in task-126.
<!-- SECTION:NOTES:END -->
