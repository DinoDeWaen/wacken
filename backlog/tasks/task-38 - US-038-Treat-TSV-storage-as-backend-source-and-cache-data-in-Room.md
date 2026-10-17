---
id: task-38
title: 'US-038: Treat TSV storage as backend source and cache data in Room'
status: Done
assignee:
  - '@codex'
created_date: '2026-05-17 07:00'
updated_date: '2026-05-17 07:14'
labels:
  - architecture
  - persistence
  - performance
  - mvp1
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a festival attendee, I want imported festival data and ratings to be cached in a local Room database while TSV files act like a backend source, so that the app is fast now and can later swap TSV synchronization for a real backend API without changing the domain model.

Business value:
- Keeps rating and browsing responsive with a real local cache.
- Makes the MVP architecture closer to the future backend model: remote/source adapter syncs data, local cache serves app reads/writes.
- Preserves clean domain/application boundaries by hiding Room, TSV, and future API details behind adapters.

In scope:
- Add Room as the app local cache for bands, stages, performances, stage distances, food options, and ratings.
- Treat existing TSV-backed repositories as a backend-like source adapter that can be replaced later by an API adapter.
- Sync imported TSV/source data into Room, then serve app reads from Room.
- Persist rating changes locally in Room and update the TSV backend-like source as the current MVP outbound sync target.
- Keep domain and application modules free of Android, Room, and file APIs.
- Update ADR/README/diagrams because the persistence architecture changes.

Out of scope:
- Implementing a real network backend API.
- Multi-user conflict resolution or server authentication.
- Changing CSV schemas or rating scale.

Architecture note:
- This is architecture-significant and requires explicit approval before implementation.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given TSV source files are imported, when the import finishes, then festival master data is available from the local Room cache.
- [x] #2 Given the band overview opens after data is cached, then reads use Room/local cache rather than reparsing TSV files for every list load.
- [x] #3 Given a user rates a band, then the rating is saved in Room and also propagated to the TSV backend-like source adapter for MVP persistence.
- [x] #4 Given a future backend API replaces TSV files, then domain/application code does not need to change because synchronization stays behind adapter boundaries.
- [x] #5 Given Room is introduced, then domain and application modules remain free of Android, Room, SQL, and file-system dependencies.
- [x] #6 Given the persistence model changes, then an ADR supersedes or updates ADR-0006 and README architecture text/diagrams are updated.
- [x] #7 Automated tests cover sync behavior, repository/cache behavior, and existing listing/rating/import behavior remains green.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Inspected repository ports, file-backed adapters, import flow, Android wiring, Gradle dependencies, and existing ADR-0006.
2. Requested and received explicit approval for the standard architecture option: Room local cache with TSV as backend-like source.
3. Added Room runtime/compiler to the Android app module and enabled AndroidX.
4. Added Room database, entities, DAOs, and Room-backed repository adapters in the Android app edge.
5. Added infrastructure sync/write-through repository decorators so reads come from cache and writes propagate to the TSV source.
6. Extended repository snapshots only where sync needs them: saved ratings and stage distances.
7. Wired AppRepositories to compose TSV source adapters, Room cache adapters, and synced decorators; first use seeds empty Room tables from existing TSV files.
8. Added domain and infrastructure tests for SavedRating and sync/cache behavior.
9. Updated business requirements, README architecture text/diagrams, superseded ADR-0006, and added ADR-0007.
10. Ran targeted compile/tests, full tests, QA tests, and assembleDebug.

Deviation: Room adapters live in the Android app module rather than infrastructure because Room is Android-specific; infrastructure keeps only framework-free source/sync decorators.
Architecture impact: significant, approved by user. ADR-0007 accepted and ADR-0006 superseded.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented the approved standard persistence architecture: TSV files now act as the MVP backend-like source, while AndroidX Room is the local cache used by app reads. AppRepositories composes TSV file-backed source adapters, Room cache adapters, and framework-free sync/write-through decorators behind the existing domain repository ports.

Room entities, DAOs, and Room-backed repositories live in the Android app edge. Domain and application modules remain free of Android, Room, SQL, file-system, and future API details. Existing TSV data is seeded into empty Room tables on first use so older MVP installs migrate into the cache. Imports and rating writes write through to both TSV and Room.

## Acceptance criteria validation

- AC1: Import use cases still call repository replaceAll; synced repositories write imported festival master data to the TSV source and Room cache.
- AC2: App reads now go through Room-backed repositories; TSV is not reparsed for normal cached overview reads.
- AC3: RatingRepository save writes to TSV source and Room cache through SyncedRatingRepository.
- AC4: TSV source is isolated behind sync/source adapters, so a future API can replace it without changing domain/application code.
- AC5: Room code is only in app/src/main/java/be/wacken/planner/persistence; domain/application have no Android, Room, SQL, or file dependencies.
- AC6: ADR-0006 is superseded, ADR-0007 is added, and README architecture text/diagrams were updated.
- AC7: Added SavedRating domain tests and SyncedRepository infrastructure tests; existing test and QA suites pass.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :infrastructure:test :app:compileDebugJavaWithJavac
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew qaTest
- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew assembleDebug

All commands passed. Debug APK regenerated at app/build/outputs/apk/debug/app-debug.apk.

### Manual validation

- Install the regenerated debug APK.
- Import the Wacken CSV files.
- Open the band list and verify imported bands appear.
- Rate a band, leave and reopen the app, and verify the rating remains.
- Return to the overview and verify it remains fast because reads come from Room cache.

## TDD / BDD / approval-test evidence

- Added domain tests for SavedRating validation.
- Added infrastructure tests proving source-to-cache sync, cache reads, stage-distance sync, and rating write-through.
- Existing listing/rating/import tests and QA scenarios stayed green.

## Architecture impact

- Architecture-significant change: yes, persistence model and adapter shape changed.
- Approval received: yes, user approved the standard approach.
- ADR: ADR-0007 added; ADR-0006 marked superseded.

## README impact

README updated with Room local cache, TSV backend-like source, ADR-0007, and updated module/container diagrams.

## Diagram impact

README Mermaid diagrams updated to show Room cache and TSV backend-like source.

## Commits / logical change list

- Added Room dependency and AndroidX enablement.
- Added Room database/entities/DAOs/repositories in the app persistence edge.
- Added sync/write-through repository decorators in infrastructure.
- Added SavedRating snapshot and repository snapshot methods needed for cache seeding.
- Rewired AppRepositories to use TSV source + Room cache.
- Updated tests, ADRs, README, and business requirements.

## Risks and follow-up

- Room uses allowMainThreadQueries to preserve the current synchronous use-case shape; a later production hardening task should move sync/database work off the UI thread.
- Current sync is simple write-through, not a conflict-aware multi-device sync engine.
<!-- SECTION:NOTES:END -->
