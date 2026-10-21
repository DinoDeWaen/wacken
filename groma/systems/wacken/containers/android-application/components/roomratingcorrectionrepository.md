---
type: C4 Component
title: Room correction audit cache
status: stable
groma:
  id: roomratingcorrectionrepository
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrectionRepository.java
      symbol: RoomRatingCorrectionRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrection.java
      symbol: RoomRatingCorrection
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrectionDao.java
      symbol: RoomRatingCorrectionDao
  group: Festival archive and correction
description: Stores acknowledged immutable audits
---

Repository adapter, RoomRatingCorrection row and DAO form one audit cache responsibility. Maps full provenance and original/correction timestamps; appends by operation identity and reads owner/target histories. Separate from mutable after-state and current established snapshot tables.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrectionRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomRatingCorrectionRepository.java) | [domain/src/main/java/be/wacken/planner/domain/RatingCorrection.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/RatingCorrection.java) | Maps immutable audit | Java call |
