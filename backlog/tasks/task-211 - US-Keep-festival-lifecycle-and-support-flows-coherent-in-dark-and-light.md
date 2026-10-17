---
id: TASK-211
title: 'US: Keep festival lifecycle and support flows coherent in dark and light'
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
priority: medium
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As an attendee, I want lifecycle, import, sync and support screens to match the selected appearance so the whole app stays understandable. Cover sign-in/session recovery, archive/history, Add/rename festival, import review, invite/export and startup/background sync. Use shared components without changing validated data guarantees or account/security behavior.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given startup/auth/recovery and an existing cache, then theme is applied consistently while cached work and background sync retain their current guarantees.
- [ ] #2 Given archive/add/rename/import or confirmation, then action scope, read-only state and success/failure recovery are clear in both appearances; no false group-wide success is shown.
- [ ] #3 Given a form or selected CSV, when theme changes or Activity recreates, then user input and pending operation state are preserved or restored with explicit safe recovery.
- [ ] #4 Given saved-local/pending/shared-saved status, then meaningful words and accessible announcements distinguish these states independently of color.
- [ ] #5 Tests cover lifecycle confirmations, atomic import failure/retry, offline cache, export/invite boundaries, auth return and theme state retention.
- [ ] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
- [ ] #7 Creation/archive feedback preserves ADR-0016 atomic carry-over/promotion and pending-data recovery, and cannot claim completion after a partial or failed lifecycle operation.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.
<!-- SECTION:NOTES:END -->
