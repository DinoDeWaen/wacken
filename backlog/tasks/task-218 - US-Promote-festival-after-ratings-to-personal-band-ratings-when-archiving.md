---
id: TASK-218
title: 'US: Promote festival after-ratings to personal band ratings when archiving'
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
  - TASK-217
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member, I want archiving to establish my latest band opinion from that festival's after-rating while preserving the festival record. Scope: one authorized atomic server operation archives the active festival and promotes each applicable member's nonempty final after-rating to that member's current band rating, retaining source festival/event and timestamps. Cache the acknowledged result transactionally and integrate with existing sync. No after-rating retains the previous current band rating; planning never supplies the promotion value. Idempotency, concurrency, pending-write conflict handling and old-client compatibility must prevent status-only archival from bypassing promotion.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given band rating4, festival planning5 and after-rating3, when archive succeeds, then the festival retains5/3, its status is archived and that member's band rating becomes3 with source provenance.
- [ ] #2 Given multiple members and bands, then each member's final nonempty after-rating is promoted to the matching personal band rating; missing/reset after-ratings retain prior values.
- [ ] #3 Given a denied, offline, interrupted or failed archive, then the UI does not falsely report a complete archive or partially promoted ratings; retries confirm the same result without duplicate effects.
- [ ] #4 Given concurrent rating edits, pending local work or an older client, then archiving cannot silently discard edits or bypass promotion; conflicts have actionable recovery and cached data remains usable.
- [ ] #5 BDD/TDD, real database transaction-fault/ownership/concurrency/idempotency tests and native archive/failure tests validate the complete operation.
- [ ] #6 README/business requirements, accepted ADR-0016, rollout guidance and canonical diagram/ADR impacts are updated.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
2026-10-17 user-approved architecture: ADR-0016 governs canonical shared bands, member-specific established band ratings, independent festival planning/after-ratings, one-time creation snapshots and atomic archive promotion. Missing after-ratings retain the prior band rating; archived corrections update it only while their festival remains the source. TASK-216–220 implement and validate the lifecycle. This supersedes earlier latest-event prefill wording; do not treat approved planning as shipped behavior.
<!-- SECTION:NOTES:END -->
