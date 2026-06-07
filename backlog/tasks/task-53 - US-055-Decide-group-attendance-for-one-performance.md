---
id: task-53
title: 'US-055: Decide group attendance for one performance'
status: To Do
assignee: []
created_date: '2026-06-07 15:36'
labels:
  - mvp2
  - scheduling
  - domain
dependencies: []
priority: high
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
As a Wacken Planner group member, I want the app to combine all group ratings for a band into a clear attendance decision, so the generated schedule can explain why a band is selected, optional, unrated, or blocked.

In scope:
- Implement the single-performance group decision rules for ratings 0-5, must-see, want-to-see, optional, unrated, and veto handling.
- Apply the lunch-window optional rule for max rating 3 during 12:00-14:00 without inserting lunch blocks yet.
- Expose a decision result that can be used by scheduling and UI code.

Out of scope:
- Conflict resolution between overlapping performances.
- Travel feasibility, lunch planning, food suggestions, PDF output, and multi-group support.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 Given a band has at least one rating of 5, when the group decision is calculated, then the decision is GO.
- [ ] #2 Given a band has maximum rating 4, when the group decision is calculated, then it is GO unless there are two or more veto ratings.
- [ ] #3 Given a band has maximum rating 3, when the group decision is calculated, then it is GO unless there is any veto, and it is OPTIONAL during 12:00-14:00.
- [ ] #4 Given a band has maximum rating 2 or only unrated values, when the group decision is calculated, then it is OPTIONAL or UNRATED according to BR-011 and BR-012.
- [ ] #5 Given veto combinations block a band, when the decision is calculated, then the result explains the veto reason.
- [ ] #6 Automated tests cover BR-001 to BR-012 and BR-020.
- [ ] #7 README and business requirements impact are recorded using the canonical wording from delivery-governance.md.
- [ ] #8 Architecture impact is assessed; ADR impact is recorded using the canonical wording from delivery-governance.md.
<!-- AC:END -->
