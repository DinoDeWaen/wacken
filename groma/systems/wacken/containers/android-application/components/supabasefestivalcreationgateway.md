---
type: C4 Component
title: Shared festival creation adapter
status: stable
groma:
  id: supabasefestivalcreationgateway
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java
  group: Festival creation and identity
description: Confirms atomic reviewed creation
---

Implements FestivalCreationGateway. Sends a deterministic request identity and reviewed canonical/alias choices to create_festival_from_reviewed_bands through SupabaseMasterDataClient; exact legacy creation remains supported. Validates acknowledged member/group, festival, lineup and aliases before delivering the snapshot to the RoomFestivalCreationCache callback bound by AppRepositories. Server creation is atomic under SQL V018 and its existing V015 creation transaction; each member is seeded once and new after-ratings stay empty. A remote success followed by cache failure is reported as shared-saved/local-refresh-needed, never rolled back or falsely called wholly failed. Retry uses the same name, CSV and choices; stale identities require review. SQL/RLS semantics are authored evidence, not Java scanner inference.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java) | [app/src/main/java/be/wacken/planner/RoomFestivalCreationCache.java](../../../../../../app/src/main/java/be/wacken/planner/RoomFestivalCreationCache.java) | Publishes acknowledged creation | Composed Java callback |
| [app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseFestivalCreationGateway.java) | [app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseMasterDataClient.java) | Submits atomic reviewed creation | Java call |
