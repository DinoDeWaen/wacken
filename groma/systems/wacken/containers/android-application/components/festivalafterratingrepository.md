---
type: C4 Component
title: Festival after-rating contract
status: stable
groma:
  id: festivalafterratingrepository
  parent: android-application
  code:
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java
      symbol: FestivalAfterRatingRepository
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/FestivalAfterRating.java
      symbol: FestivalAfterRating
  group: Festival rating context
description: Stores member and festival assessments
---

Domain port and immutable FestivalAfterRating values keyed by member, festival and canonical band. Operation identity supports replay; zero is a durable reset, and requiresReview marks uncertain migrated facts. Recording never establishes the reusable personal band opinion.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java](../../../../../../domain/src/main/java/be/wacken/planner/domain/FestivalAfterRatingRepository.java) | [app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java](../../../../../../app/src/main/java/be/wacken/planner/persistence/RoomFestivalAfterRatingRepository.java) | Uses composed local cache | Composed Java port dispatch |
