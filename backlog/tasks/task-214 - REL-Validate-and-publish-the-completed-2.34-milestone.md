---
id: TASK-214
title: 'REL: Validate and publish the completed 2.34 milestone'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 09:59'
updated_date: '2026-10-17 19:47'
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
modified_files:
  - app/build.gradle
  - releases/v2.34.md
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/docs/release-2.34-deployment.md
  - backlog/decisions/0016-band-catalog-and-archive-promoted-personal-ratings.md
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

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Final release under existing explicit user authorization/ADR0016: verify every2.34 dependency Done (only214 remains), preserve unrelated working files; set versionName2.34/code42 and prepare release notes/README/BR status. Verify existing Flyway history/current schema13 and deploy tested additive14–18 via existing process before APK, with production schema/history/security/read validation and preserved existing data. No new architecture decisions/service/signing keys. Run exact-source full JVM/QA/coverage/full lint and real SummerBreeze16/rating lifecycle/SQL/API plus native68 on API23/36 CI. Build fresh clean stable-signed release and separate same-key opt-in upgrade probe; rerun actual published2.33.5→final2.34/code42 on a separate empty emulator data image without uninstall/wipe; record auth/metadata/history/pending and provenance results. Verify package/min23/code42/version2.34/cert/v1-v2/digest. Commit reviewable release source, attach/update existing PR3, retain complete validation package before main merge. Tag exact validated commit v2.34; publish signed app-release.apk plus checksum/notes to GitHub; download asset and compare digest/cert/version. Close all milestone tasks/record final-source release URL, deployment, evidence and limitations; merge via PR only after package complete. Native paired Ironline/FieldNotes styling remains3.0. Release build/upgrade/report files only; no extra product scope.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
2026-10-17 independent preflight on f7e3628: real PostgreSQL/PostgREST festival-creation runner passed10scenarios with no skipped tests and SQL transaction/permission assertions. TASK-174,175,201,202areDone.156/169/172and architecture215remain; repeat all required gates on final release source before publication. No2.34tag or asset has been published.

2026-10-17 user-approved architecture: ADR-0016 governs canonical shared bands, member-specific established band ratings, independent festival planning/after-ratings, one-time creation snapshots and atomic archive promotion. Missing after-ratings retain the prior band rating; archived corrections update it only while their festival remains the source. TASK-216–220 implement and validate the lifecycle. This supersedes earlier latest-event prefill wording; do not treat approved planning as shipped behavior.

Full lintDebug still reports14 existing NewApi errors while minSdk is23. TASK223 is now a required 2.34 dependency; resolve compatible storage/UTF-8 encoding and pass full lint before final publication, rather than accepting a suppressed baseline.

Preflight: all16 direct2.34 dependencies Done; only214 remains in m-0. Prepared versionName2.34/code42 and release notes. Backed up public schema privately and captured7 existing-table logical hashes/counts. Existing Flyway applied approvedV014–V018 successfully; schema018, validate18 green. All seven hashes/counts unchanged; zero invalid promoted sources. New tables RLS/no direct authenticated writes; public lifecycle RPCs invoker, anon execute denied/member execute enabled. Advisor baseline unchanged except two intentional deny-by-default internal-ledger INFOs; pre-existing findings tracked separately in224 (not added to2.34 scope). Final exact-source CI/sign/upgrade/publication remain.
<!-- SECTION:NOTES:END -->
