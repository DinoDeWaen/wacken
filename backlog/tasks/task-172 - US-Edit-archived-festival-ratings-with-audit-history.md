---
id: task-172
title: 'US: Edit archived festival ratings with audit history'
status: To Do
assignee: []
created_date: '2026-08-19 11:02'
updated_date: '2026-10-17 13:21'
labels:
  - user-story
  - future
  - archive
  - ratings
  - audit
milestone: m-0
dependencies:
  - TASK-215
  - TASK-216
  - TASK-218
priority: low
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Historical ratings can be corrected after a festival without losing trust in what changed.

As a user, I want to correct ratings on archived festivals with change history so that my past festival record stays accurate.

Scope: explicit archived edit mode, planning or real-rating corrections, and audit visibility for who changed what and when.

Out of scope: editing festival master data, re-opening an archived festival as active, and multiple upcoming festival management.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given an archived festival is opened, then it is read-only by default until the user explicitly enters an allowed edit mode.
- [ ] #2 Given a rating is changed for an archived festival, when the change is saved, then the old value, new value, user, and timestamp are recorded.
- [ ] #3 Given archived ratings are exported or shown in details, then corrected values and historical context remain understandable.
- [ ] #4 Relevant tests cover read-only default behavior, allowed corrections, and audit history.
- [ ] #5 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording when implemented.
- [ ] #6 Given an archived after-rating is corrected, then its audit and any current band-rating update commit atomically only if that festival is still the source; an older correction cannot replace a newer source or refresh existing later-festival planning snapshots. Planning correction never promotes a band rating.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
2026-10-17: Current archives are read-only and have no atomic correction audit. The proposed own-rating correction port, additive audit schema/RLS and transaction require explicit approval; TASK-202 approval excluded schema changes and does not apply. See backlog/docs/release-2.34-remaining-architecture-proposal.md (TASK-215).

2026-10-17 user-approved architecture: ADR-0016 governs canonical shared bands, member-specific established band ratings, independent festival planning/after-ratings, one-time creation snapshots and atomic archive promotion. Missing after-ratings retain the prior band rating; archived corrections update it only while their festival remains the source. TASK-216–220 implement and validate the lifecycle. This supersedes earlier latest-event prefill wording; do not treat approved planning as shipped behavior.
<!-- SECTION:NOTES:END -->
