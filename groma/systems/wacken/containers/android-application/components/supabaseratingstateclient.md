---
type: C4 Component
title: Authenticated rating state client
status: stable
groma:
  id: supabaseratingstateclient
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java
      symbol: SupabaseRatingStateClient
  group: Rating synchronization
description: Saves operations and reads own state
---

Implements RatingStateSync.Remote through authenticated SupabaseMasterDataClient requests. Uniquely resolves canonical band ID, invokes save_festival_after_rating with operation/date/value, validates confirmation, and pages complete owner-scoped snapshots. Another member or malformed snapshot fails before publication; archived edits remain pending for explicit correction.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java) | [app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java) | Runs authenticated requests | Composed Java request callback |
| [app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java) | [domain/src/main/java/be/wacken/planner/domain/FestivalAfterRating.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/FestivalAfterRating.java) | Decodes own after-state | Java call |
| [app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseRatingStateClient.java) | [domain/src/main/java/be/wacken/planner/domain/PersonalBandRating.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/PersonalBandRating.java) | Decodes established provenance | Java call |
