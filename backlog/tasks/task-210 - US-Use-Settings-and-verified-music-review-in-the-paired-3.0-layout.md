---
id: TASK-210
title: 'US: Use Settings and verified music review in the paired 3.0 layout'
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
  - TASK-206
  - TASK-202
priority: medium
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival administrator, I want grouped Settings and trustworthy review screens in both appearances so I can manage links without losing work. Adapt Settings, Link bands and TASK-202 verified music review using production identity evidence. Include provider setup/errors, progress, cancellation, retry, approval/rejection and no-match. No new provider integration or changes to approved verification rules in this presentation story.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given Settings, when managing a festival, then Link bands and festival music discovery are adjacent and band-only discovery remains available on the appropriate detail page.
- [ ] #2 Given verification results, then candidate identity, canonical URL, clickable sources and verified/unverified rationale are readable in both appearances; unverified candidates cannot be saved.
- [ ] #3 Given a theme change during search/review, then search text, scope, progress and reviewed selections are retained; explicit approval alone writes missing fields.
- [ ] #4 Given rejection/no match/offline/missing configuration/rate limit/cancel/error, then accurate recovery/status is visible and existing metadata, ratings and history remain unchanged.
- [ ] #5 Native tests cover both entry points, evidence and approval gates, ongoing work across theme/recreation, no overwrite and provider failure states.
- [ ] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.
<!-- SECTION:NOTES:END -->
