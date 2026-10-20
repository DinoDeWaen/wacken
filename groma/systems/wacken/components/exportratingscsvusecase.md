---
type: C4 Component
title: Rating CSV export
status: stable
groma:
  id: exportratingscsvusecase
  parent: wacken
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ExportRatingsCsvUseCase.java
      symbol: ExportRatingsCsvUseCase
  group: Application workflows
description: Export cached rating history
---

Collects local planning ratings, post-show ratings, group rating details, and available schedule metadata into a CSV payload for Android sharing.
