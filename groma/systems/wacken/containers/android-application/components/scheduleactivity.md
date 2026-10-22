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

Renders the day-filtered stage/time calendar, applies local visibility filters, opens alternatives, and sends manual winner selections.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/ScheduleActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ScheduleActivity.java) | [application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/GenerateSharedScheduleUseCase.java) | Generates calendar slots | Java call |
| [app/src/main/java/be/wacken/planner/ScheduleActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ScheduleActivity.java) | [app/src/main/java/be/wacken/planner/MainNavigation.java](../../../../../../app/src/main/java/be/wacken/planner/MainNavigation.java) | Renders primary destinations | Android Views |
