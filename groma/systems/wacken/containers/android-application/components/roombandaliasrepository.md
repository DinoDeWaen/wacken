---
type: C4 Component
title: Confirmed alias cache
status: stable
groma:
  id: roombandaliasrepository
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomBandAliasRepository.java
      symbol: RoomBandAliasRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomBandAlias.java
      symbol: RoomBandAlias
    - scanner: java
      file: app/src/main/java/be/wacken/planner/persistence/RoomBandAliasDao.java
      symbol: RoomBandAliasDao
  group: Festival creation and identity
description: Publishes confirmed alias snapshots
---

Owns Room alias row, DAO and mapping behind one local persistence responsibility. Reads aliases with canonical identity and member/time provenance; replaceAll clears and saves the complete acknowledged snapshot in one Room transaction. SupabaseFestivalBandCatalog publishes fresh authenticated aliases through the callback bound in AppRepositories. It does not confirm or invent an alias.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/persistence/RoomBandAliasRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomBandAliasRepository.java) | [app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java) | Transacts confirmed alias cache | Java call |
| [app/src/main/java/be/wacken/planner/persistence/RoomBandAliasRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomBandAliasRepository.java) | [domain/src/main/java/be/wacken/planner/domain/ConfirmedBandAlias.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/ConfirmedBandAlias.java) | Restores confirmed alias provenance | Java call |
