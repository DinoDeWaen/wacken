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

Readable paired name/file/status controls retain selected CSV contents in the existing memory-only retained import and restore name/scroll. Process loss explicitly requests file reselection. Interrupted festival review/creation asks the user to check the active festival and review the same name/CSV again; restoration never submits creation. File-picker cancellation or missing target preserves prior selection and cached data; local review still reports local-only success.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/ImportCsvActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java) | [application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ImportFestivalCsvUseCase.java) | Imports selected data | Java call |
| [app/src/main/java/be/wacken/planner/ImportCsvActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java) | [application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/AddFestivalUseCase.java) | Adds next festival | Java call |
| [app/src/main/java/be/wacken/planner/ImportCsvActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java) | [application/src/main/java/be/wacken/planner/application/ReviewFestivalBandsUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ReviewFestivalBandsUseCase.java) | Builds identity proposals | Java call |
| [app/src/main/java/be/wacken/planner/ImportCsvActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java) | [app/src/main/java/be/wacken/planner/FestivalBandReviewDialog.java](../../../../../../app/src/main/java/be/wacken/planner/FestivalBandReviewDialog.java) | Requests identity confirmation | Java call |
| [app/src/main/java/be/wacken/planner/ImportCsvActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java) | [app/src/main/java/be/wacken/planner/AddFestivalAction.java](../../../../../../app/src/main/java/be/wacken/planner/AddFestivalAction.java) | Dispatches reviewed creation | Java call |
| [app/src/main/java/be/wacken/planner/ImportCsvActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java) | [app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java](../../../../../../app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java) | Opens local row review | Java call |
| [app/src/main/java/be/wacken/planner/ImportCsvActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java) | [application/src/main/java/be/wacken/planner/application/FestivalCsvReader.java](../../../../../../application/src/main/java/be/wacken/planner/application/FestivalCsvReader.java) | Reads uploaded lineup names | Java call |
| [app/src/main/java/be/wacken/planner/ImportCsvActivity.java](../../../../../../app/src/main/java/be/wacken/planner/ImportCsvActivity.java) | [app/src/main/java/be/wacken/planner/WackenTheme.java](../../../../../../app/src/main/java/be/wacken/planner/WackenTheme.java) | Styles import form | Android Views |
