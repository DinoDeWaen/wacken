---
type: C4 Component
title: Own rating lifecycle CSV export
status: stable
groma:
  id: exportratinglifecyclecsvusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java
      symbol: ExportRatingLifecycleCsvUseCase
  group: Pending rating recovery and export
description: Exports separate rating roles
---

Version2 own cached contract includes established provenance, exact festival planning/after-state, dated history and immutable corrections as distinct record types. Unknown dates remain blank, missing differs from zero and deleted festival provenance survives. Sorted/escaped rows do not promote latest events; legacy CSV remains a separate Settings action.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingRepository.java) | Exports established provenance | Java call |
| [application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java) | Exports missing and reset state | Java call |
| [application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/FestivalPlanningRatingRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/FestivalPlanningRatingRepository.java) | Exports festival planning | Java call |
| [application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/RatingCorrectionRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/RatingCorrectionRepository.java) | Exports dated corrections | Java call |
