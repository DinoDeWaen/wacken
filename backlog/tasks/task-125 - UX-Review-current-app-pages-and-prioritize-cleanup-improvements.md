---
id: task-125
title: 'UX: Review current app pages and prioritize cleanup improvements'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-19 05:57'
updated_date: '2026-06-19 06:14'
labels:
  - ux
  - review
dependencies: []
priority: medium
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Review the current Android pages from a festival-use perspective and create concrete follow-up improvements for clarity, density, offline confidence, and rating/schedule ergonomics.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 The review covers the overview, band detail, schedule, schedule decision detail, settings, sync feedback, and import/admin flows.
- [x] #2 Findings are grouped by severity/value and translated into actionable backlog tickets where needed.
- [x] #3 The review calls out which improvements should be done before the next field-test APK.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Review overview, band detail, schedule, schedule decision detail, settings, sync feedback, and import/admin flows from festival-use perspective.
2. Identify high-value UX gaps and convert them into actionable tickets.
3. Record priorities for the next field-test APK.

Architecture impact: not architecture-significant; review only.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## UX review summary

Reviewed the current implemented pages and flows from the perspective of using the app during Wacken with weak or absent network.

Highest-value findings:

1. Overview should stay clean and fast for scanning; per-person group ratings belong in detail contexts, not under every band name. This is being implemented in task-123.
2. Band detail needs a clearer top section: own rating, Reset, group ratings, running order, and links are all functional but visually compete. Created task-127.
3. Schedule screen needs explanatory support for colors, scratched blocks, locks, and filters; these rules are powerful but not self-evident. Created task-128.
4. Offline-first behavior needs visible trust signals: cached data, sync running, and pending changes should be clear without blocking use. Created task-126.
5. Settings/import/admin feedback should explain success/failure, last sync, and next action. Created task-129.
6. Biography text still leaks raw HTML tags/entities in places. Created task-124.

Recommended before next field-test APK:

- Finish task-123 so rating placement matches user expectation.
- Finish task-124 so band biographies are readable.
- Consider task-126 next, because Wacken field use depends heavily on offline confidence.

## Created tickets

- task-124: DEF: Strip or render HTML in band biography text
- task-126: UX: Add persistent offline and pending-sync status
- task-127: UX: Refine band detail rating header
- task-128: UX: Add schedule legend and filter clarity
- task-129: UX: Improve import and admin feedback in settings

## Impact

README impact: none, because this review only created backlog follow-up work.
Business requirements impact: none, because no behavior was changed by the review itself.
Diagram impact: none, because no architecture or flow changed.
ADR impact: none, because this is not architecture-significant.

## Design addendum

The UX review must also be interpreted as a frontend visual-design review. Future UX work should evaluate the overall aesthetic quality, visual consistency, and professional metal identity of the app, not only task flow usability. The target is a uniform, premium, dark metal/festival design across all screens with consistent typography, spacing, iconography, controls, panels, colors, rating states, schedule states, empty states, and error/sync feedback.

Created task-130 to define the visual design system and identify the highest-impact implementation tickets.
<!-- SECTION:NOTES:END -->
