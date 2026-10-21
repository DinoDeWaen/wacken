---
type: C4 Component
title: Reviewed archived correction dialog
status: stable
groma:
  id: archivedratingcorrectiondialog
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java
  group: Festival archive and correction
description: Confirms own reviewed edits
---

ArchivedBandDetailActivity opens this dialog with a supplied correction use case. Loads member/festival/band/kind and revision; stages locally then explicitly confirms planning zero-to-five or after one-to-five. Stable payload-derived operation ID supports uncertain-result retry, stale review requires reload, and lifecycle generation guards ignore obsolete UI completions.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java) | [application/src/main/java/be/wacken/planner/application/ArchivedRatingCorrectionUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ArchivedRatingCorrectionUseCase.java) | Reviews and confirms correction | Java call |
| [app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java) | [domain/src/main/java/be/wacken/planner/domain/ArchivedRatingReview.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/ArchivedRatingReview.java) | Uses reviewed target revision | Java call |
