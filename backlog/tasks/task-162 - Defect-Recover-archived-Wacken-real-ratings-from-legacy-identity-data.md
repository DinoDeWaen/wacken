---
id: task-162
title: 'Defect: Recover archived Wacken real ratings from legacy identity data'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-15 08:15'
updated_date: '2026-08-15 08:18'
labels:
  - defect
  - archive
  - ratings
  - supabase
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The V2.23 archive detail layout is correct, but Wacken real ratings can still be missing after migration. Production/older app data can still contain the real rating while the new archived detail shows no Real Rating stars.

Root cause to fix: legacy local real_ratings rows may have been stored under an older local/user identity instead of the current Supabase user id, and legacy personal-history event ids generated from user:band:legacy-real are not valid UUIDs for the Supabase personal_band_rating_events table.

Scope: adopt positive local legacy Wacken real ratings for the currently signed-in user when no current-user Wacken real history exists, use deterministic UUID-compatible ids for restored legacy personal events, keep unknown-date display, remove stale local legacy-id duplicates when replacing them, and keep Supabase sync compatible with old pending legacy ids.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given Airbourne has a positive legacy real_ratings row under an older local identity, when the signed-in user opens archived Wacken, then Airbourne's Real Rating shows that value for the current user.
- [x] #2 Given a restored legacy personal rating event is synced to Supabase, then the event id sent to personal_band_rating_events is UUID-compatible.
- [x] #3 Given a stale local legacy event id exists from a previous migration, then the app does not show duplicate personal-history rows for the same restored Wacken rating.
- [x] #4 Given old storage has no created timestamp, then restored history still displays date unknown.
- [x] #5 Automated regression coverage protects legacy identity adoption and UUID-compatible sync behavior.
- [x] #6 README impact, business requirements impact, diagram impact, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Confirmed the V2.23 UI is rendering the Real Rating section, so the remaining issue is data recovery rather than layout.
2. Updated AppRepositories to load the current session before legacy real-rating backfill and pass the current Supabase user id into recovery.
3. Updated RoomPersonalBandRatingHistoryRepository to adopt positive local legacy Wacken real_ratings rows for the current user when current-user Wacken real history is missing.
4. Switched restored legacy event ids to deterministic UUID-compatible ids, removed stale local colon-id duplicates, and preserved the unknown-date sentinel.
5. Updated SupabasePersonalBandRatingClient to map any old pending non-UUID local event id to a deterministic UUID before pushing to Supabase.
6. Added regression tests and ran focused plus full validation.

Deviation: no backend schema migration was added. The fix is local recovery and sync compatibility for already existing Room/Supabase tables.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Fixed the remaining archived Wacken real-rating loss path. Positive local legacy `real_ratings` rows are now recoverable for the currently signed-in Supabase user when no current-user Wacken personal rating history exists. Restored legacy personal rating events now use deterministic UUID-compatible ids and `1970-01-01T00:00:00Z` for unknown created dates. Stale local `user:band:legacy-real` rows are removed when replaced, and old pending non-UUID events are mapped to deterministic UUIDs before Supabase push.

## Acceptance criteria validation

- AC1: AppRepositories passes the current session user id into the legacy Wacken real-rating backfill, allowing old local identity rows to be adopted for the current signed-in user when current-user history is missing.
- AC2: Restored legacy events use `UUID.nameUUIDFromBytes(...)`; SupabasePersonalBandRatingClient also maps old pending non-UUID event ids before push.
- AC3: RoomPersonalBandRatingHistoryRepository deletes the stale colon-based legacy event id for the target user/band before saving the UUID-compatible replacement.
- AC4: Restored history still uses `Instant.EPOCH`, which PersonalRatingHistoryItem displays as `date unknown`.
- AC5: SyncingPersonalBandRatingHistoryRepositoryTest covers deterministic UUID mapping; LegacyRealRatingBackfillRegressionTest covers current-user backfill wiring, UUID-compatible ids, stale-id deletion, and adoption source markers.
- AC6: Impact notes are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug`
- `git diff --check`

### Manual validation

Install the next signed APK over the affected app data, open archived Wacken, open Airbourne, and confirm the Real Rating stars are restored from the legacy local rating.

## TDD / BDD / approval-test evidence

Added regression coverage before relying on the implementation: deterministic UUID mapping for legacy event ids and source-level regression checks for current-session legacy backfill/adoption behavior.

## Architecture impact

- Architecture-significant change: no; this keeps the existing Room table, Supabase table, repository, and sync boundaries.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because this fixes already documented archived rating history behavior.

## Business requirements impact

Business requirements impact: none, because BR-084/BR-086 already require historical personal ratings to remain visible in read-only archived festival views.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- AppRepositories: pass current session user into legacy real-rating recovery.
- RoomPersonalBandRatingHistoryRepository: adopt old identity real ratings, create deterministic UUID legacy events, remove stale duplicates.
- SupabasePersonalBandRatingClient: map old non-UUID pending ids to deterministic UUIDs for remote writes.
- Tests: legacy backfill and UUID sync regression coverage.

## Risks and follow-up

This recovery assumes local `real_ratings` rows in a device install belong to that signed-in user if the current user has no Wacken personal history yet. That matches this single-friend-group app and the legacy non-synced real-rating storage model.
<!-- SECTION:NOTES:END -->
