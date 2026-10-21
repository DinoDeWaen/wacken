---
type: C4 Component
title: Festival creation worker
status: stable
groma:
  id: addfestivalaction
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/AddFestivalAction.java
      symbol: AddFestivalAction
  group: Festival creation and identity
description: Runs creation away from the UI
---

Runs the creation Supplier supplied by ImportCsvActivity on its worker executor and delivers either AddFestivalResult or a failure message through the supplied UI executor. It does not decide identities or write data; the composed command invokes AddFestivalUseCase.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/AddFestivalAction.java](../../../../../../app/src/main/java/be/wacken/planner/AddFestivalAction.java) | [application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java) | Runs supplied creation command | Composed Java callback |
