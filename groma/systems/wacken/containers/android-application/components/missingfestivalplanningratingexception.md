---
type: C4 Component
title: Missing festival pending context
status: stable
groma:
  id: missingfestivalplanningratingexception
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/MissingFestivalPlanningRatingException.java
      symbol: MissingFestivalPlanningRatingException
  group: Pending rating recovery and export
description: Carries retained pending rating
---

Typed adapter exception identifies the exact SavedFestivalPlanningRating blocked by an unavailable festival. The recovery dialog discovers it through the exception chain; ordinary sync failure does not silently discard or reassign the fact.
