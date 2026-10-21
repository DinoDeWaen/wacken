---
type: C4 Component
title: Primary destination navigation
status: stable
groma:
  id: mainnavigation
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/MainNavigation.java
  group: Android screens
description: Reuses named primary screens
---

Builds the same labeled Bands, Schedule and Settings destination bar around each primary screen, exposing the current destination visually and to accessibility services. Selecting the current Activity is a no-op; another destination is opened with Android Intent REORDER_TO_FRONT so an existing Activity retains its screen context rather than creating duplicate navigation instances. Detail and review subflows keep their initiating Activity and normal Back behavior. Shared appearance helpers style the controls; this component owns no rating, schedule, session or persistence policy.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/MainNavigation.java](../../../../../../app/src/main/java/be/wacken/planner/MainNavigation.java) | [app/src/main/java/be/wacken/planner/MainActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MainActivity.java) | Reuses band overview | Android Intent |
| [app/src/main/java/be/wacken/planner/MainNavigation.java](../../../../../../app/src/main/java/be/wacken/planner/MainNavigation.java) | [app/src/main/java/be/wacken/planner/ScheduleActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ScheduleActivity.java) | Reuses schedule view | Android Intent |
| [app/src/main/java/be/wacken/planner/MainNavigation.java](../../../../../../app/src/main/java/be/wacken/planner/MainNavigation.java) | [app/src/main/java/be/wacken/planner/SettingsActivity.java](../../../../../../app/src/main/java/be/wacken/planner/SettingsActivity.java) | Reuses settings view | Android Intent |
| [app/src/main/java/be/wacken/planner/MainNavigation.java](../../../../../../app/src/main/java/be/wacken/planner/MainNavigation.java) | [app/src/main/java/be/wacken/planner/WackenTheme.java](../../../../../../app/src/main/java/be/wacken/planner/WackenTheme.java) | Styles destination controls | Java call |
