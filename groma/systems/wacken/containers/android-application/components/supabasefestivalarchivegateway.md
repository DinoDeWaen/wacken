---
type: C4 Component
title: Authenticated festival archive gateway
status: stable
groma:
  id: supabasefestivalarchivegateway
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java
  group: Festival archive and correction
description: Validates archive before cache publication
---

Invokes archive_festival via the composed authenticated request callback, validates selected festival and operation acknowledgement, then fetches complete own rating snapshots before invoking the Room cache callback. SQL V016 atomically archives and promotes each member nonempty after-state; missing/reset retains opinions and replay preserves later state. Remote commit followed by local failure is explicitly recoverable.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java) | [app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java) | Requests atomic archive | Composed Java request callback |
| [app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java) | [app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java) | Fetches acknowledged own snapshots | Composed Java remote dispatch |
| [app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseFestivalArchiveGateway.java) | [app/src/main/java/be/wacken/planner/RoomFestivalArchiveCache.java](../../../../../../app/src/main/java/be/wacken/planner/RoomFestivalArchiveCache.java) | Publishes archive snapshot | Composed Java Consumer |
