---
type: C4 Component
title: Verify and approve missing music links
status: stable
groma:
  id: findbandmusiclinksusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/FindBandMusicLinksUseCase.java
  group: Verified music discovery
description: Owns identity verification and approval
---

Application workflow selects missing fields within one band or festival lineup, retains completed searches and retries unfinished work. Verifies matching MusicBrainz/Wikidata artist identifiers, Group identity/name or aliases, jointly declared official site and unique linked platform profile. Ranking or caller-supplied checkbox alone is insufficient. Approval rechecks raw evidence/current band, refuses ambiguous/conflicting identities and delegates only missing YouTube/Spotify fields to existing metadata proposal application; no rating or lineup write port is supplied.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/FindBandMusicLinksUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/FindBandMusicLinksUseCase.java) | [application/src/main/java/be/wacken/planner/application/MusicLinkLookupProvider.java](../../../../../../application/src/main/java/be/wacken/planner/application/MusicLinkLookupProvider.java) | Requests raw identity evidence | Java call |
| [application/src/main/java/be/wacken/planner/application/FindBandMusicLinksUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/FindBandMusicLinksUseCase.java) | [application/src/main/java/be/wacken/planner/application/ApplyBandMetadataProposalsUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ApplyBandMetadataProposalsUseCase.java) | Applies approved missing links | Java call |
