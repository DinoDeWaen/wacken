---
type: C4 Component
title: Room archive publication
status: stable
groma:
  id: roomfestivalarchivecache
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/RoomFestivalArchiveCache.java
      symbol: RoomFestivalArchiveCache
  group: Festival archive and correction
description: Publishes acknowledged archive atomically
---

Consumer supplied by AppRepositories to the archive gateway. Rechecks pending work inside the Room transaction, merges own after-state and established snapshot, and saves the acknowledged archived festival together. Newly pending edits prevent local overwrite; remote success is retained as a recovery outcome.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/RoomFestivalArchiveCache.java](../../../../../../app/src/main/java/be/wacken/planner/RoomFestivalArchiveCache.java) | [app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java) | Publishes inside transaction | Java call |
| [app/src/main/java/be/wacken/planner/RoomFestivalArchiveCache.java](../../../../../../app/src/main/java/be/wacken/planner/RoomFestivalArchiveCache.java) | [app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java) | Merges acknowledged rating state | Java call |
