---
type: C4 Component
title: Android composition root
status: stable
groma:
  id: apprepositories
  parent: wacken
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/AppRepositories.java
    - scanner: java
      file: app/src/main/java/be/wacken/planner/PendingSyncSummary.java
      symbol: PendingSyncSummary
    - scanner: java
      file: app/src/main/java/be/wacken/planner/LifecycleSyncDecision.java
      symbol: LifecycleSyncDecision
  group: Storage and synchronization
description: Wire repositories, cache, and sync
---

Constructs Room adapters and chooses Supabase or explicit TSV fallback sources; exposes repositories and pending-sync state to Android entry points.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/AppRepositories.java](../../../../app/src/main/java/be/wacken/planner/AppRepositories.java) | [app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java](../../../../app/src/main/java/be/wacken/planner/persistence/WackenDatabase.java) | Opens local cache | Room |
| [app/src/main/java/be/wacken/planner/AppRepositories.java](../../../../app/src/main/java/be/wacken/planner/AppRepositories.java) | [app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java](../../../../app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java) | Wires remote catalog | Java call |
| [app/src/main/java/be/wacken/planner/AppRepositories.java](../../../../app/src/main/java/be/wacken/planner/AppRepositories.java) | [infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java](../../../../infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java) | Wires cached sources | Java call |
| [app/src/main/java/be/wacken/planner/AppRepositories.java](../../../../app/src/main/java/be/wacken/planner/AppRepositories.java) | [infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedBandRepository.java](../../../../infrastructure/src/main/java/be/wacken/planner/infrastructure/FileBackedBandRepository.java) | Selects TSV fallback | Java call |
| [app/src/main/java/be/wacken/planner/AppRepositories.java](../../../../app/src/main/java/be/wacken/planner/AppRepositories.java) | [app/src/main/java/be/wacken/planner/SyncingFestivalRepository.java](../../../../app/src/main/java/be/wacken/planner/SyncingFestivalRepository.java) | Wires change sync | Java call |
