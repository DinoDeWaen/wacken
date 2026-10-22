---
type: C4 Component
title: Archived festival screen
status: stable
groma:
  id: archivedfestivalactivity
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java
  group: Android screens
description: Browse read-only festival history
---

Displays an archived festival lineup and opens read-only band details.

Uses wrapping read-only rows with full planning meaning, actual stage/date/time and known music actions; title and secondary actions scroll with the list. Restores viewport and refreshes cached corrected values on resume without changing ratings. Interrupted confirmation/request recovery asks the user to check current festival/history before repeating an action.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java) | [application/src/main/java/be/wacken/planner/application/ListArchivedFestivalBandsUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ListArchivedFestivalBandsUseCase.java) | Loads archived lineup | Java call |
| [app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java) | [app/src/main/java/be/wacken/planner/DeleteFestivalDialog.java](../../../../../../app/src/main/java/be/wacken/planner/DeleteFestivalDialog.java) | Confirms archived festival deletion | Java call |
| [app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java) | [application/src/main/java/be/wacken/planner/application/FestivalAfterRatingView.java](../../../../../../application/src/main/java/be/wacken/planner/application/FestivalAfterRatingView.java) | Reads archived after-state | Java call |
| [app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java) | [app/src/main/java/be/wacken/planner/RatingStarsView.java](../../../../../../app/src/main/java/be/wacken/planner/RatingStarsView.java) | Renders read-only ratings | Android Views |
| [app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java) | [app/src/main/java/be/wacken/planner/WackenTheme.java](../../../../../../app/src/main/java/be/wacken/planner/WackenTheme.java) | Styles archived rows | Android Views |
| [app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java) | [app/src/main/java/be/wacken/planner/RatingValueText.java](../../../../../../app/src/main/java/be/wacken/planner/RatingValueText.java) | Formats read-only planning meaning | Java call |
