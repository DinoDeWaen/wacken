---
id: task-47
title: 'DEF-047: Renew expired Supabase JWT before authenticated requests'
status: Done
assignee:
  - '@codex'
created_date: '2026-06-03 15:57'
updated_date: '2026-06-03 16:19'
labels:
  - auth
  - supabase
  - defect
  - sync
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a signed-in user, I want the app to renew an expired Supabase JWT automatically so that sync and authenticated database calls continue without forcing me into a broken state.

Defect observed:
- The Supabase JWT access token can expire.
- The current renewal process is failing.
- Once expired, authenticated requests fail instead of refreshing the token and retrying or asking the user to sign in again cleanly.

Business value:
- Users can keep using rating sync and master-data sync across app sessions.
- The app avoids confusing auth failures after normal token expiry.
- Shared ratings remain reliable for multiple users.

In scope:
- Detect expired or rejected JWT responses from Supabase authenticated requests.
- Refresh the session using the stored refresh token before retrying authenticated requests.
- Persist the renewed access token, refresh token, and expiry metadata.
- If refresh fails because the refresh token is invalid/revoked, clear the session and navigate the user to login with a clear state.
- Add automated coverage for expired-token refresh success, refresh failure, and retry behavior where feasible.

Out of scope:
- Changing Supabase Auth provider or database schema.
- Adding new login methods.
- Changing rating or master-data sync business rules.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the access token is expired and the refresh token is valid, when an authenticated Supabase request is made, then the app refreshes the session, persists the renewed tokens, retries the request, and succeeds without user action.
- [x] #2 Given Supabase rejects an authenticated request because the JWT is expired, when refresh succeeds, then the failed request is retried once with the new access token.
- [x] #3 Given token refresh fails because the refresh token is invalid, expired, or revoked, when the app handles the failure, then the local session is cleared and the user is sent to login instead of remaining in a broken authenticated state.
- [x] #4 Given multiple sync operations need auth, when one refresh is performed, then subsequent requests use the renewed session rather than the stale token.
- [x] #5 Automated tests cover refresh success, refresh failure, request retry behavior, and session persistence/clearing where feasible.
- [x] #6 README impact, business requirements impact, architecture impact, and ADR impact are recorded using canonical wording from delivery-governance.md.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Add an auth session manager that refreshes expired sessions, persists renewed tokens, clears invalid sessions, and exposes retry support for expired JWT responses.
2. Wire Supabase master-data and rating clients to load fresh sessions through the manager instead of keeping stale session snapshots.
3. Add focused unit tests for refresh success, retry after rejected JWT, refresh failure/session clearing, and reuse of renewed sessions.
4. Update task notes with validation and impact statements; update README/business docs only if public behavior or setup changes.
Architecture impact: not architecture-significant; this fixes existing Supabase auth refresh behavior inside the current adapter boundary and does not change provider, schema, or module boundaries. No ADR expected.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Added a Supabase session manager that loads the latest stored session for authenticated calls, refreshes expired access tokens with the stored refresh token, persists renewed tokens, and clears the local session when refresh fails.

Supabase master-data and rating clients now send authenticated requests through a shared retry wrapper. If Supabase rejects a request because the JWT expired, the app refreshes the session and retries that failed request once with the new token. Rating sync validates identity against a fresh session before network writes.

The band list sync error path now redirects to login when refresh failure clears the session, avoiding a broken authenticated state.

## Acceptance criteria validation

- AC1: Covered by `SupabaseSessionManagerTest.refreshesExpiredSessionAndPersistsRenewedSession`; expired sessions refresh and persist the renewed access/refresh token data before use.
- AC2: Covered by `SupabaseSessionManagerTest.refreshesAndRetriesOnceAfterExpiredJwtResponse`; expired-JWT rejection is retried once with the refreshed token.
- AC3: Covered by `SupabaseSessionManagerTest.clearsSessionWhenRefreshFails`; invalid refresh clears local session and the UI sync path redirects to login after the session is cleared.
- AC4: Covered by `SupabaseSessionManagerTest.reusesRenewedSessionForLaterRequests` and `usesAlreadyRenewedSessionWhenAnotherRequestRefreshedFirst`; later operations use the stored renewed session.
- AC5: Added focused unit tests for refresh success, refresh failure/session clearing, retry behavior, and renewed-session reuse.
- AC6: README impact: updated Supabase Auth behavior to describe local token storage, automatic JWT refresh, retry, and login fallback. Business requirements impact: updated with BR-056 for authenticated Supabase token renewal. Diagram impact: none, because no component relationships or deployment flow changed. ADR impact: none, because the change stays inside the existing Supabase adapter/auth boundary and does not change provider, schema, dependencies, or module boundaries.

## Validation

- `/bin/zsh -lc "JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest"` passed.
- `/bin/zsh -lc "JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test"` passed.

## Risks and follow-up

No Supabase network integration test was added; retry behavior is covered with a deterministic unit-level request wrapper test. A future end-to-end auth smoke test could validate the exact Supabase expired-token response shape against the live project.

## Reopened validation

User validation on 2026-06-03 showed the band list still displayed `Showing cached data. Supabase sync failed: JWT expired`. The retry detector must handle the actual Supabase/PostgREST expired-JWT response shape.

## Follow-up fix after user validation

The first implementation only retried expired JWT responses with HTTP status `401` or `403`. User validation showed the live app displaying `JWT expired` from the response body, so the retry detector now treats any failed Supabase response whose body mentions an expired JWT/token as refreshable. Added `SupabaseSessionManagerTest.refreshesAndRetriesWhenSupabaseReturnsJwtExpiredBodyWithUnexpectedStatus` to lock this behavior.

## Follow-up validation

- `/bin/zsh -lc "JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest"` passed.
- `/bin/zsh -lc "JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test"` passed.
<!-- SECTION:NOTES:END -->
