---
type: C4 Component
title: Selected festival after-rating view
status: stable
groma:
  id: festivalafterratingview
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/FestivalAfterRatingView.java
      symbol: FestivalAfterRatingView
  group: Festival rating context
description: Reads one explicit festival
---

Read-only compatibility adapter to the festival after-rating port. Both active and archive screens can pin their selected festival. Does not write or fall back to the currently active festival.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/FestivalAfterRatingView.java](../../../../../../application/src/main/java/be/wacken/planner/application/FestivalAfterRatingView.java) | [domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java) | Reads selected assessments | Java call |
