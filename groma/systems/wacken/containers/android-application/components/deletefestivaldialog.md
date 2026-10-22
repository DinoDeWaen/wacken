---
type: C4 Component
title: Festival deletion confirmation
status: stable
groma:
  id: deletefestivaldialog
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/DeleteFestivalDialog.java
      symbol: DeleteFestivalDialog
  group: Festival deletion
description: Confirms group-scoped removal
---

Shown from active home or archived festival views. Requires explicit deletion confirmation, then invokes AppRepositories.deleteFestival on a worker and returns success/error on the UI thread. Explains removal of festival-scoped data and retention of canonical bands/personal history. The backend owns authorization and atomic deletion; local cleanup follows acknowledged remote success.

Confirmation/progress use shared paired native surfaces and wrapping actions; lifecycle ownership dismisses windows on Activity destruction and obsolete callbacks cannot report success on a destroyed screen. Recreated owners show explicit current-state recovery before repeating requests.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/DeleteFestivalDialog.java](../../../../../../app/src/main/java/be/wacken/planner/DeleteFestivalDialog.java) | [app/src/main/java/be/wacken/planner/AppRepositories.java](../../../../../../app/src/main/java/be/wacken/planner/AppRepositories.java) | Requests selected festival deletion | Java call |
| [app/src/main/java/be/wacken/planner/DeleteFestivalDialog.java](../../../../../../app/src/main/java/be/wacken/planner/DeleteFestivalDialog.java) | [app/src/main/java/be/wacken/planner/WackenTheme.java](../../../../../../app/src/main/java/be/wacken/planner/WackenTheme.java) | Styles deletion confirmation | Android Views |
