---
id: TASK-216
title: 'US: Keep personal band ratings separate from festival after-ratings'
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
  - TASK-215
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member, I want a stable personal band rating and a separate after-rating for each festival so my current opinion and festival history cannot overwrite each other. Follow the user-approved archive-promotion architecture in ADR-0016. Scope: canonical band references; current rating per user/band with source provenance; after-rating per user/festival/band; application-owned rules, additive Room/Flyway persistence, existing sync integration and safe legacy migration. Saving an active festival after-rating must not promote it to the current band rating. Preserve active offline editing/pending work. No new service or secret.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a canonical band appears in two festivals, when a member changes one festival after-rating, then the other festival, both planning ratings and the last known band rating remain unchanged.
- [ ] #2 Given different members rate one band, then each retains their own current band rating and festival after-ratings; shared metadata exists once.
- [ ] #3 Given an active festival after-rating is reset to unrated, then its planning rating and established band rating remain unchanged and archiving cannot promote the empty value.
- [ ] #4 Given existing installations, when migrations run, then ratings, event dates, metadata, identity, auth and pending operations are preserved; archived outcomes can backfill with provenance, while ambiguous legacy history is retained without inventing dates or promoting active results.
- [ ] #5 BDD/TDD, real database/RLS, Room migration and pending-sync tests validate isolation, ownership, reset, migration and recovery. Existing observable behavior is characterized before refactoring.
- [ ] #6 README and business requirements document the implemented model; ADR-0016 and canonical diagram/ADR impacts are recorded, with explicit user approval linked before significant changes.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
2026-10-17 user-approved architecture: ADR-0016 governs canonical shared bands, member-specific established band ratings, independent festival planning/after-ratings, one-time creation snapshots and atomic archive promotion. Missing after-ratings retain the prior band rating; archived corrections update it only while their festival remains the source. TASK-216–220 implement and validate the lifecycle. This supersedes earlier latest-event prefill wording; do not treat approved planning as shipped behavior.
<!-- SECTION:NOTES:END -->
