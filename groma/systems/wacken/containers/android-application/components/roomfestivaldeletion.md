---
type: C4 Component
title: Festival cache deletion
status: stable
groma:
  id: roomfestivaldeletion
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFestivalDeletion.java
      symbol: RoomFestivalDeletion
  group: Festival deletion
description: Clears acknowledged festival scope
---

Runs after remote deletion acknowledgement. One Room transaction detaches retained personal history with festival name, removes selected festival planning/after state, lineup, performances, locks and festival record, and marks the cache initialized. Canonical bands and established personal band ratings are retained. It does not independently authorize group-wide deletion.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/persistence/RoomFestivalDeletion.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalDeletion.java) | [app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java) | Transacts scoped cache deletion | Java call |
| [app/src/main/java/be/wacken/planner/persistence/RoomFestivalDeletion.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalDeletion.java) | [app/src/main/java/be/wacken/planner/persistence/RoomFestivalCacheMarkerDao.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalCacheMarkerDao.java) | Marks empty cache initialized | Java call |
