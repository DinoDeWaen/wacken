---
id: TASK-208
title: 'US: Rate bands accessibly in the paired 3.0 detail flow'
status: Done
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-22 09:17'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-207
references:
  - banddetailactivity
  - wackentheme
modified_files:
  - app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java
  - app/src/main/java/be/wacken/planner/RatingValueText.java
  - app/src/main/java/be/wacken/planner/LineupPresentation.java
  - app/src/main/java/be/wacken/planner/RatingStarsView.java
  - app/src/main/res/values/ids.xml
  - app/src/main/java/be/wacken/planner/MainActivity.java
  - app/src/main/java/be/wacken/planner/BandDetailPresentation.java
  - app/src/main/java/be/wacken/planner/BandDetailActivity.java
  - app/src/main/java/be/wacken/planner/ArchivedBandDetailActivity.java
  - app/src/main/java/be/wacken/planner/WackenTheme.java
  - >-
    groma/systems/wacken/containers/android-application/components/ratingvaluetext.md
  - >-
    groma/systems/wacken/containers/android-application/components/banddetailactivity.md
  - app/src/test/java/be/wacken/planner/RatingValueTextTest.java
  - >-
    groma/systems/wacken/containers/android-application/components/wackentheme.md
  - >-
    groma/systems/wacken/containers/android-application/components/mainactivity.md
  - >-
    groma/systems/wacken/containers/android-application/components/archivedfestivalactivity.md
  - README.md
  - backlog/docs/business-requirements.md
  - backlog/docs/groma-rating-controls-review-2026-10-22.md
  - tools/groma/architecture-baseline.json
priority: medium
type: enhancement
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As an attendee, I want planning and post-show ratings clearly separated in both appearances so I can rate confidently without changing the wrong value. Implement planning-first hierarchy, After the show disclosure, read-only group ratings, history, music links and the TASK-202 discovery entry. Preserve biography, images and performance context supplied by real data.

Validated baseline: TASK-201, design/release-2.34/themes.html; user approved 2026-10-17. Keep the existing Android Views stack and current domain/application/infrastructure boundaries. Assess architecture impact before implementation; significant changes require explicit approval and ADR handling.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 Given planning rating 0 or 1-5, when touch, keyboard or TalkBack selects/clears it, then the selected value and meaning are announced and persisted; zero remains Unrated.
- [x] #2 Given After the show, when a real rating is changed/reset, then planning and shared schedule decisions are unchanged and personal history semantics remain intact.
- [x] #3 Given a theme change or recreation during editing, then selected band, ratings, pending state and expanded sections are preserved without duplicate events.
- [x] #4 Given unknown music links or archived read-only context, then available actions respect metadata and archive rules and the verified discovery action appears only in an allowed context.
- [x] #5 Domain/application characterization plus native accessibility tests cover clear, independent ratings, history, read-only archives and theme/recreation.
- [x] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
- [x] #7 Given ADR-0016 ratings for one canonical band differ by member/festival, then both appearances show the correct selected-festival values and labeled established/history context; edits cannot change another rating role or festival.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Added red native accessible-value/keyboard/clear and disclosure/event-identity scenarios under existing scoped characterization. 2. Replaced touch-only stars with real native Buttons, scoped meanings/selection,48dp/wrapping/large-font stacking and theme-safe keyboard focus surfaces; shared plain vocabulary retains prior labels. 3. Made active detail planning-first with saved After the show disclosure and stable scroll/control IDs; preserved selected festival/member/history/pending state, actual metadata/images and canonical no-festival lookup. Disabled archived children and explicit read-only actions. 4. Passed87 native cases each API23/API36 and local full suite, unit/BDD/application/domain/ArchUnit/coverage/QA/lint/API/database/safety gates and fresh stable-key signed checkpoint. 5. Curated existing detail/theme ownership/prose and5 actual source rows through Groma, rendered7-source detail reviewed,2 replacement duplicate identities and exact scanner deltas explicitly dispositioned; two scans pass. 6. Updated README/requirements and validation package. Deviations: native Button posted-event synchronization; real Tab enters keyboard mode portably; full suite caught and corrected overrestricted no-festival canonical lookup. Architecture standard/ADR0024 retained; no new significant decision. Remaining209-213 stay separate.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
## Implementation summary
Shared rating choices now use native Buttons with explicit scoped value/meaning/selected state, keyboard focus outlines and48dp targets. Large fonts stack choices; zero remains Unrated through explicit clear actions. Active detail is planning-first with separate established/source/history and read-only group planning context. After the show is a saved disclosure around the independent post-show editor. Stable scroll/control IDs support hierarchy restoration; pending device state remains visible without replaying writes. Archived children/clear actions are disabled and read-only context explicit. Known links/biography/images/performance remain real; canonical-band music discovery retains its existing no-festival entry and is absent in captured archived context.

