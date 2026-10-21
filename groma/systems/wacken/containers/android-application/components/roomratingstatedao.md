---
type: C4 Component
title: Room rating state queries
status: stable
groma:
  id: roomratingstatedao
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomRatingStateDao.java
      symbol: RoomRatingStateDao
  group: Rating synchronization
description: Separates pending after-state and established snapshots
---

Shared Room query interface for after-ratings and established band opinions. Exact member/festival/band predicates and operation-matched acknowledgement preserve scopes. Keeping this shared query responsibility separate avoids assigning it exclusively to either reader or writer.
