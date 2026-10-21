---
id: TASK-204
title: 'DOC: Define the validated 3.0 paired appearance and flow contract'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-21 16:39'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies: []
modified_files:
  - backlog/docs/release-3.0-design-contract.md
  - backlog/docs/business-requirements.md
  - README.md
  - backlog/docs/visual-design-system.md
priority: medium
type: docs
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the product owner, I want one explicit approved design contract so the native implementation matches the reviewed flow. Document Ironline dark and Field Notes light palettes, shared hierarchy/spacing/targets, selective serif light headings, labeled navigation, state wording and screen inventory. Record the user validation without inventing completion timings. Mark planned behavior versus current release. Out of scope: Android implementation and changing rating/scheduling rules.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. This task is design implementation scope, not permission for an architecture-significant change; assess such changes and obtain explicit approval/ADR before coding if necessary.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the validated HTML, when the design contract is read, then both palettes and shared component/navigation behavior are specified with links to review evidence.
- [ ] #2 Given existing BR-047/BR-061 and the visual design system, when 3.0 scope is documented, then planned theme/navigation changes and retained business rules are explicit without describing them as already shipped.
- [ ] #3 Given every current screen and dialog, when the coverage inventory is reviewed, then loading/empty/error/offline/pending and both appearances are assigned to implementation and validation tasks.
- [ ] #4 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
- [ ] #5 The flow contract explicitly distinguishes the established personal band rating, selected-festival planning and after-rating under ADR-0016, and specifies one-time carry-over, archive promotion and source-aware correction states.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Reconcile approved paired HTML and qualitative review with current Android screen/dialog inventory and ADR0016 lifecycle. 2. Write a single3.0 design contract with exact paired palettes/type/spacing/target/navigation/state semantics and screen/state coverage owners TASK205-212; distinguish planned from shipped. 3. Align planned BR047/061 scope and README handover without claiming implementation or measured usability. 4. Validate source/inventory links and prototype preference/contrast checks, inspect retained rating policy, build fresh signed APK from clean completed doc source, record canonical validation impacts and commit before TASK205. Architecture impact: documentation of existing approved design only; Android Views/runtime/module/database decisions unchanged, no new ADR needed. No product code or rule changes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.
<!-- SECTION:NOTES:END -->
