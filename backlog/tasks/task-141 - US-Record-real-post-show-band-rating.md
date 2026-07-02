---
id: task-141
title: 'US: Record real post-show band rating'
status: To Do
assignee: []
created_date: '2026-07-02 08:26'
labels:
  - mvp3
  - ratings
  - offline
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Business value: after seeing a band live, users need to capture how good the performance really was without rewriting the planning preference used for the group schedule.

As a Wacken Planner user, I want to add a real post-show rating on the band detail screen, so that I can compare my planning expectation with the actual performance.

Scope: band detail UI, separate real-rating persistence, reset to unrated, offline local save, and display/export availability.

Out of scope: using real ratings in MVP2 schedule decision rules or replacing planning ratings.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a band detail screen, when I set a real post-show rating from 1 to 5, then it is saved separately from my planning rating
- [ ] #2 Given I reset the real post-show rating, when I return to the band detail screen, then the real rating is unrated and my planning rating is unchanged
- [ ] #3 Given I am offline, when I set or reset a real post-show rating, then the change is saved locally and remains visible after reopening the app
- [ ] #4 Given a real rating exists, when ratings are exported, then the real rating is available to the export story
- [ ] #5 Given schedule generation runs, then real post-show ratings do not change planning decisions
- [ ] #6 Automated tests cover separation between planning rating and real post-show rating
- [ ] #7 Manual test steps are documented in implementation notes
- [ ] #8 Business requirements and README impact use canonical delivery-governance wording
- [ ] #9 Architecture impact is assessed before implementation; if a new Room/Supabase schema is needed, explicit approval and ADR handling are completed before coding
<!-- AC:END -->
