---
id: TASK-197
title: 'DEF: Make festival CSV creation reliable and validate the complete workflow'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-16 18:44'
updated_date: '2026-10-16 20:14'
labels:
  - defect
  - android
  - supabase
  - testing
dependencies: []
references:
  - supabasemasterdataclient
  - addfestivalusecase
  - importcsvactivity
  - apprepositories
documentation:
  - backlog/docs/add-festival-creation-incident-2026-10-16.md
modified_files:
  - >-
    app/src/test/java/be/wacken/planner/AddFestivalPersistenceRegressionTest.java
  - backlog/docs/add-festival-creation-incident-2026-10-16.md
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalCreationGateway.java
  - >-
    application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java
  - backend/flyway/tests/festival_creation.sql
  - backend/flyway/sql/V013__atomic_festival_creation.sql
  - app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java
  - app/src/main/java/be/wacken/planner/RoomFestivalCreationCache.java
  - app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java
  - app/src/main/java/be/wacken/planner/AppRepositories.java
  - app/src/main/java/be/wacken/planner/ImportCsvActivity.java
  - >-
    application/src/test/java/be/wacken/planner/application/AddFestivalUseCaseTest.java
  - >-
    application/src/qaTest/java/be/wacken/planner/application/FestivalArchiveQaScenarioTest.java
  - scripts/verify-festival-creation.sh
  - backend/flyway/tests/local_auth.sql
  - app/src/test/java/be/wacken/planner/SupabaseMasterDataClientTest.java
  - app/build.gradle
  - app/src/androidTest/java/be/wacken/planner/FestivalCreationAndroidTest.java
  - .github/workflows/ci.yml
  - backlog/decisions/0014-atomic-festival-creation.md
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/docs/release-process.md
  - >-
    application/src/test/java/be/wacken/planner/application/FestivalCsvReaderTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalCsvReader.java
  - backend/flyway/repairs/task-197-summer-breeze.sql
  - backlog/decisions/0011-post-wacken-festival-rating-model.md
priority: high
type: bug
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The V2.33.3 Add Festival action fails on the real Summer Breeze CSV with bands_pkey and leaves a partial active festival. Diagnose and repair the complete creation path, preserve exact-name matching and existing data, and add executable tests that would have caught the regression. Scope includes import failure/retry consistency, band identity, authorization, cache integration, and recovery of this failed attempt. No unrelated feature changes or release publication in this task.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the supplied Summer Breeze CSV and the current catalog, when a group member adds a festival, then every CSV band appears group-wide and Any Given Day cannot collide with Any given Day or overwrite its metadata/history.
- [x] #2 Different exact names that produce the same slug, including punctuation and non-ASCII names, can coexist with distinct database identities; exact-name reuse preserves existing metadata and personal history.
- [x] #3 A remote persistence failure rolls back every creation write. A local cache failure leaves the previous cache intact; retry confirms the complete remote result without partial festivals, duplicate bands, or reset ratings.
- [x] #4 Retries after a lost response and concurrent creation attempts cannot duplicate or overwrite an existing festival; an already active unrelated festival or an archived identity is preserved.
- [x] #5 Creation uses authoritative remote data when the cache is stale, prefills only the creating user's latest personal rating, and leaves previous festival ratings and personal history unchanged.
- [x] #6 Automated tests reproduce this exact regression and validate the real schema/RLS and Android cache/UI workflow, with failure injection and the complete CSV fixture included in a repeatable validation command.
- [x] #7 Document the incident causes, changes that introduced them, missed coverage, audited related failures, manual steps, and executable release regression gate.
- [x] #8 Architecture approval is recorded before any transaction/API change; update ADR, README, affected business requirements, and diagram impact through the validation package.
- [x] #9 Investigate and safely recover the failed Summer breeze import without deleting existing shared band metadata or rating history.
- [x] #10 Malformed CSV headers or blank band names are rejected before any creation request; existing quoting and blank-line behavior stays covered.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed full treatment for the critical creation workflow.
1. Reproduced four defects before repair (5e4b334): full CSV band-ID collision, partial writes, duplicate lineup rows, and archived identity overwrite. Added two red CSV validation cases.
2. Implemented the approved creation port, SECURITY INVOKER RPC transaction, collision-safe band identities, member checks, serialized active-festival guard, current-user prefill, and request-ledger replay. Room publishes the complete snapshot in one transaction and preserves pending edits.
3. Replaced shallow fake persistence coverage with ten real PostgreSQL/PostgREST integration scenarios plus SQL fault injection. Added six Android UI/Room cases; all ten Android tests including existing deletion/migration tests passed.
4. Added CI API and emulator jobs and mandatory release gates. Fixed the pre-existing SDK action default requesting the removed tools package.
5. Deployed V013 through Flyway; verified hosted member creation/replay/non-member denial in a rollback-only transaction with unchanged fingerprints. Recovered the legacy Summer breeze import under guards and preservation assertions.
6. Updated ADR 0014, ADR 0011, README, business rules, release process, and incident report; ran clean full validation and a fresh signed local APK build.
Architecture approval: user explicitly said Please proceed before implementation. No new production dependency or public release.
Deviation: AC3 clarified the approved independent remote/local boundaries: a post-commit local failure is confirmed by idempotent retry, not a distributed rollback. Local database Auth fixture mirrors the deployed JWT helper. The existing 14 lintDebug NewApi errors are unchanged; release lint-vital passes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed Add Festival with one atomic, authorized remote creation and one transactional Room publication. Names with colliding slugs no longer overwrite or block each other; duplicate CSV rows are deduplicated. An archived identity is protected, active-festival decisions use the database, and replay confirms an uncertain result without duplicating data or resetting later edits. Malformed name headers and blank names fail before persistence.

