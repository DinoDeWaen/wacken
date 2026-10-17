---
id: TASK-205
title: 'US: Choose System, Ironline dark or Field Notes light appearance'
status: To Do
assignee: []
created_date: '2026-10-17 09:21'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-204
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
