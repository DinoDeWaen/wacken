---
id: TASK-214
title: 'REL: Validate and publish the completed 2.34 milestone'
status: To Do
assignee: []
created_date: '2026-10-17 09:59'
updated_date: '2026-10-17 16:47'
labels:
  - release
  - android
  - release-2.34
milestone: m-0
dependencies:
  - TASK-156
  - TASK-169
  - TASK-172
  - TASK-174
  - TASK-175
  - TASK-201
  - TASK-202
  - TASK-215
  - TASK-216
  - TASK-217
  - TASK-218
  - TASK-219
  - TASK-220
  - TASK-221
  - TASK-222
  - TASK-223
priority: high
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As the release owner, I want a signed, regression-validated 2.34 package so users can safely install the completed milestone. Scope: finish or explicitly resolve every m-0 task, retain the published 2.33.5 festival/unknown-link fixes, execute required unit/BDD/API/native gates, version the Android app, verify stable signing and upgrade/data preservation, publish release notes/tag and the signed artifact through the selected distribution path. The validated appearance redesign is planned for 3.0 and must not be described as shipped in 2.34. No scope item may be silently dropped to claim milestone completion.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the release 2.34 milestone, when packaging starts, then each feature/test/distribution task is Done or has an explicit user-approved scope resolution with evidence.
- [ ] #2 Given the final source, then unit/QA/database integration and native UI checks run against that state and pass; skipped gates are resolved or explicitly accepted before publication.
- [ ] #3 Given an existing supported installation, then the signed update preserves ratings/history, festival identity, metadata, auth and pending work; package identity and certificate are verified.
- [ ] #4 Given the signed artifact, then versionName 2.34, an incremented versionCode, Git tag, checksum, release notes and published APK all refer to the same validated commit.
- [ ] #5 README, business requirements, distribution documentation and the complete delivery-governance validation package accurately describe shipped features, remaining limitations and the separate 3.0 design roadmap.
- [ ] #6 Given ADR-0016 is the approved lifecycle, then TASK-216–220 are complete and the release demonstrates band4 → planning4 → planning5/after3 → archive band3 → next planning3, including source-aware corrections and legacy/pending-data preservation.
<!-- AC:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
2026-10-17 independent preflight on f7e3628: real PostgreSQL/PostgREST festival-creation runner passed10scenarios with no skipped tests and SQL transaction/permission assertions. TASK-174,175,201,202areDone.156/169/172and architecture215remain; repeat all required gates on final release source before publication. No2.34tag or asset has been published.

2026-10-17 user-approved architecture: ADR-0016 governs canonical shared bands, member-specific established band ratings, independent festival planning/after-ratings, one-time creation snapshots and atomic archive promotion. Missing after-ratings retain the prior band rating; archived corrections update it only while their festival remains the source. TASK-216–220 implement and validate the lifecycle. This supersedes earlier latest-event prefill wording; do not treat approved planning as shipped behavior.

Full lintDebug still reports14 existing NewApi errors while minSdk is23. TASK223 is now a required 2.34 dependency; resolve compatible storage/UTF-8 encoding and pass full lint before final publication, rather than accepting a suppressed baseline.
<!-- SECTION:NOTES:END -->
