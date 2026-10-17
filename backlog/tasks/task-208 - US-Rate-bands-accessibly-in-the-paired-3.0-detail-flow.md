---
id: TASK-208
title: 'US: Rate bands accessibly in the paired 3.0 detail flow'
status: To Do
assignee: []
created_date: '2026-10-17 09:21'
updated_date: '2026-10-17 13:21'
labels:
  - ux
  - release-3.0
milestone: m-2
dependencies:
  - TASK-207
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

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Cross-cutting architecture approved2026-10-17: follow ADR-0016 and the implemented2.34 lifecycle from TASK-216–220. The3.0 appearance/navigation redesign must preserve canonical band metadata, member-specific current band ratings, separate festival planning/after-ratings, one-time creation snapshots, archive-only promotion and source-aware corrections. No theme/layout change may reintroduce latest-event-as-band-rating or cross-festival fallback.
<!-- SECTION:NOTES:END -->
