---
id: TASK-207
title: 'US: Browse and filter the lineup in the paired 3.0 presentation'
status: To Do
assignee: []
created_date: '2026-10-17 09:21'
updated_date: '2026-10-17 13:21'
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
As an attendee, I want a readable alphabetical lineup in either appearance so I can find and rate bands quickly. Adapt existing overview behavior to the shared Ironline/Field Notes layout with accessible controls and unknown-link handling. Preserve actual stage/date/time data and existing filtering/business rules; sample prototype data is not production data.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a populated lineup, when viewed in either appearance, then band names, rating meaning and available performance context remain readable and alphabetically ordered.
- [ ] #2 Given unknown YouTube/Spotify metadata, then the app offers no fabricated or null links; known links retain correct hand-off behavior.
- [ ] #3 Given search/filter and a selected band, when detail is opened then closed or theme changes, then search/filter and row position are retained.
- [ ] #4 Given empty/loading/error/offline states or long names at 200% font size, then useful actions remain reachable with at least 48dp targets and no clipped essential text.
- [ ] #5 BDD/native regression coverage proves find/open/return, empty search, unknown links, cached use and large-font behavior.
- [ ] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.
<!-- SECTION:NOTES:END -->
