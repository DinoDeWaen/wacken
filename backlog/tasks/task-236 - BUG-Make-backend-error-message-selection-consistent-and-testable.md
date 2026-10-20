---
id: TASK-236
title: 'BUG: Make backend error-message selection consistent and testable'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-20 06:26'
updated_date: '2026-10-20 19:58'
labels:
  - groma-audit
  - release-2.35
milestone: m-3
dependencies: []
references:
  - wacken
  - supabaseauthclient
  - supabaseauthenticatedrequest
documentation:
  - backlog/docs/groma-audit-2026-10-20.md
modified_files:
  - app/src/test/java/be/wacken/planner/SupabaseErrorMessageTest.java
  - app/src/main/java/be/wacken/planner/SupabaseErrorMessages.java
  - app/src/main/java/be/wacken/planner/SupabaseAuthClient.java
  - app/src/main/java/be/wacken/planner/SupabaseAuthenticatedRequest.java
  - groma/systems/wacken/components/supabaseerrormessages.md
  - groma/systems/wacken/components/supabaseauthenticatedrequest.md
  - README.md
  - backlog/docs/business-requirements.md
  - groma/systems/wacken/components/supabaseauthclient.md
priority: medium
type: bug
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## User story
As an attendee, I want clear backend failures so I can understand failed login or sync without ambiguous message selection.

## Scope
Characterize SupabaseAuthClient and SupabaseAuthenticatedRequest error extraction, then define and test intended message/msg precedence, empty/malformed responses and operation-specific fallback. Preserve authentication semantics and raw-response privacy; do not change backend policy or session lifecycle.

## Audit reference
[Groma architecture audit — 20 October 2026](../docs/groma-audit-2026-10-20.md)
Repository path: backlog/docs/groma-audit-2026-10-20.md
Findings: F6-related error drift; lint group 59. Investigation: TASK-225.

## Boundaries and impacts
Preserve ADR-0016: shared canonical metadata, member-specific established band rating, independent festival planning/after-ratings, one-time creation snapshots and source-aware archive promotion/correction. Follow Groma CLI-only curation and Backlog CLI-only task maintenance.
Business requirements impact: none planned unless an approved behaviour change is explicitly required; record the actual impact on completion.
README and diagram impacts are assessed for the affected workflow. ADR impact: none assumed; approval is required for significant changes.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given message, msg, both, neither, blank or malformed response bodies, the selected user-facing error follows an explicit documented rule with an appropriate status/operation fallback.
- [x] #2 Login versus authenticated retry/session behaviour remains unchanged and no credential or token is exposed in user-visible errors or new logs.
- [x] #3 Existing behaviour is characterized before refactoring; red/green tests cover precedence and malformed responses, with any intentional display change explicitly documented.
- [x] #4 Error-parsing reuse stays inside adapters and does not let transport own domain policy; README/business requirements impacts and manual examples are recorded.
- [x] #5 Architecture impact is assessed before implementation; significant boundary, dependency, persistence or test-strategy changes receive explicit human approval before coding and an ADR is recorded when required.
- [x] #6 README, business requirements, diagram and ADR impacts use canonical delivery-governance wording; test evidence, manual verification, finalized plan and remaining risks are recorded before merge.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added four passing characterization scenarios for existing auth/sync error selection before production changes; recorded reversed precedence and raw fallback.
2. Changed intended assertions and expanded to seven regression scenarios; five failed red before implementation.
3. Added adapter-local SupabaseErrorMessages, delegated both display seams and reused existing diagnostics redaction. Auth/session/retry/status handling unchanged.
4. Ran full Android unit suite and compatibility lint: 242 tests, 226 passed and 16 existing AddFestivalPersistenceRegressionTest scenarios skipped without disposable API environment. Updated README and BR-056a for intentional display changes.
5. Used Groma CLI to combine the helper into existing authenticated-request ownership and connect auth error selection; rescan confirmed zero recreated components. Built fresh signed APK with all 53 release tasks rerun, verified v1/v2 and digest. Finalized evidence and committed task.
Minimal adapter implementation detail reuse; no architecture-significant boundary, dependency, schema or auth policy change; approval/ADR unnecessary. No material plan deviations. Reflection confined to legacy private display seam characterization to avoid introducing transport changes.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Login and authenticated sync now select a nonblank string message before msg and otherwise show operation plus HTTP status. Selected text is trimmed and uses existing credential/token redaction. Raw malformed/unsupported payloads and non-string JSON fields are not displayed. Authentication and retry logic are unchanged.

