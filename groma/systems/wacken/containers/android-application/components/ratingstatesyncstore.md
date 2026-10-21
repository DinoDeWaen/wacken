---
type: C4 Component
title: Rating sync cache port
status: stable
groma:
  id: ratingstatesyncstore
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/RatingStateSyncStore.java
      symbol: RatingStateSyncStore
  group: Rating synchronization
description: Preserves pending operations during merge
---

Extends the after-rating domain port with pending enumeration, exact operation acknowledgement, own after-state merge and established-opinion replacement. RoomFestivalAfterRatingRepository implements this adapter-facing protocol.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/RatingStateSyncStore.java](../../../../../../app/src/main/java/be/wacken/planner/RatingStateSyncStore.java) | [app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java) | Uses composed sync cache | Composed Java port dispatch |
