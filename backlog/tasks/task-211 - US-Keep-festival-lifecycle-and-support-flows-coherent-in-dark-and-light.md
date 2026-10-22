---
id: TASK-211
title: 'US: Keep festival lifecycle and support flows coherent in dark and light'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-22 20:25'
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
- [x] #1 Given startup/auth/recovery and an existing cache, then theme is applied consistently while cached work and background sync retain their current guarantees.
- [x] #2 Given archive/add/rename/import or confirmation, then action scope, read-only state and success/failure recovery are clear in both appearances; no false group-wide success is shown.
- [x] #3 Given a form or selected CSV, when theme changes or Activity recreates, then user input and pending operation state are preserved or restored with explicit safe recovery.
- [x] #4 Given saved-local/pending/shared-saved status, then meaningful words and accessible announcements distinguish these states independently of color.
- [x] #5 Tests cover lifecycle confirmations, atomic import failure/retry, offline cache, export/invite boundaries, auth return and theme state retention.
- [x] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
- [x] #7 Creation/archive feedback preserves ADR-0016 atomic carry-over/promotion and pending-data recovery, and cannot claim completion after a partial or failed lifecycle operation.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Completed standard existing Views/ADR0024 support slice: characterized23 existing lifecycle/review/correction/appearance cases; red→green200% sign-in/archive; six new native support cases. Added wrapping read-only archived rows, scrollable forms and paired owned native dialog/actions; weak dialog ownership dismisses windows. Retained one sign-in request/memory-only password, rename draft/festival scope, local CSV review/viewport; explicit safe recovery for uncertain lifecycle/correction requests without replay. Archive now requires scoped confirmation before unchanged atomic workflow; status is live and distinguishes local/pending/shared/error. Source review added refreshed archived values/Bands return state; corrected extracted member ID. API23 CI caught weak-entry GC race and offscreen-footer test assumption; fixed strong per-refresh targets while keeping weak long-term storage and actual scrolling. Final local98, CI39847849598 API23/API36 each98 plus complete build/database/Groma gates,27 fixtures and clean9a7073b test/QA/lint/fresh stable-key APK all pass. README/BR/map/exact83 groups and diagnostic deltas reviewed. No new architecture boundary/policy/API/schema/dependency or ADR; Standard/ADR0024 and ADR0016 retained. Integrated actual TalkBack/keyboard/contrast/native visual validation remains212; publication213.
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
5. Native98 cases pass before final archive-return refinements, final41 affected cases pass afterward; existing atomic import/correction/archive/export/invite/offline cases retained and six support cases added. Final local98 and required CI39847849598 pass; API23/API36 each98, all four jobs green.
6. README/BR and reviewed source/map/duplicate/scanner evidence updated; full CI evidence recorded below.
7. Creation/archive/correction application and gateway policies are unchanged; existing transaction/replay/source/pending regressions run in complete required CI.

## How to test

### Automated tests