## Acceptance criteria validation
1. Seven automated scenarios cover both fields, either field, missing/blank/null/non-string/malformed bodies, whitespace and status-specific operation fallbacks.
2. Both clients delegate only error display; existing refresh/session tests pass. Secret-bearing selected messages use existing SupabaseDiagnostics sanitization; raw token payload is rejected. No new logs.
3. Four characterization scenarios passed first; five of seven intended regressions failed red, then all seven passed after implementation.
4. Reuse stays in Android adapters. README and BR-056a document exact selection rule and intentional presentation changes.
5. Assessed before implementation: local parser reuse only, no new transport or domain boundary, dependency, schema, auth policy or testing strategy. No architecture approval/ADR required.
6. Final plan and impact/validation evidence recorded.

## How to test
### Automated tests
JDK21 ./gradlew :app:testDebugUnitTest :app:lintDebug passed. Unit XML: 242 total, 226 passed, 0 failures/errors, 16 skipped AddFestivalPersistenceRegressionTest cases requiring disposable API setup. Existing auth refresh/offline session/invalid refresh and sync tests passed. Groma scan after combining helper: zero new components, known 98 lint candidate groups; scanner warnings remain analysis limitations. git diff --check passed.
Required per-ticket fresh signed build: deleted old APK, JDK21 ./gradlew assembleRelease --rerun-tasks succeeded (53 executed). Existing key only; apksigner verified v1/v2. APK SHA256 c62d669f027893b7e18723c0984ac1e6af8ea3e74f2e5c80ef2b79a22102f11d at app/build/outputs/apk/release/app-release.apk. Remains 2.34/code42; not published.
### Manual validation
Reviewed production diff: only extraction delegation/import cleanup; no auth flow changed. Examples: both fields now show message for login and sync; blank message with msg shows msg; HTML/unknown JSON yields operation with status. No live login, native device or disposable database gate run for display-only change; full release checks remain TASK-241.

## TDD / BDD / approval-test evidence
Given existing clients, four legacy characterization scenarios passed. Given desired consistent/private messages, five regressions failed before production change; all seven passed afterward. Existing authenticated session/retry behaviors stayed green. Reflection used only at the legacy private display seam, avoiding real authentication/network or new transport injection.

## Architecture impact
- Architecture-significant change: no; pure display parser within existing adapter responsibility.
- Approval received: not required.
- ADR: ADR impact: none, because domain, adapter contracts, runtime/dependency/schema/auth policy and testing strategy are unchanged.

## README impact
README impact: updated backend-error troubleshooting with precedence, status fallback, redaction and unchanged session behavior.

## Business requirements impact
Business requirements impact: updated BR-056a to record consistent privacy-preserving error presentation.

## Diagram impact
Diagram impact: updated Groma source ownership to retain the shared helper in existing authenticated-request responsibility and authored auth-to-parser collaboration through CLI; no new architecture island.

## Commits / logical change list
Characterization/regressions; shared parser and two delegations; README/BR-056a; Groma ownership/collaboration; finalized task record.

## Risks and follow-up
Backend messages remain untrusted text; redaction uses existing known secret formats rather than claiming detection of arbitrary undisclosed secrets. Raw proxy/unsupported payloads intentionally replaced by generic operation/status. Sixteen unrelated real-API scenarios skipped in unit-only environment and remain required at release. Groma containment limitation remains TASK-227. No merge/tag/publication.
<!-- SECTION:NOTES:END -->
