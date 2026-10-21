---
id: TASK-205
title: 'US: Choose System, Ironline dark or Field Notes light appearance'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-21 19:05'
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
- [x] #1 Given no saved preference, when the app starts or the device theme changes, then System follows the device; an explicit Dark or Light choice overrides device changes.
- [x] #2 Given a header toggle, when it is used, then it selects the opposite explicit appearance, labels the next action, and Settings reflects the same preference.
- [x] #3 Given an in-progress form or rating, when appearance changes or an Activity is recreated, then input, selected context and pending work are retained without duplicate writes.
- [x] #4 Given a restart, when the app reopens, then the saved preference is restored with a safe System fallback for missing/invalid values.
- [x] #5 Unit and native tests cover System changes, overrides, persistence, recreation and state retention; both palettes meet contrast requirements in enabled states.
- [x] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Recorded explicit Standard approval and accepted ADR0024. 2. Established red/green pure preference/contrast policy; added composable private local controller and semantic view/panel/window bindings with lifecycle cleanup and uiMode handling in11 Activities. 3. Added shared Header/Settings controls, Login saved/retained input safety and native preference/configuration/recreation/pending/history/Settings tests; corrected two test-harness assumptions. 4. Curated new sources into existing Groma theme owner via CLI, reviewed rendered source links, seven replacement duplicate identities and exact diagnostic deltas, accepted reviewed baseline with pins/exceptions unchanged. 5. Passed clean full JVM/QA/lint/fresh signed build, local Groma gate and final-source CI including77 native cases on bothAPI23/36 and real database/API gates. Finalized docs and task evidence before completion. No new framework, base Activity, backend schema or business policy. Optional local visual capture blocked by System UI ANR; full native visual/accessibility remains TASK212. Other3.0 layout/navigation/per-screen recovery belongs to TASK206-211.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented the explicitly approved standard appearance foundation (ADR0024) using existing Android Views and direct Activities. A pure System/Dark/Light preference/palette policy and private SharedPreferences controller update explicit semantic view/panel bindings and system bars in place. All11 Activities attach by composition and handle uiMode without theme-triggered recreation; listeners detach on destruction. Header and Settings share one local preference, default/fallback System, opposite explicit toggle and next-action labels. Login has stable input IDs and a configuration-only password draft in memory, not serialized saved state. No business operation is invoked by palette refresh.

## Acceptance criteria validation

AC1/2/4: Four pure preference/contrast tests cover missing/invalid values, device resolution, overrides, stored round-trip and toggle semantics. Native tests exercise actual controller configuration callbacks, explicit overrides, malformed stored types, new Activity persistence, open-screen propagation and Settings selected-radio/header agreement. AC3: Native tests preserve existing input/view identity, email/password on independent recreation, selected rating context and pending planning value/status with unchanged history count; existing review/session and critical-flow characterizations remain green. AC5: Both approved palettes pass enabled text/control contrast combinations and actual native color assertions. Final-source full77-case native suite passes on API23 and36. AC6: Docs, approved ADR, CLI-curated ownership and complete validation package below. Remaining navigation/layout/accessibility and screen-specific recovery scope stays assigned to TASK206-212; no full3.0 completion is claimed.

## How to test

### Automated tests

Clean checkout source e1358b979d85a9c5eda5f71e81ff0871ca230a2c: ./gradlew test qaTest :app:lintDebug assembleRelease --rerun-tasks passed. Domain63/application163/QA3/infrastructure29 including ArchUnit5, zero failures/skips. Android debug/release unit variants each268 with16 pre-existing environment-gated integration skips, not counted as passes. Required real database/API gates are green separately.

CI https://github.com/DinoDeWaen/wacken/actions/runs/39656193154 passed all4 jobs on final implementation source: JVM/ArchUnit/QA/coverage/lint,27 Groma fixtures plus two-scan gate, disposable PostgreSQL/PostgREST creation/lifecycle/correction/replay/isolation, API23 and36 each77 native tests with no failures/skips. Local accepted Groma gate also passed95 components/296 sources/13 flows/88 reviewed groups, with zero unexplained islands. Pins/exceptions unchanged.

Native local progression: initial15-case run had14 passes and a second-Activity test-launch timeout; fixed explicit separate-task test launch and16-case run passed. Full77-case local run had76 passes and a RadioButton performClick-return assertion; corrected the harness assertion, retained actual preference/checked-state assertions, and focused Settings test passed. Final CI reran all77 cases successfully on both APIs. No earlier failed mandatory run is represented as a pass.

### Manual validation

Reviewed semantic bindings, callback cleanup, pure policy, retained Login draft and independent rating/pending/history assertions. Exported/rendered Groma map and inspected theme responsibility, collaboration and all three exact source links. Seven changed duplicate identities and exact scanner diagnostic growth reviewed individually in groma-appearance-review-2026-10-21.md before accepting baseline. Optional post-suite native screen capture was blocked by System UI ANR on the disposable local emulator; no native visual/accessibility pass is inferred from it. TASK212 owns actual full-screen visual/device/TalkBack/200%/physical target evidence.

Fresh signed APK: /private/tmp/wacken-205-build-checkout/app/build/outputs/apk/release/app-release.apk, SHA256 3b352595e424c682c15c6dbe2b1b90938b0cdd192f9e64f8eb5f80d20bae79ac. Built from clean e1358b9 with no prior APK, verifies v1/v2 and stable certificate ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. Validation build only; published2.35.1 assets unchanged.

## TDD / BDD / approval-test evidence

Pure preference/contrast suite failed before the policy existed, then4/4 passed. Native Given/When/Then scenarios cover stored preference/configuration/toggle/recreation and unchanged pending/history state. Existing critical UI, lifecycle, review and database characterization remains green; no business-rule refactoring. Source-text initialization regression updated to recognize the new semantic binding setter.

## Architecture impact

Architecture-significant change: shared presentation lifecycle/preference integration. Approval received: explicit Standard response21October2026 to the concrete proposal, before coding. Android Views, direct Activities, JVM boundaries, Room/backend contracts and ADR0016 remain unchanged. ADR impact: created accepted ADR0024. No new runtime library, framework/base-Activity hierarchy or backend preference synchronization.

## README impact

README impact: updated implemented development-branch appearance scope, commands, ownership review and remaining3.0 work, distinct from published2.35.1.

## Business requirements impact

Business requirements impact: updated3.0 development increment status while preserving all business/rating/source policies and planned-versus-published distinction.

## Diagram impact

Diagram impact: updated visual-theme role/ownership through Groma CLI, combining the two new source files into the existing responsibility and reviewing rendered details. Runtime C4 boundaries/collaborations unchanged; reviewed baseline records296 owned sources and seven replacement duplicate identities, not a count allowance.

## Commits / logical change list

62bc7c1: approved controller/policy, semantic bindings, controls, native/unit tests and ADR. db7dfda: remaining row/label/sync bindings. e1358b9: final native harness correction, documentation and reviewed architecture baseline. Task closure commit contains evidence only. PR https://github.com/DinoDeWaen/wacken/pull/8.

## Risks and follow-up

TASK206-211 retain ownership of labeled navigation, full layouts/reflow/accessibility, schedule-specific rendering, broader per-screen recreation/recovery and support flows. TASK212 still requires integrated native/device/manual evidence; current automated configuration callback tests are not a human TalkBack or daylight-usability claim. Known Java scanner limitations and the two accepted stale importer arrows remain explicit. Invalid preference types fall back safely to System. Unrelated user worktree files were preserved and excluded from commits and signed build.
<!-- SECTION:NOTES:END -->
