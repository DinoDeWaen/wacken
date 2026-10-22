---
id: TASK-209
title: 'US: Read schedule choices and alternatives in both 3.0 appearances'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-22 14:02'
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
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/docs/groma-schedule-review-2026-10-22.md
  - tools/groma/architecture-baseline.json
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
- [x] #1 Given actual schedule data, then dates, stage labels, time ranges, ratings, walking context and chosen/alternative meaning remain available in both appearances without depending on color alone.
- [x] #2 Given day/threshold/barred filters, when changed or theme switches, then selected filters and calendar position are preserved with 48dp control targets.
- [x] #3 Given a chosen act, when its decision opens and an alternative is retained or selected, then existing conflict/lock behavior and personal ratings are preserved.
- [x] #4 Given 200% text or assistive input, then legend, filters and decision actions remain reachable; horizontal scrolling is limited to the calendar where needed.
- [x] #5 Characterization/domain/native tests cover ties, skipped overlap, locks, filters and both appearances.
- [x] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed standard presentation slice: characterized existing scratch/tie/filter/layout/manual-selection rules; red native selectors and palette tests then green native controls/state/viewport/200% decision behavior; paired calendar geometry/style and full accessibility semantics. Preserved application/domain/lock/rating ownership. CLI-only Groma overview/collaboration and exact two duplicate replacements reviewed against source/rendered export; pins/exceptions unchanged. README/BR development increments updated. Local89 native, clean stable-key signed build/test/QA/lint, two-scan Groma/27 fixtures and required CI39791333268 (API23/API36 each89 plus complete build/database gates) pass. No significant architecture change; Standard/ADR0024 retained, no new ADR. Deviation: guarded async cached-lock dialog restoration and filtered-empty viewport after native evidence; integrated manual device/accessibility remains212, publication213.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Labeled native day/threshold selectors and wrapping48dp controls replace squeezed horizontal strips. Local day/filter/key, calendar/outer viewport, input focus and actual chosen decision survive appearance changes and recreation, including filtered-empty results and delayed cached locks. The calendar alone scrolls horizontally; font-scaled geometry, paired grid/rank/scratch styling and full semantic block labels retain real facts. Wrapping chosen/alternative details keep explicit lock actions and independent personal values.

## Acceptance criteria validation

1. Actual cached dates/stages/time/rank/walking/scratch/lock context remains visible or available through full native block/decision semantics in both palettes; rank meanings no longer depend on color.
2. Native48dp day/threshold/barred controls and saved local state; regression restores horizontal position through empty results/theme/recreation. Existing day-selection unit cases retain actual date behavior.
3. Native actual alternative selection creates one pending group lock and preserves personal4/3 values; recreation restores chosen alternative and Unlock action.
4.200% native decision test checks full long-name wrapping, accessibility Button role, reachable actions and retained context; surrounding controls/dialog scroll vertically.
5. Existing schedule unit/domain/BDD scratch/tie/walking/cutoff/lock characterizations retained; two new native regressions cover both palettes and state.
6. README/requirements and reviewed Groma source/map/exact identities updated; required implementation CI39791333268 passes all four jobs.

## How to test

### Automated tests

- Existing schedule characterization passed before production refactor; new selector expectation failed red, palette overload tests failed red, then focused unit/native tests passed. Logs /private/tmp/wacken-209-characterization.log, wacken-209-red-native.log, wacken-209-green-unit.log and wacken-209-green-native.log.
- sh scripts/verify-android-smoke.sh emulator-5554 all:89/89 pass on owned API36 AVD appearance205; /private/tmp/wacken-209-native-full.log. Critical25/25 passed before final accessibility refinement; final full89 includes that refinement.
- Clean clone68e7d2d: ./gradlew test qaTest :app:lintDebug assembleRelease --rerun-tasks passes; /private/tmp/wacken-209-signed-build.log. Includes JVM/domain/application/BDD/ArchUnit and QA.
- Pinned Bun scripts/architecture/verify-groma.mjs passes, two scans created0/refreshed96;96components/299sources/13flows/81reviewedgroups. Exact existing two stale-arrow exceptions retained. /private/tmp/wacken-209-groma-final.log.
- git diff --check passes. Required implementation CI39791333268 on bfc6d7d3ec76c5c6159248d725a51e3b21ffd07b passes all four jobs:89 native tests on API23 and89 on API36; JVM/BDD/coverage/QA/ArchUnit/lint/supported APIs; real PostgreSQL/PostgREST creation/rating/archive/correction/deletion/ADR0016 lifecycle gates; pinned Groma and27 positive/negative fixtures. Local27 Groma fixtures also pass.

### Manual validation

Source/composition and Chromium rendered /private/tmp/wacken-209-map overview/source/collaborations reviewed; screenshot /private/tmp/wacken-209-map-detail.png. Native automation exercises real widgets/data/dialog actions, not prototype output. Human TalkBack, physical sunlight/device experience and integrated contrast/accessibility matrix remain TASK212; no such manual completion is claimed.

Fresh stable-key APK from clean68e7d2d: /private/tmp/wacken-209-final-build-checkout/app/build/outputs/apk/release/app-release.apk. SHA2566b4fce67cd9266c8d4f3004206edd84876e1a103ce4900627bdeb40e2d025e77; apksigner verifies certificate ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. Fresh checkout has no previous APK. Subsequent changes are docs/baseline/task evidence only. Unpublished checkpoint; release still2.35.1.

## TDD / BDD / approval-test evidence

Existing scratch/style/filter/layout/manual-selection and domain schedule tests characterize unchanged behavior. New Given actual cached schedule/When filter/theme/recreate and Given200% actual conflict/When choose/recreate native scenarios moved red→green. No scheduling policy was moved into the presentation.

## Architecture impact

Architecture-significant change: no new framework/module/port/dependency/storage/API or business ownership change. User Standard and ADR0024 remain the approved presentation foundation; ADR0016 roles remain unchanged.
ADR impact: none, because existing Views/appearance and domain/application boundaries suffice.

## README impact

README impact: updated development schedule controls/state/large-font behavior, validation scope and architecture review link.

## Business requirements impact

Business requirements impact: updated planned3.0 development increment; BR063–076 rules remain unchanged and2.35.1 stays the published baseline.

## Diagram impact

Diagram impact: updated Groma descriptions and actual ScheduleActivity→WackenTheme style collaboration through CLI; existing flow/ten-source ownership unchanged. Mermaid runtime boundaries do not change.

## Commits / logical change list

68e7d2d schedule/native controls/state/style/tests; bfc6d7d requirements/README and reviewed map/baseline evidence. PR https://github.com/DinoDeWaen/wacken/pull/12.

## Risks and follow-up

Essential calendar two-dimensional geometry remains; full detail/accessibility retains compact block facts. Native automated geometry does not establish real TalkBack/outdoor usability;212 owns integrated evaluation,213 publication. No migration, backend or write-policy change. Unrelated user edits excluded.
<!-- SECTION:NOTES:END -->
