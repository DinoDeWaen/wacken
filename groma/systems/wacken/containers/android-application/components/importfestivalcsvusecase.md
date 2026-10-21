---
type: C4 Component
title: CSV import values and initial band import
status: stable
groma:
  id: importfestivalcsvusecase
  parent: android-application
  code:
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
description: Shares reviewed CSV input/result values and retains the initial band-list API
---

Owns FestivalCsvFiles and ImportFestivalCsvResult, which the active ReviewFestivalCsvUseCase uses, alongside the separate ImportInitialBandListUseCase and its result. The unused full ImportFestivalCsvUseCase was removed in TASK-228. Its deleted source reference was detached through the CLI after scanning retained it. Groma still stores authored relationships to that deleted file; these are known stale claims, pending safe relationship-removal tooling support. Current Android five-file imports use explicit row review and the TSV/Room transaction.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/Band.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/Band.java) | Creates catalog bands | Java call |
