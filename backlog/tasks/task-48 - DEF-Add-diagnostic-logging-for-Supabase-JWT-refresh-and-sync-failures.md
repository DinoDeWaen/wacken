---
id: task-48
title: 'DEF: Add diagnostic logging for Supabase JWT refresh and sync failures'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-03 18:03'
updated_date: '2026-06-03 18:08'
labels:
  - auth
  - supabase
  - defect
  - logging
  - sync
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
## Business value

JWT refresh and rating/master-data sync are still failing in user validation. Diagnostic logs are needed so the next fix can be based on the actual auth state transitions and Supabase response data instead of guessing.

## User story

As the app maintainer, I want safe diagnostic logging around Supabase JWT refresh and authenticated sync requests, so that a user can share enough evidence to fix the remaining sync failure without exposing access tokens or refresh tokens.

## Scope

In scope:
- Log session refresh decisions: session missing, token considered fresh, token considered expired/near expiry, refresh attempt started, refresh succeeded, refresh failed, and session cleared.
- Log authenticated Supabase request retry decisions: request label/endpoint category, HTTP status, sanitized error message, expired-JWT detection result, retry attempt, retry success, and retry failure.
- Log high-level master-data and rating sync start/success/failure events.
- Redact tokens and secrets; logs must not print access tokens, refresh tokens, anon keys, passwords, database URLs, or full Authorization headers.
- Add automated tests for the diagnostic redaction or logging helper where feasible.

Out of scope:
- Changing the actual refresh algorithm beyond what is needed to add diagnostics.
- Adding remote crash/log collection.
- Logging personally sensitive values beyond coarse user/session state.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given Supabase sync fails, when the user captures logs, then the logs include refresh and retry decision points with sanitized status/error details.
- [x] #2 Given tokens or secrets are present in auth/session objects or HTTP headers, when diagnostics are written, then access tokens, refresh tokens, anon keys, passwords, and Authorization header values are not logged.
- [x] #3 Given master-data or rating sync runs, when logging is enabled through normal Android logcat, then start, success, and failure events identify the sync area without exposing sensitive data.
- [x] #4 Automated tests cover redaction or diagnostic formatting where feasible.
- [x] #5 README impact, business requirements impact, diagram impact, and ADR impact are recorded in implementation notes.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add a small package-local Supabase diagnostics helper that logs through Android logcat when available and redacts tokens/secrets from messages.
2. Add focused unit tests for diagnostic redaction so access tokens, refresh tokens, anon keys, passwords, and Authorization header values cannot appear in logs.
3. Wire diagnostics into `SupabaseSessionManager` for missing/fresh/expired/refresh success/failure/session-clear decisions.
4. Wire diagnostics into `SupabaseAuthenticatedRequest` for request status, sanitized error body, expired-JWT detection, retry attempt, retry success, and retry failure.
5. Add high-level sync start/success/failure logs in `AppRepositories` and run app unit tests.

Design approach: diagnostic-only change, no refresh algorithm changes. Logging remains in the Android app adapter package and does not affect domain/application rules.
Architecture impact: not architecture-significant; no provider, schema, dependency, or module-boundary change. No ADR expected.
README/business/diagram impact: expected none beyond task notes because this is diagnostic behavior for a defect investigation.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Implementation notes
- Added package-local SupabaseDiagnostics for Android logcat diagnostics under tag WackenSupabase.
- Added sanitized auth/session refresh logs for missing session, fresh session, near-expiry refresh, rejected-session refresh, already-refreshed reuse, refresh success, refresh failure, and session clearing.
- Added sanitized authenticated request logs for initial send, success, expired-JWT detection, retry start, retry success, retry failure, and IO exceptions.
- Added high-level ratings_sync and master_data_sync start/success/failure diagnostics.
- Added SupabaseDiagnosticsTest coverage for bearer JWTs, JSON token fields, key-value secrets, Authorization values, passwords, and Postgres database URLs.

Validation
- Automated: JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest passed.
- Manual: not run on device; user can capture logs with adb logcat -s WackenSupabase after reproducing the sync issue.

Impact
- README impact: not updated; this is defect diagnostic instrumentation and the capture command is reported in the task/final notes.
- Business requirements impact: none; BR-056 behavior is unchanged, only diagnostics were added.
- Diagram impact: none; no architecture or flow diagram changed.
- ADR impact: none; not architecture-significant and no provider/schema/module-boundary decision changed.
- Risks: log messages intentionally avoid tokens/secrets, but user-shared logs should still be treated as support data.
<!-- SECTION:NOTES:END -->
