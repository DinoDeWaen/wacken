---
type: C4 Component
title: Metadata proposal workflow
status: stable
groma:
  id: searchbandmetadatausecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/SearchBandMetadataUseCase.java
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BandMetadataField.java
      symbol: BandMetadataField
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BandMetadataLookupProvider.java
      symbol: BandMetadataLookupProvider
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BandMetadataLookupException.java
      symbol: BandMetadataLookupException
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BandMetadataProposal.java
      symbol: BandMetadataProposal
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BandMetadataProviderCandidate.java
      symbol: BandMetadataProviderCandidate
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BandMetadataSearchResult.java
      symbol: BandMetadataSearchResult
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BandMetadataSearchRun.java
      symbol: BandMetadataSearchRun
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/EnrichBandMetadataFromCatalogUseCase.java
      symbol: EnrichBandMetadataFromCatalogUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BandMetadataEnrichmentResult.java
      symbol: BandMetadataEnrichmentResult
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ApplyBandMetadataProposalsUseCase.java
      symbol: ApplyBandMetadataProposalsUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ApplyBandMetadataProposalsResult.java
      symbol: ApplyBandMetadataProposalsResult
  group: Metadata enrichment
description: Find and apply reviewed metadata
---

Finds missing band fields, proposes own-catalog and provider values, and applies only accepted values without overwriting populated metadata.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/ApplyBandMetadataProposalsUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ApplyBandMetadataProposalsUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/BandRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/BandRepository.java) | Saves canonical missing fields | Java call |
