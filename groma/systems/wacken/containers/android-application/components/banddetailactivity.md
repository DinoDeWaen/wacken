---
type: C4 Component
title: Band detail screens
status: stable
groma:
  id: banddetailactivity
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/BandDetailActivity.java
      symbol: BandDetailActivity
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java
      symbol: ArchivedBandDetailActivity
    - scanner: java
      file: app/src/main/java/be/wacken/planner/BandDetailLayoutPolicy.java
      symbol: BandDetailLayoutPolicy
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ExternalLinks.java
      symbol: ExternalLinks
    - scanner: java
      file: app/src/main/java/be/wacken/planner/RatingStarsView.java
    - scanner: java
      file: app/src/main/java/be/wacken/planner/BandDetailPresentation.java
      symbol: BandDetailPresentation
  group: Android screens
description: Present band information and personal ratings
---

Shows active or archived band details, group star summaries, music links, and the separate post-show rating control where editing is allowed.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/BandDetailActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandDetailActivity.java) | [application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java) | Loads band details | Java call |
| [app/src/main/java/be/wacken/planner/BandDetailActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandDetailActivity.java) | [app/src/main/java/be/wacken/planner/BandImageLoader.java](../../../../../../app/src/main/java/be/wacken/planner/BandImageLoader.java) | Loads lifecycle-safe band images | Java call |
| [app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java) | [app/src/main/java/be/wacken/planner/BandImageLoader.java](../../../../../../app/src/main/java/be/wacken/planner/BandImageLoader.java) | Loads lifecycle-safe band images | Java call |
| [app/src/main/java/be/wacken/planner/BandDetailActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandDetailActivity.java) | [application/src/main/java/be/wacken/planner/application/FestivalPlanningRatingView.java](../../../../../../application/src/main/java/be/wacken/planner/application/FestivalPlanningRatingView.java) | Pins selected planning scope | Java call |
| [app/src/main/java/be/wacken/planner/BandDetailActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandDetailActivity.java) | [application/src/main/java/be/wacken/planner/application/FestivalAfterRatingView.java](../../../../../../application/src/main/java/be/wacken/planner/application/FestivalAfterRatingView.java) | Pins selected after-state | Java call |
| [app/src/main/java/be/wacken/planner/BandDetailActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandDetailActivity.java) | [application/src/main/java/be/wacken/planner/application/RecordFestivalAfterRatingUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/RecordFestivalAfterRatingUseCase.java) | Records explicit after-rating | Java call |
| [app/src/main/java/be/wacken/planner/BandDetailActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandDetailActivity.java) | [application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java) | Shows own rating context | Java call |
| [app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java) | [application/src/main/java/be/wacken/planner/application/FestivalAfterRatingView.java](../../../../../../application/src/main/java/be/wacken/planner/application/FestivalAfterRatingView.java) | Reads selected archive | Java call |
| [app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java) | [application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ViewBandRatingContextUseCase.java) | Shows archived rating context | Java call |
| [app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java) | [app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java) | Reviews own correction | Java call |
| [app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java) | [domain/src/main/java/be/wacken/planner/domain/RatingCorrectionRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/RatingCorrectionRepository.java) | Shows own correction history | Java call |
| [app/src/main/java/be/wacken/planner/BandDetailActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandDetailActivity.java) | [application/src/main/java/be/wacken/planner/application/RateBandUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/RateBandUseCase.java) | Saves planning choice | Java call |
| [app/src/main/java/be/wacken/planner/BandDetailActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandDetailActivity.java) | [app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java) | Opens one band lookup | Android Intent |
