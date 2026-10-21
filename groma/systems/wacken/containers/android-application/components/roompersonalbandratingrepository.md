---
type: C4 Component
title: Room established opinion reader
status: stable
groma:
  id: roompersonalbandratingrepository
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingRepository.java
      symbol: RoomPersonalBandRatingRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRating.java
      symbol: RoomPersonalBandRating
  group: Rating synchronization
description: Maps retained established provenance
---

Read-only adapter and RoomPersonalBandRating row expose current own opinion and its source festival/name/event/date. Uses shared RoomRatingStateDao; normal application rating edits do not write established opinions.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingRepository.java) | [app/src/main/java/be/wacken/planner/persistence/RoomRatingStateDao.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomRatingStateDao.java) | Reads established rows | Java call |
| [app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomPersonalBandRatingRepository.java) | [domain/src/main/java/be/wacken/planner/domain/PersonalBandRating.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/PersonalBandRating.java) | Maps retained provenance | Java call |
