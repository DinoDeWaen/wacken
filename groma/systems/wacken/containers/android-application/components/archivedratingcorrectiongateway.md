---
type: C4 Component
title: Archived correction review contract
status: stable
groma:
  id: archivedratingcorrectiongateway
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ArchivedRatingCorrectionGateway.java
      symbol: ArchivedRatingCorrectionGateway
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/ArchivedRatingKind.java
      symbol: ArchivedRatingKind
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/ArchivedRatingReview.java
      symbol: ArchivedRatingReview
  group: Festival archive and correction
description: Carries reviewed target and revision
---

Port with ArchivedRatingKind and immutable ArchivedRatingReview values: these are one correction protocol, not separate runtime components. Review/correct/history preserve exact owner and target, expected revision and original recorded date. Adapter/server enforce atomic audit and current-source protection.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/ArchivedRatingCorrectionGateway.java](../../../../../../application/src/main/java/be/wacken/planner/application/ArchivedRatingCorrectionGateway.java) | [app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java](../../../../../../app/src/main/java/be/wacken/planner/SupabaseArchivedRatingCorrectionGateway.java) | Uses configured remote gateway | Composed Java port dispatch |
