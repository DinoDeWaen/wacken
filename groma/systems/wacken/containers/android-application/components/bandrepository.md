---
type: C4 Component
title: Domain repository ports
status: stable
groma:
  id: bandrepository
  parent: android-application
  code:
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/BandRepository.java
      symbol: BandRepository
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/StageRepository.java
      symbol: StageRepository
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/StageDistanceRepository.java
      symbol: StageDistanceRepository
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/PerformanceRepository.java
      symbol: PerformanceRepository
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/FoodOptionRepository.java
      symbol: FoodOptionRepository
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/FestivalRepository.java
      symbol: FestivalRepository
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/FestivalLineupRepository.java
      symbol: FestivalLineupRepository
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/FestivalPlanningRatingRepository.java
      symbol: FestivalPlanningRatingRepository
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingHistoryRepository.java
      symbol: PersonalBandRatingHistoryRepository
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/RatingRepository.java
      symbol: RatingRepository
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/RealRatingRepository.java
      symbol: RealRatingRepository
  group: Domain model
description: Storage contracts for festival planning data
---

Defines the domain-facing access contracts for bands, festivals, lineups, ratings, performances, stages, distances, food, and personal history. Implementations are supplied by Room, Supabase, or file adapters.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [domain/src/main/java/be/wacken/planner/domain/BandRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/BandRepository.java) | [infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java](../../../../../../infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java) | Uses shared source-first writer | Composed Java port dispatch |
