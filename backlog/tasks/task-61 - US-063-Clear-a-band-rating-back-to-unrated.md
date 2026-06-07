---
id: task-61
title: 'US-063: Clear a band rating back to unrated'
status: To Do
assignee: []
created_date: '2026-06-07 16:02'
labels:
  - mvp2
  - rating
  - sync
  - android
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want to delete/clear my rating for a band, so mistakes can be reset to unrated instead of forcing me to choose 1-5.

In scope:
- Provide a clear rating action in the overview and/or detail rating workflow.
- Store the cleared rating as `0`/unrated locally.
- Sync the cleared rating state to Supabase so other devices and schedule decisions no longer treat the previous rating as active.
- Keep existing 1-5 rating behavior unchanged.

Out of scope:
- Deleting other users ratings, audit history, multiple groups, and schedule manual overrides.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given I have rated a band, when I clear the rating, then the band returns to unrated value 0 locally.
- [ ] #2 Given I clear a rating, when sync succeeds, then Supabase no longer contributes my previous explicit rating to group decisions.
- [ ] #3 Given another device syncs after I clear a rating, then the cleared band appears unrated for my user and the group decision reflects the cleared state.
- [ ] #4 Given I clear a rating from the detail screen, when I return to the overview, then the overview shows no filled stars for that band.
- [ ] #5 Given sync fails after clearing, then the local pending clear is preserved and retry sync can complete it later.
- [ ] #6 Automated tests cover domain/application rating clear behavior and focused adapter validation covers sync behavior where feasible.
- [ ] #7 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [ ] #8 Architecture impact is assessed; if backend schema or external API semantics change, explicit approval and ADR handling are required before implementation.
<!-- AC:END -->
