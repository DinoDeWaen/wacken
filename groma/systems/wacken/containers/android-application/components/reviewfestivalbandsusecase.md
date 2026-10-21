---
type: C4 Component
title: Festival identity proposals
status: stable
groma:
  id: reviewfestivalbandsusecase
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ReviewFestivalBandsUseCase.java
  group: Festival creation and identity
description: Proposes reuse without selecting
---

Loads the FestivalBandCatalog snapshot, rejects duplicate exact canonical names, and proposes exact matches plus alias/name-similarity suggestions for uploaded names. It never silently chooses a fuzzy/alias match, creates a band or merges ratings. FestivalBandReviewDialog owns human confirmation; domain identity values retain the selected canonical ID and alias choice.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/ReviewFestivalBandsUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ReviewFestivalBandsUseCase.java) | [application/src/main/java/be/wacken/planner/application/FestivalBandCatalog.java](../../../../../../application/src/main/java/be/wacken/planner/application/FestivalBandCatalog.java) | Loads canonical review data | Java call |
| [application/src/main/java/be/wacken/planner/application/ReviewFestivalBandsUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ReviewFestivalBandsUseCase.java) | [domain/src/main/java/be/wacken/planner/domain/ConfirmedBandAlias.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/ConfirmedBandAlias.java) | Matches confirmed alias keys | Java call |