The legacy Summer breeze import is recovered group-wide with all 63 bands. Its existing planning rating is preserved; archived Wacken retains 165 lineup entries and 272 planning ratings. The guarded repair asserted that every prior band, planning rating, festival, other lineup and personal-history row remained unchanged.

## Acceptance criteria validation

- AC1–2: the actual checked-in 63-band CSV passes through the real use case, adapter, HTTP/PostgREST, schema and member RLS. Exact case variants and punctuation/Unicode names keep independent identities and existing metadata survives.
- AC3: SQL triggers inject failures at band, lineup, planning prefill and request-ledger writes; all creation writes roll back. Android SQLite failure injection proves local rollback, then identical replay publishes the complete result.
- AC4: lost-response, concurrent identical and competing requests, active-festival conflicts and archived-ID tests pass. Replay preserves later rating edits.
- AC5: server-side authoritative checks, latest requester-only prefill and Android stale-cache reconciliation/pending-history preservation pass.
- AC6–7: ten real API scenarios, six new Android UI/Room cases, incident analysis, mandatory CI/release gates and manual instructions are present. GitHub validation: both jobs passed in run https://github.com/DinoDeWaen/wacken/actions/runs/38960044738 on d2ab031.
- AC8: explicit user approval preceded implementation; ADR, README, requirements and flow diagram updated.
- AC9: guarded recovery committed; current Summer breeze lineup is 63, planning count is 1, Wacken remains 165/272.
- AC10: red/green CSV tests reject missing name headers and blank band names; an actual screen test proves rejection before a remote call.

## How to test

### Automated tests

- JDK 21, Android SDK, Docker/Python/curl: scripts/verify-festival-creation.sh — passed all ten real HTTP scenarios with zero skips and transactional SQL fault/permission assertions.
- With JDK 21, SDK and the existing Keychain signing values: ./gradlew clean test qaTest :app:connectedDebugAndroidTest :app:lintVitalRelease assembleRelease --no-daemon — passed, 139 tasks executed and ten Android tests with zero skips/failures.
- Flyway migrate and validate — V013 applied; all 13 migrations validate.
- Hosted member-role verification created/replayed all 63 CSV bands and denied a non-member inside a rollback-only transaction; catalog/festival/lineup/planning/history fingerprints matched before and after.
- apksigner verifies v1/v2 and the stable certificate ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. Local untagged APK: app/build/outputs/apk/release/app-release.apk, SHA-256 ae211c03bb8866d1285220b5ba12c39a22108b02939b7c73794275ae660f047d. Metadata remains 2.33.3/39, package be.wacken.planner, minSdk 23, targetSdk 36.
- git diff --check and bash -n passed. Full lintDebug still reports 14 pre-existing NewApi errors; comparison with the prior report found no new error diagnostics. Release lint-vital passed.

### Manual validation

- Existing phone: return to the band list and use Sync from Supabase to load the repaired 63-band festival. No physical phone was attached, so that final user interaction was not observed.
- Future release smoke test: install a build containing this fix, archive the active test festival, choose the complete fixture, confirm progress and success, sync a second member session, interrupt the response and retry the identical submission. Verify no duplicates or reset ratings.
- The emulator exercises the actual Add Festival activity, worker, CSV selection contents and Room; the real HTTP integration suite separately exercises the production use case/adapter against PostgreSQL/PostgREST. Android's document-picker interaction on the reported phone was not tested.

