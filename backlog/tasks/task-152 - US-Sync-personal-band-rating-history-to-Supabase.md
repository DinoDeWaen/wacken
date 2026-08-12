---
id: task-152
title: 'US: Sync personal band rating history to Supabase'
status: To Do
assignee: []
created_date: '2026-08-12 07:53'
labels:
  - user-story
  - post-mvp3
  - ratings
  - supabase
  - history
dependencies:
  - task-149
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want my real ratings for bands I have seen to sync to Supabase so that my personal band history is preserved across devices and future festivals.

Business value: Personal experience ratings become durable history and can be reused for later festival planning.

Scope: store personal band rating events per user, band, festival, rating value, and created date; sync them to Supabase; show multiple historical ratings for the same band with festival/date context.

Out of scope: group-average personal ratings, alias-based band linking, editing historical archived festival data.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a user records a real post-show rating, when sync succeeds, then Supabase stores a personal band rating event with user, band, festival, rating, and created date.
- [ ] #2 Given the same user rates the same band at two festivals, then both rating events are retained with their festival and created-date context.
- [ ] #3 Given a user opens a known band, then they can see their historical personal ratings for that band with festival and date reference.
- [ ] #4 Given the device is offline, then the rating remains local and is queued for later sync without logging the user out solely because Supabase cannot be reached.
- [ ] #5 Domain/application tests cover rating-history rules and infrastructure tests cover Supabase mapping/sync behavior.
- [ ] #6 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->
