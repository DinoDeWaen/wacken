---
id: task-112
title: 'US: Show group member ratings on band detail'
status: To Do
assignee: []
created_date: '2026-06-16 20:01'
labels:
  - band-detail
  - ratings
  - group
  - ui
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner user, I want to see the ratings other people gave on the band detail screen, so that I understand the group preference before deciding what to rate or attend.

Business value: band detail should expose the same shared group context as planning and overview, making disagreements and must-see ratings visible without leaving the detail page.

In scope:
- Show read-only per-person group ratings on the band detail screen when shared group rating data is available.
- Clearly distinguish my editable rating from other people's read-only ratings.
- Handle unrated group members and missing/offline group-rating data without broken UI.
- Keep existing biography, image, schedule, and music-link behavior intact.

Out of scope:
- Editing another user's rating.
- Adding multiple-group support.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given group ratings are available for a band, when I open that band detail, then I see each available group member's rating for that band.
- [ ] #2 Given I am on the band detail screen, when my own rating is shown, then it remains the editable rating control and other people's ratings are read-only.
- [ ] #3 Given a group member has not rated the band, when group ratings are displayed, then the UI shows an unrated/empty state instead of treating it as a veto.
- [ ] #4 Given group ratings are not yet synced or are unavailable offline, when I open band detail, then the screen still works and shows a clear no-group-ratings state.
- [ ] #5 Automated tests or focused app coverage prove detail rating presentation and separation between editable own rating and read-only group ratings; Android compile validation passes.
- [ ] #6 Business requirements and README impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
