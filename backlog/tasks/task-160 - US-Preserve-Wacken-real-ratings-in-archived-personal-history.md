---
id: task-160
title: 'US: Preserve Wacken real ratings in archived personal history'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-14 09:42'
updated_date: '2026-08-14 20:00'
labels:
  - user-story
  - defect
  - archive
  - ratings
  - supabase
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want real ratings that I entered during Wacken to remain visible after archiving so that my personal experience ratings are not lost when the festival moves to history.

Business value: Real ratings are personal history and must survive the transition from active festival to archived festival; otherwise future festival prefilling and historical reference become untrustworthy.

Scope: existing Wacken real ratings, including ratings stored before personal rating events existed, must be migrated or backfilled into archived personal history; archived band detail must show the real rating value, for example Airbourne with 4 stars; Supabase sync must preserve the restored personal rating events where applicable.

Out of scope: changing planning ratings, recalculating schedules from real ratings, editing archived real ratings, fuzzy band aliases, or assigning exact historical timestamps when old storage did not contain them.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a user recorded a real rating for Airbourne at Wacken before archiving, when Wacken is archived and Airbourne is opened, then the archived band detail shows Airbourne's real rating as 4 stars.
- [x] #2 Given legacy real ratings exist in local storage without personal rating event rows, when the app migrates or reads archived Wacken data, then those ratings are preserved as personal history instead of being shown as missing.
- [x] #3 Given restored Wacken real ratings are available locally, when Supabase sync succeeds, then the corresponding personal band rating history is stored remotely where the current sync model supports it.
- [x] #4 Given old real-rating storage did not contain a created timestamp, then the UI must show an honest fallback such as date unknown instead of inventing a date.
- [x] #5 Automated regression tests cover legacy real-rating preservation for archived Wacken and visible real-rating display on archived band detail data.
- [x] #6 README impact, business requirements impact, diagram impact, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Traced active real-rating storage, archived history lookup, legacy real_ratings fallback, Room migration, and Supabase personal-history sync.
2. Added runtime backfill from legacy Wacken real_ratings into personal_band_rating_events using the explicit unknown-date sentinel.
3. Changed the migration legacy timestamp from migration time to 1970-01-01T00:00:00Z so the UI can honestly show date unknown.
4. Updated archived detail to map selected-festival personal history into the visible Real Rating stars, with Airbourne-style 4-star legacy ratings supported.
5. Added regression coverage for legacy rating value/date fallback and archive detail display extraction.
6. Ran focused and full validation including debug APK assembly.

Deviation: the backfill is intentionally Wacken-only because legacy real_ratings did not carry festival identity and only Wacken is safely inferable in this first-version data set. No ADR was needed because no schema, port, or boundary changed.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Legacy Wacken real ratings are no longer lost when viewing archived Wacken. App startup backfills positive legacy `real_ratings` into `personal_band_rating_events` for `wacken-2026` when no real user-created Wacken event exists, using `1970-01-01T00:00:00Z` as the unknown-date sentinel. The archived band detail now displays that selected-festival real rating in the Real Rating stars, so Airbourne can show 4 stars again.

## Acceptance criteria validation

- AC1: Archived detail maps Wacken personal history/legacy fallback into `detail.realRating()`, so Airbourne-style 4-star history displays as Real Rating stars.
- AC2: RoomPersonalBandRatingHistoryRepository backfills legacy positive Wacken `real_ratings`; ViewArchivedFestivalHistoryUseCase still falls back directly when personal events are missing.
- AC3: Backfilled events are saved through the existing local personal-history sync store as PENDING, so `syncPendingEvents()` can push them to Supabase. Already restored unknown-date legacy events are not re-marked pending on each app start.
- AC4: Migration and runtime backfill use `1970-01-01T00:00:00Z`; PersonalRatingHistoryItem displays that as `date unknown`.
- AC5: ArchivedFestivalHistoryUseCaseTest covers legacy 4-star/date-unknown fallback; WackenDatabaseFestivalMigrationTest covers the sentinel timestamp; ArchivedFestivalLayoutRegressionTest covers archived detail real-rating display extraction.
- AC6: Impact notes are recorded below.

## How to test

### Automated tests

- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac`
- `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :infrastructure:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac assembleDebug`
- `git diff --check`

### Manual validation

Install the built APK over a device with Wacken data, open the Wacken archive, open Airbourne, and confirm Real Rating shows 4 stars and Personal History shows Wacken with `date unknown` if the rating came only from legacy storage.

## TDD / BDD / approval-test evidence

Extended the archive-history unit test around legacy Wacken real ratings and added Android source-level regression coverage for the archived detail real-rating extraction.

## Architecture impact

- Architecture-significant change: no; this uses the existing Room table, migration version, personal-history repository, and Supabase sync adapter.
- Approval received: not required.
- ADR: none.

## README impact

README impact: none, because this fixes a defect in already documented archived rating history behavior.

## Business requirements impact

Business requirements impact: none, because BR-084 and BR-086 already require personal ratings to remain historical and archived festivals to be inspectable read-only.

## Diagram impact

Diagram impact: none, because no architecture or workflow diagram changed.

## Commits / logical change list

- RoomPersonalBandRatingHistoryRepository: Wacken-only legacy real-rating backfill with unknown-date sentinel and no repeated pending requeue.
- WackenDatabase: migration legacy personal-history rows use the unknown-date sentinel.
- ArchivedBandDetailActivity: selected archived festival real rating is shown in Real Rating stars.
- Tests: archive history, migration source, and archive detail regression coverage.

## Risks and follow-up

Existing devices that already synced a migration-time legacy event may push an updated event with the same legacy id and the unknown-date timestamp. This is intentional so old storage does not pretend to know a created date.
<!-- SECTION:NOTES:END -->
