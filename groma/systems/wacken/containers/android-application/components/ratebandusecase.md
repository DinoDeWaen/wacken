---
type: C4 Component
title: Rating use cases
status: stable
groma:
  id: ratebandusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/RateBandUseCase.java
      symbol: RateBandUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/RateBandResult.java
      symbol: RateBandResult
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/RateRealBandUseCase.java
      symbol: RateRealBandUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/RecordPersonalBandRatingUseCase.java
      symbol: RecordPersonalBandRatingUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ActiveFestivalRatingRepository.java
      symbol: ActiveFestivalRatingRepository
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/EffectiveRating.java
      symbol: EffectiveRating
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/EffectiveRatingResolver.java
      symbol: EffectiveRatingResolver
  group: Application workflows
description: Save planning and post-show ratings
---

Orchestrates planning rating changes, effective group rating lookup, and separate real post-show personal rating history.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/RateBandUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/RateBandUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/Rating.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/Rating.java) | Validates rating value | Java call |
