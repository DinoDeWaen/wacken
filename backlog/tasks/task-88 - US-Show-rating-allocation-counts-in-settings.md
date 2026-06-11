---
id: task-88
title: 'US: Show rating allocation counts in settings'
status: To Do
assignee: []
created_date: '2026-06-11 15:18'
labels:
  - android
  - settings
  - ratings
  - mvp2
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Add a rating distribution summary to the Settings screen.

As a festival attendee, I want to see how many bands I have rated with 5, 4, 3, 2, and 1 stars so that I can quickly understand and balance my rating allocation before generating or reviewing the group schedule.

Scope: show counts for the signed-in user ratings available in the app. Out of scope: changing the rating scale, changing schedule rules, or adding charts beyond the count summary.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given I am signed in and have rated bands, when I open Settings, then I see separate counts for 5-star, 4-star, 3-star, 2-star, and 1-star ratings.
- [ ] #2 Given I have no bands for a rating value, when I open Settings, then that rating value is still shown with a count of 0.
- [ ] #3 Given ratings change and Settings is opened again after refresh/sync, then the displayed counts reflect the current local ratings.
- [ ] #4 Automated tests or focused characterization coverage prove the rating-count calculation and Settings wiring.
- [ ] #5 README, business requirements, ADR, and diagram impact are recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
