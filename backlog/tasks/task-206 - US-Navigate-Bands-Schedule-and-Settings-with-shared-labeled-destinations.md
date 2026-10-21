---
id: TASK-206
title: 'US: Navigate Bands, Schedule and Settings with shared labeled destinations'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-21 20:23'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-205
references:
  - mainnavigation
modified_files:
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - app/src/main/java/be/wacken/planner/MainNavigation.java
  - app/src/main/res/values/ids.xml
  - app/src/main/java/be/wacken/planner/MainActivity.java
  - app/src/main/java/be/wacken/planner/ScheduleActivity.java
  - app/src/main/java/be/wacken/planner/SettingsActivity.java
  - app/src/main/java/be/wacken/planner/BandDetailActivity.java
  - >-
    groma/systems/wacken/containers/android-application/components/mainnavigation.md
  - >-
    groma/systems/wacken/containers/android-application/components/mainactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/scheduleactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/settingsactivity.md
  - backlog/docs/groma-navigation-review-2026-10-21.md
  - tools/groma/architecture-baseline.json
  - README.md
  - backlog/docs/business-requirements.md
priority: medium
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As an attendee, I want consistent labeled destinations so I can move between rating and planning without losing context. Implement the approved three-destination pattern, detail/back behavior and accessible current-destination indication. Keep sync/close and festival management reachable, including return from external music apps. No changes to schedule rules or festival ownership.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given any main destination, when Bands/Schedule/Settings is selected, then the named destination and selected state are exposed visually and to accessibility services.
- [ ] #2 Given band detail or a review subflow, when Back is used, then the user returns to the correct band row or initiating Settings context without duplicate navigation.
- [ ] #3 Given the same flow in dark/light or after external-app return, then destination order and action meaning remain consistent and state is retained.
- [ ] #4 Native navigation tests cover Back, recreation, external-app return, offline use and reachable sync/close.
- [ ] #5 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspect existing Activity navigation/back/external return and characterize current selected-row/Settings return behavior. 2. Add a small stateless shared Bands/Schedule/Settings navigation view using existing Android Views/theme helpers, with fixed order, visible labels and accessible selected state. Reuse existing Activities/intents; repeated current destination is a no-op and existing destinations are reordered rather than duplicated. 3. Preserve detail/review Back and initiating context, sync/close and festival management; add native tests for order/selection, repeat navigation, Back/recreation, external-style pause/resume and offline use in both appearances. 4. Run characterization/JVM/native/API23/API36/ArchUnit/lint/Groma gates, review any intentional map/baseline changes, update README/planned requirement state, build fresh stable-key signed APK and record complete validation before Done. Architecture impact: stateless presentation composition in the already approved Android Views/controller boundary, no new navigation framework, module, data model or business-rule ownership; no additional significant architecture decision expected. ADR0024 retained. Screen search/filter and full layout work remains207-211.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.

Added the shared Bands/Schedule/Settings bar with visible/accessibility current state, no-op current destination and REORDER_TO_FRONT Activity reuse. Preserved existing detail/review Back and made Back to bands actually select Bands from either primary screen. Added stable root/list IDs for hierarchy restoration and changed detail Home wording to Back to match its initiating-context behavior. Red native scenario failed before implementation; after implementation the destination reuse scenario passed and app unit suite passed. Critical native run15/16 passed; launcher-return scenario is being rerun from a real launcher intent to distinguish task setup from production behavior. Groma CLI created/described an independent stateless primary-navigation responsibility and authored its three caller, three destination and theme-helper collaborations; source/role/baseline review remains pending.

Final local full native suite passed80/80 on API36 with no failures/skips. The launcher-return failure was traced to the test initial root using an explicit non-launcher intent; using the real package launcher intent passes Settings Back/recreation/scroll/Home-return without a production task workaround. Shared destination reuse, actual Back to original row and reachable sync/festival controls pass. Added bounded wait for the restored ScrollView layout to avoid asserting before the framework layout phase on older APIs. Reviewed rendered new navigation component, exact source/seven collaborations and baseline:96 components/297 sources/13 flows, same88 identities/reviews/pins/exceptions; only expected Android missing-type/unresolved-call facts and added source coverage changed. Updated README/requirements development status. Final clean signed build, normal Groma gate and CI remain before Done.
<!-- SECTION:NOTES:END -->
