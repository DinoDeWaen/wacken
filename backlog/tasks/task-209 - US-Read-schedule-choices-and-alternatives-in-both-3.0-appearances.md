---
id: TASK-209
title: 'US: Read schedule choices and alternatives in both 3.0 appearances'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-22 12:49'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-206
references:
  - scheduleactivity
  - wackentheme
modified_files:
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - app/src/test/java/be/wacken/planner/ScheduleBlockStyleTest.java
  - app/src/main/java/be/wacken/planner/ScheduleBlockStyle.java
  - app/src/main/res/values/ids.xml
  - app/src/main/java/be/wacken/planner/WackenTheme.java
  - app/src/main/java/be/wacken/planner/ScheduleActivity.java
  - app/src/main/java/be/wacken/planner/ScheduleLegendContent.java
  - app/src/test/java/be/wacken/planner/ScheduleLegendContentTest.java
  - >-
    groma/systems/wacken/containers/android-application/components/scheduleactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/wackentheme.md
priority: medium
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee, I want readable schedule controls and clear choice explanations in dark and light so I can plan confidently in the field. Restyle actual calendar, filters, legend, walking context and decision dialogs. Preserve day cutoff, ties, vetoes, walking-time overlap rules and shared manual locks; retain essential two-dimensional calendar scrolling.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given actual schedule data, then dates, stage labels, time ranges, ratings, walking context and chosen/alternative meaning remain available in both appearances without depending on color alone.
- [ ] #2 Given day/threshold/barred filters, when changed or theme switches, then selected filters and calendar position are preserved with 48dp control targets.
- [ ] #3 Given a chosen act, when its decision opens and an alternative is retained or selected, then existing conflict/lock behavior and personal ratings are preserved.
- [ ] #4 Given 200% text or assistive input, then legend, filters and decision actions remain reachable; horizontal scrolling is limited to the calendar where needed.
- [ ] #5 Characterization/domain/native tests cover ties, skipped overlap, locks, filters and both appearances.
- [ ] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Standard presentation slice inside approved Views/ADR0024 boundary. 1. Preserve existing schedule/scratch/tie/lock characterization and add red native filter/viewport/recreation/large-font decision tests using actual cached data. 2. Replace horizontally squeezed day/threshold strips with labeled native selectors and wrapping48dp controls; keep horizontal scrolling only inside calendar, readable legend toggle and color-independent status. 3. Save day/threshold/barred/legend/outer/calendar positions across rerenders, theme and recreation; retain existing application generation and durable manual-lock behavior, guard stale Activity callbacks. 4. Bind custom calendar grid/block/scratch surfaces to paired semantic palette while retaining original rank/style/scratch classifications and exact walking/timecutoff/tie rules; scale calendar geometry for fonts and preserve full stage/date/time/value/context in accessible block labels and wrapping decision dialog. 5. Preserve chosen/alternative selection/lock semantics and personal ratings with native regression, including200% reachability; no new scheduling/business configuration or domain-rule movement. 6. Run unit/BDD/domain/application/native API23/API36, ArchUnit/lint/database/Groma gates, review actual source/map/duplicate/scanner changes, update README/requirements and fresh stable-key signed checkpoint before closure. No new framework/module/dependency/persistence/API or significant architecture decision; approval Standard/ADR0024 retained. Risks: async lock rerender racing view restoration, filtered-empty viewport, native selector callback loops, calendar custom-drawable contrast/large-font geometry and dialog actions. Integrated human/device/TalkBack/contrast matrix remains212.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.
<!-- SECTION:NOTES:END -->
