---
type: C4 Component
title: Room local cache
status: stable
groma:
  id: wackendatabase
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomBand.java
      symbol: RoomBand
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomBandDao.java
      symbol: RoomBandDao
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomBandRepository.java
      symbol: RoomBandRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFestival.java
      symbol: RoomFestival
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFestivalDao.java
      symbol: RoomFestivalDao
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFestivalLineupEntry.java
      symbol: RoomFestivalLineupEntry
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFestivalLineupEntryDao.java
      symbol: RoomFestivalLineupEntryDao
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFestivalLineupRepository.java
      symbol: RoomFestivalLineupRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRating.java
      symbol: RoomFestivalPlanningRating
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingDao.java
      symbol: RoomFestivalPlanningRatingDao
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingRepository.java
      symbol: RoomFestivalPlanningRatingRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFestivalRepository.java
      symbol: RoomFestivalRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFoodOption.java
      symbol: RoomFoodOption
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFoodOptionDao.java
      symbol: RoomFoodOptionDao
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFoodOptionRepository.java
      symbol: RoomFoodOptionRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomPerformance.java
      symbol: RoomPerformance
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomPerformanceDao.java
      symbol: RoomPerformanceDao
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomPerformanceRepository.java
      symbol: RoomPerformanceRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingEvent.java
      symbol: RoomPersonalBandRatingEvent
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingEventDao.java
      symbol: RoomPersonalBandRatingEventDao
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingHistoryRepository.java
      symbol: RoomPersonalBandRatingHistoryRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomRating.java
      symbol: RoomRating
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomRatingDao.java
      symbol: RoomRatingDao
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomRatingRepository.java
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomRealRating.java
      symbol: RoomRealRating
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomRealRatingDao.java
      symbol: RoomRealRatingDao
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomRealRatingRepository.java
      symbol: RoomRealRatingRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomScheduleLock.java
      symbol: RoomScheduleLock
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomScheduleLockDao.java
      symbol: RoomScheduleLockDao
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomScheduleLockStore.java
      symbol: RoomScheduleLockStore
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomStage.java
      symbol: RoomStage
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomStageDao.java
      symbol: RoomStageDao
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomStageDistance.java
      symbol: RoomStageDistance
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomStageDistanceDao.java
      symbol: RoomStageDistanceDao
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomStageDistanceRepository.java
      symbol: RoomStageDistanceRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomStageRepository.java
      symbol: RoomStageRepository
  technology: AndroidX Room / SQLite
  group: Storage and synchronization
description: Store lineup and user state on device
---

Room database, DAOs, entities, and repository adapters keep festival and group data available offline and track pending mutable operations.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/persistence/RoomFestivalRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalRepository.java) | [app/src/main/java/be/wacken/planner/persistence/RoomFestivalCacheMarkerDao.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalCacheMarkerDao.java) | Guards initial festival seeding | Java call |
| [app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java) | [app/src/main/java/be/wacken/planner/persistence/RoomRatingStateDao.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomRatingStateDao.java) | Exposes generated queries | Room generated implementation |
