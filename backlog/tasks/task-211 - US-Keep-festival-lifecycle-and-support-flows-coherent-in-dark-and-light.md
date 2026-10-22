---
id: TASK-211
title: 'US: Keep festival lifecycle and support flows coherent in dark and light'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-22 19:15'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-205
  - TASK-206
references:
  - loginactivity
  - archivedfestivalactivity
  - importcsvactivity
  - wackentheme
  - settingsactivity
  - mainactivity
modified_files:
  - app/src/androidTest/java/be/wacken/planner/AppearanceAndroidTest.java
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - app/src/main/java/be/wacken/planner/AppearanceController.java
  - app/src/main/java/be/wacken/planner/WackenTheme.java
  - app/src/main/java/be/wacken/planner/LoginActivity.java
  - app/src/main/java/be/wacken/planner/ArchivedFestivalActivity.java
  - >-
    app/src/test/java/be/wacken/planner/ArchivedFestivalLayoutRegressionTest.java
  - app/src/main/java/be/wacken/planner/FestivalBandReviewDialog.java
  - app/src/main/java/be/wacken/planner/ArchivedRatingCorrectionDialog.java
  - app/src/main/java/be/wacken/planner/LocalCsvImportReviewDialog.java
  - app/src/main/java/be/wacken/planner/DeleteFestivalDialog.java
  - app/src/main/java/be/wacken/planner/MissingFestivalRatingDialog.java
  - app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java
  - app/src/main/java/be/wacken/planner/SettingsActivity.java
  - app/src/main/java/be/wacken/planner/ImportCsvActivity.java
  - app/src/main/java/be/wacken/planner/MainActivity.java
  - >-
    app/src/androidTest/java/be/wacken/planner/ArchivedRatingCorrectionAndroidTest.java
  - >-
    groma/systems/wacken/containers/android-application/components/loginactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedfestivalactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/banddetailactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/importcsvactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/mainactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/settingsactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/festivalbandreviewdialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/localcsvimportreviewdialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedratingcorrectiondialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/deletefestivaldialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/missingfestivalratingdialog.md
  - >-
    groma/systems/wacken/containers/android-application/components/wackentheme.md
  - README.md
  - backlog/docs/business-requirements.md
priority: medium
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As an attendee, I want lifecycle, import, sync and support screens to match the selected appearance so the whole app stays understandable. Cover sign-in/session recovery, archive/history, Add/rename festival, import review, invite/export and startup/background sync. Use shared components without changing validated data guarantees or account/security behavior.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given startup/auth/recovery and an existing cache, then theme is applied consistently while cached work and background sync retain their current guarantees.
- [ ] #2 Given archive/add/rename/import or confirmation, then action scope, read-only state and success/failure recovery are clear in both appearances; no false group-wide success is shown.
- [ ] #3 Given a form or selected CSV, when theme changes or Activity recreates, then user input and pending operation state are preserved or restored with explicit safe recovery.
- [ ] #4 Given saved-local/pending/shared-saved status, then meaningful words and accessible announcements distinguish these states independently of color.
- [ ] #5 Tests cover lifecycle confirmations, atomic import failure/retry, offline cache, export/invite boundaries, auth return and theme state retention.
- [ ] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
- [ ] #7 Creation/archive feedback preserves ADR-0016 atomic carry-over/promotion and pending-data recovery, and cannot claim completion after a partial or failed lifecycle operation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Standard presentation slice under approved Views/ADR0024 and unchanged ADR0016. 1. Characterize existing lifecycle/import/correction/appearance/export/invite/auth tests and add red native cases for200% scrollable sign-in, wrapping read-only archived rows, retained rename draft and paired confirmation controls. 2. Reuse common palette/48dp text/input/choice helpers and style owned native dialogs including large-font vertical actions without changing their business callbacks. 3. Make sign-in and import forms scroll/wrap/readable, retain nonsecret drafts/files/viewport; password stays memory-only. Preserve or explicitly explain safe recovery for interrupted lifecycle/review operations, never imply shared success before acknowledgement. 4. Replace compressed archived table with wrapping read-only rows retaining actual planning/performance/music/detail context and viewport. 5. Make archive/delete scope and confirmation explicit; keep existing atomic lifecycle application gateways, pending checks, replay and source-aware corrections. Preserve rename draft and status, make sync/local/pending/error states live and readable; guards prevent destroyed-screen callbacks. 6. Run native both palettes/font/recreation plus full JVM/BDD/QA/ArchUnit/lint/API/database/Groma gates; review actual rendered map/exact duplicate/scanner drift; update README/BR and fresh stable-key signed checkpoint before closure. No new auth/provider integration,framework,module,port,DB/API,dependency or domain ownership; no architecture-significant change beyond already approved presentation foundation. Risks: async callbacks after recreation, uncertain acknowledged operations and retained dialog drafts; choose explicit safe recovery instead of replaying writes. Integrated actual accessibility/device/contrast review remains212.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.
<!-- SECTION:NOTES:END -->