- Existing23 appearance/archive/identity/local-import/correction cases characterized before source changes: /private/tmp/wacken-211-characterization.log.
- New200% sign-in and archived-name scenarios fail red for missing scroll/ellipsis, then pass after wrapping layouts: wacken-211-red-native.log and wacken-211-green-native.log.
- Styled-dialog15 and expanded support55 native cases pass: wacken-211-dialog-native.log and wacken-211-support-native.log.
- sh scripts/verify-android-smoke.sh emulator-5554 all:98/98 pass on owned API36 appearance205, wacken-211-native-full.log. Final41 affected cases pass on corrected 9a7073b after archive-return/recovery refinements, wacken-211-final-focused.log. Includes all six new support scenarios.
- Final clean clone9a7073b: ./gradlew test qaTest :app:lintDebug assembleRelease --rerun-tasks passes, /private/tmp/wacken-211-signed-final.log. Includes JVM/domain/application/BDD/coverage/QA/ArchUnit. A scope error from extracting archived list refresh was caught by the previous clean/native compilation and corrected before this successful build.
- Pinned Groma two-scan gate passes96components/299sources/13flows/83reviewedgroups, existing two stale-arrow exceptions only;27 positive/negative fixtures pass. wacken-211-groma-final.log and wacken-211-groma-fixtures.log.
- Obsolete archived-table source-string assertions removed; existing read-only/known-action guards retained and actual native wrapping/meaning checks replace geometric source matching.
- git diff --check passes. Initial CI39845732438 passed API36/build/database/Groma but exposed an API23 weak-map refresh null-target race and an offscreen-footer test assumption. Fixed both in9a7073b: copy weak keys into strong per-refresh target lists, check current bindings, and scroll to Archive before clicking. New two-scan Groma gate passes with exact two accessor calls removed and investigated diagnostic deltas; wacken-211-groma-gc-final.log. Fresh corrected clean9a7073b build passes. Final local98/98 passes on9a7073b (wacken-211-final-native-all.log). Required CI39847849598 on509d57de0621567360b228b70008971b8a9ba247 passes all four jobs:98 native each API23/API36; complete JVM/BDD/coverage/QA/ArchUnit/lint/supported APIs; real PostgreSQL/PostgREST creation/rating/archive/correction/deletion and integrated ADR0016 lifecycle; pinned Groma/fixtures. Parallel PR-triggered39847858271 tests the same head; no weaker result substituted.

### Manual validation

Actual source/composition and Chromium /private/tmp/wacken-211-map overview/source/style/caller relationships reviewed; screenshot /private/tmp/wacken-211-map-detail.png. Current Supabase changelog/session docs checked with the Supabase skill; no backend/auth endpoint,token,security or TLS configuration was changed. Native sign-in progress uses a controlled retained-state fixture, not a real credential/network sign-in; existing auth adapter tests remain separate. No human TalkBack/outdoor/physical-device experience is claimed; integrated native assistive/visual/contrast review is212.

Fresh stable-key APK from corrected clean9a7073b: /private/tmp/wacken-211-gc-fixed-build-checkout/app/build/outputs/apk/release/app-release.apk. SHA25625be3451687238346c53aaa76edd0bd19bc975cf4524b4065b5616f3cf86fac2; apksigner certificate ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f verifies. Fresh checkout contains no prior APK. Later changes are docs/baseline/task evidence only. Unpublished checkpoint;2.35.1 remains the release.

## TDD / BDD / approval-test evidence

Existing23 native and application/gateway characterizations preserve local import rollback, explicit correction, archive promotion and offline guarantees. New large-font sign-in/archive cases moved red→green. New Given rename/When theme+recreate+cancel, Given archive/When cancel, Given staged correction/When recreate and retained sign-in progress/failure cases cover no writes/duplicate submission. Recovery refinements followed source review and are included in final41. API23 CI supplies the red weak-reference regression; the fix holds live views/panels strongly only during each refresh, retaining weak long-term storage. No policy moved to presentation.

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

480a81f readable support layouts, retained state, confirmations/tests/docs/map;9a7073b corrected captured member ID for archive refresh;a39ec38 reviewed exact Groma duplicate/diagnostic evidence;9a7073b GC-safe palette refresh and actual footer scrolling;509d57d reviewed exact post-fix scanner evidence. PR https://github.com/DinoDeWaen/wacken/pull/14.

## Risks and follow-up

Interrupted remote work may have completed even when its old screen cannot observe acknowledgement; recovery explicitly asks for current-state/history review and never automatically retries a write. Existing stable/revision/atomic application operations remain the authority. Process loss intentionally does not persist passwords/CSV contents. Android scanner warning2 for anonymous adapter super calls was investigated against actual successful javac/native execution and explicitly documented, not treated as compiler success.212 owns integrated actual accessibility/contrast/visual review and physical/outdoor limitations;213 publication. Unrelated user edits excluded.
<!-- SECTION:NOTES:END -->
