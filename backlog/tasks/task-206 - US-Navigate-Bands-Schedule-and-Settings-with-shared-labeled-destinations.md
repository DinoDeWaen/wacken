---
id: TASK-206
title: 'US: Navigate Bands, Schedule and Settings with shared labeled destinations'
status: To Do
assignee: []
created_date: '2026-10-17 09:21'
updated_date: '2026-10-17 13:21'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-205
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.
<!-- SECTION:NOTES:END -->
