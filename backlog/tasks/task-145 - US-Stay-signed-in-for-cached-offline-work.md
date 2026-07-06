---
id: task-145
title: 'US: Stay signed in for cached offline work'
status: Done
assignee:
  - '@codex'
created_date: '2026-07-06 06:48'
updated_date: '2026-07-06 07:42'
labels:
  - mvp3
  - offline
  - auth
  - sync
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: Wacken network conditions are unreliable, and forcing a Supabase login when the device has no Wi-Fi or mobile data makes offline field use impossible.

As a festival attendee with previously cached app data, I want the app to keep my local signed-in session usable when the network is unavailable, so that I can keep viewing and editing cached Wacken data offline and sync later.

Scope: distinguish network/connectivity failures from genuinely invalid Supabase sessions during startup, reactivation, manual sync, and background sync. Preserve cached app access and pending local edits when the device cannot reach Supabase.

Out of scope: allowing first-time login without network; changing Supabase account provisioning; resolving remote conflicts beyond the existing offline-first sync policy.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given the user has previously signed in and cached festival data exists, when the app starts or resumes with no Wi-Fi or mobile data, then the app does not clear the local Supabase session or redirect to login only because Supabase cannot be reached
- [x] #2 Given the device has no network, when startup, background, manual, or close sync tries to refresh the Supabase token or call Supabase, then cached overview, band detail, settings, ratings, real ratings, and schedule remain usable where cached data exists
- [x] #3 Given offline edits are made while Supabase cannot be reached, when sync is not possible, then the edits are saved locally with pending sync state and no login is required
- [x] #4 Given Supabase explicitly rejects the session as invalid or the refresh token is unusable for a non-connectivity reason, when authenticated access is attempted, then the app may clear the session and return the user to login with a clear message
- [x] #5 Given there is no cached/imported festival data and no network, when the app opens, then it explains that an initial sync or import is needed instead of showing a misleading Supabase login loop
- [x] #6 Automated tests or documented manual checks cover offline startup/reactivation, token-refresh network failure, and the invalid-session path
- [x] #7 Business requirements and README impact use canonical delivery-governance wording
- [x] #8 Architecture impact is assessed before implementation; if auth or sync boundaries change, explicit approval and ADR handling are completed before coding
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected Supabase auth/session refresh, authenticated request retry, lifecycle/manual/close sync, empty-cache messaging, and existing offline-first rating/schedule-lock paths.
2. Added regression coverage in SupabaseSessionManagerTest for token-refresh connectivity failure preserving the stored session, invalid refresh clearing it, and expired-JWT retry preserving the session when refresh cannot reach Supabase.
3. Added InvalidAuthSessionException and changed SupabaseSessionManager so only explicit invalid-session failures clear the local session; network and other plain IO failures now remain sync failures with the session preserved.
4. Updated SupabaseAuthClient to classify auth HTTP 400/401/403 refresh failures as invalid-session failures while leaving connectivity/server failures as ordinary IO failures.
5. Updated the no-cache empty-state text, README, and business requirements to document offline auth/session behavior.
6. Ran focused, app, multi-module, debug APK, signed release APK, signature, hash, and diff hygiene validation.

Architecture impact: not architecture-significant; the change stays inside the existing Android app-edge Supabase auth/session adapter and ADR 0010 offline-first sync boundary. No new port, adapter boundary, schema, dependency, API contract, module boundary, or security model was introduced. No architecture approval or ADR was required.
Deviation: physical device airplane-mode validation was not run in this environment; automated regression tests and APK validation cover the implemented classification path.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

- Preserved the local Supabase session when token refresh fails because Supabase cannot be reached, so offline/cached use is not converted into a login requirement.
- Kept the existing invalid-session behavior for refresh tokens that are missing or explicitly rejected by Supabase.
- Updated the first-run empty-cache message to tell the user an initial Supabase sync or import is needed.
- Updated README and business requirements to distinguish connectivity failure from invalid credentials.

## Acceptance criteria validation

- AC1: SupabaseSessionManagerTest now proves a token-refresh network failure preserves the stored session instead of clearing it.
- AC2: Startup/background/manual/close sync all go through the same AppRepositories and SupabaseSessionManager path; connectivity failures now remain sync failures, so existing cached-screen recovery paths stay in place.
- AC3: Existing local-first rating and schedule-lock queues are unchanged and remain covered by app tests; preserving the session prevents offline sync failure from forcing login.
- AC4: InvalidAuthSessionException still clears the stored session and returns AuthenticationRequiredException for missing or explicitly rejected refresh credentials.
- AC5: The empty band-list message now explains that an initial Supabase sync or import is needed.
- AC6: Automated tests cover token-refresh network failure, expired-JWT refresh network failure, and invalid-session clearing. Existing app tests cover pending rating and schedule-lock offline queues.
- AC7: README and business requirements were updated with canonical impact notes below.
- AC8: Architecture impact was assessed as not architecture-significant; no approval or ADR was required.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest --tests be.wacken.planner.SupabaseSessionManagerTest`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug assembleRelease`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) WACKEN_RELEASE_* ./gradlew assembleRelease` with local keychain-backed signing values
- `git diff --check`

### Manual validation

- Signed APK verification: `apksigner verify --verbose app/build/outputs/apk/release/app-release.apk` verifies v1 and v2 signatures with one signer.
- APK metadata: package `be.wacken.planner`, versionCode `23`, versionName `2.20`, minSdkVersion `23`.
- Release APK SHA-256: `8b983c710dc12aef3966bccb02990e667026911b7263653490babba8fa00b92c`.
- Physical device airplane-mode validation was not run in this environment.

## TDD / BDD / approval-test evidence

- Added failing regression tests first for the auth/session classification bug, then implemented the smallest app-edge change to pass them.
- No approval tests were needed because this was a focused behavior fix, not a legacy structural refactor.

## Architecture impact

- Architecture-significant change: no.
- Approval received: not required.
- ADR: none, because the existing Supabase auth adapter and ADR 0010 offline-first sync boundary were preserved.

## README impact

README impact: updated Supabase Auth behavior to state that connectivity failures preserve the stored session while invalid/revoked refresh tokens still return the user to login.

## Business requirements impact

Business requirements impact: updated BR-056 and BR-080 to distinguish offline/connectivity refresh failures from invalid sessions.

## Diagram impact

Diagram impact: none, because module/container relationships did not change.

## Commits / logical change list

- Added invalid-session error classification for Supabase token refresh.
- Preserved stored sessions on token-refresh connectivity failures.
- Added regression tests for offline refresh and invalid-session paths.
- Clarified empty-cache messaging and documentation.

## Risks and follow-up

- Physical no-network behavior should still be smoke-tested on an installed Android device before relying on the APK in festival conditions.
- This task does not add first-time offline login; users still need a previous successful sign-in and cached or imported data.
<!-- SECTION:NOTES:END -->
