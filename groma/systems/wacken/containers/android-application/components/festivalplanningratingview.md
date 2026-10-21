---
type: C4 Component
title: Selected festival planning view
status: stable
groma:
  id: festivalplanningratingview
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/FestivalPlanningRatingView.java
      symbol: FestivalPlanningRatingView
  group: Festival rating context
description: Keeps detail edits festival-bound
---

Implements the planning compatibility port for a fixed selected festival. Reads only that festival and validates it remains active and the band remains in its lineup before saving. Uses the composed planning repository, retaining pending offline edits.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/FestivalPlanningRatingView.java](../../../../../../application/src/main/java/be/wacken/planner/application/FestivalPlanningRatingView.java) | [domain/src/main/java/be/wacken/planner/domain/FestivalPlanningRatingRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/FestivalPlanningRatingRepository.java) | Reads and saves festival planning | Java call |
