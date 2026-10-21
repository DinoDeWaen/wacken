---
id: TASK-205
title: 'US: Choose System, Ironline dark or Field Notes light appearance'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-21 18:38'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-204
references:
  - wackentheme
modified_files:
  - backlog/docs/release-3.0-appearance-architecture-proposal.md
  - backlog/decisions/0024-shared-native-appearance-controller.md
  - app/src/test/java/be/wacken/planner/AppearancePreferenceTest.java
  - app/src/main/java/be/wacken/planner/AppearancePreference.java
  - app/src/main/java/be/wacken/planner/AppearanceController.java
  - app/src/main/java/be/wacken/planner/WackenTheme.java
  - app/src/main/res/values/styles.xml
  - app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java
  - app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java
  - app/src/main/java/be/wacken/planner/BandDetailActivity.java
  - app/src/main/java/be/wacken/planner/BandDetailPresentation.java
  - app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java
  - app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java
  - app/src/main/java/be/wacken/planner/ImportCsvActivity.java
  - app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java
  - app/src/main/java/be/wacken/planner/LoginActivity.java
  - app/src/main/java/be/wacken/planner/MainActivity.java
  - app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java
  - app/src/main/java/be/wacken/planner/ScheduleActivity.java
  - app/src/main/java/be/wacken/planner/SettingsActivity.java
  - app/src/main/res/values/ids.xml
  - app/src/main/AndroidManifest.xml
  - app/src/androidTest/java/be/wacken/planner/AppearanceAndroidTest.java
  - app/src/test/java/be/wacken/planner/SettingsActivityRegressionTest.java
  - >-
    groma/systems/wacken/containers/android-application/components/appearancecontroller.md
  - >-
    groma/systems/wacken/containers/android-application/components/appearancepreference.md
  - >-
    groma/systems/wacken/containers/android-application/components/wackentheme.md
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - backlog/docs/groma-appearance-review-2026-10-21.md
  - tools/groma/architecture-baseline.json
  - README.md
  - backlog/docs/business-requirements.md
priority: medium
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee, I want System/Dark/Light in Settings and a quick light/dark header action so I can read the app in my preferred appearance. Default to System; remember explicit device-local preference across restarts. Theme changes preserve screen, selected festival/band, input, scroll, expanded controls and unsaved edits. Reuse the existing Android presentation stack; no new framework or backend preference sync.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given no saved preference, when the app starts or the device theme changes, then System follows the device; an explicit Dark or Light choice overrides device changes.
- [ ] #2 Given a header toggle, when it is used, then it selects the opposite explicit appearance, labels the next action, and Settings reflects the same preference.
- [ ] #3 Given an in-progress form or rating, when appearance changes or an Activity is recreated, then input, selected context and pending work are retained without duplicate writes.
- [ ] #4 Given a restart, when the app reopens, then the saved preference is restored with a safe System fallback for missing/invalid values.
- [ ] #5 Unit and native tests cover System changes, overrides, persistence, recreation and state retention; both palettes meet contrast requirements in enabled states.
- [ ] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect hardcoded presentation tokens, Activity lifecycle/state, dialogs and existing private SharedPreferences usage. 2. Prepare concrete architecture options for shared Android presentation lifecycle/theme ownership; request explicit approval under AGENTS and task205 before implementing the recommended shared controller. Keep Android Views, current layers and Room/backend schema unchanged. 3. After approval, add failing pure preference/palette/import/contrast and native state-retention fixtures; implement device-local System/Dark/Light, contextual palette and header/Settings controls with semantic bindings and in-place restyling. 4. Preserve Activity Bundle/retained-session state without replaying writes; run native device/config/recreation/input/scroll/pending tests and ordinary ArchUnit/Groma gates with intentional source-ownership review. 5. Record accepted ADR if approved, docs/requirements implemented scope, required fresh signed build and validation package before Done. Standard presentation scope; minimal duplicated per-screen alternative and maximum framework migration considered in proposal. All production coding waits for explicit architecture approval because the proposed controller establishes shared lifecycle/state integration used by all11 Activities/dialogs.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.

Inspection found static color copies across Activities and uneven saved-state support; music and local CSV reviews already retain sessions. Prepared a concrete architecture request in backlog/docs/release-3.0-appearance-architecture-proposal.md. Recommended standard option: composable presentation-only controller, context-resolved semantic palette helpers and private device-local SharedPreferences; no new framework/base-Activity hierarchy/backend schema. Shared lifecycle/persistent-UI-state ownership requires explicit approval under AGENTS/task205; earlier design approval does not cover this mechanism. No production implementation written. TASK206-213 remain queued behind205; TASK204 completed and committed.

Explicit human architecture approval received21October2026: Standard, in response to the concrete shared appearance lifecycle proposal. Proceed with composable Android presentation controller, private device-local preference and semantic in-place bindings, retaining existing Activity/Views and domain/application boundaries. Record accepted ADR0024; no repeat approval required for the described scope.

Implemented approved pure preference/palette policy, private SharedPreferences controller and explicit view/panel bindings; no business operations in refresh. All11 Activities attach through composition and handle uiMode without theme-triggered recreation. Header/Settings share one preference; login has stable input IDs and password draft retained only in memory. Pure4-case preference/contrast suite and full app unit regressions passed (268 total,16 existing environment-gated skips). Initial native15-case run:14 passed,1 second-Activity harness timeout; corrected with explicit separate task launch, current16-case run progressing without failures. Added pending planning/history-count retention and Settings/radio/toggle native coverage; these need final full native run. Groma new sources combined through CLI into existing wackentheme responsibility and role overview updated; baseline review remains pending, no automatic acceptance.
<!-- SECTION:NOTES:END -->
