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
    - scanner: java
      file: app/src/main/java/be/wacken/planner/LineupPresentation.java
      symbol: LineupPresentation
  group: Android screens
description: Show active lineup and archived start state
---

Presents the actual alphabetically scoped active lineup with wrapping band, planning-rating meaning and performance context, local name search and an Unrated filter. Its presentation helper filters existing display items without owning rating or festival policy. Keeps query, filter and selected-row context through detail return and recreation, opens only stored music links, and exposes cached/offline retry and archive/add-next entry states. Planning saves and festival lifecycle actions use the existing application use cases; primary destinations use shared stateless navigation.

Secondary lifecycle actions wrap vertically and sync status uses words/live announcements. Archive explicitly identifies the festival and group-wide carry-over scope before calling the unchanged atomic archive workflow; success follows acknowledgement and failures preserve pending work. Returning from an archive reloads cached festival state. Interrupted owned dialogs/sync/archive produce explicit check-current-state recovery rather than resubmitting a lifecycle write.

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
| [app/src/main/java/be/wacken/planner/MainActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MainActivity.java) | [app/src/main/java/be/wacken/planner/ArchiveFestivalAction.java](../../../../../../app/src/main/java/be/wacken/planner/ArchiveFestivalAction.java) | Starts archive worker | Java call |
| [app/src/main/java/be/wacken/planner/MainActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MainActivity.java) | [app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java](../../../../../../app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java) | Reviews blocked pending work | Java call |
| [app/src/main/java/be/wacken/planner/MainActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MainActivity.java) | [app/src/main/java/be/wacken/planner/MainNavigation.java](../../../../../../app/src/main/java/be/wacken/planner/MainNavigation.java) | Renders primary destinations | Android Views |
| [app/src/main/java/be/wacken/planner/MainActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MainActivity.java) | [app/src/main/java/be/wacken/planner/RatingStarsView.java](../../../../../../app/src/main/java/be/wacken/planner/RatingStarsView.java) | Renders planning choices | Android Views |
| [app/src/main/java/be/wacken/planner/LineupPresentation.java](../../../../../../app/src/main/java/be/wacken/planner/LineupPresentation.java) | [app/src/main/java/be/wacken/planner/RatingValueText.java](../../../../../../app/src/main/java/be/wacken/planner/RatingValueText.java) | Formats planning meaning | Java call |
