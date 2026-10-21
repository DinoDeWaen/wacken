---
type: C4 Component
title: YouTube provider
status: stable
groma:
  id: youtubemetadataprovider
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/YouTubeMetadataProvider.java
  technology: YouTube Data API
  group: Metadata enrichment
description: Propose channel links
---

When an API key is configured, searches for artist channels and proposes links for review.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/YouTubeMetadataProvider.java](../../../../../../app/src/main/java/be/wacken/planner/YouTubeMetadataProvider.java) | [youtube](../../../../../externals/youtube.md) | Searches artist channels | HTTPS |
