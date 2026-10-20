---
id: TASK-234
title: 'BUG: Bound rating and schedule network waits without losing pending changes'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:26'
updated_date: '2026-10-20 20:25'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies: []
references:
  - wacken
  - supabaseauthenticatedrequest
  - supabaseratingclient
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - backlog/docs/task-234-transport-approval.md
  - app/src/test/java/be/wacken/planner/SupabaseHttpTransportTest.java
  - app/src/main/java/be/wacken/planner/SupabaseHttpTransport.java
  - app/src/main/java/be/wacken/planner/SupabaseRatingClient.java
  - app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingClient.java
  - >-
    app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java
  - app/src/main/java/be/wacken/planner/SupabaseScheduleLockClient.java
  - app/src/test/java/be/wacken/planner/SupabaseClientTransportContractTest.java
  - backlog/decisions/0020-bounded-supabase-adapter-transport.md
  - groma/systems/wacken/components/supabasehttptransport.md
  - groma/systems/wacken/components/supabaseauthenticatedrequest.md
  - README.md
  - backlog/docs/business-requirements.md
  - groma/systems/wacken/components/supabaseratingclient.md
priority: medium
type: bug
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As an attendee, I want failed or stalled sync to finish with a useful error so cached data remains usable and pending changes can retry.

## Scope
Characterize the duplicated HTTP clients and define bounded connect/read waits for rating, personal history and schedule calls, consistent with a reviewed adapter transport policy. Preserve existing auth refresh, operation identity, request headers/body/upsert and write retention. Reuse mechanics only where justified; no new network framework assumed.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F6; lint groups 33/34/65/66. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a connection or response that stalls, each affected operation stops within the documented configured timeout and reports failure while cached data and pending writes remain intact.
- [x] #2 Auth expiry/refresh/retry, member and group checks, upsert headers, body serialization, success parsing and failure status handling retain their characterized contracts.
- [x] #3 Controlled local transport tests prove connect/read policy and stalled-response outcomes without depending on live Supabase availability; regressions are written red before implementation.
- [x] #4 Domain/application remain free of HTTP dependencies; any new shared transport boundary receives explicit approval and ADR handling before coding.
- [x] #5 README documents timeout/retry/offline troubleshooting and the validation package records supported Android verification.
- [x] #6 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #7 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Reviewed legacy four-client send/read contracts and ran existing 242-test unit baseline before transport changes; preserved existing auth/session and pending-write characterization tests.
2. Received explicit user approval on 2026-10-20 for the standard proposal. Added ADR-0020 with exact ownership, timeout policy and limits.
3. Wrote transport tests red before the helper existed; implemented shared package-private HttpURLConnection mechanics with default 15000ms connect/30000ms read and finally disconnect. Migrated four affected clients only; kept endpoints/payload mapping/auth wrapper unchanged.
4. Added controlled real loopback stalled-header/body tests plus deterministic timeout/resource failures and seven client contract tests. Full unit suite and Android lint passed: 256 total, 240 passed, 16 pre-existing disposable-API scenarios skipped. No connected emulator; no native execution claimed.
5. Updated README and BR-056b, Groma ownership/four source relationships via CLI. Rescan created zero components. Deleted old release APK and built a fresh stable-key signed APK, verified signatures/digest; finalized evidence and committed.
Deviation: expanded client contract assertions were added during extraction, using the pre-change unit suite plus reviewed legacy send bodies as baseline; no production test-only URL global hooks or new dependency. Standard approved adapter boundary; domain/application unchanged. Per-I/O limits are not total sync deadlines. Full native/database release gates remain TASK-241.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Rating, festival planning, personal history and schedule-lock clients now share package-private SupabaseHttpTransport with 15-second connect and 30-second inactive-read limits. Owned streams close and connections disconnect on success/failure. Endpoints, business serialization/parsing and authenticated refresh/retry ownership remain in their existing clients/wrapper.

