---
type: C4 Component
title: Wikidata provider
status: stable
groma:
  id: wikidatametadataprovider
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/WikidataMetadataProvider.java
  technology: Wikidata HTTP API
  group: Metadata enrichment
description: Propose structured artist metadata
---

Reads item claims to propose image, Spotify, and YouTube values for user review.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/WikidataMetadataProvider.java](../../../../../../app/src/main/java/be/wacken/planner/WikidataMetadataProvider.java) | [wikidata](../../../../../externals/wikidata.md) | Reads structured claims | HTTPS |
