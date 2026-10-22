---
id: TASK-207
title: 'US: Browse and filter the lineup in the paired 3.0 presentation'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-22 07:24'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-206
references:
  - mainactivity
modified_files:
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - app/src/test/java/be/wacken/planner/LineupPresentationTest.java
  - app/src/main/java/be/wacken/planner/LineupPresentation.java
  - app/src/main/res/values/ids.xml
  - app/src/main/java/be/wacken/planner/MainActivity.java
  - >-
    groma/systems/wacken/containers/android-application/components/lineuppresentation.md
  - >-
    groma/systems/wacken/containers/android-application/components/mainactivity.md
  - backlog/docs/groma-lineup-review-2026-10-22.md
  - tools/groma/architecture-baseline.json
  - README.md
  - backlog/docs/business-requirements.md
  - app/src/androidTest/java/be/wacken/planner/NativeUiTestRunner.java
  - app/build.gradle
priority: medium
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As an attendee, I want a readable alphabetical lineup in either appearance so I can find and rate bands quickly. Adapt existing overview behavior to the shared Ironline/Field Notes layout with accessible controls and unknown-link handling. Preserve actual stage/date/time data and existing filtering/business rules; sample prototype data is not production data.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a populated lineup, when viewed in either appearance, then band names, rating meaning and available performance context remain readable and alphabetically ordered.
- [x] #2 Given unknown YouTube/Spotify metadata, then the app offers no fabricated or null links; known links retain correct hand-off behavior.
- [x] #3 Given search/filter and a selected band, when detail is opened then closed or theme changes, then search/filter and row position are retained.
- [x] #4 Given empty/loading/error/offline states or long names at 200% font size, then useful actions remain reachable with at least 48dp targets and no clipped essential text.
- [x] #5 BDD/native regression coverage proves find/open/return, empty search, unknown links, cached use and large-font behavior.
- [x] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added red native find/filter/return and pure ordered-data/meaning tests. 2. Implemented wrapping real lineup rows with known-only music actions and local retained query/Unrated filter, stable adapter updates and scoped inline writes. 3. Put identification/search/filter in scrollable header and secondary/sync/recovery actions after bands; archive target height wraps. 4. Passed all84 native cases on API23/API36, extra320x480dp20 critical cases, unit/coverage/QA/ArchUnit/lint/API/database/safety gates and fresh stable-key signed build. 5. Combined internal helper into home via Groma CLI, reviewed rendered7-source home,7 removed table-duplicate pairs and exact scanner deltas; normal two scans pass. 6. Updated README/requirements and full evidence. Deviations: fixed adapter detachment discovered native; CI small-screen correction moved controls to footer; deterministic test-only pre-creation font Configuration replaces unreliable Android6 shell setting. Architecture standard unchanged; no new significant decision, ADR0024 retained; remaining208–213 stay separate.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Replaced compressed fixed-height lineup table with wrapping actual band names, explicit planning-rating meaning, real stage/date/time and stored-only music controls. Added local name search and All bands/Unrated display filtering. Stable adapter updates retain input focus; query/filter and selected-row context survive detail Back, appearance changes and recreation. A scrollable header contains identification/search/filter; secondary actions and sync/recovery feedback follow the bands in a footer so small screens show data sooner. Unknown links and no results are explicit; Clear search and filters and Retry sync preserve cached work. Controls/archive entries have minimum48dp targets and wrapping height.

## Acceptance criteria validation
AC1: existing scoped alphabetic use-case items are retained unchanged; unit selection tests and native planning/performance assertions pass in both appearances.
AC2: unknown platform has no button; known YouTube ACTION_VIEW handoff is intercepted and proved without opening a real provider.
AC3: native query/filter/detail Back/theme/recreation and original-row tests pass; filter reacts correctly after inline scoped rating change.
AC4: cached offline startup, readable200% long name with no ellipsis, TBA for actual unknown performance and reachable48dp footer actions tested. No-result recovery and failed refresh keep data and offer explicit actions.
AC5: all84 native tests pass on both API23/API36, including four new lineup scenarios; extra320x480dp20-case critical run passes.
AC6: docs, architecture evidence and canonical impacts below complete before merge.

