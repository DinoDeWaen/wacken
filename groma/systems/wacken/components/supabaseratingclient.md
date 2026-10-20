---
type: C4 Component
title: Supabase shared state adapters
status: stable
groma:
  id: supabaseratingclient
  parent: wacken
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseRatingClient.java
      symbol: SupabaseRatingClient
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseRatingRemote.java
      symbol: SupabaseRatingRemote
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingClient.java
      symbol: SupabaseFestivalPlanningRatingClient
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseFestivalPlanningRatingRemote.java
      symbol: SupabaseFestivalPlanningRatingRemote
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingClient.java
      symbol: SupabasePersonalBandRatingClient
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabasePersonalBandRatingRemote.java
      symbol: SupabasePersonalBandRatingRemote
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseScheduleLockClient.java
      symbol: SupabaseScheduleLockClient
  group: Storage and synchronization
description: Sync ratings and schedule choices
---

Uses authenticated Supabase endpoints for group planning ratings, personal rating history, and locked schedule winners.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/SupabaseRatingClient.java](../../../../app/src/main/java/be/wacken/planner/SupabaseRatingClient.java) | [supabase](../../../externals/supabase.md) | Syncs group ratings | HTTPS/PostgREST |
