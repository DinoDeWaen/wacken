---
id: TASK-212
title: 'TEST: Validate 3.0 paired appearance and critical flows on Android'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-23 07:18'
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
- [ ] #1 Given both appearances, when critical find/rate/return, schedule decision, metadata review, festival import/archive and offline retry journeys execute, then automated assertions prove correct outcomes and no unintended writes.
- [ ] #2 Given TalkBack, keyboard/Switch Access where supported, 200% font size and narrow devices, then controls have meaningful labels/values, visible focus, reachable 48dp targets and readable essential content.
- [ ] #3 Given theme/system transitions and Activity recreation mid-flow, then ratings, forms, selected files/context and review decisions remain intact with no duplicate operation.
- [ ] #4 Measured text/control contrast and actual device/manual findings are recorded with screenshots, device/build identifiers, failures fixed or explicitly accepted; no fabricated outdoor/user evidence.
- [ ] #5 README documents repeatable native validation commands; the full delivery validation package and remaining risks are reviewed before release.
- [ ] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
- [ ] #7 Both appearances pass the ADR-0016 lifecycle scenario and source-aware correction regressions from TASK-220; the redesign cannot change established ratings or snapshot semantics.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Standard integrated validation using the existing JVM/native/BDD/SQL test strategy, Views/UiAutomation and owned emulator only. 1. Add independent measured palette text/control contrast assertions against the approved4.5/3 thresholds; no production style change unless a real failure requires it. 2. Extend native critical fixtures with paired200% semantic/keyboard/focus/target checks and repeatable screenshot evidence; exercise the installed real TalkBack service on owned API36 with explicit optional audit arguments and restore accessibility settings afterward. 3. Run representative320x480dp and normal owned-device audits, inspect real native screenshots and record device/build/source IDs, actual failures/fixes and physical/outdoor/one-handed/user-study limits honestly. 4. Reuse full existing native cases for search/rate/return,schedule/locks,proof/approval,forms/CSV/confirmations/offline/system/recreation and no unintended writes; run both palettes for ADR0016/source-aware correction regressions. 5. Run complete JVM/BDD/QA/coverage/ArchUnit/lint/API/real PostgreSQL/PostgREST/Groma and API23/API36 CI; fresh clean stable-key signed APK checkpoint and canonical report/README/BR impacts before closure. No new test framework,dependency,module,port,DB/API,domain policy or architecture-significant boundary; existing Standard/ADR0024 and ADR0016 retained. Risks: UI automation suppressing TalkBack by default, first-run service overlays, actual reachable bounds versus offscreen recycled views, nonlinear font scaling and fixture/privacy cleanup. Screenshots supplement outcome assertions; no claim of human/physical/outdoor validation from emulator evidence.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.
<!-- SECTION:NOTES:END -->
