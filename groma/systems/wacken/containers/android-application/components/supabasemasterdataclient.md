---
type: C4 Component
title: Supabase master data adapters
status: stable
groma:
  id: supabasemasterdataclient
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java
      symbol: SupabaseMasterDataClient
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseBandRepository.java
      symbol: SupabaseBandRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseStageRepository.java
      symbol: SupabaseStageRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabasePerformanceRepository.java
      symbol: SupabasePerformanceRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseStageDistanceRepository.java
      symbol: SupabaseStageDistanceRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseFoodOptionRepository.java
      symbol: SupabaseFoodOptionRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseFestivalRepository.java
      symbol: SupabaseFestivalRepository
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseFestivalLineupRepository.java
      symbol: SupabaseFestivalLineupRepository
  group: Storage and synchronization
description: Read and write shared festival records
---

Uses Supabase PostgREST for band, stage, performance, distance, food, festival, and lineup records.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java) | [supabase](../../../../../externals/supabase.md) | Syncs festival records | HTTPS/PostgREST |
| [app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java) | [app/src/main/java/be/wacken/planner/SupabaseAuthenticatedRequest.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseAuthenticatedRequest.java) | Sends authenticated requests | Java call |
