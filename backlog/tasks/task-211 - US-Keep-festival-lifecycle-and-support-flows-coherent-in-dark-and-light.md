---
id: TASK-211
title: 'US: Keep festival lifecycle and support flows coherent in dark and light'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-22 20:08'
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
  - backlog/docs/groma-support-review-2026-10-22.md
  - tools/groma/architecture-baseline.json
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
## Implementation summary

Sign-in/import and archived lists use readable wrapping native forms/rows. Owned confirmation/recovery dialogs use paired surfaces,48dp controls and vertical wrapping native actions, keep existing listeners and dismiss with their Activity. Sign-in retains one active request and memory-only password; rename retains its unsent captured-festival draft. Archive explicitly confirms festival/group scope before the unchanged atomic workflow. Interrupted sync/import/lifecycle/correction work shows current-state recovery instead of resubmitting writes. Archived rows refresh cached corrected values on return, and Bands reloads festival state after archive navigation.

## Acceptance criteria validation

1. Existing cached/offline startup/auth return and appearance tests pass; sign-in retains progress/failure without a second submission, password excluded from saved hierarchy.
2. Explicit archive/group scope; delete/import/rename/correction remain explicit acknowledged workflows. Native dialogs wrap and restyle in place; failures retain cached/pending state.
3. Rename name/festival draft, CSV contents/local review and viewport retain through recreation; process loss requests CSV reselection. Interrupted creation/requests and correction staged values use explicit safe recovery; restoration submits nothing.
4. Status wording distinguishes local accepted CSV, pending work, confirmed shared lifecycle success and unconfirmed errors; status views are polite live regions. Animation does not replace status text.
5. Native98 cases pass before final archive-return refinements, final41 affected cases pass afterward; existing atomic import/correction/archive/export/invite/offline cases retained and six support cases added. Required cross-version CI pending at note preparation.
6. README/BR and reviewed source/map/duplicate/scanner evidence updated; full CI evidence required before closure.
7. Creation/archive/correction application and gateway policies are unchanged; existing transaction/replay/source/pending regressions run in complete required CI.

## How to test

### Automated tests

- Existing23 appearance/archive/identity/local-import/correction cases characterized before source changes: /private/tmp/wacken-211-characterization.log.
- New200% sign-in and archived-name scenarios fail red for missing scroll/ellipsis, then pass after wrapping layouts: wacken-211-red-native.log and wacken-211-green-native.log.
- Styled-dialog15 and expanded support55 native cases pass: wacken-211-dialog-native.log and wacken-211-support-native.log.
- sh scripts/verify-android-smoke.sh emulator-5554 all:98/98 pass on owned API36 appearance205, wacken-211-native-full.log. Final41 affected cases pass on corrected ed65ba7 after archive-return/recovery refinements, wacken-211-final-focused.log. Includes all six new support scenarios.
- Final clean cloneed65ba7: ./gradlew test qaTest :app:lintDebug assembleRelease --rerun-tasks passes, /private/tmp/wacken-211-signed-final.log. Includes JVM/domain/application/BDD/coverage/QA/ArchUnit. A scope error from extracting archived list refresh was caught by the previous clean/native compilation and corrected before this successful build.
- Pinned Groma two-scan gate passes96components/299sources/13flows/83reviewedgroups, existing two stale-arrow exceptions only;27 positive/negative fixtures pass. wacken-211-groma-final.log and wacken-211-groma-fixtures.log.
- Obsolete archived-table source-string assertions removed; existing read-only/known-action guards retained and actual native wrapping/meaning checks replace geometric source matching.
- git diff --check passes. Required implementation CI39845732438 on a39ec3839b6873a5adbe5031950eb5ebc21d99d2 pending; do not merge until all four jobs pass.

### Manual validation

Actual source/composition and Chromium /private/tmp/wacken-211-map overview/source/style/caller relationships reviewed; screenshot /private/tmp/wacken-211-map-detail.png. Current Supabase changelog/session docs checked with the Supabase skill; no backend/auth endpoint,token,security or TLS configuration was changed. Native sign-in progress uses a controlled retained-state fixture, not a real credential/network sign-in; existing auth adapter tests remain separate. No human TalkBack/outdoor/physical-device experience is claimed; integrated native assistive/visual/contrast review is212.

Fresh stable-key APK from corrected cleaned65ba7: /private/tmp/wacken-211-validated-build-checkout/app/build/outputs/apk/release/app-release.apk. SHA256614f32daf429ac018378b25d76b1ed548750410350e99b71fd8e314f3a5cdb4c; apksigner certificate ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f verifies. Fresh checkout contains no prior APK. Later changes are docs/baseline/task evidence only. Unpublished checkpoint;2.35.1 remains the release.

## TDD / BDD / approval-test evidence

Existing23 native and application/gateway characterizations preserve local import rollback, explicit correction, archive promotion and offline guarantees. New large-font sign-in/archive cases moved red→green. New Given rename/When theme+recreate+cancel, Given archive/When cancel, Given staged correction/When recreate and retained sign-in progress/failure cases cover no writes/duplicate submission. Recovery refinements followed source review and are included in final41. No policy moved to presentation.

## Architecture impact

Architecture-significant change: no new module/framework/dependency/port/schema/API/domain or security policy. Retained screen data/work follows the existing approved Views/session pattern; existing Auth client/store and Main destination are preserved. Weak dialog ownership is presentation lifecycle only.
Approval received: user Standard/ADR0024 retained; ADR0016 lifecycle remains unchanged.
ADR impact: none, because existing presentation and application boundaries suffice.

## README impact

README impact: updated support/form/dialog/recovery development behavior and exact architecture review.

## Business requirements impact

Business requirements impact: updated planned3.0 support increment and explicit archive confirmation; atomic carry-over/promotion, corrections, pending-data and auth/security guarantees remain unchanged.

## Diagram impact

Diagram impact: updated12 Groma responsibility overviews, eight actual style collaborations and archived display-vocabulary call through CLI. Existing ownership/containment/flows retained; reviewed83 exact groups and scanner evidence in backlog/docs/groma-support-review-2026-10-22.md. Runtime Mermaid boundaries unchanged.

## Commits / logical change list

480a81f readable support layouts, retained state, confirmations/tests/docs/map;ed65ba7 corrected captured member ID for archive refresh;a39ec38 reviewed exact Groma duplicate/diagnostic evidence. PR https://github.com/DinoDeWaen/wacken/pull/14.

## Risks and follow-up

Interrupted remote work may have completed even when its old screen cannot observe acknowledgement; recovery explicitly asks for current-state/history review and never automatically retries a write. Existing stable/revision/atomic application operations remain the authority. Process loss intentionally does not persist passwords/CSV contents. Android scanner warning2 for anonymous adapter super calls was investigated against actual successful javac/native execution and explicitly documented, not treated as compiler success.212 owns integrated actual accessibility/contrast/visual review and physical/outdoor limitations;213 publication. Unrelated user edits excluded.
<!-- SECTION:NOTES:END -->
