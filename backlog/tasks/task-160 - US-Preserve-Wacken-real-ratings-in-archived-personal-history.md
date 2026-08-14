---
id: task-160
title: 'US: Preserve Wacken real ratings in archived personal history'
status: To Do
assignee: []
created_date: '2026-08-14 09:42'
labels:
  - user-story
  - defect
  - archive
  - ratings
  - supabase
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want real ratings that I entered during Wacken to remain visible after archiving so that my personal experience ratings are not lost when the festival moves to history.

Business value: Real ratings are personal history and must survive the transition from active festival to archived festival; otherwise future festival prefilling and historical reference become untrustworthy.

Scope: existing Wacken real ratings, including ratings stored before personal rating events existed, must be migrated or backfilled into archived personal history; archived band detail must show the real rating value, for example Airbourne with 4 stars; Supabase sync must preserve the restored personal rating events where applicable.

Out of scope: changing planning ratings, recalculating schedules from real ratings, editing archived real ratings, fuzzy band aliases, or assigning exact historical timestamps when old storage did not contain them.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a user recorded a real rating for Airbourne at Wacken before archiving, when Wacken is archived and Airbourne is opened, then the archived band detail shows Airbourne's real rating as 4 stars.
- [ ] #2 Given legacy real ratings exist in local storage without personal rating event rows, when the app migrates or reads archived Wacken data, then those ratings are preserved as personal history instead of being shown as missing.
- [ ] #3 Given restored Wacken real ratings are available locally, when Supabase sync succeeds, then the corresponding personal band rating history is stored remotely where the current sync model supports it.
- [ ] #4 Given old real-rating storage did not contain a created timestamp, then the UI must show an honest fallback such as date unknown instead of inventing a date.
- [ ] #5 Automated regression tests cover legacy real-rating preservation for archived Wacken and visible real-rating display on archived band detail data.
- [ ] #6 README impact, business requirements impact, diagram impact, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->
