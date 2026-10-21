---
type: C4 Component
title: Metadata review screen
status: stable
groma:
  id: bandmetadatareviewactivity
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java
  group: Android screens
description: Review enrichment proposals
---

Displays missing fields, provider status, candidate values, and save results before applying accepted metadata.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java) | [application/src/main/java/be/wacken/planner/application/SearchBandMetadataUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/SearchBandMetadataUseCase.java) | Requests metadata proposals | Java call |
| [app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java) | [app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java](../../../../../../app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java) | Queries artist candidates | Java call |
