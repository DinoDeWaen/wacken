---
type: C4 Component
title: Remote identity review catalog
status: stable
groma:
  id: supabasefestivalbandcatalog
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseFestivalBandCatalog.java
      symbol: SupabaseFestivalBandCatalog
  group: Festival creation and identity
description: Fetches fresh canonical review data
---

Implements FestivalBandCatalog through authenticated paged bands/alias reads using SupabaseMasterDataClient. Builds canonical identity values and replaces the Room alias cache only after a complete successful alias fetch via the callback supplied by AppRepositories. Errors retain prior cached aliases and require retry; cached suggestions are not silent approval.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/SupabaseFestivalBandCatalog.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseFestivalBandCatalog.java) | [app/src/main/java/be/wacken/planner/persistence/RoomBandAliasRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomBandAliasRepository.java) | Publishes confirmed alias cache | Composed Java callback |
| [app/src/main/java/be/wacken/planner/SupabaseFestivalBandCatalog.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseFestivalBandCatalog.java) | [app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java) | Fetches canonical identity snapshots | Java call |
| [app/src/main/java/be/wacken/planner/SupabaseFestivalBandCatalog.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseFestivalBandCatalog.java) | [domain/src/main/java/be/wacken/planner/domain/CanonicalBand.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/CanonicalBand.java) | Builds stable catalog identities | Java call |
