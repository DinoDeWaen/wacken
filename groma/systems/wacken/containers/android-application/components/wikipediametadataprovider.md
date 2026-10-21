---
type: C4 Component
title: Wikipedia provider
status: stable
groma:
  id: wikipediametadataprovider
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/WikipediaMetadataProvider.java
  technology: Wikipedia HTTP API
  group: Metadata enrichment
description: Propose biography and image metadata
---

Uses English Wikipedia summaries to propose neutral biography text and available image metadata for review.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/WikipediaMetadataProvider.java](../../../../../../app/src/main/java/be/wacken/planner/WikipediaMetadataProvider.java) | [wikipedia](../../../../../externals/wikipedia.md) | Reads page summaries | HTTPS |
