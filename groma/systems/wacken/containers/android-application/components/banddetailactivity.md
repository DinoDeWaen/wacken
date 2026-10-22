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
    - scanner: java
      file: app/src/main/java/be/wacken/planner/RatingValueText.java
      symbol: RatingValueText
  group: Android screens
description: Present band information and personal ratings
---

Presents active and archived band information with the captured member/festival scope, established source/history context and read-only group planning summaries. Active editing puts planning first and keeps the independent post-show value behind a retained After the show disclosure. Owns the reusable native rating choices and shared meaning vocabulary used by detail and lineup: touch, keyboard and accessibility actions use the existing callbacks/use cases, while archived children remain disabled. Restyling and recreation retain views/state without replaying rating events; actual biography, images, performance context and stored music links are preserved. Canonical-band discovery retains its existing no-festival entry but is absent in captured archive context; archived corrections keep their explicit reviewed workflow.

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
| [app/src/main/java/be/wacken/planner/RatingStarsView.java](../../../../../../app/src/main/java/be/wacken/planner/RatingStarsView.java) | [app/src/main/java/be/wacken/planner/WackenTheme.java](../../../../../../app/src/main/java/be/wacken/planner/WackenTheme.java) | Styles native rating choices | Java call |
| [app/src/main/java/be/wacken/planner/BandDetailPresentation.java](../../../../../../app/src/main/java/be/wacken/planner/BandDetailPresentation.java) | [app/src/main/java/be/wacken/planner/WackenTheme.java](../../../../../../app/src/main/java/be/wacken/planner/WackenTheme.java) | Styles band information | Java call |
