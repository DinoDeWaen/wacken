---
type: C4 Component
title: Master data cache decorators
status: stable
groma:
  id: syncedbandrepository
  parent: android-application
  code:
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java
      symbol: SyncedBandRepository
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedFoodOptionRepository.java
      symbol: SyncedFoodOptionRepository
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedPerformanceRepository.java
      symbol: SyncedPerformanceRepository
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedRatingRepository.java
      symbol: SyncedRatingRepository
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedStageDistanceRepository.java
      symbol: SyncedStageDistanceRepository
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedStageRepository.java
      symbol: SyncedStageRepository
  group: Storage and synchronization
description: Refresh Room from selected sources
---

Wraps band, stage, performance, distance, food, and rating repositories to expose cached reads with source refresh or write-through behavior.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java](../../../../../../infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java) | [infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedBandRepository.java](../../../../../../infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedBandRepository.java) | Writes selected TSV source | Java call |
| [infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java](../../../../../../infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java) | [app/src/main/java/be/wacken/planner/persistence/RoomBandRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomBandRepository.java) | Publishes local cached bands | Java call |
