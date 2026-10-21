---
type: C4 Component
title: Correct own archived rating
status: stable
groma:
  id: archivedratingcorrectionusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ArchivedRatingCorrectionUseCase.java
      symbol: ArchivedRatingCorrectionUseCase
  group: Festival archive and correction
description: Validates target and reviewed ownership
---

Validates signed-in owner, archived selected festival and lineup. Review response must match member/festival/band/kind; correction validates operation UUID and scale before invoking the gateway. Does not implement server revision/audit/provenance policy in the UI.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/ArchivedRatingCorrectionUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ArchivedRatingCorrectionUseCase.java) | [application/src/main/java/be/wacken/planner/application/ArchivedRatingCorrectionGateway.java](../../../../../../application/src/main/java/be/wacken/planner/application/ArchivedRatingCorrectionGateway.java) | Requests own archived correction | Java call |
