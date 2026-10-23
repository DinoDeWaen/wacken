---
id: TASK-212
title: 'TEST: Validate 3.0 paired appearance and critical flows on Android'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-23 07:38'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-207
  - TASK-208
  - TASK-209
  - TASK-210
  - TASK-211
references:
  - wackentheme
  - mainactivity
  - scheduleactivity
  - banddetailactivity
modified_files:
  - app/src/test/java/be/wacken/planner/AppearanceContrastTest.java
  - app/src/androidTest/java/be/wacken/planner/NativeAccessibilityEvidence.java
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - scripts/verify-android-accessibility.py
  - app/src/main/java/be/wacken/planner/AppearanceController.java
  - app/src/androidTest/java/be/wacken/planner/NativeUiTestRunner.java
  - >-
    groma/systems/wacken/containers/android-application/components/wackentheme.md
  - tools/groma/architecture-baseline.json
  - design/release-3.0/native/normal/dark-add-festival-200.png
  - design/release-3.0/native/normal/dark-after-disclosure-200.png
  - design/release-3.0/native/normal/dark-archived-200.png
  - design/release-3.0/native/normal/dark-bands-200.png
  - design/release-3.0/native/normal/dark-calendar-200.png
  - design/release-3.0/native/normal/dark-decision-200.png
  - design/release-3.0/native/normal/dark-decision-actions-200.png
  - design/release-3.0/native/normal/dark-music-review-200.png
  - design/release-3.0/native/normal/dark-planning-focus-200.png
  - design/release-3.0/native/normal/dark-schedule-filters-200.png
  - design/release-3.0/native/normal/dark-settings-200.png
  - design/release-3.0/native/normal/light-add-festival-200.png
  - design/release-3.0/native/normal/light-after-disclosure-200.png
  - design/release-3.0/native/normal/light-archived-200.png
  - design/release-3.0/native/normal/light-bands-200.png
  - design/release-3.0/native/normal/light-calendar-200.png
  - design/release-3.0/native/normal/light-decision-200.png
  - design/release-3.0/native/normal/light-decision-actions-200.png
  - design/release-3.0/native/normal/light-music-review-200.png
  - design/release-3.0/native/normal/light-planning-focus-200.png
  - design/release-3.0/native/normal/light-schedule-filters-200.png
  - design/release-3.0/native/normal/light-settings-200.png
  - design/release-3.0/native/narrow/dark-add-festival-200.png
  - design/release-3.0/native/narrow/dark-after-disclosure-200.png
  - design/release-3.0/native/narrow/dark-archived-200.png
  - design/release-3.0/native/narrow/dark-bands-200.png
  - design/release-3.0/native/narrow/dark-calendar-200.png
  - design/release-3.0/native/narrow/dark-decision-200.png
  - design/release-3.0/native/narrow/dark-decision-actions-200.png
  - design/release-3.0/native/narrow/dark-music-review-200.png
  - design/release-3.0/native/narrow/dark-planning-focus-200.png
  - design/release-3.0/native/narrow/dark-schedule-filters-200.png
  - design/release-3.0/native/narrow/dark-settings-200.png
  - design/release-3.0/native/narrow/light-add-festival-200.png
  - design/release-3.0/native/narrow/light-after-disclosure-200.png
  - design/release-3.0/native/narrow/light-archived-200.png
  - design/release-3.0/native/narrow/light-bands-200.png
  - design/release-3.0/native/narrow/light-calendar-200.png
  - design/release-3.0/native/narrow/light-decision-200.png
  - design/release-3.0/native/narrow/light-decision-actions-200.png
  - design/release-3.0/native/narrow/light-music-review-200.png
  - design/release-3.0/native/narrow/light-planning-focus-200.png
  - design/release-3.0/native/narrow/light-schedule-filters-200.png
  - design/release-3.0/native/narrow/light-settings-200.png
  - design/release-3.0/native/evidence.json
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/docs/release-3.0-native-validation-2026-10-23.md
  - app/src/main/java/be/wacken/planner/MainNavigation.java
  - >-
    groma/systems/wacken/containers/android-application/components/mainnavigation.md
