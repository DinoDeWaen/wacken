---
type: C4 Component
title: TSV fallback adapters
status: stable
groma:
  id: filebackedstorage
  parent: android-application
  code:
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedStorage.java
      symbol: FileBackedStorage
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedBandRepository.java
      symbol: FileBackedBandRepository
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedFoodOptionRepository.java
      symbol: FileBackedFoodOptionRepository
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedPerformanceRepository.java
      symbol: FileBackedPerformanceRepository
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedRatingRepository.java
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedStageDistanceRepository.java
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedStageRepository.java
      symbol: FileBackedStageRepository
    - scanner: java
      file: infrastructure/src/main/java/be/wacken/planner/infrastructure/InfrastructureBoundary.java
      symbol: InfrastructureBoundary
  technology: Java NIO / TSV
  group: Storage and synchronization
description: Persist explicit fallback festival data
---

Reads and writes app-private TSV data for the explicit fallback import/source mode; this path is separate from the default Supabase source.
