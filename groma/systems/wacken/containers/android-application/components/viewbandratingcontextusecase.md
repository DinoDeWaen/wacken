---
type: C4 Component
title: Selected festival rating context
status: stable
groma:
  id: viewbandratingcontextusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java
      symbol: ViewBandRatingContextUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BandRatingContext.java
      symbol: BandRatingContext
  group: Festival rating context
description: Separates established and festival values
---

Returns BandRatingContext containing the signed-in member opinion, selected festival planning and after-rating, and retained source provenance. Validates membership in that lineup before reading festival values; wrong user/festival/band results are filtered. Missing selected context stays empty, with no cross-festival fallback.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingRepository.java) | Reads established opinion | Java call |
| [application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java) | Reads selected after-state | Java call |
| [application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/FestivalPlanningRatingRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/FestivalPlanningRatingRepository.java) | Reads selected planning | Java call |
| [application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/Band.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/Band.java) | Resolves canonical band | Java call |
