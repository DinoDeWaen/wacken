---
type: C4 Component
title: Shared festival deletion adapter
status: stable
groma:
  id: supabasefestivaldeletiongateway
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseFestivalDeletionGateway.java
      symbol: SupabaseFestivalDeletionGateway
  group: Festival deletion
description: Orders remote delete before cleanup
---

Implements the deletion use-case Gateway supplied by AppRepositories. Calls SupabaseMasterDataClient.deleteFestival first, then the supplied RoomFestivalDeletion callback. Remote errors leave local state intact; local cleanup failure after acknowledged remote deletion reports shared success with sync recovery needed. SQL V011 owns authorization, transactional scope and preservation of canonical bands/history.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/SupabaseFestivalDeletionGateway.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseFestivalDeletionGateway.java) | [app/src/main/java/be/wacken/planner/persistence/RoomFestivalDeletion.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalDeletion.java) | Clears acknowledged local festival | Composed Java callback |
| [app/src/main/java/be/wacken/planner/SupabaseFestivalDeletionGateway.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseFestivalDeletionGateway.java) | [app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java) | Deletes shared festival first | Java call |
