---
id: TASK-212
title: 'TEST: Validate 3.0 paired appearance and critical flows on Android'
status: To Do
assignee: []
created_date: '2026-10-17 09:21'
updated_date: '2026-10-17 13:21'
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.
<!-- SECTION:NOTES:END -->
