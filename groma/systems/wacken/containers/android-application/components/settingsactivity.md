---
type: C4 Component
title: Settings screen
status: stable
groma:
  id: settingsactivity
  parent: android-application
  code:
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SettingsActivity.java
      symbol: SettingsActivity
    - scanner: java
      file: app/src/main/java/be/wacken/planner/SettingsFeedback.java
      symbol: SettingsFeedback
    - scanner: java
      file: app/src/main/java/be/wacken/planner/InviteShareText.java
      symbol: InviteShareText
  group: Android screens
description: Launch import, review, export, and sync actions
---

Provides entry points for group invite sharing, lineup import, festival rename, metadata and band-link review, rating export, and manual sync.

## Relationships

| Source | Target | Description | Technology |
| --- | --- | --- | --- |
| [app/src/main/java/be/wacken/planner/SettingsActivity.java](../../../../../../app/src/main/java/be/wacken/planner/SettingsActivity.java) | [application/src/main/java/be/wacken/planner/application/ExportRatingsCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ExportRatingsCsvUseCase.java) | Exports cached ratings | Java call |
| [app/src/main/java/be/wacken/planner/SettingsActivity.java](../../../../../../app/src/main/java/be/wacken/planner/SettingsActivity.java) | [app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java](../../../../../../app/src/main/java/be/wacken/planner/BandLinkReviewActivity.java) | Opens link review | Android Intent |
| [app/src/main/java/be/wacken/planner/SettingsActivity.java](../../../../../../app/src/main/java/be/wacken/planner/SettingsActivity.java) | [application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ExportRatingLifecycleCsvUseCase.java) | Exports own lifecycle state | Java call |
| [app/src/main/java/be/wacken/planner/SettingsActivity.java](../../../../../../app/src/main/java/be/wacken/planner/SettingsActivity.java) | [application/src/main/java/be/wacken/planner/application/ExportRatingCorrectionsCsvUseCase.java](../../../../../../application/src/main/java/be/wacken/planner/application/ExportRatingCorrectionsCsvUseCase.java) | Exports own correction audit | Java call |
| [app/src/main/java/be/wacken/planner/SettingsActivity.java](../../../../../../app/src/main/java/be/wacken/planner/SettingsActivity.java) | [app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java](../../../../../../app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java) | Reviews blocked pending work | Java call |
| [app/src/main/java/be/wacken/planner/SettingsActivity.java](../../../../../../app/src/main/java/be/wacken/planner/SettingsActivity.java) | [app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java](../../../../../../app/src/main/java/be/wacken/planner/MusicLinksReviewActivity.java) | Opens active festival lookup | Android Intent |
| [app/src/main/java/be/wacken/planner/SettingsActivity.java](../../../../../../app/src/main/java/be/wacken/planner/SettingsActivity.java) | [app/src/main/java/be/wacken/planner/MainNavigation.java](../../../../../../app/src/main/java/be/wacken/planner/MainNavigation.java) | Renders primary destinations | Android Views |
