---
type: C4 Component
title: Local five-file import review
status: stable
groma:
  id: reviewfestivalcsvusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java
  group: Local festival import
description: Validates selected local changes
---

Builds before/after/error rows for five optional CSV inputs against an active-festival snapshot. Accepted keys are explicit and initially unchecked; missing companion files retain their baseline. Before publish it validates combined references/overlaps and rechecks stale data inside the supplied transaction. It retains canonical catalog metadata, other festivals, ratings/history and pending work; removing a lineup entry does not delete a canonical band. AppRepositories supplies TSV fallback decorators and TsvImportTransaction with Room callback. This is local master-data review, not a shared-source import API.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java) | [application/src/main/java/be/wacken/planner/application/FestivalCsvReader.java](../../../../../../application/src/main/java/be/wacken/planner/application/FestivalCsvReader.java) | Parses selected CSV rows | Java call |
| [application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java) | [infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java](../../../../../../infrastructure/src/main/java/be/wacken/planner/infrastructure/SyncedBandRepository.java) | Publishes accepted master data | Java call |
| [application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java) | [infrastructure/src/main/java/be/wacken/planner/infrastructure/TsvImportTransaction.java](../../../../../../infrastructure/src/main/java/be/wacken/planner/infrastructure/TsvImportTransaction.java) | Runs supplied local transaction | Composed Java callback |
| [application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java) | [application/src/main/java/be/wacken/planner/application/ImportFestivalCsvResult.java](../../../../../../application/src/main/java/be/wacken/planner/application/ImportFestivalCsvResult.java) | Reads shared CSV values | Java call |
