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
