---
type: C4 Component
title: Active festival after-rating compatibility
status: stable
groma:
  id: activefestivalafterratingrepository
  parent: android-application
  code:
    - scanner: java
      file: application/src/main/java/be/wacken/planner/application/ActiveFestivalAfterRatingRepository.java
      symbol: ActiveFestivalAfterRatingRepository
  group: Festival rating context
description: Reads only the current active festival
---

Read-only RealRatingRepository compatibility for existing detail and legacy export consumers. Resolves the active festival before querying its after-rating port; never selects the latest event or another festival. save deliberately refuses writes.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [application/src/main/java/be/wacken/planner/application/ActiveFestivalAfterRatingRepository.java](../../../../../../application/src/main/java/be/wacken/planner/application/ActiveFestivalAfterRatingRepository.java) | [domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java) | Reads active assessments | Java call |
