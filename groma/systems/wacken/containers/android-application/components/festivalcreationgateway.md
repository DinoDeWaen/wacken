---
type: C4 Component
title: Atomic festival creation port
status: stable
groma:
  id: festivalcreationgateway
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/FestivalCreationGateway.java
      symbol: FestivalCreationGateway
  group: Festival creation and identity
description: Commits complete group creation
---

Application write port for atomic group-wide creation. The transaction checks authoritative active-festival state, reuses reviewed canonical identities and copies each existing member's established band rating once into a new planning snapshot. Missing opinions stay unrated and after-ratings start empty. Only the creator's planning snapshot is returned. Retry confirms the same creation without resetting edits. SupabaseFestivalCreationGateway is supplied by Android composition.