priority: medium
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the release owner, I want native evidence for the validated design so HTML limitations do not become undetected production defects. Execute targeted Android instrumentation, accessibility and device review across both themes, System transitions, 200% font size and representative narrow screens. Complement existing unit/BDD/integration tests; do not replace them with screenshots. Record real user feedback and outdoor/one-handed limitations honestly.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given both appearances, when critical find/rate/return, schedule decision, metadata review, festival import/archive and offline retry journeys execute, then automated assertions prove correct outcomes and no unintended writes.
- [x] #2 Given TalkBack, keyboard/Switch Access where supported, 200% font size and narrow devices, then controls have meaningful labels/values, visible focus, reachable 48dp targets and readable essential content.
- [x] #3 Given theme/system transitions and Activity recreation mid-flow, then ratings, forms, selected files/context and review decisions remain intact with no duplicate operation.
- [x] #4 Measured text/control contrast and actual device/manual findings are recorded with screenshots, device/build identifiers, failures fixed or explicitly accepted; no fabricated outdoor/user evidence.
- [x] #5 README documents repeatable native validation commands; the full delivery validation package and remaining risks are reviewed before release.
- [x] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
- [x] #7 Both appearances pass the ADR-0016 lifecycle scenario and source-aware correction regressions from TASK-220; the redesign cannot change established ratings or snapshot semantics.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Standard completed: independent contrast references/thresholds; paired real TalkBack and native keyboard/role/selected-value/reachable48dp audits on normal411dp and narrow320x480dp at200%;44 source/device/APK-bound captures retained and inspected. Failing native regressions preceded safe-area fitting, correct exposed palette background and intact adaptive destination labels. Full native99 cases pass separately in Dark and Light; required CI39917868083 API23/API36 each99 plus build/database/architecture green. Fresh clean stable-key signed checkpoint, JVM/QA/lint/ArchUnit/BDD/coverage, real local creation/lifecycle/corrections and27 Groma/3 runner fixtures pass. Two exact authored overviews and missing-Android diagnostic/call deltas reviewed; final two clean scans pass. README/BR/native report/full evidence finalized. Deviations: visual review required three small presentation fixes; harness wrong detail assumption/shell quoting/service overlay corrected; disconnected emulator and stopped-Docker/default-JDK attempts replaced with successful runs. No architecture-significant boundary, dependency, schema, API or domain policy change; existing Standard/ADR0024 and ADR0016 retained. No fabricated physical, human spoken-feedback or outdoor evidence; those limits remain explicit.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Validated the paired 3.0 Android presentation with independent native contrast assertions, an optional real TalkBack audit, actual keyboard/semantic/48dp checks and 44 normal/narrow 200% screenshots with device/source/APK hashes. Visual review found and fixed system-bar overlap on Android15+, incorrect exposed dark safe-area background and split narrow destination labels. Shared content insets and adaptive primary navigation remain presentation-only. Product source5cc6a8cacb31b7233e28691c5b5af906c14185b9; final implementation/evidence CI head d3a9ac86f22f0fa378425eabbb069dffb315f760.

## Acceptance criteria validation

AC1/3: Complete99-case native suite passes with initial Dark and initial Light. Existing outcome assertions cover find/rate/return, scoped ratings, schedule choices/explicit locks, review approval/cancellation, import/archive/offline retry and no duplicate writes. System/theme/recreation, unsent forms/files/context/reviews and interrupted-request recovery stay protected. Actual normal and320x480dp audits each pass both palettes at fontScale2.
AC2/4: Installed TalkBack16.0.0.738667889 with touch exploration really enabled; native targets expose role/label/value/selected state and accept accessibility focus. Real Tab/Enter plus measured reachable48dp bounds and intact primary labels pass. Agent inspected actual captures; final manifest records both run heads, app/source hashes, fingerprint, exact settings and successful restoration. Minimum normal-text ratios dark7.315/light5.619, meaningful boundaries4.279/3.698, primary text12.480/8.233 exceed4.5/3 thresholds. No human spoken-comprehension, physical Switch Access, outdoor, glove or one-handed pass is fabricated.
AC5/6: README repeatable commands, BR validation limits, CLI-owned theme/navigation meaning, native report and complete package recorded before merge.
AC7: Real PostgreSQL/PostgREST lifecycle/correction gate passes band4→planning4→planning5/after3→archive band3→next planning3, current/older source corrections, durable reset, replay, original dates and separate members. Paired native Room/correction/rating presentation runs complement the palette-independent server/domain policy gates.

## How to test

### Automated tests

