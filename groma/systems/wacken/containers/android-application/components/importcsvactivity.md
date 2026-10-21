---
type: C4 Component
title: CSV import screen
status: stable
groma:
  id: importcsvactivity
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/ImportCsvActivity.java
  group: Android screens
description: Select and import festival files
---

Receives Android document selections and invokes the CSV import workflow for lineup and related festival data.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/ImportCsvActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java) | [application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java) | Imports selected data | Java call |
| [app/src/main/java/be/wacken/planner/ImportCsvActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java) | [application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java) | Adds next festival | Java call |
