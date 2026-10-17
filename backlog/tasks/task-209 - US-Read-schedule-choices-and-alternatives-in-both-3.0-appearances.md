---
id: TASK-209
title: 'US: Read schedule choices and alternatives in both 3.0 appearances'
status: To Do
assignee: []
created_date: '2026-10-17 09:21'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-206
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
