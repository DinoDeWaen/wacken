---
id: task-146
title: 'DEF: Fix settings screen crash on open'
status: Done
assignee:
  - '@codex'
created_date: '2026-07-06 13:34'
updated_date: '2026-07-06 13:39'
labels: []
dependencies: []
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Settings must open reliably because MVP3 export, sync, import, and group actions all live there.

Defect: Opening Settings crashes because the rating allocation TextView is used before it is initialized.

Scope: initialize the rating allocation view before styling/adding it and add focused regression coverage or compile validation for Settings startup wiring.

Out of scope: unrelated Settings redesign or new MVP4 behavior.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Opening Settings no longer crashes before the rating allocation section renders
- [x] #2 The rating allocation section still shows local rating counts and export action
- [x] #3 Relevant tests/compile checks pass
- [x] #4 Backlog notes record root cause, fix, validation, and documentation impact
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirmed the Settings crash path by reading SettingsActivity startup/render code and related search results.
2. Fixed the root cause by initializing `ratingAllocation` with `new TextView(this)` before styling or adding it to the rating allocation section.
3. Added `SettingsActivityRegressionTest` to guard the initialization order because this project does not currently have a Robolectric/ActivityScenario harness.
4. Ran focused app unit/compile checks, then full clean domain/application/infrastructure/app validation plus debug and signed release APK builds.
5. Recorded root cause, validation, and canonical README/business/diagram/ADR impact notes.

Deviation: the first test run failed because `Files.readString` is unavailable for this test compile target; the regression test now uses `Files.readAllBytes`. Architecture impact: not architecture-significant; no domain, persistence, sync, dependency, schema, signing, or module boundary changes. Approval and ADR: not required.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed the Settings crash on open. Root cause: `SettingsActivity.content()` created the `Rating allocation` section and immediately called `ratingAllocation.setTextColor(...)`, but the `ratingAllocation` field was still null. That means opening Settings crashes deterministically during view construction before rating counts, CSV export, sync, or import actions can render.

The fix initializes `ratingAllocation = new TextView(this);` before the existing style calls and before adding it to the rating section. The rating allocation summary and `Export ratings CSV` action remain in the same section.

## Acceptance criteria validation

- AC1: Settings no longer dereferences a null `ratingAllocation` before rendering; the field is initialized at `SettingsActivity.java:95` before styling at lines 96-100.
- AC2: The rating allocation section still adds `ratingAllocation`, still calls `refreshRatingAllocation()`, and still includes the `Export ratings CSV` action.
- AC3: Relevant unit, compile, full validation, debug build, and signed release build checks passed.
- AC4: Root cause, fix, validation, and documentation impact are recorded here.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_STORE_FILE=$(security find-generic-password -s WACKEN_RELEASE_STORE_FILE -w) WACKEN_RELEASE_STORE_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_STORE_PASSWORD -w) WACKEN_RELEASE_KEY_ALIAS=$(security find-generic-password -s WACKEN_RELEASE_KEY_ALIAS -w) WACKEN_RELEASE_KEY_PASSWORD=$(security find-generic-password -s WACKEN_RELEASE_KEY_PASSWORD -w) ./gradlew clean :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug assembleRelease`
- `/Users/dino/Library/Android/sdk/build-tools/36.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk`
- `shasum -a 256 app/build/outputs/apk/debug/app-debug.apk app/build/outputs/apk/release/app-release.apk`
- `git diff --check`

Debug APK SHA-256: `6a2f688d2a8b5e9aeb7ac337b961881963ca5a8f5f9ce94794a50bd6c6e35690`
Release APK SHA-256: `fb990f018216b3c28f303d059c3019a7282fab63ec25166364b0facb7f392db9`

APK signature verification result: verifies with v1 and v2 schemes; apksigner reported Android packaging META-INF warnings only.

### Manual validation

Physical-device Settings smoke test was not run in this environment. Manual check for an installed fixed APK: sign in, open Settings from the overview cog, confirm the screen renders Group, Rating allocation, Sync, and Admin sections; confirm `Export ratings CSV` is visible.

## TDD / BDD / approval-test evidence

Bug-fix regression coverage was added in `SettingsActivityRegressionTest`, guarding that `ratingAllocation` is initialized before it is styled or added to the section. The first test compile failed because `Files.readString` is unavailable for this module test target; the test was corrected to use `Files.readAllBytes`, then app unit tests passed.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none required; this is a local Android UI initialization fix and does not change architecture, boundaries, persistence, sync, dependencies, schema, auth, or signing strategy.

## README impact

README impact: none, because the fix restores already documented Settings behavior and does not change setup, commands, architecture, public feature scope, or release-note navigation.

## Business requirements impact

Business requirements impact: none, because the fix restores existing BR-062 and BR-077 Settings behavior without changing business rules or product scope.

## Diagram impact

Diagram impact: none, because no architecture, module, dependency, or runtime data-flow diagram changed.

## ADR impact

ADR impact: none, because no architecture decision was introduced or changed.

## Commits / logical change list

- Initialize `ratingAllocation` before styling it in `SettingsActivity`.
- Add `SettingsActivityRegressionTest` for the initialization-order crash.
- Add task-146 backlog defect tracking and validation evidence.

## Risks and follow-up

- The published V2.21 GitHub release APK was built before this fix and is affected by the Settings crash. A hotfix release should be cut from this fixed state if you want an installable APK immediately.
- This project still lacks a real Android Activity test harness; the regression test is source-level coverage for the specific null dereference, backed by Android compile/build validation.
<!-- SECTION:NOTES:END -->
