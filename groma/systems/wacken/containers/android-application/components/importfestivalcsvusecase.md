---
type: C4 Component
title: Legacy CSV import workflows
status: stable
groma:
  id: importfestivalcsvusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ImportFestivalCsvResult.java
      symbol: ImportFestivalCsvResult
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ImportInitialBandListUseCase.java
      symbol: ImportInitialBandListUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ImportInitialBandListResult.java
      symbol: ImportInitialBandListResult
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/FestivalCsvFiles.java
      symbol: FestivalCsvFiles
  group: Application workflows
description: Retains direct CSV import APIs
---

Contains the legacy application CSV import and initial-band-list APIs plus their result/input values. These validate CSV and publish through repository ports for direct callers. The current Android five-file workflow instead invokes ReviewFestivalCsvUseCase through LocalCsvImportReviewDialog, with explicit row acceptance and rollback protection; the old import-screen relationship is a verified stale map claim awaiting explicit safe removal support.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/Band.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/Band.java) | Creates catalog bands | Java call |
