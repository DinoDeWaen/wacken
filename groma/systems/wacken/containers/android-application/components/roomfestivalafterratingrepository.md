---
type: C4 Component
title: Room festival after-rating cache
status: stable
groma:
  id: roomfestivalafterratingrepository
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java
      symbol: RoomFestivalAfterRatingRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRating.java
      symbol: RoomFestivalAfterRating
  group: Rating synchronization
description: Persists pending assessments and durable resets
---

Adapter and RoomFestivalAfterRating row form one cache responsibility. save writes PENDING after-state and a dated history fact only for nonzero values in a Room transaction. Merge replaces own synced state while retaining newer pending rows; acknowledgement matches operation identity. Established snapshots are replaced only for the authenticated owner.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java) | [app/src/main/java/be/wacken/planner/persistence/RoomRatingStateDao.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomRatingStateDao.java) | Stores and merges assessments | Java call |
| [app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java) | [domain/src/main/java/be/wacken/planner/domain/FestivalAfterRating.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/FestivalAfterRating.java) | Maps scoped operation | Java call |
