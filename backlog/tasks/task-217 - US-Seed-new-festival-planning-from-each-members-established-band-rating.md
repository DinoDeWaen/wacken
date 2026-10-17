---
id: TASK-217
title: 'US: Seed new festival planning from each member''s established band rating'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 13:18'
updated_date: '2026-10-17 14:32'
labels:
  - ratings
  - festival
  - architecture
  - release-2.34
milestone: m-0
dependencies:
  - TASK-216
references:
  - supabasemasterdataclient
modified_files:
  - >-
    app/src/test/java/be/wacken/planner/AddFestivalPersistenceRegressionTest.java
  - backend/flyway/sql/V015__member_established_rating_creation_snapshots.sql
  - backend/flyway/tests/festival_creation.sql
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalCreationGateway.java
  - app/src/androidTest/java/be/wacken/planner/FestivalCreationAndroidTest.java
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/decisions/0016-band-catalog-and-archive-promoted-personal-ratings.md
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member, I want a new festival to start from my established band ratings so I can plan independently without losing my longer-term opinion. Reuse/create canonical catalog bands and link the festival to them. Copy each applicable existing group member's personal current band rating into that member's new festival planning row once; unknown bands/ratings stay unrated. Keep after-ratings empty for the new festival. Extend the existing atomic creation contract compatibly with ADR-0016; preserve request replay and published festival fixes. Reviewed aliases remain TASK-156.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given an existing band and member band rating4, when a festival is created, then the canonical band is linked and that member's festival planning rating starts at4 while its after-rating is empty.
- [x] #2 Given several existing members have different band ratings, then creation seeds each member's own value without exposing private history or copying the creator's value to others.
- [x] #3 Given an unknown band or missing personal rating, then the catalog band is created if needed, linked once and left unrated; metadata/music links are not invented.
- [x] #4 Given planning or band ratings change later, or an uncertain creation request is retried, then existing festival planning edits and after-ratings are not reset or continuously refreshed.
- [x] #5 BDD/TDD, real PostgreSQL/PostgREST rollback/concurrency/RLS/replay tests and native creation tests pass, retaining the Summer Breeze63-band and old exact-name client gates.
- [x] #6 README/business requirements document carry-over; accepted ADR-0016 and canonical diagram/ADR impacts are recorded.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed red/green real API cases, compatible V015 private creation transaction, current-member snapshots with creator-only response, unforgeable request ledger, preserved exact-name/replay/rollback behavior, native planning/after/current isolation checks. Full JVM/QA/coverage and fresh signed task APK passed. README/BR/ADR updated. Standard approved ADR0016 scope; no architecture deviation. Archive promotion remains218.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Creation now copies each existing group member's established band opinion into their own festival planning row once. Missing opinions remain unrated, after-ratings start empty, and canonical metadata is reused or left empty for new bands. Latest history no longer supplies planning defaults.

Additive V015 keeps the published RPC signature and creator-only snapshot. A validated private transaction reads member preferences without exposing private provenance. Direct client creation-ledger inserts are revoked. Replay returns current planning values without resetting subsequent planning/after edits.

## Acceptance criteria validation

1. Established4 seeds planning4 with empty after-state.
2. Real SQL/HTTP verifies members4/5 receive their own values and the response contains only creator planning.
3. Unknown bands are linked once without invented music links; history without established preference remains unrated.
4. Lost-response replay preserves edited planning and after-state despite changed established opinions.
5. Thirteen real PostgreSQL/PostgREST scenarios pass without skips, including Summer Breeze63, exact-name/collision identity, concurrent identical/different requests, outsider denial and injected band/lineup/request/member-prefill failures. Seven native creation cases pass.
6. README/BR/ADR0016 updated.

## How to test

### Automated tests

- JAVA_HOME=JDK21 bash scripts/verify-festival-creation.sh:13 tests passed,0 skipped plus SQL membership/transaction assertions.
- bash scripts/verify-rating-state.sh passed with V015 included.
- sh scripts/verify-android-smoke.sh emulator-5554 be.wacken.planner.FestivalCreationAndroidTest:7 passed,0 skipped; real cache rollback, actual CSV screen, planning/after/current isolation and replay.
- Fresh :app:clean test qaTest :app:lintVitalRelease :app:assembleRelease passed, including domain/application coverage gates. App216 tests per build variant;13 fixture tests skip outside the mandatory integration runner.
- APK certificate verified against stable release SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f; APK digest b0bbb5dae0ae80ee0d5c2c104d9ba72f63ad19335d3a07f0f6867022db981a2f. Baseline2.33.5/code41 task-validation build, not published2.34.
- git diff --check passed.

### Manual validation

Native instrumentation exercises actual controls; no production writes or separate human acceptance claimed.

## TDD / BDD / approval-test evidence

Existing ten API/native creation regressions characterized published atomic behavior. Three added API scenarios failed before implementation (member snapshots, history exclusion, other-member failure rollback), then all13 passed. Native acceptance verifies cached planning4 and empty after-state, then independent planning5/after3 with replay retaining established4.

## Architecture impact

- Architecture-significant change: compatible extension of accepted atomic creation and private authorized write boundary.
- Approval received: explicit2026-10-17 user approval, TASK215/ADR0016.
- ADR impact: updated ADR0016 with V015 and member-specific snapshot contract.
- No new framework, service, dependency or secret.

## README impact

README impact: updated creation behavior, migration rollout and rating-state table descriptions.

## Business requirements impact

Business requirements impact: updated creation implementation status; archive promotion remains218.

## Diagram impact

Diagram impact: none, because existing C4 components and relationships already describe this creation transaction and rating stores.

## Commits / logical change list

V015 creation transaction; SQL/HTTP member isolation/replay/fault regressions; native cache lifecycle acceptance; creation-port documentation; README/BR/ADR.

## Risks and follow-up

V015 tested against disposable services only; deploy additive migrations before final APK. Archive promotion218 and corrections172 remain required. GitHub Android CI for216 passes; one duplicate JVM job hit an external registry data limit and was retried. Final-source CI remains required before merge/release214. No main merge/tag/publication.
<!-- SECTION:NOTES:END -->
