---
type: C4 Component
title: Band detail screens
status: stable
groma:
  id: banddetailactivity
  parent: wacken
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
  group: Android screens
description: Present band information and personal ratings
---

Shows active or archived band details, group star summaries, music links, and the separate post-show rating control where editing is allowed.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/BandDetailActivity.java](../../../../app/src/main/java/be/wacken/planner/BandDetailActivity.java) | [application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java](../../../../application/src/main/java/be/wacken/planner/application/ShowBandDetailUseCase.java) | Loads band details | Java call |
| [app/src/main/java/be/wacken/planner/BandDetailActivity.java](../../../../app/src/main/java/be/wacken/planner/BandDetailActivity.java) | [app/src/main/java/be/wacken/planner/BandImageLoader.java](../../../../app/src/main/java/be/wacken/planner/BandImageLoader.java) | Loads lifecycle-safe band images | Java call |
| [app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java](../../../../app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java) | [app/src/main/java/be/wacken/planner/BandImageLoader.java](../../../../app/src/main/java/be/wacken/planner/BandImageLoader.java) | Loads lifecycle-safe band images | Java call |
