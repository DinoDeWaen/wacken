---
type: C4 Component
title: Band link review screen
status: stable
groma:
  id: bandlinkreviewactivity
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java
  group: Android screens
description: Confirm imported band matches
---

Presents own-catalog matches for imported band names in full-width vertical rows. Labeled native search and match controls retain edited terms, explicit No match or chosen canonical name and viewport through recreation; search changes require a current match choice. Both palettes use the shared focused input/selector styling. Only the existing explicit link action invokes the application linker; searching, theme changes and restoration do not rename or merge bands or change ratings.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java) | [application/src/main/java/be/wacken/planner/application/ReviewImportedBandLinksUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ReviewImportedBandLinksUseCase.java) | Finds band matches | Java call |
| [app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java) | [app/src/main/java/be/wacken/planner/WackenTheme.java](../../../../../../app/src/main/java/be/wacken/planner/WackenTheme.java) | Styles native review controls | Android Views |
