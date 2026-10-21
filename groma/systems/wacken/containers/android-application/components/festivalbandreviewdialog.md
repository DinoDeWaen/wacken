---
type: C4 Component
title: Festival identity confirmation
status: stable
groma:
  id: festivalbandreviewdialog
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/FestivalBandReviewDialog.java
  group: Festival creation and identity
description: Stages explicit band choices
---

Presents proposals from ReviewFestivalBandsUseCase. Exact canonical names retain exact reuse; suggestions and aliases require an explicit choice to reuse a band or create a separate band. Alias remembrance is opt-in. Only the final summary confirmation invokes the callback supplied by ImportCsvActivity; cancellation creates no festival or alias.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/FestivalBandReviewDialog.java](../../../../../../app/src/main/java/be/wacken/planner/FestivalBandReviewDialog.java) | [app/src/main/java/be/wacken/planner/ImportCsvActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java) | Returns confirmed creation choices | Composed Java callback |
| [app/src/main/java/be/wacken/planner/FestivalBandReviewDialog.java](../../../../../../app/src/main/java/be/wacken/planner/FestivalBandReviewDialog.java) | [domain/src/main/java/be/wacken/planner/domain/ReviewedFestivalBand.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/ReviewedFestivalBand.java) | Captures explicit identity choices | Java call |
