---
type: C4 Component
title: Recover orphaned pending planning rating
status: stable
groma:
  id: recoverpendingplanningratingusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/RecoverPendingPlanningRatingUseCase.java
  group: Pending rating recovery and export
description: Requires explicit safe recovery
---

Moves only a reviewed own pending fact whose original festival is missing to active Wacken2026, with local and remote no-overwrite checks and exact stale-state protection. Explicit discard similarly validates owner and unchanged pending fact. Otherwise retains work; does not automatically reassign festivals or change lifecycle rules.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/RecoverPendingPlanningRatingUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/RecoverPendingPlanningRatingUseCase.java) | [app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalPlanningRatingRepository.java) | Checks and moves pending fact | Composed Java LocalStore dispatch |
| [application/src/main/java/be/wacken/planner/application/RecoverPendingPlanningRatingUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/RecoverPendingPlanningRatingUseCase.java) | [app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java) | Checks remote no-overwrite | Composed Java RemoteLookup callback |
