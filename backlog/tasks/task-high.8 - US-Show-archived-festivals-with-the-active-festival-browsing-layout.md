---
id: task-high.8
title: 'US: Show archived festivals with the active festival browsing layout'
status: To Do
assignee: []
created_date: '2026-08-14 09:41'
updated_date: '2026-08-14 09:41'
labels:
  - user-story
  - archive
  - ui
  - ratings
dependencies: []
parent_task_id: task-high
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want an archived festival to use the same browsing layout as the active festival so that looking back at Wacken feels like opening the original festival, not a reduced summary screen.

Business value: Archived festivals remain useful and familiar, with the same band list and band detail experience users already trust.

Scope: archived festival opens to the same style band list as the active festival; archived band rows show band, planning rating, stage, date, time, and music-link actions where available; tapping a band opens a band detail screen with the same visual sections as the active band detail screen, including image, Your Rating, Real Rating, Running Order, Band Links, and biography where available. Archived data remains read-only unless a later requirement explicitly allows editing.

Out of scope: changing active festival behavior, editing archived ratings, adding analytics dashboards, fuzzy band matching, or multiple active/upcoming festivals.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a user opens an archived festival, when archived band data exists, then the screen uses the same table layout and visual style as the active festival band list.
- [ ] #2 Given an archived band has stage/date/time/music-link metadata, when the archived list is shown, then those values appear in the same columns/actions as the active festival list.
- [ ] #3 Given a user taps an archived band, then the detail screen uses the same visual sections as the active band detail screen: image, Your Rating, Real Rating, Running Order, Band Links, and biography where available.
- [ ] #4 Given the archived festival is read-only, then rating controls and reset actions do not modify archived data; if controls are visible for layout parity, they must be disabled or otherwise clearly non-editing.
- [ ] #5 Automated tests or characterization coverage protect active festival layout behavior while adding archived layout parity.
- [ ] #6 README impact, business requirements impact, diagram impact, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->
