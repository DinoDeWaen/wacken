---
type: C4 Component
title: Spotify provider
status: stable
groma:
  id: spotifymetadataprovider
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SpotifyMetadataProvider.java
  technology: Spotify Web API
  group: Metadata enrichment
description: Propose artist profile and image
---

When credentials are configured, searches the Spotify catalog for artist profile and image candidates for review.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/SpotifyMetadataProvider.java](../../../../../../app/src/main/java/be/wacken/planner/SpotifyMetadataProvider.java) | [spotify](../../../../../externals/spotify.md) | Searches artist catalog | HTTPS |
