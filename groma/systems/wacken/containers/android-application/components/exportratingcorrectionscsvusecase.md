---
type: C4 Component
title: Own correction audit CSV export
status: stable
groma:
  id: exportratingcorrectionscsvusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ExportRatingCorrectionsCsvUseCase.java
      symbol: ExportRatingCorrectionsCsvUseCase
  group: Pending rating recovery and export
description: Exports original and correction dates
---

Separate audit-only contract reads the signed-in member corrections and emits stable operation/target/kind/old/new and original/correction timestamps. Retains festival-name snapshots after deletion; does not replace either legacy ratings CSV or lifecycle version2 export.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/ExportRatingCorrectionsCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ExportRatingCorrectionsCsvUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/RatingCorrectionRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/RatingCorrectionRepository.java) | Exports audit contract | Java call |
