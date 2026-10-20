---
type: C4 Component
title: Rating model
status: stable
groma:
  id: rating
  parent: wacken
  code:
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/Rating.java
      symbol: Rating
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/SavedRating.java
      symbol: SavedRating
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/SavedFestivalPlanningRating.java
      symbol: SavedFestivalPlanningRating
    - scanner: java
      file: domain/src/main/java/be/wacken/planner/domain/PersonalBandRatingEvent.java
      symbol: PersonalBandRatingEvent
  group: Domain model
description: Planning and personal rating facts
---

Represents the one-to-five rating scale, saved planning ratings, and personal rating events retained with festival and creation context.
