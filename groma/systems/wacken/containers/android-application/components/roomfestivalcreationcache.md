---
type: C4 Component
title: Acknowledged creation cache
status: stable
groma:
  id: roomfestivalcreationcache
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/RoomFestivalCreationCache.java
      symbol: RoomFestivalCreationCache
  group: Festival creation and identity
description: Publishes the creation response atomically
---

Consumes the validated creation snapshot supplied by SupabaseFestivalCreationGateway through AppRepositories. Within one WackenDatabase Room transaction it publishes festivals, canonical metadata, lineup, creator-only planning values and acknowledged aliases. Existing pending planning edits for that festival/member are retained. Other members' private history or planning snapshots are not returned or fabricated.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/RoomFestivalCreationCache.java](../../../../../../app/src/main/java/be/wacken/planner/RoomFestivalCreationCache.java) | [app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java) | Commits acknowledged creation cache | Java call |
| [app/src/main/java/be/wacken/planner/RoomFestivalCreationCache.java](../../../../../../app/src/main/java/be/wacken/planner/RoomFestivalCreationCache.java) | [app/src/main/java/be/wacken/planner/persistence/RoomBandAliasRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomBandAliasRepository.java) | Stores acknowledged alias snapshot | Java call |
