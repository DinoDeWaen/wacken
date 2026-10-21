---
type: C4 Component
title: Review orphaned pending rating
status: stable
groma:
  id: missingfestivalratingdialog
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java
      symbol: MissingFestivalRatingDialog
  group: Pending rating recovery and export
description: Offers keep move or discard
---

Home/Settings sync failure opens this dialog only for the typed missing-festival error. Keep is default; move and separately confirmed discard call AppRepositories recovery commands on a worker. Successful recovery retries sync; failures report that pending work remains.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java](../../../../../../app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java) | [app/src/main/java/be/wacken/planner/MissingFestivalPlanningRatingException.java](../../../../../../app/src/main/java/be/wacken/planner/MissingFestivalPlanningRatingException.java) | Extracts pending target | Java call |
| [app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java](../../../../../../app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java) | [app/src/main/java/be/wacken/planner/AppRepositories.java](../../../../../../app/src/main/java/be/wacken/planner/AppRepositories.java) | Requests confirmed recovery | Java call |
