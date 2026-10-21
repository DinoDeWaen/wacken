---
type: C4 Component
title: Authenticated correction gateway
status: stable
groma:
  id: supabasearchivedratingcorrectiongateway
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java
  group: Festival archive and correction
description: Reviews and commits audited corrections
---

Uniquely resolves canonical band, checks authenticated owner, captures pending state, submits reviewed revision and stable operation to correct_archived_rating, validates receipt, then reloads current target and private snapshots before cache publication. SQL V017 commits immutable audit with eligible established update only while this festival remains current source; planning and older-source corrections cannot promote. Paged audit refresh checks every owner.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java) | [app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java) | Runs reviewed correction RPC | Composed Java request callback |
| [app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java) | [app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java) | Fetches own post-correction state | Composed Java remote dispatch |
| [app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java) | [app/src/main/java/be/wacken/planner/RoomArchivedRatingCorrectionCache.java](../../../../../../app/src/main/java/be/wacken/planner/RoomArchivedRatingCorrectionCache.java) | Publishes acknowledged correction | Composed Java cache dispatch |
