---
type: C4 Component
title: Correction audit contract
status: stable
groma:
  id: ratingcorrectionrepository
  parent: android-application
  code:
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/RatingCorrectionRepository.java
      symbol: RatingCorrectionRepository
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/RatingCorrection.java
      symbol: RatingCorrection
  group: Festival archive and correction
description: Retains immutable change history
---

Repository port and RatingCorrection value expose own audit by user or exact festival/band. Stable operation, old/new values, reviewed revision, original recorded time and correction time are distinct. History survives festival deletion; it is not a source for latest-event band opinion promotion.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [domain/src/main/java/be/wacken/planner/domain/RatingCorrectionRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/RatingCorrectionRepository.java) | [app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrectionRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrectionRepository.java) | Uses composed audit reader | Composed Java port dispatch |