CI https://github.com/DinoDeWaen/wacken/actions/runs/39917868083 on exact d3a9ac8: all4 jobs green; native API23/API36 each99 cases with no failed/skipped cases. Clean signed checkpoint: test qaTest :app:lintDebug assembleRelease --rerun-tasks passed. Domain63/application163/infrastructure29/QA3 pass; Android debug/release JVM variants each257 pass plus16 intentionally environment-gated PostgREST skips. Dedicated creation integration executes those16 with fixture API credentials and passes without mandatory skips. Creation/lifecycle/correction/replay/isolation scripts pass against disposable local containers.
Final Groma gate: two repeatable scans,96 components/299 sources/13 flows/83 exact reviewed duplicate groups, no errors.27 gate fixtures and3 smoke-runner guards pass. Native report and README provide exact commands.
Fresh clean signed checkpoint APK /private/tmp/wacken-212-navigation-build-checkout/app/build/outputs/apk/release/app-release.apk SHA256 de487df9a6a17ed72089be673242144ea3f43f953f0d9cb831401812155b7291. Existing certificate ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f verified; unpublished2.35.1/code44 development checkpoint. TASK213 owns3.0/code45 and supported signed upgrade.

### Manual validation

Agent inspected real normal/narrow planning focus, scoped disclosure, schedule filters/decision actions, draft forms, music context and archived read-only images. Final narrow bar has intact full-width labels. Rendered Groma theme/navigation detail and exact source links inspected in Chromium. Evidence design/release-3.0/native/evidence.json and backlog/docs/release-3.0-native-validation-2026-10-23.md distinguishes agent image review from human/physical testing. Existing qualitative user approval17 October2026 remains the design review evidence.

## TDD / BDD / approval-test evidence

Bug regressions failed before fixes: screen starts under status bar; safe-area background absent/incorrect; narrow Schedule label has2 lines. Final native audit/CI passes after fixes. Contrast reference controls check21:1 and1:1. Existing BDD/domain/application/characterization/Room/SQL tests remain authoritative for lifecycle outcomes. Audit harness failures for wrong detail-navigation assumption, shell quoting and observed service permission overlay were corrected without masking app failures.

## Architecture impact

Architecture-significant change: none; existing Standard/ADR0024 and ADR0016 retained. No dependency, framework, module, base Activity, schema, API or domain policy change. Only existing theme/navigation overviews change through Groma CLI; exact ownership/arrows/flows/pins/exceptions and duplicate dispositions remain equal. One listener and fourteen calls yield2457 operations/1515 tokenized/12580 invocations; nine external-type/fourteen unresolved Android facts investigated with no warning increase. Actual Android compilation/native calls pass. ADR impact: none, because native fitting and audit extend existing approved presentation and validation seams.

## README impact

README impact: updated implemented-development scope, repeatable native/contrast/paired-suite commands, adaptive fitting and evidence/limitations.

## Business requirements impact

Business requirements impact: updated actual native validation scope, safe-area behavior and honest physical/field limits; no rating/snapshot or source-aware correction policy change.

## Diagram impact

Diagram impact: updated CLI-owned native theme/navigation responsibilities and rendered source review; existing C4 boundaries/relationships/flows retained.

## Commits / logical change list

2ea7286 safe areas, audit/contrast/runner;1e73236 theme meaning;5cc6a8c narrow destination fix/regression;6ffd0d7 navigation meaning;229682d reviewed baseline;d3a9ac8 captures/manifest/report/README/BR. Final evidence-only closure follows verified green implementation. PR https://github.com/DinoDeWaen/wacken/pull/15.

## Risks and follow-up

No physical phone, Switch Access hardware, spoken-feedback human study, bright-sun/glove/wet-screen or one-handed evidence; carry limits into release notes without claiming certification/outdoor suitability. Emulator font/device matrix is representative, not exhaustive. Actual TalkBack optional audit requires API24+, ordinary tests support23. Harness may decline only the observed Accessibility Suite notification prompt on the owned emulator and restores display/service settings. An interrupted7/99 run after device disconnect is not counted as a pass; restarted/final successful replacements are used. Initially stopped Docker and wrong default-JDK integration attempt were corrected; dedicated Java21 creation gate passes. Published release remains2.35.1 until TASK213. Unrelated user files preserved and excluded.
<!-- SECTION:NOTES:END -->
