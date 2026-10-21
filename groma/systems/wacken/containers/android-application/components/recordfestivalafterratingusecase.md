---
type: C4 Component
title: Record festival after-rating
status: stable
groma:
  id: recordfestivalafterratingusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/RecordFestivalAfterRatingUseCase.java
      symbol: RecordFestivalAfterRatingUseCase
  group: Festival rating context
description: Validates and records explicit assessments
---

Checks active festival and lineup membership, validates zero-to-five, then saves an operation-identified assessment with original clock time. Zero persists a reset. No planning or established-opinion write port is supplied.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/RecordFestivalAfterRatingUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/RecordFestivalAfterRatingUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java) | Saves explicit operation | Java call |
| [application/src/main/java/be/wacken/planner/application/RecordFestivalAfterRatingUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/RecordFestivalAfterRatingUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/FestivalAfterRating.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/FestivalAfterRating.java) | Creates dated assessment | Java call |
