---
type: C4 Component
title: Festival-scoped performance view
status: stable
groma:
  id: festivalscopedperformancerepository
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/FestivalScopedPerformanceRepository.java
      symbol: FestivalScopedPerformanceRepository
description: Guards selected festival access
---

Wraps a caller-selected PerformanceRepository with a festival ID supplied by AppRepositories. Reads only that festival, rejects writes for another festival and refuses global replaceAll. Explicit master-data import uses the unscoped target. This adapter preserves active/archive scope without copying or deleting another festival's performances.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/FestivalScopedPerformanceRepository.java](../../../../../../app/src/main/java/be/wacken/planner/FestivalScopedPerformanceRepository.java) | [infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedPerformanceRepository.java](../../../../../../infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedPerformanceRepository.java) | Filters configured performance source | Java interface dispatch |
