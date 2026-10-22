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

Displays missing fields, provider status, candidate values, clickable source URLs and explicit accepted-metadata results in the paired native layout. A retained screen session owns its existing read-only search worker, result and checked proposals so recreation does not repeat in-flight lookup or discard review. Cancel interrupts unfinished search and preserves saved data; results after cancellation are discarded and retry is explicit. Shared48dp choices, live status and wrapping evidence preserve large-text reachability. Existing search/apply use cases retain missing-field and no-overwrite authority; only Save accepted metadata applies checked proposals.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java) | [application/src/main/java/be/wacken/planner/application/SearchBandMetadataUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/SearchBandMetadataUseCase.java) | Requests metadata proposals | Java call |
| [app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java) | [app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java](../../../../../../app/src/main/java/be/wacken/planner/MusicBrainzMetadataProvider.java) | Queries artist candidates | Java call |
| [app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandMetadataReviewActivity.java) | [app/src/main/java/be/wacken/planner/WackenTheme.java](../../../../../../app/src/main/java/be/wacken/planner/WackenTheme.java) | Styles native review controls | Android Views |
