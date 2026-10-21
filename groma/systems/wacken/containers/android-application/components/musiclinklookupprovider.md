---
type: C4 Component
title: Music identity evidence contract
status: stable
groma:
  id: musiclinklookupprovider
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/MusicLinkLookupProvider.java
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/MusicLinkEvidence.java
  group: Verified music discovery
description: Returns facts for application verification
---

Lookup port and immutable MusicLinkEvidence/Page values form one protocol. Candidates contain artist identifiers, corroborating source facts, declared websites and extracted platform links with retry/messages; these facts are not a verification decision. The application verifies them, distinct from general ranked BandMetadataProviderCandidate proposals.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/MusicLinkLookupProvider.java](../../../../../../application/src/main/java/be/wacken/planner/application/MusicLinkLookupProvider.java) | [app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java](../../../../../../app/src/main/java/be/wacken/planner/VerifiedMusicLinksProvider.java) | Uses composed evidence collector | Composed Java port dispatch |
