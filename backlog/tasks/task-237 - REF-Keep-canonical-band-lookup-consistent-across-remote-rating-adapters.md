---
id: TASK-237
title: 'REF: Keep canonical band lookup consistent across remote rating adapters'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:26'
updated_date: '2026-10-21 05:56'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies: []
references:
  - wacken
  - supabaseratingclient
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - app/src/test/java/be/wacken/planner/SupabaseClientTransportContractTest.java
  - app/src/main/java/be/wacken/planner/SupabaseCanonicalBandLookup.java
  - app/src/main/java/be/wacken/planner/SupabaseRatingClient.java
  - >-
    app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java
  - app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingClient.java
  - groma/systems/wacken/components/supabasecanonicalbandlookup.md
  - groma/systems/wacken/components/supabaseratingclient.md
  - >-
    app/src/test/java/be/wacken/planner/LegacyRealRatingBackfillRegressionTest.java
  - README.md
  - backlog/docs/business-requirements.md
priority: medium
type: task
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As a maintainer, I want canonical identity lookups consistent so rating adapters cannot drift in how they address the same band.

## Scope
Characterize exact-name-to-ID and ID/name mapping in rating/history clients, then consolidate only the justified adapter-owned lookup responsibility. Retain distinct schedule-lock parsing and festival/member-scoped queries. No database schema, identity normalization or canonical-band merge change.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F9; lint groups 63/64. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Characterization tests capture existing exact-name/case/escaping behaviour, missing and malformed IDs, unknown bands and response mappings before refactoring.
- [x] #2 Given the same canonical band, affected clients resolve the same backend identity while preserving user/group/festival predicates and pending-write behaviour.
- [x] #3 Failure, account change and response errors do not create bands, guess IDs, merge aliases or apply a rating to a different identity.
- [x] #4 Domain/application depend on existing inward-facing contracts; significant new ports or shared transport ownership require approval before coding.
- [x] #5 Tests prove equivalence across affected clients; README/business requirements/diagram/ADR impacts are recorded.
- [x] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added and ran pre-refactor characterization: 10 client contract tests cover exact case/escaping, malformed/missing catalog IDs, unknown references, mappings and existing scoped payloads.
2. Added an account-switch regression which failed red. Consolidated exact catalog lookup/mapping into adapter-local SupabaseCanonicalBandLookup; reused existing executeForUser for each lookup/read/write so a changed member is rejected before sending.
3. Replaced one brittle legacy source-string test with stronger real GET-then-POST/no-extra-history-write assertions. Full Android unit suite and compatibility lint passed: 259 total, 243 passed, 16 pre-existing disposable API tests skipped.
4. Updated README and BR-056c for account-switch rejection; retained schedule-lock parsing and after-rating unique-ID lookup separately. Combined helper ownership into existing Groma shared-state adapter responsibility through CLI; rescan created zero components.
5. Built fresh stable-key signed APK after deleting prior artifact; signatures/digest and final delivery evidence recorded; commit completed task.
Minimal internal adapter refactor; no new inward-facing port, HTTP ownership/dependency/schema/normalization/merge rule or auth lifecycle. Existing member-binding mechanism reused. ADR/architecture approval not required. Deviation: added AC3 account-switch fix after characterization demonstrated missing guard; intentional behavior documented. One source-pattern assertion replaced by behavioral transport coverage.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Legacy rating, festival planning and personal history clients share exact canonical-name lookup and ID/name decoding inside the Android adapter package. Case, escaping, missing-band errors, catalog mapping and scoped operation queries remain unchanged. Lookup and subsequent requests reuse executeForUser so a replacement account cannot send the original operation under different credentials.

## Acceptance criteria validation
1. Before refactoring, 10 transport contract tests passed including exact-case/escaping, absent/malformed ID and unknown-reference mapping scenarios across all three clients.
2. Shared helper resolves the same canonical ID; client-specific user/group/festival predicates, history identity and pending-write tests remain green.
3. Malformed/missing IDs do not send a write or create bands. Account-switch regression failed red and passes with existing member-binding guard. No alias guessing, name normalization or band creation introduced.
4. Domain/application and inward-facing contracts unchanged. No new shared transport ownership; existing transport/auth wrapper retained. GetRequest is a private package adapter callback for GET execution only, not a domain port/policy.
5–7. Equivalence/regression tests, finalized plan, documentation and impacts recorded.

## How to test
### Automated tests
JDK21 ./gradlew :app:testDebugUnitTest :app:lintDebug passed: 259 tests, 243 passed, 0 failures/errors, 16 skipped AddFestivalPersistenceRegressionTest cases requiring disposable API setup. Expanded client suite has 11 tests; account-change matrix covers all three adapters. Existing auth/session/pending-write and transport timeout tests pass. Groma rescan created zero components after helper combine; source ownership retained in supabaseratingclient. git diff --check passed. Fresh signed build: deleted prior APK; JDK21 assembleRelease --rerun-tasks succeeded (53 executed); apksigner v1/v2 verified. Final APK digest recorded in follow-up evidence below.
### Manual validation
Reviewed helper against all three old implementations: exact query/limit1, same error strings and getString mapping, no new canonical classification. After-rating unique limit2 lookup and schedule-lock parsing deliberately unchanged. No live Supabase/native run; no emulator connected. Full release/native/database gates remain TASK-241.

## TDD / BDD / approval-test evidence
Ten existing/extended contract scenarios green before extraction. Given account changes after exact band lookup, new rejection test failed before code change; after reuse of executeForUser it passes for all three clients. Existing unknown/malformed lookup scenarios still produce no write. Full suite caught a source-text assertion dependent on old argument ordering; replaced with behavioral history GET/POST-only assertions, retaining other startup/backfill tests.

## Architecture impact
- Architecture-significant change: no; internal lookup reuse and adoption of existing member-binding guard, no new transport/domain/API/schema/dependency/auth lifecycle.
- Approval received: not required.
- ADR: ADR impact: none, because existing adapter boundaries and authentication decisions remain unchanged.

## README impact
README impact: updated exact canonical addressing and reopen/retry guidance on account change.

## Business requirements impact
Business requirements impact: updated BR-056c to make intended-member lookup/write continuity explicit.

## Diagram impact
Diagram impact: updated Groma source ownership to include the helper under existing shared-state adapters; no new island or runtime boundary.

## Commits / logical change list
Pre-refactor characterizations; shared canonical lookup; existing member-binding reuse; account-change regression; replacement of brittle source check; README/BR-056c; Groma ownership; finalized task evidence.

## Risks and follow-up
Legacy limit1 behavior is preserved; stricter uniqueness semantics in after-rating adapter remain separate. No new fuzzy matching, UUID guessing, merge or schema changes. Sixteen disposable API cases and native supported-device release gates remain unexecuted here. No merge/tag/publication.

Fresh signed APK SHA-256: ee11846b8a28aad81eb3ccf1dc3611be94bfa60b1e7cd6c3f7b57631b4a86174 at app/build/outputs/apk/release/app-release.apk. Version remains 2.34/code42; existing stable key, v1/v2 verified; not published.
<!-- SECTION:NOTES:END -->
