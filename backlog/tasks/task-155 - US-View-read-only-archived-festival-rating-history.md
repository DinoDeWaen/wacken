---
id: task-155
title: 'US: View read-only archived festival rating history'
status: To Do
assignee: []
created_date: '2026-08-12 07:53'
labels:
  - user-story
  - post-mvp3
  - archive
  - ratings
  - history
dependencies:
  - task-150
  - task-152
  - task-153
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a group member, I want to open archived festivals and band histories so that I can look back at what we planned and how I actually rated performances.

Business value: Completed festivals remain useful context instead of disappearing once planning moves to the next festival.

Scope: archived festival list when there is no active festival, read-only archived festival detail, band-level history showing planning and personal rating events with festival and created-date context where available.

Out of scope: editing archived festivals, admin-only archive permissions, analytics dashboards.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given no festival is active, when the app starts, then the user sees the archived festival list and can open an archived festival.
- [ ] #2 Given a user opens an archived festival, then its lineup, planning ratings, real/personal ratings, schedule context, and imported festival data are displayed read-only where available.
- [ ] #3 Given a band has personal rating events from multiple festivals, then the user can see each event with rating value, festival, and created date.
- [ ] #4 Given an archived festival is open, then controls that would edit festival data or ratings are unavailable in the first version.
- [ ] #5 BDD covers the no-active-festival start state and read-only archive inspection; domain/application tests cover historical rating retrieval.
- [ ] #6 README, business requirements, diagram, and ADR impact are recorded using delivery-governance wording.
<!-- AC:END -->
