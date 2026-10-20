---
type: C4 Component
title: Band link review screen
status: stable
groma:
  id: bandlinkreviewactivity
  parent: wacken
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java
  group: Android screens
description: Confirm imported band matches
---

Presents candidate matches and commits only the band links selected by the user.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java](../../../../app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java) | [application/src/main/java/be/wacken/planner/application/ReviewImportedBandLinksUseCase.java](../../../../application/src/main/java/be/wacken/planner/application/ReviewImportedBandLinksUseCase.java) | Finds band matches | Java call |
