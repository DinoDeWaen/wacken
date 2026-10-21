---
type: C4 Component
title: Offline operation sync
status: stable
groma:
  id: syncingratingrepository
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SyncingRatingRepository.java
      symbol: SyncingRatingRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SyncingFestivalRepository.java
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SyncingFestivalLineupRepository.java
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SyncingFestivalPlanningRatingRepository.java
      symbol: SyncingFestivalPlanningRatingRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SyncingPersonalBandRatingHistoryRepository.java
      symbol: SyncingPersonalBandRatingHistoryRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SyncingScheduleLockStore.java
      symbol: SyncingScheduleLockStore
    - scanner: java
      file: app/src/main/java/be/wacken/planner/RatingSyncLocalStore.java
      symbol: RatingSyncLocalStore
    - scanner: java
      file: app/src/main/java/be/wacken/planner/FestivalPlanningRatingSyncLocalStore.java
      symbol: FestivalPlanningRatingSyncLocalStore
    - scanner: java
      file: app/src/main/java/be/wacken/planner/PersonalBandRatingSyncLocalStore.java
      symbol: PersonalBandRatingSyncLocalStore
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ScheduleLockLocalStore.java
      symbol: ScheduleLockLocalStore
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ScheduleLockStore.java
  group: Storage and synchronization
description: Queue and replay mutable changes
---

Coordinates local pending ratings, festival changes, personal history, and schedule locks with remote writes when a session is available.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/SyncingFestivalPlanningRatingRepository.java](../../../../../../app/src/main/java/be/wacken/planner/SyncingFestivalPlanningRatingRepository.java) | [app/src/main/java/be/wacken/planner/MissingFestivalPlanningRatingException.java](../../../../../../app/src/main/java/be/wacken/planner/MissingFestivalPlanningRatingException.java) | Reports retained orphan rating | Java call |