## How to test
### Automated tests
CI https://github.com/DinoDeWaen/wacken/actions/runs/39735123093 passed all4 jobs at f4a0f66a2105341321346eed0bd2b50262c3ae52:84 native tests each API23/API36, JVM/unit/coverage/ArchUnit, QA, Android lint/API checks, signed-runner/image safety tests and real PostgreSQL/PostgREST creation plus integrated lifecycle/correction/replay/member-isolation gates.
Local app unit suite passed; CriticalFlows20/20 and full84/84 passed. Additional owned-emulator320x480dp CriticalFlows20/20 passed with display overrides restored in finally. Font200% is a per-intent test-only Configuration applied before real Activity view creation by NativeUiTestRunner; existing AndroidJUnitRunner framework and upgrade-probe conditional remain. Official API17+ contract: https://developer.android.com/reference/android/view/ContextThemeWrapper#applyOverrideConfiguration(android.content.res.Configuration).
Normal Groma gate passed two repeatable scans:96 components,298 uniquely owned sources,13 flows,81 prior duplicate identities with unchanged dispositions; no new exception.27 gate fixtures remain covered by CI. git diff --check passed.
Fresh clean clone at exact f4a0f66: JDK21 ./gradlew test qaTest :app:lintDebug assembleRelease --rerun-tasks passed99 executed tasks. APK /private/tmp/wacken-207-small-screen-build-checkout/app/build/outputs/apk/release/app-release.apk SHA256 3a30548345653d3752a3d2d6f5d84f32d8b031d5a528b39cc536e37746a110e0. apksigner verify passed existing stable certificate SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. Development checkpoint version2.35.1/code44 remains unpublished.
### Manual validation
Rendered Groma home overview,7 exact source files and actual collaborators inspected in /private/tmp/wacken-207-map. Native geometry/configuration assertions are automated evidence, not a human TalkBack/daylight/visual acceptance claim; integrated matrix remains TASK212. Manual reproduction: search a band, select Unrated/All, open/Back detail, switch theme/recreate; inspect same query/filter/row. Scroll to footer for sync/archive/retry. Test long names200% and known-only music buttons.

## TDD / BDD / approval-test evidence
Red native search test failed before implementation; red pure selection/label tests failed for missing helper. Green tests characterize cached values, actual metadata, scoped writes and retained context. First native run found that replacing adapter detached controls; update-in-place fixes it. CI small-screen failures showed header hid actual rows; moving secondary controls/feedback to footer fixes both APIs. Shell font_scale was ineffective live on Android6; explicit test Configuration now verifies actual fontScale2.0 without global-state contamination. Final all suites pass.

## Architecture impact
Architecture-significant change: none beyond approved standard Views/ADR0024 composition. Approval received: user Standard for shared presentation foundation. No framework/module/provider/persistence/business-policy change. Pure display helper combines via Groma CLI into existing home responsibility; existing rows/13 flows/pins/exceptions retained. Reviewed7 removed duplicate pairs correspond to deleted obsolete table helpers; all81 remaining identities/dispositions unchanged. Three TextWatcher callbacks account for override warning growth235to238; exact external-type/call/coverage deltas reviewed in backlog/docs/groma-lineup-review-2026-10-22.md. Thin test runner adds only deterministic per-intent configuration to the existing instrumentation framework; production has no test hook.
ADR impact: none, because display filtering, wrapping Views and native test configuration preserve existing boundaries and test strategy.

## README impact
README impact: updated3.0 development lineup/search/filter/known-link/recovery behavior while distinguishing published2.35.1.

## Business requirements impact
Business requirements impact: updated development lineup presentation and state retention, preserving canonical identities, scoped ratings and ADR0016 lifecycle semantics; remaining208-213 not implied complete.

## Diagram impact
Diagram impact: updated existing Groma home responsibility/source ownership through CLI;7 sources include the internal presentation helper. No new component/runtime boundary or authored relationship/flow change. Exact reviewed baseline refreshed after source/rendered/duplicate/diagnostic review.

## Commits / logical change list
6b7cb13 wrapping/filtering/native scenarios;3ea7f27 docs and archive target correction;5face93 exact reviewed scanner evidence;869d130 small-screen footer and deterministic native font fixture;f4a0f66 final reviewed footer evidence.

## Risks and follow-up
Accessible shared rating control/detail208, schedule209, Settings210, lifecycle/support211, integrated visual/TalkBack/physical validation212 and final signed upgrade/publication213 remain. Two previously accepted stale importer arrows remain exact, with no new exception. Signed checkpoint is not a3.0 publication.
<!-- SECTION:NOTES:END -->
