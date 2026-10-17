---
id: task-152
title: 'US: Sync personal band rating history to Supabase'
status: Done
assignee:
  - '@codex'
created_date: '2026-08-12 07:53'
updated_date: '2026-08-13 05:39'
labels:
  - user-story
  - post-mvp3
  - ratings
  - supabase
  - history
dependencies:
  - task-149
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want my real ratings for bands I have seen to sync to Supabase so that my personal band history is preserved across devices and future festivals.

Business value: Personal experience ratings become durable history and can be reused for later festival planning.

Scope: store personal band rating events per user, band, festival, rating value, and created date; sync them to Supabase; show multiple historical ratings for the same band with festival/date context.

Out of scope: group-average personal ratings, alias-based band linking, editing historical archived festival data.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given a user records a real post-show rating, when sync succeeds, then Supabase stores a personal band rating event with user, band, festival, rating, and created date.
- [x] #2 Given the same user rates the same band at two festivals, then both rating events are retained with their festival and created-date context.
- [x] #3 Given a user opens a known band, then they can see their historical personal ratings for that band with festival and date reference.
- [x] #4 Given the device is offline, then the rating remains local and is queued for later sync without logging the user out solely because Supabase cannot be reached.
- [x] #5 Domain/application tests cover rating-history rules and infrastructure tests cover Supabase mapping/sync behavior.
- [x] #6 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added personal band rating event domain/application model and tests.
2. Persisted personal rating events locally with pending sync metadata and added Supabase schema/client mapping.
3. Wired real rating save to create personal rating history while preserving latest real rating behavior.
4. Showed personal rating history on band detail and archived festival history.
5. Added app sync decorator and Supabase JSON mapping tests.
6. Updated README and business requirements and ran full validation.

Deviation: implemented alongside festival planning ratings so personal history can be linked to active festival context and used for prefill. Architecture approval was previously received for ADR 0011 standard option.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary

Implemented synced personal band rating history. Real post-show ratings now create personal rating events with user, band, festival id where available, rating value, and created date; events are queued locally and synced to Supabase. Band detail and archived festival history show historical personal ratings with festival/date context.

## Acceptance criteria validation

- AC1: Supabase schema/client supports personal_band_rating_events with user, band, festival, rating, created_at.
- AC2: application tests preserve multiple events and latest-by-created-date behavior.
- AC3: ShowBandDetailUseCase and BandDetailActivity display personal history.
- AC4: SyncingPersonalBandRatingHistoryRepositoryTest verifies events remain pending when push fails; offline session preservation is covered by existing auth/sync behavior.
- AC5: FestivalRatingIndependenceTest, ArchivedFestivalHistoryUseCaseTest, and SyncingPersonalBandRatingHistoryRepositoryTest cover rules and sync mapping.
- AC6: documentation and impact notes recorded here.

## How to test

### Automated tests

- JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew :domain:test :application:test :app:testDebugUnitTest :app:compileDebugJavaWithJavac qaTest assembleDebug

### Manual validation

- Open a band detail screen, set a real rating, sync, and reopen the band to confirm personal history is visible.

## TDD / BDD / approval-test evidence

- Added application tests for personal rating history and app tests for sync pending/mapping behavior.
- QA scenario exercises personal-history based prefill through new festival import.

## Architecture impact

- Architecture-significant change: yes, persistence/schema and sync boundary.
- Approval received: yes, user approved ADR 0011 standard option.
- ADR: ADR impact: none, because ADR 0011 already records the approved personal rating history direction.

## README impact

README impact: updated current behavior, repository responsibilities, and backend sync/database notes.

## Business requirements impact

Business requirements impact: updated implemented post-MVP3 capability status.

## Diagram impact

Diagram impact: none, because existing module diagrams still describe the same container/module boundaries.

## Commits / logical change list

- Add personal rating event model, Room table, Supabase migration, sync client/decorator, detail/history display, and tests.

## Risks and follow-up

- Editing or deleting historical personal rating events remains out of scope.
<!-- SECTION:NOTES:END -->
