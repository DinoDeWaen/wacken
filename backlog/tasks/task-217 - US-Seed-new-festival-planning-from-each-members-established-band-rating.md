---
id: TASK-217
title: 'US: Seed new festival planning from each member''s established band rating'
status: To Do
assignee: []
created_date: '2026-10-17 13:18'
updated_date: '2026-10-17 13:21'
labels:
  - ratings
  - festival
  - architecture
  - release-2.34
milestone: m-0
dependencies:
  - TASK-216
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member, I want a new festival to start from my established band ratings so I can plan independently without losing my longer-term opinion. Reuse/create canonical catalog bands and link the festival to them. Copy each applicable existing group member's personal current band rating into that member's new festival planning row once; unknown bands/ratings stay unrated. Keep after-ratings empty for the new festival. Extend the existing atomic creation contract compatibly with ADR-0016; preserve request replay and published festival fixes. Reviewed aliases remain TASK-156.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given an existing band and member band rating4, when a festival is created, then the canonical band is linked and that member's festival planning rating starts at4 while its after-rating is empty.
- [ ] #2 Given several existing members have different band ratings, then creation seeds each member's own value without exposing private history or copying the creator's value to others.
- [ ] #3 Given an unknown band or missing personal rating, then the catalog band is created if needed, linked once and left unrated; metadata/music links are not invented.
- [ ] #4 Given planning or band ratings change later, or an uncertain creation request is retried, then existing festival planning edits and after-ratings are not reset or continuously refreshed.
- [ ] #5 BDD/TDD, real PostgreSQL/PostgREST rollback/concurrency/RLS/replay tests and native creation tests pass, retaining the Summer Breeze63-band and old exact-name client gates.
- [ ] #6 README/business requirements document carry-over; accepted ADR-0016 and canonical diagram/ADR impacts are recorded.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
2026-10-17 user-approved architecture: ADR-0016 governs canonical shared bands, member-specific established band ratings, independent festival planning/after-ratings, one-time creation snapshots and atomic archive promotion. Missing after-ratings retain the prior band rating; archived corrections update it only while their festival remains the source. TASK-216–220 implement and validate the lifecycle. This supersedes earlier latest-event prefill wording; do not treat approved planning as shipped behavior.
<!-- SECTION:NOTES:END -->
