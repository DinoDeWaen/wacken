---
type: C4 Component
title: Schedule calendar screen
status: stable
groma:
  id: scheduleactivity
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ScheduleActivity.java
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ScheduleBandDisplayName.java
      symbol: ScheduleBandDisplayName
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ScheduleBlockContent.java
      symbol: ScheduleBlockContent
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ScheduleBlockStyle.java
      symbol: ScheduleBlockStyle
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ScheduleCalendarLayout.java
      symbol: ScheduleCalendarLayout
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ScheduleDaySelection.java
      symbol: ScheduleDaySelection
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ScheduleErrorMessage.java
      symbol: ScheduleErrorMessage
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ScheduleLegendContent.java
      symbol: ScheduleLegendContent
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ScheduleManualSelections.java
      symbol: ScheduleManualSelections
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ScheduleRatingFilter.java
      symbol: ScheduleRatingFilter
  group: Android screens
description: Present and adjust the group timeline
---

Renders the day-filtered stage/time calendar and its existing generated decisions. Labeled native day and threshold selectors, a wrapping barred-overlap control and a readable key retain local filters, focus and calendar position across rerenders, appearance changes and recreation, including empty results. Calendar geometry scales with text while essential horizontal scrolling stays inside the timeline; full date, stage, time, rank, scratch and lock meanings remain available through native accessibility and the wrapping decision dialog. Both palettes restyle the existing grid, rank outlines, scratch marks and walking context. Restored dialogs wait for cached group locks and retain the actual chosen act. Alternative selection still uses the existing local manual-selection and pending group-lock paths without changing personal ratings, generated evidence, ties, day cutoff or walking rules.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/ScheduleActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ScheduleActivity.java) | [application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java) | Generates calendar slots | Java call |
| [app/src/main/java/be/wacken/planner/ScheduleActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ScheduleActivity.java) | [app/src/main/java/be/wacken/planner/MainNavigation.java](../../../../../../app/src/main/java/be/wacken/planner/MainNavigation.java) | Renders primary destinations | Android Views |
| [app/src/main/java/be/wacken/planner/ScheduleActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ScheduleActivity.java) | [app/src/main/java/be/wacken/planner/WackenTheme.java](../../../../../../app/src/main/java/be/wacken/planner/WackenTheme.java) | Styles calendar controls | Android Views |
