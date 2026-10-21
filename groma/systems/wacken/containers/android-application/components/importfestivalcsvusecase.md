---
type: C4 Component
title: Festival CSV import
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
description: Validate and import festival data
---

Accepts selected CSV files for bands, stages, performances, distances, and food; validates the records and stores them through repository ports.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/Band.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/Band.java) | Creates catalog bands | Java call |
