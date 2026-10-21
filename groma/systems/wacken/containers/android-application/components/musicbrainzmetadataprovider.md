---
type: C4 Component
title: MusicBrainz provider
status: stable
groma:
  id: musicbrainzmetadataprovider
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java
  technology: MusicBrainz HTTP API
  group: Metadata enrichment
description: Propose artist identity and music links
---

Searches artist candidates and URL relationships to propose canonical identity, Spotify, or YouTube links for review.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java](../../../../../../app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java) | [musicbrainz](../../../../../externals/musicbrainz.md) | Searches artist metadata | HTTPS |
