---
type: C4 Component
title: Festival cache initialization marker
status: stable
groma:
  id: roomfestivalcachemarker
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFestivalCacheMarker.java
      symbol: RoomFestivalCacheMarker
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomFestivalCacheMarkerDao.java
      symbol: RoomFestivalCacheMarkerDao
description: Prevents deleted festival reseeding
---

Owns the initialization-marker Room entity and DAO. RoomFestivalRepository and acknowledged deletion mark initialization so an intentionally empty/deleted festival cache is not mistaken for first launch and reseeded. Count/insert-ignore are storage mechanics, not a second festival or runtime.
