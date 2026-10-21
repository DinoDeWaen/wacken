---
type: C4 Component
title: Festival schedule model
status: stable
groma:
  id: performance
  parent: android-application
  code:
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/Performance.java
      symbol: Performance
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/Stage.java
      symbol: Stage
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/StageDistance.java
      symbol: StageDistance
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/FoodOption.java
      symbol: FoodOption
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/ConflictDistanceContext.java
      symbol: ConflictDistanceContext
  group: Domain model
description: Performances, stages, distances, and food
---

Represents scheduled band appearances and stage context used by lineup and schedule workflows.
