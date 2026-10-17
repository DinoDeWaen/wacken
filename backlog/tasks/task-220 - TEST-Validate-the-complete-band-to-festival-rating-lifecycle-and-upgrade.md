---
id: TASK-220
title: 'TEST: Validate the complete band-to-festival rating lifecycle and upgrade'
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
  - TASK-156
  - TASK-169
  - TASK-172
  - TASK-174
  - TASK-217
  - TASK-218
  - TASK-219
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the release owner, I need end-to-end evidence that the approved rating lifecycle survives imports, sync, archive, corrections and upgrades. Validate the integrated implementation, not only isolated units. Retain actual artifact/source identifiers and no-skip database/native gates in the delivery package. Final signing/version/publication remains TASK-214; this story owns lifecycle integration and legacy/pending-data preservation evidence.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given band rating4, when a festival is created, planning changes to5, after-rating becomes3 and it is archived, then history retains5/3, band rating becomes3 and the next festival starts at3.
- [ ] #2 Given no/reset after-rating, duplicate submissions, interrupted sync/archive or concurrent edits, then previous established ratings, user ownership and history are preserved with no partial or false-success transitions.
- [ ] #3 Given an archived correction, then correcting the current source updates that personal band rating atomically with audit; correcting an older festival preserves a newer established source and existing later-festival planning snapshots.
- [ ] #4 Given a previous supported signed installation containing archived and active history, metadata, auth and pending work, when upgraded, then migration/normal sync preserve data and do not promote active or ambiguous legacy values accidentally.
- [ ] #5 Unit/BDD, real PostgreSQL/PostgREST/RLS, Room migration and native tests pass on the final source, including mandatory Summer Breeze creation regressions; no skipped required gate is treated as a pass.
- [ ] #6 README/test commands and canonical business/diagram/ADR impacts are recorded, with reproducible manual checks, exact-source reports and upgrade evidence linked for TASK-214.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
2026-10-17 user-approved architecture: ADR-0016 governs canonical shared bands, member-specific established band ratings, independent festival planning/after-ratings, one-time creation snapshots and atomic archive promotion. Missing after-ratings retain the prior band rating; archived corrections update it only while their festival remains the source. TASK-216–220 implement and validate the lifecycle. This supersedes earlier latest-event prefill wording; do not treat approved planning as shipped behavior.
<!-- SECTION:NOTES:END -->