## Acceptance criteria validation
AC1: native touch characterization, real Tab/Enter selection, framework accessibility ACTION_CLICK, meaning/selected labels and0/clear persistence pass.
AC2: existing independent after-rating/reset/established/planning/history regressions and real database lifecycle/schedule tests pass; UI still calls original scoped application writers.
AC3: native disclosure/theme/recreation preserves selected festival/expansion and exact after operationId/recordedAt without replay; selected-row and metadata return remain correct.
AC4: disabled archived child accessibility clicks and clear guards pass; no fabricated links; existing canonical discovery/no-festival and return tests pass, captured archived detail has no discovery.
AC5: focused+full native characterization and domain/application/backend gates pass.
AC6: full package and canonical impacts below.
AC7: actual context retains member/festival values and established source/deleted-source/history labels; existing isolation/source-correction/creation snapshot/archive promotion gates pass in both native suites and database tests.

## How to test
### Automated tests
CI https://github.com/DinoDeWaen/wacken/actions/runs/39753427522 passed all4 jobs on3eae536b00eecb5428defd9b426ac87269bf9f09:87 native tests each on API23/API36, JVM/unit/coverage/ArchUnit, QA, Android lint/API checks, signed-upgrade/image safety tests and real PostgreSQL/PostgREST creation plus integrated lifecycle/source-correction/member-isolation/replay/reset/date gates. Local full87/87 passed with no skips/failures; focused real Tab/Enter/accessibility/clear passes. New tests cover native Button semantics/minimum geometry/stateful focus,200% stacked choices, disclosure/no-duplicate operation identity and archived action rejection. Unit shared-value vocabulary and prior lineup label characterization pass.
Normal Groma gate passed two scans with96 components/299 sources/13 flows/81 individually reviewed identities.79 identities/dispositions unchanged;2 exact replacements reviewed individually. Five supported collaborations added, no row/flow removed, pins/exceptions unchanged. git diff --check passed.
Fresh clean clone at f6e89ec54c2493c7197807111d7f125333edd61e: JDK21 ./gradlew test qaTest :app:lintDebug assembleRelease --rerun-tasks passed99 executed tasks. APK /private/tmp/wacken-208-final-build-checkout/app/build/outputs/apk/release/app-release.apk SHA256 ff7be32007f263c77c954867073b8c577c37eee74e3e4a98a69778e2b10b23f8. apksigner verify passed existing stable certificate SHA256 ede2ae97735a305900e87bc0fdc021c03382db4e30435f2349a96a3fdcc7545f. Subsequent commits only update evidence/docs/baseline and test keyboard setup; production source is identical. Development checkpoint2.35.1/code44 is not published.
### Manual validation
Rendered Groma detail role, exact7 sources and five shared caller/style collaborations inspected in /private/tmp/wacken-208-final-map. Native framework actions/geometry/input tests are automated evidence; no claim of a human TalkBack or physical/daylight pass. Integrated manual/device/contrast matrix stays TASK212.
Reproduce: choose a planning value by touch or keyboard, clear to Unrated, expand After the show and save/reset separately, change appearance/recreate and verify retained expansion/rating/context. Inspect archived controls as disabled and known music return. Use87-case full smoke runner on isolated emulator.

## TDD / BDD / approval-test evidence
Red native test showed android.widget.TextView where actionable Button was required. Shared Button implementation passes accessibility/value/clear and real keyboard tests. Native Button touch posts its click; existing touch characterization now waits for persisted outcome instead of assuming synchronous dispatch. Actual input-mode delivery is asynchronous; final test enters keyboard mode with real Tab before Enter. Initial full suite caught overrestricted canonical lookup with no festival; restored existing contract without changing provider policy. Final full suites pass both APIs.

## Architecture impact
Architecture-significant change: none beyond approved standard Android Views/ADR0024 composition. Approval received: user Standard. No new framework/module/dependency/domain policy/persistence/security/provider contract. Shared display vocabulary is combined via Groma CLI into existing detail/control responsibility; native selected/focus surfaces remain theme-owned. Existing application writers own all member/festival/lifecycle rules and are unchanged.
ADR impact: none, because native widgets/disclosure/view state and shared presentation vocabulary preserve approved boundaries and decisions. Exact source/role/collaboration/duplicate/scanner review is backlog/docs/groma-rating-controls-review-2026-10-22.md.

## README impact
README impact: updated3.0 development accessible rating/disclosure/canonical discovery behavior, native validation limitation and current reviewed map counts; published2.35.1 remains separate.

## Business requirements impact
Business requirements impact: updated development planning-first/after disclosure, scoped accessibility/Unrated and read-only/canonical lookup presentation, preserving all ADR0016 rules and remaining209-213 scope.

## Diagram impact
Diagram impact: updated existing detail/theme role descriptions and helper ownership plus five source-supported shared-control/style rows through CLI.96 components/299 sources/13 flows remain; no new container/system or removed collaboration/flow. Normal baseline reviewed explicitly.

## Commits / logical change list
036e609 accessible ratings/disclosure/shared focus/native scenarios;f6e89ec preserve canonical discovery outside festival;edb1ce7 exact map/duplicate/scanner review and docs;3eae536 real-key keyboard entry fixture.

## Risks and follow-up
Schedule209, Settings/reviews210, lifecycle/support211, integrated native contrast/TalkBack/physical limitations212 and final signed upgrade/publication213 remain. No new exception; the two previously accepted stale importer arrows stay exact. This task does not publish3.0.
<!-- SECTION:NOTES:END -->