## Acceptance criteria validation
1. Controlled synthetic connect/read/write failures propagate IOException and disconnect; local loopback stalls before headers and during body trigger SocketTimeoutException. Existing sync tests preserve pending ratings, clears, history and schedule selections on IO failure; cached/session retention tests remain green. Limits are per-I/O as approved, not a total sync deadline.
2. Seven client contracts cover canonical identity, group/member/festival filters, upsert/body/date/event payloads, schedule attribution, invalid-member rejection and refreshed credentials on one JWT retry. Existing session/sync tests pass.
3. Transport tests were red before helper existed, then green. Default 15s/30s is verified before I/O; short 100ms inactive-read values enable deterministic loopback stalls with no live Supabase.
4. Explicit human approval recorded; ADR-0020 created. HTTP remains Android-adapter-only, domain/application unchanged.
5. README documents timeout/retry/offline behavior and honest limits. Supported-device compile/lint passed with minSdk23 target36. adb devices listed no connected target; no native device test claimed.
6–7. Final plan, canonical impacts, approved ADR and validation evidence recorded.

## How to test
### Automated tests
JDK21 ./gradlew :app:testDebugUnitTest :app:lintDebug passed: 256 tests total, 240 passed, 0 failures/errors, 16 skipped AddFestivalPersistenceRegressionTest cases requiring disposable API setup. New tests: SupabaseHttpTransportTest (7), SupabaseClientTransportContractTest (7). Existing auth/session/sync retention and error-message regressions passed. Real loopback cases stall response headers/body; deterministic fake connections verify connection-timeout settings before I/O and cleanup. Groma CLI rescan after combine created 0 components; helper retained under supabaseauthenticatedrequest, clients under supabaseratingclient. Four exact source relationships recorded. git diff --check passed.
Required fresh signed build: deleted previous APK; JDK21 assembleRelease --rerun-tasks succeeded, 53 tasks executed. apksigner verified v1/v2 signatures. APK SHA256 f8c9672fcbbb29e0939a368432c58fad4312d438e66d8e62ef9c71a3f2cc9dce at app/build/outputs/apk/release/app-release.apk. Remains 2.34/code42; not published.
### Manual validation
Reviewed diff: only four clients replace send/read duplication and gain package-private test injection; existing constructors use production defaults. No schema/dependency or application/domain changes. adb devices returned no connected devices; native API23/API36 and real database release gates not run here and remain TASK-241.

## TDD / BDD / approval-test evidence
Existing 242-test suite characterized pre-change behavior. Reviewed four identical legacy header/body/read paths before extraction. New transport spec first failed compilation without implementation, then passed; controlled loopback and client contract assertions passed after migration. Given stalled inactive reads, IOException is reported; given IO failure, existing sync tests retain pending local work. Expanded client contracts added during extraction are explicitly recorded, not claimed as pre-change executions.

## Architecture impact
- Architecture-significant change: shared adapter HTTP mechanics, approved before coding.
- Approval received: user "task 234 go for standard proposal", 2026-10-20.
- ADR: ADR impact: created ADR-0020 for timeout/resource ownership and limits. No new framework, port, module, public API or schema.

## README impact
README impact: updated timeout/retry/offline troubleshooting and ADR-0020 link.

## Business requirements impact
Business requirements impact: updated BR-056b for stalled connect/inactive-read failure with cache/pending preservation and explicit per-I/O limits.

## Diagram impact
Diagram impact: updated Groma helper ownership within existing transport responsibility and four source-supported client collaborations through CLI. No new island or runtime boundary.

## Commits / logical change list
Shared transport and four client migrations; controlled transport/client contract tests; approved ADR-0020; README/BR-056b; Groma ownership/relationships; finalized task record.

## Risks and follow-up
No total DNS/upload/trickle/sync deadline is promised; JWT retry and multiple operations can extend total time. Timeout after accepted write can leave pending work for idempotent retry. Auth/master-data/provider/image transport not migrated. Native emulator and disposable real-API tests remain required at TASK-241; no emulator connected here. No merge, tag or publication.
<!-- SECTION:NOTES:END -->
