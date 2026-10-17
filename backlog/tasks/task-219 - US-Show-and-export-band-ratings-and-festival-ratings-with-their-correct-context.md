---
id: TASK-219
title: >-
  US: Show and export band ratings and festival ratings with their correct
  context
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 13:18'
updated_date: '2026-10-17 18:46'
labels:
  - ratings
  - festival
  - architecture
  - release-2.34
milestone: m-0
dependencies:
  - TASK-216
  - TASK-218
references:
  - listactivefestivalbandsusecase
  - banddetailactivity
  - wackendatabase
  - bandrepository
  - generatesharedscheduleusecase
  - exportratingscsvusecase
  - settingsactivity
  - apprepositories
  - syncingratingrepository
  - archivedfestivalactivity
modified_files:
  - >-
    application/src/test/java/be/wacken/planner/application/BandRatingContextTest.java
  - >-
    application/src/main/java/be/wacken/planner/application/BandRatingContext.java
  - >-
    application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalPlanningRatingView.java
  - >-
    application/src/main/java/be/wacken/planner/application/FestivalAfterRatingView.java
  - >-
    application/src/main/java/be/wacken/planner/application/ListBandsUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ListArchivedFestivalBandsUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ExportRatingsCsvUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java
  - >-
    application/src/main/java/be/wacken/planner/application/ListActiveFestivalBandsUseCase.java
  - app/src/main/java/be/wacken/planner/AppRepositories.java
  - app/src/main/java/be/wacken/planner/BandDetailActivity.java
  - app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java
  - >-
    application/src/test/java/be/wacken/planner/application/FestivalDetailRatingScopeTest.java
  - >-
    application/src/test/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCaseTest.java
  - >-
    domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingHistoryRepository.java
  - >-
    application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingEventDao.java
  - >-
    app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingHistoryRepository.java
  - >-
    app/src/main/java/be/wacken/planner/SyncingPersonalBandRatingHistoryRepository.java
  - app/src/main/java/be/wacken/planner/SettingsActivity.java
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - >-
    app/src/test/java/be/wacken/planner/ArchivedFestivalLayoutRegressionTest.java
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/decisions/0016-band-catalog-and-archive-promoted-personal-ratings.md
  - >-
    application/src/main/java/be/wacken/planner/application/ViewArchivedFestivalHistoryUseCase.java
  - app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a member, I want detail/history/export to distinguish my current band rating, festival planning rating and festival after-rating so I understand what carries forward. Apply ADR-0016 to active and archived detail, history, Settings export, missing-value states and any current-rating consumers. Metadata and music links resolve through the canonical band. Do not show another festival's after-rating as the selected festival's value. Keep group scheduling based only on that festival's planning ratings. Archived correction audit presentation remains TASK-172;3.0 styling remains separate.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a band has current rating4, selected-festival planning5 and after-rating3, then details/history/export identify these as three separate values with user/festival/source context.
- [ ] #2 Given the selected festival has no after-rating but another festival does, then its after-rating remains empty and the other result appears only in labeled history/current-band context.
- [ ] #3 Given planning or after-rating edits, then group scheduling uses only the selected festival's planning ratings and metadata/music links retain canonical identity.
- [ ] #4 Given an existing exported CSV contract, then any extension is documented and compatibility is preserved or versioned; original event dates and correction/archive timestamps are distinguishable.
- [ ] #5 Application/native/export regression tests cover different users, repeated bands, renamed/deleted festivals, unrated states and preserved navigation.
- [ ] #6 README and business requirements describe the implemented presentation/export; canonical diagram and ADR impacts reference ADR-0016.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Standard approved ADR0016 presentation/export scope. TDD read-only rating-context use case/DTO and a separately versioned own lifecycle CSV using existing personal/planning/after/history/audit/festival repositories. Label user, selected festival and source/dates; missing/reset stays unrated and never borrows another festival. Preserve legacy CSV contract and correction audit export; add separate v2 filename/header with explicit record types and timestamps. Fix canonical name joins that currently compare full Band metadata (hiding group ratings/schedule/performance context), and case-insensitive archived matching that can mix distinct exact-name identities; add regressions before fixes. Active/archive details show established band rating/source separately, retain selected festival scope, metadata-only canonical links and scroll/navigation. Scheduling remains based only on festival planning, never personal/after. Native tests cover4/5/3, absent vs other festival, users, renamed/deleted source, links and resume; export tests cover owner isolation/date context/escaping and unchanged legacy format. Full JVM/QA/coverage/lint, real API baseline, full native API23/36 and fresh signed build. Update README/BR/ADR0016/C4 impacts. No new schema/service/dependency, no3.0 styling; existing explicit architecture approval covers219.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
2026-10-17 user-approved architecture: ADR-0016 governs canonical shared bands, member-specific established band ratings, independent festival planning/after-ratings, one-time creation snapshots and atomic archive promotion. Missing after-ratings retain the prior band rating; archived corrections update it only while their festival remains the source. TASK-216–220 implement and validate the lifecycle. This supersedes earlier latest-event prefill wording; do not treat approved planning as shipped behavior.

Implementation progress: regression tests reproduced metadata-dependent group/schedule joins and case-collapsed archived identities (red, then green). Added separate established/planning/after context with source/dates, festival-bound detail views, canonical-only native music links, and a separate own lifecycle CSV v2. Legacy nine-column and audit export contracts retained. Full JVM/QA/coverage, debug lint and native compilation pass; native full run is in progress. Added owner-scoped read-only cached history enumeration (no schema/remote contract change) to retain detached/deleted history in exports. Final Android API23/36 CI, selected-festival return/rotation test, clean signed package and delivery notes remain before closure.
<!-- SECTION:NOTES:END -->
