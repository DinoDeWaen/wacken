---
id: TASK-192
title: 'DEF: Discard a blocked pending rating from recovery dialog'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-16 05:56'
updated_date: '2026-10-16 05:58'
labels: []
milestone: m-1
dependencies: []
priority: high
type: bug
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: a signed-in user who decides not to keep an orphaned local planning rating can explicitly clear that one blocked pending change and resume sync. The popup shows the band, value, and stored festival ID. Scope: discard only the displayed pending rating after a second confirmation; leave every other pending change, synced rating, band, and festival untouched. Out of scope: clearing all app data or all pending changes at once.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a blocked pending planning rating, when the recovery popup opens, then it offers a clearly labeled discard action alongside keep and move.
- [ ] #2 Given the user confirms discard, when the exact displayed pending row is unchanged, then only that row is removed and the pending count decreases; a later sync no longer retries it.
- [ ] #3 Given cancellation or a changed row, no rating is deleted and the user sees an accurate result.
- [ ] #4 Tests cover exact-row deletion, preservation of other pending changes, and retry behavior.
- [ ] #5 README explains this irreversible per-rating action and architecture impacts are recorded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add a secondary confirmation for Discard this pending rating in the existing missing-festival review dialog, showing the exact band, stars, and stored festival ID.
2. Add an application use case and local-store port operation that conditionally deletes only the exact current user/group/festival/band/value row while its status is PENDING; do not clear synced or other pending rows. Return a clear changed-row result.
3. After confirmed discard, refresh the pending count and retry normal sync. Add red/green tests for exact discard, changed row, cancellation, and preservation of other rows.
4. Update README, BR-095a and ADR 0011 as needed, run focused and full validation, then commit TASK-192 before starting TASK-193.

Architecture-significant: adds an application port operation and Room deletion boundary. Explicit approval requested before coding.
<!-- SECTION:PLAN:END -->
