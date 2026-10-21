---
type: C4 Component
title: Festival home screen
status: stable
groma:
  id: mainactivity
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/MainActivity.java
    - scanner: java
      file: app/src/main/java/be/wacken/planner/FestivalStartScreenContent.java
      symbol: FestivalStartScreenContent
    - scanner: java
      file: app/src/main/java/be/wacken/planner/RatingAllocationSummary.java
      symbol: RatingAllocationSummary
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SelectedBandScrollTarget.java
      symbol: SelectedBandScrollTarget
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SyncVisualMode.java
      symbol: SyncVisualMode
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SyncVisualPolicy.java
      symbol: SyncVisualPolicy
  group: Android screens
description: Show active lineup and archived start state
---

Presents the active festival band table and rating entry, launches related workflows, and preserves the selected row when returning from detail.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/MainActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MainActivity.java) | [application/src/main/java/be/wacken/planner/application/ListActiveFestivalBandsUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ListActiveFestivalBandsUseCase.java) | Loads festival bands | Java call |
| [app/src/main/java/be/wacken/planner/MainActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MainActivity.java) | [application/src/main/java/be/wacken/planner/application/RateBandUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/RateBandUseCase.java) | Saves planning rating | Java call |
| [app/src/main/java/be/wacken/planner/MainActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MainActivity.java) | [app/src/main/java/be/wacken/planner/AppRepositories.java](../../../../../../app/src/main/java/be/wacken/planner/AppRepositories.java) | Obtains repositories | Java call |
| [app/src/main/java/be/wacken/planner/MainActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MainActivity.java) | [app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java) | Opens festival history | Android Intent |
| [app/src/main/java/be/wacken/planner/MainActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MainActivity.java) | [app/src/main/java/be/wacken/planner/BandDetailActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandDetailActivity.java) | Opens band details | Android Intent |
| [app/src/main/java/be/wacken/planner/MainActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MainActivity.java) | [app/src/main/java/be/wacken/planner/LoginActivity.java](../../../../../../app/src/main/java/be/wacken/planner/LoginActivity.java) | Requests sign-in | Android Intent |
| [app/src/main/java/be/wacken/planner/MainActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MainActivity.java) | [app/src/main/java/be/wacken/planner/WackenTheme.java](../../../../../../app/src/main/java/be/wacken/planner/WackenTheme.java) | Styles home screen | Java call |
| [app/src/main/java/be/wacken/planner/MainActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MainActivity.java) | [app/src/main/java/be/wacken/planner/DeleteFestivalDialog.java](../../../../../../app/src/main/java/be/wacken/planner/DeleteFestivalDialog.java) | Confirms active festival deletion | Java call |
