---
type: C4 Component
title: Local CSV acceptance review
status: stable
groma:
  id: localcsvimportreviewdialog
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java
  group: Local festival import
description: Retains staged row choices
---

Displays ReviewFestivalCsvUseCase before/after/error rows with unchecked defaults and explicit valid-row acceptance. Retained state stores data/work without an Activity; fresh lifecycle callbacks render status and detach safely. Apply invokes the application use case once with selected keys, protects in-flight work and reports local success or retained-data errors. Cancel does not save. Shared sync may later refresh this local cache.

A scrollable body and adaptive list viewport keep long before/after/error rows and wrapping48dp native actions reachable. Accepted keys, in-flight local work and list position retain their existing screen-owned state; status is announced independently of color. Shared dialog restyling never applies changes.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java](../../../../../../app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java) | [application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ReviewFestivalCsvUseCase.java) | Reviews and applies choices | Java call |
| [app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java](../../../../../../app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java) | [app/src/main/java/be/wacken/planner/WackenTheme.java](../../../../../../app/src/main/java/be/wacken/planner/WackenTheme.java) | Styles import review | Android Views |
