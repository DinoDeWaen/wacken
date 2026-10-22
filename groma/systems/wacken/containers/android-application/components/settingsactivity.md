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

Provides grouped appearance, account/group, festival administration, rating export/allocation and manual sync actions. Link imported bands and active-festival music discovery are adjacent in festival administration; canonical-band discovery stays on detail. Shared headings, readable wrapping body text and live status use the selected palette, and the Settings scroll position survives recreation. Import, rename, export, invite and pending-sync behavior still delegate to the existing workflows.

Rename preserves its nonsecret draft and captured festival identity across recreation without submitting; changed festival context requires reopening review. Interrupted sync has explicit unconfirmed-result recovery with cached/pending work retained. Invite/export remain Android target handoffs, with app-side missing-target/cancellation wording. Owned dialogs use shared paired styling and wrapping native actions.

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
| [app/src/main/java/be/wacken/planner/SettingsActivity.java](../../../../../../app/src/main/java/be/wacken/planner/SettingsActivity.java) | [app/src/main/java/be/wacken/planner/WackenTheme.java](../../../../../../app/src/main/java/be/wacken/planner/WackenTheme.java) | Styles native review controls | Android Views |