## TDD / BDD / approval-test evidence

Four full-flow regressions failed on the old production implementation in 5e4b334. Two CSV validation regressions also failed before the parser correction. The database suite failed before the new transaction existed; final SQL/API/Android behavior now passes. State-based assertions check saved data, preservation, rollback, permissions and retry rather than only method calls.

## Architecture impact

- Architecture-significant change: creation-specific application port, RPC transaction and replay ledger, plus atomic Room publication.
- Approval received: user said Please proceed on 2026-10-16 before coding.
- ADR impact: created ADR 0014 and updated ADR 0011 for the approved boundary and trade-offs. Security remains INVOKER/RLS with explicit actor/group checks.

## README impact

README impact: updated the creation behavior, ADR link, incident reference, prerequisites and repeatable API/emulator validation commands.

## Business requirements impact

Business requirements impact: updated Workflow 11 and BR-087a/BR-087b for complete creation and safe confirmation after interruption, and clarified the critical Android regression test scope.

## Diagram impact

Diagram impact: updated ADR 0014 with a Mermaid flow from UI/application port to the database result and Room transaction. The deployed container boundaries remain unchanged.

## Commits / logical change list

- 5e4b334: red regression baseline.
- cb01a4a: atomic creation, collision-safe identity, retry/cache behavior, SQL/API regression tests and guarded repair.
- 804184b: Android UI/Room tests, CI/release gates, source-of-truth documentation and incident outcome.
- d2ab031: fix removed SDK tools package in CI.
- Final task closure is synchronized to the user-visible checkout and fix branch.

## Risks and follow-up

- A new app build must be installed to deliver the code change to phones. The local signed validation APK is not a newly tagged/published release and retains 2.33.3/39 metadata. Existing V2.33.3 can sync the recovered festival now.
- Remote commit and local cache publication are separate atomic boundaries. Lost responses/local publication failures retain a complete remote result and are resolved by retrying the same name/CSV.
- Exact-name matching remains intentional; case variants are separate until reviewed linking is approved. No silent fuzzy merge or history rewrite occurs.
- Security advisor names/counts are unchanged: the existing mutable function search path, Flyway metadata RLS, legacy security-definer execute grants and leaked-password-protection findings remain outside this fix. References: https://supabase.com/docs/guides/database/database-linter?lint=0011_function_search_path_mutable and https://supabase.com/docs/guides/database/database-linter?lint=0013_rls_disabled_in_public .

## Modified files

app/src/test/java/be/wacken/planner/AddFestivalPersistenceRegressionTest.java, backlog/docs/add-festival-creation-incident-2026-10-16.md, application/src/main/java/be/wacken/planner/application/FestivalCreationGateway.java, application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java, backend/flyway/tests/festival_creation.sql, backend/flyway/sql/V013__atomic_festival_creation.sql, app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java, app/src/main/java/be/wacken/planner/RoomFestivalCreationCache.java, app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java, app/src/main/java/be/wacken/planner/AppRepositories.java, app/src/main/java/be/wacken/planner/ImportCsvActivity.java, application/src/test/java/be/wacken/planner/application/AddFestivalUseCaseTest.java, application/src/qaTest/java/be/wacken/planner/application/FestivalArchiveQaScenarioTest.java, scripts/verify-festival-creation.sh, backend/flyway/tests/local_auth.sql, app/src/test/java/be/wacken/planner/SupabaseMasterDataClientTest.java, app/build.gradle, app/src/androidTest/java/be/wacken/planner/FestivalCreationAndroidTest.java, .github/workflows/ci.yml, backlog/decisions/0014-atomic-festival-creation.md, README.md, backlog/docs/business-requirements.md, backlog/docs/release-process.md, application/src/test/java/be/wacken/planner/application/FestivalCsvReaderTest.java, application/src/main/java/be/wacken/planner/application/FestivalCsvReader.java, backend/flyway/repairs/task-197-summer-breeze.sql, backlog/decisions/0011-post-wacken-festival-rating-model.md
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Fixed atomic festival creation and safe retry, deployed V013, recovered all 63 Summer breeze bands while preserving existing data. Ten real API and ten Android tests passed, clean signed build passed, and both GitHub CI jobs passed: https://github.com/DinoDeWaen/wacken/actions/runs/38960044738 . No new release published.
<!-- SECTION:FINAL_SUMMARY:END -->
