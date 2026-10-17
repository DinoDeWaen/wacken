---
id: TASK-219
title: >-
  US: Show and export band ratings and festival ratings with their correct
  context
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
  - TASK-218
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member, I want detail/history/export to distinguish my current band rating, festival planning rating and festival after-rating so I understand what carries forward. Apply ADR-0016 to active and archived detail, history, Settings export, missing-value states and any current-rating consumers. Metadata and music links resolve through the canonical band. Do not show another festival's after-rating as the selected festival's value. Keep group scheduling based only on that festival's planning ratings. Archived correction audit presentation remains TASK-172;3.0 styling remains separate.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a band has current rating4, selected-festival planning5 and after-rating3, then details/history/export identify these as three separate values with user/festival/source context.
- [ ] #2 Given the selected festival has no after-rating but another festival does, then its after-rating remains empty and the other result appears only in labeled history/current-band context.
- [ ] #3 Given planning or after-rating edits, then group scheduling uses only the selected festival's planning ratings and metadata/music links retain canonical identity.
- [ ] #4 Given an existing exported CSV contract, then any extension is documented and compatibility is preserved or versioned; original event dates and correction/archive timestamps are distinguishable.
- [ ] #5 Application/native/export regression tests cover different users, repeated bands, renamed/deleted festivals, unrated states and preserved navigation.
- [ ] #6 README and business requirements describe the implemented presentation/export; canonical diagram and ADR impacts reference ADR-0016.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
2026-10-17 user-approved architecture: ADR-0016 governs canonical shared bands, member-specific established band ratings, independent festival planning/after-ratings, one-time creation snapshots and atomic archive promotion. Missing after-ratings retain the prior band rating; archived corrections update it only while their festival remains the source. TASK-216–220 implement and validate the lifecycle. This supersedes earlier latest-event prefill wording; do not treat approved planning as shipped behavior.
<!-- SECTION:NOTES:END -->
