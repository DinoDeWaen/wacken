---
id: TASK-208
title: 'US: Rate bands accessibly in the paired 3.0 detail flow'
status: In Progress
assignee:
  - '@codex'
created_date: '2026-10-17 09:21'
updated_date: '2026-10-22 09:03'
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
- [ ] #1 Given planning rating 0 or 1-5, when touch, keyboard or TalkBack selects/clears it, then the selected value and meaning are announced and persisted; zero remains Unrated.
- [ ] #2 Given After the show, when a real rating is changed/reset, then planning and shared schedule decisions are unchanged and personal history semantics remain intact.
- [ ] #3 Given a theme change or recreation during editing, then selected band, ratings, pending state and expanded sections are preserved without duplicate events.
- [ ] #4 Given unknown music links or archived read-only context, then available actions respect metadata and archive rules and the verified discovery action appears only in an allowed context.
- [ ] #5 Domain/application characterization plus native accessibility tests cover clear, independent ratings, history, read-only archives and theme/recreation.
- [ ] #6 README and business requirements impacts use canonical delivery-governance wording; approved behavior is documented, diagrams updated only where affected, and the full validation package is recorded before merge.
- [ ] #7 Given ADR-0016 ratings for one canonical band differ by member/festival, then both appearances show the correct selected-festival values and labeled established/history context; edits cannot change another rating role or festival.
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
Standard presentation slice in approved Views/ADR0024 boundary. 1. Characterize scoped planning/after/history and archived read-only behavior; add red native keyboard/accessibility/clear and After the show disclosure/recreation scenarios. 2. Replace touch-only shared stars with keyboard-focusable native button choices, explicit scope/value/meaning/selected semantics, meaningful Unrated/reset and theme bindings; use48dp targets and stack/reflow at large fonts. Preserve existing callback/use-case writes and read-only guards. 3. Make active detail planning-first, show established/context/history separately and expose After the show disclosure with saved expansion and stable scroll IDs; retain band/festival/pending state without writes on theme/recreation. Preserve actual biography/images/performance and known links; discovery only in active allowed context. 4. Update relevant existing native characterization for disclosure and add independent ratings/clear/archive tests using actual framework accessibility actions and keyboard events. 5. Run unit/domain/application/native API23/API36, ArchUnit/lint/Groma/database gates; review source/map/duplicate/diagnostic deltas, update README/requirements and fresh stable-key signed checkpoint, record full validation before Done. No new framework/module/data contract/domain policy or persistence change; no additional significant approval/ADR needed. Risks: shared star control read-only consumers, large-font geometry, accessibility focus/announcements, selected festival capture and retained disclosure. Human TalkBack/device integrated matrix remains212.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.

Red native test proved old star children exposed TextView instead of actionable Button. Added native scoped choices/selected semantics, explicit clear/Unrated, wrapping48dp controls, theme-safe selected/focus styling and planning-first retained After the show disclosure. Shared plain meaning text extracted under existing detail responsibility; archived child clicks disabled, selected festival read-only guard/discovery restrictions retained. App unit suite passed; focused accessibility/keyboard test passes after awaiting actual input-mode transition and framework-posted native clicks in characterization. Full87-case native suite running; Groma CLI combines meaning helper and authors5 supported shared-control/style collaborators. Source/map/baseline/signed/CI validation remains before Done.
<!-- SECTION:NOTES:END -->
