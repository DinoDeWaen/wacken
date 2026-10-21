---
type: C4 Component
title: Band and history queries
status: stable
groma:
  id: listactivefestivalbandsusecase
  parent: wacken
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ListActiveFestivalBandsUseCase.java
      symbol: ListActiveFestivalBandsUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ListBandsUseCase.java
      symbol: ListBandsUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ListArchivedFestivalBandsUseCase.java
      symbol: ListArchivedFestivalBandsUseCase
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ViewArchivedFestivalHistoryUseCase.java
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BandListItem.java
      symbol: BandListItem
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BandDetailItem.java
      symbol: BandDetailItem
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/PersonalRatingHistoryItem.java
      symbol: PersonalRatingHistoryItem
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/PersonRatingStars.java
      symbol: PersonRatingStars
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/MusicLinks.java
      symbol: MusicLinks
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/BiographyText.java
      symbol: BiographyText
  group: Application workflows
description: Read active and archived festival views
---

Builds band list and detail projections, including archived festival history and per-person rating details, from repository data.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/ListBandsUseCase.java](../../../../application/src/main/java/be/wacken/planner/application/ListBandsUseCase.java) | [application/src/main/java/be/wacken/planner/application/MemberRatingProjection.java](../../../../application/src/main/java/be/wacken/planner/application/MemberRatingProjection.java) | Projects member planning stars | Java call |
| [application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java](../../../../application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java) | [application/src/main/java/be/wacken/planner/application/MemberRatingProjection.java](../../../../application/src/main/java/be/wacken/planner/application/MemberRatingProjection.java) | Projects member planning stars | Java call |
| [application/src/main/java/be/wacken/planner/application/ListArchivedFestivalBandsUseCase.java](../../../../application/src/main/java/be/wacken/planner/application/ListArchivedFestivalBandsUseCase.java) | [application/src/main/java/be/wacken/planner/application/MemberRatingProjection.java](../../../../application/src/main/java/be/wacken/planner/application/MemberRatingProjection.java) | Projects member planning stars | Java call |
