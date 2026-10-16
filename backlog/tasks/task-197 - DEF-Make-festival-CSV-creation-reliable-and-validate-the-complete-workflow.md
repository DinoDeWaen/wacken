---
id: TASK-197
title: 'DEF: Make festival CSV creation reliable and validate the complete workflow'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-16 18:44'
updated_date: '2026-10-16 18:46'
labels:
  - defect
  - android
  - supabase
  - testing
dependencies: []
references:
  - supabasemasterdataclient
  - addfestivalusecase
modified_files:
  - >-
    app/src/test/java/be/wacken/planner/AddFestivalPersistenceRegressionTest.java
priority: high
type: bug
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The V2.33.3 Add Festival action fails on the real Summer Breeze CSV with bands_pkey and leaves a partial active festival. Diagnose and repair the complete creation path, preserve exact-name matching and existing data, and add executable tests that would have caught the regression. Scope includes import failure/retry consistency, band identity, authorization, cache integration, and recovery of this failed attempt. No unrelated feature changes or release publication in this task.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given the supplied Summer Breeze CSV and the current catalog, when a group member adds a festival, then every CSV band appears group-wide and Any Given Day cannot collide with Any given Day or overwrite its metadata/history.
- [ ] #2 Different exact names that produce the same slug, including punctuation and non-ASCII names, can coexist with distinct database identities; exact-name reuse preserves existing metadata and personal history.
- [ ] #3 A failed creation at any persistence step leaves no newly active empty festival, partial lineup, new bands, or planning ratings locally or remotely.
- [ ] #4 Retries after a lost response and concurrent creation attempts cannot duplicate or overwrite an existing festival; an already active unrelated festival or an archived identity is preserved.
- [ ] #5 Creation uses authoritative remote data when the cache is stale, prefills only the creating user's latest personal rating, and leaves previous festival ratings and personal history unchanged.
- [ ] #6 Automated tests reproduce this exact regression and validate the real schema/RLS and Android cache/UI workflow, with failure injection and the complete CSV fixture included in a repeatable validation command.
- [ ] #7 Document the incident causes, changes that introduced them, missed coverage, audited related failures, manual steps, and executable release regression gate.
- [ ] #8 Architecture approval is recorded before any transaction/API change; update ADR, README, affected business requirements, and diagram impact through the validation package.
- [ ] #9 Investigate and safely recover the failed Summer breeze import without deleting existing shared band metadata or rating history.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Treatment: full for this critical persistence flow: behavioral regression, real database/API integration, Android cache/UI integration, and recovery evidence.
1. Reproduce the supplied 63-band CSV collision and characterize partial writes using real constraints and the current adapter/use case. Inspect band identity, duplicate rows, stale cache, active-festival checks, prefill, RLS, retry/lost-response handling, and cache commits.
2. Write failing regressions for the collision and atomicity/retry invariants before changing production behavior. Use an isolated local PostgreSQL/PostgREST test environment, not production test writes.
3. Pending explicit architecture approval: introduce one creation port and one SECURITY INVOKER Supabase RPC transaction with collision-safe IDs, exact-name reuse, idempotent retries, and authority checks. Publish local cache only after a successful complete result.
4. Integrate the actual Android add action; run complete CSV and failure paths with Room on the emulator; make the tests an explicit release gate.
5. Safely recover the incomplete Summer breeze attempt, verify every expected lineup row and retained history, document the incident and coverage gaps, and complete the validation package.
Likely files: AddFestivalUseCase and creation port/plan, Supabase creation adapter, AppRepositories/ImportCsvActivity, Flyway migration and SQL/API tests, Android tests, release process/CI, README/requirements, ADR.
Architecture impact: new transaction/API boundary proposed; explicit user approval requested asynchronously on 2026-10-16 and pending. No production code or schema changes until approved.
README/business requirements/ADR will reflect atomic creation and repeatable validation. Diagram impact assessed after final design.
Current production data is read-only during analysis; no automatic deletion of partial or historic data.
<!-- SECTION:PLAN:END -->
